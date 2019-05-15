package com.seasun.management.service.impl;

import com.seasun.management.constant.CpConstant;
import com.seasun.management.dto.IdValueDto;
import com.seasun.management.dto.SimpleBudgetDto;
import com.seasun.management.dto.cp.CPOrderDto;
import com.seasun.management.dto.cp.ExchangeRateDto;
import com.seasun.management.dto.cp.OrdissuesExDto;
import com.seasun.management.exception.UserInvalidOperateException;
import com.seasun.management.helper.cp.CPHelper;
import com.seasun.management.mapper.*;
import com.seasun.management.mapper.cp.CPMapper;
import com.seasun.management.mapper.cp.GameProjectMapper;
import com.seasun.management.mapper.cp.OrdissuesExMapper;
import com.seasun.management.mapper.cp.OrdissuesMapper;
import com.seasun.management.model.CfgExchangeRate;
import com.seasun.management.model.CpBudget;
import com.seasun.management.model.CpBudgetAppendInfo;
import com.seasun.management.model.CpProjectRelation;
import com.seasun.management.model.cp.GameProject;
import com.seasun.management.model.cp.OrdissuesEx;
import com.seasun.management.service.CpInfoService;
import com.seasun.management.util.MyDateUtils;
import com.seasun.management.util.MyListUtils;
import com.seasun.management.vo.BasePeriodAppVo;
import com.seasun.management.vo.BaseQueryConditionVo;
import com.seasun.management.vo.BaseQueryResponseVo;
import com.seasun.management.vo.cp.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CpInfoServiceImpl implements CpInfoService {

    private static final Logger logger = LoggerFactory.getLogger(CpInfoServiceImpl.class);

    @Autowired
    private CPMapper cpMapper;

    @Autowired
    private CpSupportDataMapper cpSupportDataMapper;

    @Autowired
    private OrdissuesExMapper ordissuesExMapper;

    @Autowired
    private OrdissuesMapper ordissuesMapper;

    @Autowired
    private CpBudgetMapper cpBudgetMapper;

    @Autowired
    private CpProjectRelationMapper cpProjectRelationMapper;

    @Autowired
    private CpBudgetAppendInfoMapper cpBudgetAppendInfoMapper;

    @Autowired
    private CfgExchangeRateMapper cfgExchangeRateMapper;

    @Autowired
    private GameProjectMapper gameProjectMapper;

    @Override
    public BaseQueryResponseVo getOutsourcers(Integer year, Integer currentPage, Integer pageSize, List<Integer> grades, String makingType, String styleType, String sortField) {

        if (year == null) {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }
        if (currentPage == null) {
            currentPage = BaseQueryConditionVo.defaultPage;
        }
        if (pageSize == null) {
            pageSize = BaseQueryConditionVo.defaultPageSize;
        }

        //排序
        boolean desc = true;
        if (sortField != null && sortField != "") {
            String[] split = sortField.split("\\|");
            if ("sortNumber".equals(split[0])) {
                sortField = "ordersRecord";
                if ("des".equals(split[1])) {
                    sortField = sortField + " desc";
                }
            } else {
                if (!"des".equals(split[1])) {
                    desc = false;
                }
                sortField = null;
            }
        }

        BaseQueryResponseVo result = new BaseQueryResponseVo();

        //外包商信息
        List<OutsourcerVo> outsourcerVos = cpMapper.selectByCond(year, grades, makingType, styleType, sortField);
        if (!outsourcerVos.isEmpty()) {

            List<OrdissuesExDto> ordissuesExDtos = ordissuesExMapper.selectByCPIdsAndCreatedYear(outsourcerVos.stream().map(x -> x.getId()).collect(Collectors.toList()), year);
            Map<String, BigDecimal> rateByMoneyNameMap = cfgExchangeRateMapper.selectAll().stream().filter(x -> OrdissuesEx.Currencies.RMB.equals(x.getdName())).collect(Collectors.toMap(x -> x.getsName(), x -> x.getRate()));
            Map<Integer, List<OrdissuesExDto>> ordissuesExDtosByCPIdMap = ordissuesExDtos.stream().collect(Collectors.groupingBy(x -> x.getcPId()));
            for (OutsourcerVo outsourcerVo : outsourcerVos) {
                BigDecimal totalAmount = new BigDecimal(0);
                for (OrdissuesExDto ordissuesExDto : ordissuesExDtosByCPIdMap.get(outsourcerVo.getId())) {
                    totalAmount = totalAmount.add(getOrdissueExAmountOfMoney(ordissuesExDto, rateByMoneyNameMap.get(ordissuesExDto.getCurrencies())));
                }
                outsourcerVo.setTotalAmount(totalAmount);
            }
            // 默认按接单金额排序
            if (sortField == null) {
                boolean descFlag = desc;
                Collections.sort(outsourcerVos, new Comparator<OutsourcerVo>() {
                    @Override
                    public int compare(OutsourcerVo o1, OutsourcerVo o2) {
                        if (descFlag) {
                            return o2.getTotalAmount().compareTo(o1.getTotalAmount());
                        }
                        return o1.getTotalAmount().compareTo(o2.getTotalAmount());
                    }
                });
            }

            int size = outsourcerVos.size();
            if (size >= (currentPage + 1) * pageSize) {
                outsourcerVos = outsourcerVos.subList(currentPage * pageSize, (currentPage + 1) * pageSize);
            } else if (size >= currentPage * pageSize) {
                outsourcerVos = outsourcerVos.subList(currentPage * pageSize, size);
            }
        }

        Integer totalRecord = cpMapper.selectCountByCond(year, grades, makingType, styleType);

        result.setItems(outsourcerVos);
        result.setTotalRecord(totalRecord == null ? 0 : totalRecord);
        return result;
    }

    /**
     * 获取制作类型和风格
     *
     * @return
     */
    public Map<String, List<String>> getSupport() {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        List<String> category = new ArrayList<String>();
        List<String> style = new ArrayList<String>();
        List<CpSupportVo> list = cpSupportDataMapper.getSupport();
        for (CpSupportVo vo : list) {
            if (CpConstant.TYPE_ONE.equals(vo.getType())) {
                category.add(vo.getValue());
            } else if (CpConstant.TYPE_TWO.equals(vo.getType())) {
                style.add(vo.getValue());
            }
        }
        map.put(CpConstant.CATEGORY, category);
        map.put(CpConstant.STYLE, style);
        return map;
    }

    @Override
    public BaseQueryResponseVo getProjectInfos(Integer year, Integer currentPage, Integer pageSize, String sortField) {
        BaseQueryResponseVo result = new BaseQueryResponseVo();
        Integer totalRecord = 0;
        if (year == null) {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }
        if (currentPage == null) {
            currentPage = BaseQueryConditionVo.defaultPage;
        }
        if (pageSize == null) {
            pageSize = BaseQueryConditionVo.defaultPageSize;
        }
//        List<CPProjectInfoVo> cpProjectInfoVosTemp1 = cpProjectRelationMapper.selectAllActiveItProject(year, currentPage * pageSize, pageSize);
        List<CPProjectInfoVo> cpProjectInfoVos = cpProjectRelationMapper.selectAllAmountProject(year, currentPage * pageSize, pageSize);
        if (!cpProjectInfoVos.isEmpty()) {
            totalRecord = cpProjectRelationMapper.selectAllActiveItAppProjectCountByCond(year); //统计个数
            List<String> gameProjectNames = new ArrayList<>();
            List<Integer> allGameProjectIds = new ArrayList<>();
            Map<Long, List<Integer>> gameProjectIdsByItProjectIdMap = new HashMap<>();

            for (CPProjectInfoVo cpProjectInfoVo : cpProjectInfoVos) {
                List<Integer> gameProjectIds = new ArrayList<>();
                // 拿取外包项目名，用于查询外包订单
                if (cpProjectInfoVo.getcPProjectName() != null) {
                    for (String gameProjectName : cpProjectInfoVo.getcPProjectName().split(",")) {
                        gameProjectNames.add(gameProjectName);
                    }
                }
                // 拿取外包项目ID，用去查询预算
                if (cpProjectInfoVo.getcPProjectId() != null) {
                    for (String gameProjectId : cpProjectInfoVo.getcPProjectId().split(",")) {
                        gameProjectIds.add(Integer.parseInt(gameProjectId));
                    }
                }
                gameProjectIdsByItProjectIdMap.put(cpProjectInfoVo.getId(), gameProjectIds);
                allGameProjectIds.addAll(gameProjectIds);
            }

            Map<String, BigDecimal> rateMap = getRate();
            List<IdValueDto> cpBudgets = cpBudgetMapper.selectByProjectIdsAndYear(allGameProjectIds, year);
            Map<Integer, Float> cpBudgetValueByIdMap = cpBudgets.stream().collect(Collectors.toMap(x -> Integer.parseInt(x.getId().toString()), x -> x.getValue()));//cp_project_id :预算
            List<OrdissuesExDto> ordissuesExDtos = ordissuesExMapper.selectByGameProjectNamesAndCreatedYear(gameProjectNames, year);
            Map<Integer, List<OrdissuesExDto>> ordissuesExDtosByCPGamneProjectIdMap = ordissuesExDtos.stream().collect(Collectors.groupingBy(x -> x.getcPProjectId()));
            BigDecimal zero = new BigDecimal(0);
            for (CPProjectInfoVo cpProjectInfoVo : cpProjectInfoVos) { //遍历外包项目
                // 总预算金额
                BigDecimal totalAmount = new BigDecimal(0);
                // 已支付金额
                BigDecimal payAmount = new BigDecimal(0);
                // 待支付金额
                BigDecimal willPayAmount = new BigDecimal(0);
                // 订单数量
                int orderCount = 0;

                for (Integer cPProjectId : gameProjectIdsByItProjectIdMap.get(cpProjectInfoVo.getId())) {
                    // 计算总预算
                    if (cpBudgetValueByIdMap.containsKey(cPProjectId)) {
                        totalAmount = totalAmount.add(new BigDecimal(cpBudgetValueByIdMap.get(cPProjectId)));
                    }

                    if (ordissuesExDtosByCPGamneProjectIdMap.containsKey(cPProjectId)) {
                        for (OrdissuesExDto ordissuesExDto : ordissuesExDtosByCPGamneProjectIdMap.get(cPProjectId)) {
                            boolean rmbFlag = OrdissuesEx.Currencies.RMB.equals(ordissuesExDto.getCurrencies());
                            BigDecimal rate = rateMap.get(ordissuesExDto.getCurrencies());
                            if (rate == null) {
                                rate = new BigDecimal(1);
                            }
                            // 计算已支付
                            if (CPHelper.doneOrdissuesStatus.contains(ordissuesExDto.getStatus())) {
                                if (ordissuesExDto.getRealPayMoney() != null) {
                                    if (rmbFlag) {
                                        payAmount = payAmount.add(ordissuesExDto.getRealPayMoney());
                                    } else {
                                        if (ordissuesExDto.getModifyRealPayMoney().toString().equals("1")) {
                                            payAmount = payAmount.add(ordissuesExDto.getRealPayMoney());
                                        } else {
                                            payAmount = payAmount.add(ordissuesExDto.getRealPayMoney().multiply(rate));
                                        }
                                    }
                                }
                            } else {
                                willPayAmount = willPayAmount.add(getOrdissueExAmountOfMoney(ordissuesExDto, rate));
                            }
                        }
                        // 订单数量
                        orderCount = orderCount + ordissuesExDtosByCPGamneProjectIdMap.get(cPProjectId).size();
                    }
                }
                cpProjectInfoVo.setOrdersRecord(orderCount);
                cpProjectInfoVo.setTotalAmount(totalAmount);
                cpProjectInfoVo.setPayAmount(payAmount);
                cpProjectInfoVo.setWillPayAmount(willPayAmount);
                if (totalAmount.compareTo(zero) != 0) {
                    double percent = payAmount.add(willPayAmount).divide(totalAmount, 4, RoundingMode.UP).multiply(new BigDecimal(100)).setScale(4, RoundingMode.HALF_UP).doubleValue();
                    if (percent > 100d) {
                        percent = 100d;
                    }
                    cpProjectInfoVo.setPercent(percent);
                } else {
                    cpProjectInfoVo.setPercent(0d);
                }
                cpProjectInfoVo.setSurplusAmount(totalAmount.subtract(payAmount).subtract(willPayAmount));
            }
        }

        //订单排序
        if (sortField == null) {
            sortField = CPProjectInfoVo.sort.sortIndex1 + "|" + CPProjectInfoVo.sort.sortDesc;
        }
        String[] split = sortField.split("\\|");
        switch (split[0]) {
            case CPProjectInfoVo.sort.sortIndex1:
                Collections.sort(cpProjectInfoVos, new Comparator<CPProjectInfoVo>() {
                    @Override
                    public int compare(CPProjectInfoVo o1, CPProjectInfoVo o2) {
                        return Integer.parseInt(o2.getTotalAmount().toString()) - Integer.parseInt(o1.getTotalAmount().toString());
                    }
                });
                break;
            case CPProjectInfoVo.sort.sortIndex2:
                Collections.sort(cpProjectInfoVos, new Comparator<CPProjectInfoVo>() {
                    @Override
                    public int compare(CPProjectInfoVo o1, CPProjectInfoVo o2) {
                        return o2.getPayAmount().add(o2.getWillPayAmount()).intValue() - o1.getPayAmount().add(o1.getWillPayAmount()).intValue();
                    }
                });
                break;
            case CPProjectInfoVo.sort.sortIndex3:
                Collections.sort(cpProjectInfoVos, new Comparator<CPProjectInfoVo>() {
                    @Override
                    public int compare(CPProjectInfoVo o1, CPProjectInfoVo o2) {
                        return Integer.parseInt(o2.getOrdersRecord().toString()) - Integer.parseInt(o1.getOrdersRecord().toString());
                    }
                });
                break;
            default:
                Collections.sort(cpProjectInfoVos, new Comparator<CPProjectInfoVo>() {
                    @Override
                    public int compare(CPProjectInfoVo o1, CPProjectInfoVo o2) {
                        return Integer.parseInt(o2.getTotalAmount().toString()) - Integer.parseInt(o1.getTotalAmount().toString());
                    }
                });
        }

        if (split[1].equals(CPProjectInfoVo.sort.sortAsc)) {
            Collections.reverse(cpProjectInfoVos);
        }
        result.setTotalRecord(totalRecord);
        result.setItems(cpProjectInfoVos);
        return result;
    }

    @Override
    public Map<String, BigDecimal> getRate() {
        Map<String, BigDecimal> result = null;
        List<CfgExchangeRate> cfgExchangeRates = cfgExchangeRateMapper.selectAll();
        if (cfgExchangeRates.isEmpty()) {
            result = new HashMap<>();
            return result;
        }
        result = cfgExchangeRates.stream().filter(x -> OrdissuesEx.Currencies.RMB.equals(x.getdName())).collect(Collectors.toMap(x -> x.getsName(), x -> x.getRate()));
        return result;
    }

    // 获取订单总金额
    @Override
    public BigDecimal getOrdissueExAmountOfMoney(OrdissuesEx ordissuesEx, BigDecimal rate) {
        BigDecimal totalAmount = new BigDecimal(0);
        if (ordissuesEx.getPrePayMoney() != null) {
            totalAmount = totalAmount.add(ordissuesEx.getPrePayMoney());
        }
        if (ordissuesEx.getMidPayMoney() != null) {
            totalAmount = totalAmount.add(ordissuesEx.getMidPayMoney());
        }
        if (ordissuesEx.getLastPayMoney() != null) {
            totalAmount = totalAmount.add(ordissuesEx.getLastPayMoney());
        }
        if (rate != null) {
            totalAmount = totalAmount.multiply(rate);
        }

        return totalAmount;
    }

    // 获取订单待支付金额
    private BigDecimal getOrdissueWillPayAmount(OrdissuesEx ordissuesEx) {

        BigDecimal willPayAmount = new BigDecimal(0);

        if (ordissuesEx.getPrePayMoney() != null) {
            willPayAmount = willPayAmount.add(ordissuesEx.getPrePayMoney());
//            if (ordissuesEx.getRealPrePayMoney() != null) {
//                willPayAmount = willPayAmount.subtract(ordissuesEx.getRealPrePayMoney());
//            }
        }
        if (ordissuesEx.getMidPayMoney() != null) {
            willPayAmount = willPayAmount.add(ordissuesEx.getMidPayMoney());
//            if (ordissuesEx.getRealMidPayMoney() != null) {
//                willPayAmount = willPayAmount.subtract(ordissuesEx.getRealMidPayMoney());
//            }
        }
        if (ordissuesEx.getLastPayMoney() != null) {
            willPayAmount = willPayAmount.add(ordissuesEx.getLastPayMoney());
//            if (ordissuesEx.getRealLastPayMoney() != null) {
//                willPayAmount = willPayAmount.subtract(ordissuesEx.getRealLastPayMoney());
//            }
        }
        if (ordissuesEx.getRealPayMoney() != null) {
            willPayAmount = willPayAmount.subtract(ordissuesEx.getRealPayMoney());
        }
        return willPayAmount;
    }

    public int createProjectRelation(Long cpProjectId, Long itProjectId) {
        CpProjectRelation record = new CpProjectRelation();
        record.setCpProjectId(cpProjectId);
        record.setItProjectId(itProjectId);
        cpProjectRelationMapper.deleteByCpProjectId(cpProjectId);
        int count = cpProjectRelationMapper.insert(record);
        return count;
    }

    public int createProjectBudget(Long cpProjectId, BigDecimal amount, Integer year) {
        CpBudget record = cpBudgetMapper.selectCpBudget(cpProjectId, year);
        Date d = new Date();
        int count = 0;
        if (record == null) {
            record = new CpBudget();
            record.setCpProjectId(cpProjectId);
            record.setBudgetYear(year);
            record.setBudgetAmount(amount);
            record.setCreateTime(d);
            record.setUpdateTime(d);
            count = cpBudgetMapper.insert(record);
            if (record.getId() != null) {
                GameProject gameProject = gameProjectMapper.selectByPrimaryKey(Integer.parseInt(String.valueOf(cpProjectId)));
                if (!CpConstant.SEASUN_CP_PROJECT_CODE.equalsIgnoreCase(gameProject.getCode())) {
                    List<CpBudgetAppendInfoVo> list = new ArrayList<CpBudgetAppendInfoVo>();
                    CpBudgetAppendInfoVo cpBudgetAppendInfoVo = new CpBudgetAppendInfoVo();
                    cpBudgetAppendInfoVo.setCpBudgetId(record.getId());
                    cpBudgetAppendInfoVo.setAmount(amount);
                    cpBudgetAppendInfoVo.setReason(CpConstant.INIT_BUDGET_AMOUNT);
                    cpBudgetAppendInfoVo.setCreateTime(new Date());
                    cpBudgetAppendInfoVo.setType("0");
                    list.add(cpBudgetAppendInfoVo);
                    appendBudget(record.getId(), list);
                }
            }

        } else {
            record.setBudgetAmount(amount);
            record.setUpdateTime(d);
            count = cpBudgetMapper.updateCpBudget(record);
        }
        return count;
    }

    public int appendBudget(Long cpBudgetId, List<CpBudgetAppendInfoVo> list) {
        CpBudgetAppendInfo record = null;
        int count = 0;
        if (cpBudgetId != null) {
            cpBudgetAppendInfoMapper.deleteByBudgetId(cpBudgetId);
            if (list != null) {
                for (CpBudgetAppendInfoVo vo : list) {
                    record = new CpBudgetAppendInfo();
                    record.setCpBudgetId(vo.getCpBudgetId());
                    record.setAmount(vo.getAmount());
                    record.setReason(vo.getReason());
                    record.setCreateTime(vo.getCreateTime());
                    record.setType(vo.getType());
                    count = cpBudgetAppendInfoMapper.insert(record);
                }
            }
        }
        return count;
    }

    public ProjectBudgetVo getBudgetAmounts(Long cpProjectId, Integer year) {
        ProjectBudgetVo vo = new ProjectBudgetVo();
        if (cpProjectId != null && year != null) {
            CpBudget record = cpBudgetMapper.selectCpBudget(cpProjectId, year);
            if (record != null && record.getId() != null) {
                vo.setId(record.getId());
                vo.setCpBudgetId(record.getId());
                vo.setCpProjectId(record.getCpProjectId());
                vo.setBudgetYear(record.getBudgetYear());
                vo.setAmount(record.getBudgetAmount());
                List<CpBudgetAppendInfoVo> items = cpBudgetAppendInfoMapper.selectByCpBudgetId(record.getId());
                if (items != null) {
                    for (CpBudgetAppendInfoVo avo : items) {
                        avo.setCreateTimeStr(MyDateUtils.dateToString(avo.getCreateTime(), MyDateUtils.DATE_FORMAT_YYYY_MM_DD));
                    }
                }
                vo.setItems(items);
            }
        }
        return vo;
    }

    public BaseQueryResponseVo projects(Integer currentPage, Integer pageSize, Integer year, String keyWord) {
        BaseQueryResponseVo result = new BaseQueryResponseVo();
        Integer totalRecord = 0;
        List<CpProjectRelationVo> items = cpProjectRelationMapper.selectProjects(currentPage * pageSize, pageSize, year, keyWord);
        totalRecord = cpProjectRelationMapper.selectProjectsCount(year, keyWord);
        result.setItems(items);
        result.setTotalRecord(totalRecord);
        return result;
    }

    @Override
    public ProjectBudgetInfo getSeasunBudgetInfo(Integer year) {
        if (year == null) {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }

        ProjectBudgetInfo result = new ProjectBudgetInfo();
        int payNumber = 0;
        int willPayNumber = 0;
        BigDecimal payAmount = new BigDecimal(0);
        BigDecimal willPayAmount = new BigDecimal(0);
        // 西山居预算
        BigDecimal budgetCount = cpBudgetMapper.selectSeasunBudgetByYear(year);
        if (budgetCount == null) {
            budgetCount = new BigDecimal(0);
        }
        // 时间轴
        BasePeriodAppVo basePeriodAppVo = ordissuesMapper.selectPeriod();

        Map<String, BigDecimal> rateByMoneyNameMap = cfgExchangeRateMapper.selectAll().stream().filter(x -> OrdissuesEx.Currencies.RMB.equals(x.getdName())).collect(Collectors.toMap(x -> x.getsName(), x -> x.getRate()));

        if (basePeriodAppVo != null) {
            List<OrdissuesExDto> ordissuesExDtos = ordissuesExMapper.selectActiveGameProjectOrdissuesByYear(year);
            if (!ordissuesExDtos.isEmpty()) {
                for (OrdissuesExDto ordissuesExDto : ordissuesExDtos) {
                    BigDecimal rate = rateByMoneyNameMap.get(ordissuesExDto.getCurrencies());
                    if (rate == null) {
                        rate = new BigDecimal(1);
                    }
                    // 已支付
                    if (CPHelper.doneOrdissuesStatus.contains(ordissuesExDto.getStatus())) {
                        if (ordissuesExDto.getRealPayMoney() != null) {
                            if (ordissuesExDto.getModifyRealPayMoney().toString().equals("1")) {
                                payAmount = payAmount.add(ordissuesExDto.getRealPayMoney());
                            } else {
                                payAmount = payAmount.add(ordissuesExDto.getRealPayMoney().multiply(rate));
                            }
                        }
                        payNumber++;
                    } else {
                        // 不是已验收，算待支付
                        willPayNumber++;
                        willPayAmount = willPayAmount.add(getOrdissueExAmountOfMoney(ordissuesExDto, rate));
                    }
                }
            }
        } else {
            basePeriodAppVo = new BasePeriodAppVo();
            basePeriodAppVo.setStartMonth(year);
            basePeriodAppVo.setEndYear(year);
        }

        result.setStartYear(basePeriodAppVo.getStartYear());
        result.setEndYear(basePeriodAppVo.getEndYear());
        result.setTotalAmount(budgetCount);
        result.setPayAmount(payAmount);
        result.setPayNumber(payNumber);
        result.setWillPayAmount(willPayAmount);
        result.setWillPayNumber(willPayNumber);
        result.setSurplusAmount(result.getTotalAmount().subtract(result.getPayAmount()).subtract(result.getWillPayAmount()));
        return result;
    }

    @Override
    public ProjectOutcourceInfoVo getProjectInfo(Long id, Integer year, Integer quarter, Integer month, Integer currentPage, Integer pageSize) {
        if (currentPage == null) {
            currentPage = BaseQueryConditionVo.defaultPage;
        }
        if (pageSize == null) {
            pageSize = BaseQueryConditionVo.defaultPageSize;
        }
        if (year == null) {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }
        if (id == null) {
            throw new UserInvalidOperateException("没有该项目");
        }

        ProjectOutcourceInfoVo result = new ProjectOutcourceInfoVo();
        BasePeriodAppVo basePeriodAppVo = null;
        BigDecimal totalAmount = new BigDecimal(0);
        BigDecimal payAmount = new BigDecimal(0);
        BigDecimal willPayAmount = new BigDecimal(0);
        int inProgress = 0;
        int verify = 0;
        int totalRecord = 0;
        List<CPOrderDto> orderItems = new ArrayList<>();
        List<SimpleBudgetDto> budgetItems = new ArrayList<>();
        List<Integer> cPProjectIds = cpProjectRelationMapper.selectActiveCPProjectIdsByITProjectId(id);

        if (!cPProjectIds.isEmpty()) {
            budgetItems = cpBudgetAppendInfoMapper.selectSimpleBudgetDtoByProjectIdsAndYear(cPProjectIds, year);
            totalAmount = cpBudgetMapper.selectSumBudgetByProjectIdsAndYear(cPProjectIds, year);
            basePeriodAppVo = ordissuesMapper.selectPeriodByCPProjectIds(cPProjectIds);
            if (basePeriodAppVo != null) {
                Map<String, BigDecimal> rateByMoneyNameMap = cfgExchangeRateMapper.selectAll().stream().filter(x -> OrdissuesEx.Currencies.RMB.equals(x.getdName())).collect(Collectors.toMap(x -> x.getsName(), x -> x.getRate()));
                List<CPOrderDto> cPOrderDtos = ordissuesExMapper.selectByGameProjectIdsAndCreatedYear(cPProjectIds, year);
                Map<String, List<CPOrderDto>> cPOrderDtosByMonthMap = new HashMap<>();
                for (CPOrderDto ordissuesExDto : cPOrderDtos) {
                    String status = CPOrderDto.Status.done;
                    // 外币汇率
                    BigDecimal rate = null;
                    if (!OrdissuesEx.Currencies.RMB.equals(ordissuesExDto.getCurrencies())) {
                        rate = rateByMoneyNameMap.get(ordissuesExDto.getCurrencies());
                        if (rate == null) {
                            rate = new BigDecimal(1);
                        }
                        ordissuesExDto.setAmount(ordissuesExDto.getAmount().multiply(rate));
                    }
                    if (rate == null) {
                        rate = new BigDecimal(1);
                    }

                    // 放入map,做月份分页用
                    String orderMonth = ordissuesExDto.getCreateDateStr().split("-")[1];
                    if (cPOrderDtosByMonthMap.containsKey(orderMonth)) {
                        cPOrderDtosByMonthMap.get(orderMonth).add(ordissuesExDto);
                    } else {
                        List<CPOrderDto> datas = new ArrayList<>();
                        datas.add(ordissuesExDto);
                        cPOrderDtosByMonthMap.put(orderMonth, datas);
                    }

                    if (!CPHelper.confirmedOrdissuesStatus.contains(Integer.parseInt(ordissuesExDto.getStatus()))) {
                        if (CPHelper.dingOrdissuesStatus.contains(Integer.parseInt(ordissuesExDto.getStatus()))) {
                            status = CPOrderDto.Status.doing;
                        }
                        inProgress++;
                        willPayAmount = willPayAmount.add(ordissuesExDto.getAmount());
                    } else {
                        // 如果这个字段为1，付款用的人民币
                        if (ordissuesExDto.getModifyRealPayMoney() == 1) {
                            payAmount = payAmount.add(ordissuesExDto.getRealPayMoney());
                        } else {
                            payAmount = payAmount.add(ordissuesExDto.getRealPayMoney().multiply(rate));
                        }
                    }
                    ordissuesExDto.setStatus(status);

                }
                // 季度
                if (quarter != null) {
                    Integer startMonth = 1 + (quarter - 1) * 3;
                    for (int i = startMonth; i <= quarter * 3; i++) {
                        String key = i + "";
                        if (i < 10) {
                            key = "0" + key;
                        }
                        if (cPOrderDtosByMonthMap.containsKey(key)) {
                            orderItems.addAll(cPOrderDtosByMonthMap.get(key));
                        }
                    }
                    totalRecord = orderItems.size();
                    sortCPOrderStatus(orderItems);
                    orderItems = MyListUtils.paging(orderItems, currentPage, pageSize);

                } else {
                    // 月份
                    if (month != null) {
                        String key = month + "";
                        if (month < 10) {
                            key = "0" + key;
                        }
                        if (cPOrderDtosByMonthMap.containsKey(key)) {
                            List<CPOrderDto> cpOrderDtos = cPOrderDtosByMonthMap.get(key);
                            totalRecord = cpOrderDtos.size();
                            sortCPOrderStatus(cpOrderDtos);
                            orderItems = MyListUtils.paging(cpOrderDtos, currentPage, pageSize);
                        }
                    } else {
                        sortCPOrderStatus(cPOrderDtos);
                        totalRecord = cPOrderDtos.size();
                        orderItems = MyListUtils.paging(cPOrderDtos, currentPage, pageSize);
                    }
                }
            } else {
                basePeriodAppVo = new BasePeriodAppVo();
                basePeriodAppVo.setStartYear(year);
                basePeriodAppVo.setStartMonth(1);
                basePeriodAppVo.setEndYear(year);
                basePeriodAppVo.setEndMonth(12);
            }
        }

        result.setBaseInfo(basePeriodAppVo);
        result.setTotalAmount(totalAmount);
        result.setPayAmount(payAmount);
        result.setWillPayAmount(willPayAmount);
        result.setSurplusAmount(totalAmount.subtract(payAmount).subtract(willPayAmount));
        if (totalAmount.compareTo(new BigDecimal(0)) != 0) {
            result.setPercent((payAmount.add(willPayAmount)).divide(totalAmount, BigDecimal.ROUND_HALF_UP).doubleValue() * 100);
        } else {
            result.setPercent(0d);
        }
        result.setInProgress(inProgress);
        result.setVerify(verify);
        result.setOrderItems(orderItems);
        result.setBudgetItems(budgetItems);
        result.setTotalRecord(totalRecord);
        return result;
    }

    /*
    * 排序，订单状态为中文，进行中状态在前,已验收状态在后。
    * */
    private void sortCPOrderStatus(List<CPOrderDto> orderDtos) {

        Collections.sort(orderDtos, new Comparator<CPOrderDto>() {
            @Override
            public int compare(CPOrderDto o1, CPOrderDto o2) {
                if (CPOrderDto.Status.done.equals(o1.getStatus())) {
                    if (CPOrderDto.Status.done.equals(o2.getStatus())) {
                        return 0;
                    }
                    return 1;
                }
                if (CPOrderDto.Status.doing.equals(o1.getStatus())) {
                    if (CPOrderDto.Status.doing.equals(o2.getStatus())) {
                        return 0;
                    }
                    return -1;
                }
                return 0;
            }
        });
    }

    public int updateCpBudgetAmount(Long cpBudgetId, BigDecimal amount) {
        int count = 0;
        CpBudget cpBudget = new CpBudget();
        cpBudget.setId(cpBudgetId);
        cpBudget.setBudgetAmount(amount);
        cpBudget.setUpdateTime(new Date());
        count = cpBudgetMapper.updateCpBudgetAmount(cpBudget);
        return count;
    }

    @Override
    public List<ExchangeRateDto> getExchangeRates() {
        return cfgExchangeRateMapper.selectAll().stream().map(v -> v.toDto()).collect(Collectors.toList());
    }

    @Override
    public Long addExchangeRate(ExchangeRateDto rate) {
        CfgExchangeRate obj = rate.toStandardRateObj();
        CfgExchangeRate result = cfgExchangeRateMapper.selectBySourceCode(obj.getsCode());
        if (result != null) {
            return 0L;
        }
        cfgExchangeRateMapper.insertSelective(obj);
        return obj.getId();
    }

    @Override
    public int updateExchangeRate(Long id, BigDecimal rate) {
        return cfgExchangeRateMapper.updateRateByPrimaryKey(id, rate);
    }

    @Override
    public int deleteExchangeRate(Long id) {
        return cfgExchangeRateMapper.deleteByPrimaryKey(id);
    }

    /**
     * 折算为外币
     * @param foreignCurrency
     * @param value
     * @return
     */
    private BigDecimal getConversionAmoun(BigDecimal foreignCurrency ,BigDecimal value){
        BigDecimal  total = null;
        if(foreignCurrency != null && BigDecimal.ZERO.compareTo(foreignCurrency) != 0 && value != null){
            total = value.divide(foreignCurrency, 2, BigDecimal.ROUND_HALF_EVEN);
        }
        return total;
    }
    /**
     * 查询项目剩余金额
     * @param projectName
     * @param date
     * @return
     */
    public Map<String,BigDecimal> getProjectInfo(String projectName,String date) {
        Map<String,BigDecimal>  surplusAmountMap = new HashMap<String,BigDecimal>();
        surplusAmountMap.put("人民币",BigDecimal.ZERO);
        surplusAmountMap.put("美元",BigDecimal.ZERO);
        surplusAmountMap.put("欧元",BigDecimal.ZERO);
        surplusAmountMap.put("英镑",BigDecimal.ZERO);
        surplusAmountMap.put("日元",BigDecimal.ZERO);
        if(StringUtils.isNotEmpty(projectName)) {
            BigDecimal totalAmount =  BigDecimal.ZERO;
            BigDecimal payAmount =  BigDecimal.ZERO;
            BigDecimal willPayAmount =  BigDecimal.ZERO;
            BigDecimal surplusAmount = BigDecimal.ZERO;
            Date current = new Date();
            if (StringUtils.isNotEmpty(date)) {
                current = MyDateUtils.strToDate(date, MyDateUtils.DATE_FORMAT_YYYY_MM_DD);
            }
            Map<String, BigDecimal> rateByMoneyNameMap = cfgExchangeRateMapper.selectAll().stream().filter(x -> OrdissuesEx.Currencies.RMB.equals(x.getdName())).collect(Collectors.toMap(x -> x.getsName(), x -> x.getRate()));
            Integer year = Integer.parseInt(MyDateUtils.dateToString(current, "yyyy"));
            List<Integer> cPProjectIds = cpProjectRelationMapper.selectItProjectIdsByCPProjectName(projectName);
            if (!cPProjectIds.isEmpty()) {
                totalAmount = cpBudgetMapper.selectSumBudgetByProjectIdsAndYear(cPProjectIds, year);
                List<CPOrderDto> cPOrderDtos = ordissuesExMapper.selectByGameProjectIdsAndCreatedYear(cPProjectIds, year);
                for (CPOrderDto ordissuesExDto : cPOrderDtos) {
                    // 外币汇率
                    BigDecimal rate = null;
                    if (!OrdissuesEx.Currencies.RMB.equals(ordissuesExDto.getCurrencies())) {
                        rate = rateByMoneyNameMap.get(ordissuesExDto.getCurrencies());
                        if (rate == null) {
                            rate = new BigDecimal(1);
                        }
                        ordissuesExDto.setAmount(ordissuesExDto.getAmount().multiply(rate));
                    }
                    if (rate == null) {
                        rate = new BigDecimal(1);
                    }
                    if (!CPHelper.confirmedOrdissuesStatus.contains(Integer.parseInt(ordissuesExDto.getStatus()))) {
                        willPayAmount = willPayAmount.add(ordissuesExDto.getAmount());
                    } else {
                        // 如果这个字段为1，付款用的人民币
                        if (ordissuesExDto.getModifyRealPayMoney() == 1) {
                            payAmount = payAmount.add(ordissuesExDto.getRealPayMoney());
                        } else {
                            payAmount = payAmount.add(ordissuesExDto.getRealPayMoney().multiply(rate));
                        }
                    }
                }
            }
            BigDecimal USD = rateByMoneyNameMap.get("美元");
            BigDecimal EUR = rateByMoneyNameMap.get("欧元");
            BigDecimal GBP = rateByMoneyNameMap.get("英镑");
            BigDecimal JPY = rateByMoneyNameMap.get("日元");
            surplusAmount = totalAmount.subtract(payAmount).subtract(willPayAmount);
            surplusAmountMap.put("人民币",surplusAmount);
            surplusAmountMap.put("美元",getConversionAmoun(USD,surplusAmount));
            surplusAmountMap.put("欧元",getConversionAmoun(EUR,surplusAmount));
            surplusAmountMap.put("英镑",getConversionAmoun(GBP,surplusAmount));
            surplusAmountMap.put("日元",getConversionAmoun(JPY,surplusAmount));
        }
        return surplusAmountMap;
    }
}
