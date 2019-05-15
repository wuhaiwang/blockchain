package com.seasun.management.service.app;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.constant.MonthType;
import com.seasun.management.dto.*;
import com.seasun.management.dto.cp.OrdissuesExDto;
import com.seasun.management.exception.ParamException;
import com.seasun.management.helper.PermissionHelper;
import com.seasun.management.helper.ReportHelper;
import com.seasun.management.mapper.*;
import com.seasun.management.mapper.cp.OrdissuesExMapper;
import com.seasun.management.model.*;
import com.seasun.management.service.CpInfoService;
import com.seasun.management.service.WorkGroupService;
import com.seasun.management.service.impl.DepartmentServiceImpl;
import com.seasun.management.util.MyCellUtils;
import com.seasun.management.util.MyDateUtils;
import com.seasun.management.util.MyListUtils;
import com.seasun.management.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.seasun.management.helper.ReportHelper.PlatInShareProjectMap;

@Service
public class ProjectReportServiceImpl implements ProjectReportService {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    FnProjectStatDataMapper fnProjectStatDataMapper;

    @Autowired
    FnProjectSchedDataMapper fnProjectSchedDataMapper;

    @Autowired
    FnShareDataMapper fnShareDataMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    WorkGroupService workGroupService;

    @Autowired
    RUserProjectPermMapper rUserProjectPermMapper;

    @Autowired
    FnPlatShareConfigMapper fnPlatShareConfigMapper;

    @Autowired
    FnSumShareConfigMapper fnSumShareConfigMapper;

    @Autowired
    FnShareTemplateMapper fnShareTemplateMapper;

    @Autowired
    HrReportService hrReportService;

    @Autowired
    CfgPlatAttrMapper cfgPlatAttrMapper;

    @Autowired
    private PmFinanceReportMapper pmFinanceReportMapper;

    @Autowired
    private PmAttachmentMapper pmAttachmentMapper;

    @Autowired
    private PmPlanDetailMapper pmPlanDetailMapper;

    @Autowired
    private CpProjectRelationMapper cpProjectRelationMapper;

    @Autowired
    private OrdissuesExMapper ordissuesExMapper;

    @Autowired
    private CpInfoService cpInfoService;

    @Override
    public List<ProjectAppVo> getAppProjects(String status) {
        if (!Project.Status.operating.equals(status) && !Project.Status.developing.equals(status)) {
            logger.info("getAppProjects failed...");
            throw new ParamException("Status must be 运营|在研");
        }

        PermissionProjectDto permissionProjectDto = PermissionHelper.getPermissionByProject();

        List<ProjectAppVo> retProjectAppVos = new ArrayList<>();

        Calendar today = Calendar.getInstance();
        FnProjectStatData fnProjectStatData = fnProjectStatDataMapper.selectByNew();
        List<IdValueDto> projectDetailCounts = pmPlanDetailMapper.selectPublishedProjectDeatilCount();
        Map<Long, Float> projectDetailCountByProjectIdMap = projectDetailCounts.stream().collect(Collectors.toMap(x -> x.getId(), x -> x.getValue()));
        if (null != fnProjectStatData) {
            today.set(fnProjectStatData.getYear(), fnProjectStatData.getMonth() - 1, 1);
        }
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH) + 1;
        today.add(Calendar.MONTH, -1);
        int lastYear = today.get(Calendar.YEAR);
        int lastMonth = today.get(Calendar.MONTH) + 1;
        List<ProjectAppVo> projectAppVos = projectMapper.selectAllAppProject(status, year, month, lastYear, lastMonth);
        Map<Integer, List<OrdissuesExDto>> ordersByCPProjectIdMap = ordissuesExMapper.selectActiveGameProjectOrdissuesByYear(year).stream().collect(Collectors.groupingBy(x -> x.getcPProjectId()));
        Map<String, BigDecimal> rateByMoneyNameMap = cpInfoService.getRate();
        List<CfgPlatAttr> cfgRealTimeProjectList = cfgPlatAttrMapper.selectAllRealTimeFlag();
        BigDecimal zero = new BigDecimal(0);
        for (ProjectAppVo projectAppVo : projectAppVos) {
            if (!permissionProjectDto.isAllPerm() && !permissionProjectDto.getPermProjectIds().contains(projectAppVo.getId())) {
                continue;
            }

            if (null != projectAppVo.getPlatShareNumber()) {
                projectAppVo.setPlatShareNumber(ReportHelper.formatNumberByScale(projectAppVo.getPlatShareNumber(), 1));
            } else {
                projectAppVo.setPlatShareNumber(0F);
            }
            if (null != projectAppVo.getCost()) {
                projectAppVo.setCost(ReportHelper.formatNumberByScale(projectAppVo.getCost(), 1));
            } else {
                projectAppVo.setCost(0F);
            }
            if (null != projectAppVo.getLastMonthCost()) {
                projectAppVo.setLastMonthCost(ReportHelper.formatNumberByScale(projectAppVo.getLastMonthCost(), 1));
            } else {
                projectAppVo.setLastMonthCost(0F);
            }
            switch (status) {
                case Project.Status.operating:
                    if (null != projectAppVo.getIncome()) {
                        projectAppVo.setIncome(ReportHelper.formatNumberByScale(projectAppVo.getIncome(), 1));
                    } else {
                        projectAppVo.setIncome(0F);
                    }
                    if (null != projectAppVo.getLastMonthIncome()) {
                        projectAppVo.setLastMonthIncome(ReportHelper.formatNumberByScale(projectAppVo.getLastMonthIncome(), 1));
                    } else {
                        projectAppVo.setLastMonthIncome(0F);
                    }
                    break;
                case Project.Status.developing:
                    if (null != projectAppVo.getTotalCost()) {
                        projectAppVo.setTotalCost(ReportHelper.formatNumberByScale(projectAppVo.getTotalCost(), 1));
                    } else {
                        projectAppVo.setTotalCost(0F);
                    }
                    break;
                default:
            }

            // 判断是否有外包tab
            // 已用金额
            BigDecimal payAmount = new BigDecimal(0);
            if (projectAppVo.getcPProjectIdsStr() != null && !projectAppVo.getcPProjectIdsStr().isEmpty()) {
                projectAppVo.setHasCPFlag(true);
                for (String cpProjectId : projectAppVo.getcPProjectIdsStr().split(",")) {
                    List<OrdissuesExDto> ordissuesExDtos = ordersByCPProjectIdMap.get(Integer.parseInt(cpProjectId));
                    if (ordissuesExDtos != null) {
                        for (OrdissuesExDto ordissuesExDto : ordissuesExDtos) {
                            payAmount = payAmount.add(cpInfoService.getOrdissueExAmountOfMoney(ordissuesExDto, rateByMoneyNameMap.get(ordissuesExDto.getCurrencies())));
                        }
                    }
                }
            } else {
                projectAppVo.setHasCPFlag(false);
            }
            // 预算警戒比例
            if (projectAppVo.getBudgetAmount().compareTo(zero) == 0) {
                projectAppVo.setBudgetPercent(0f);
            } else {
                projectAppVo.setBudgetPercent(MyCellUtils.formatFloatNumber(payAmount.divide(projectAppVo.getBudgetAmount(), BigDecimal.ROUND_HALF_UP).floatValue() * 100f, 2));
            }

            // 判断是否有stage阶段的tab
            if (projectDetailCountByProjectIdMap.containsKey(projectAppVo.getId()) && projectDetailCountByProjectIdMap.get(projectAppVo.getId()) > 0F) {
                projectAppVo.setHasStage(true);
            } else {
                projectAppVo.setHasStage(false);
            }

            // 判断是否有实时数据
            if (cfgRealTimeProjectList.stream().anyMatch(p -> p.getPlatId().equals(projectAppVo.getId()))) {
                projectAppVo.setHasRealTimeFlag(true);
            }

            retProjectAppVos.add(projectAppVo);
        }
        retProjectAppVos.sort((x, y) -> y.getCity().compareTo(x.getCity()));

        // 暂时封存的放在最后面
        List<ProjectAppVo> result = new ArrayList<>();
        List<ProjectAppVo> zsfcProjects = new ArrayList<>();
        List<ProjectAppVo> notZsfcProjects = new ArrayList<>();
        for (ProjectAppVo temp : retProjectAppVos) {
            if (Project.Type.zsfc.equals(temp.getType())) {
                zsfcProjects.add(temp);
            } else {
                notZsfcProjects.add(temp);
            }
        }
        result.addAll(notZsfcProjects);
        result.addAll(zsfcProjects);
        return result;
    }

    @Override
    public FnProjectInfoAppVo getAppFnProjectInfo(Long projectId, Integer year, Integer month) {
        if (null == projectId || null == year || null == month) {
            logger.info("getAppFnProjectInfo failed...");
            throw new ParamException("projectId or year or month can not be null");
        }

        Project project = projectMapper.selectByPrimaryKey(projectId);
        if (null == project) {
            logger.info("getAppFnProjectInfo failed...");
            throw new ParamException("project not found");
        }

        ProjectAppVo projectAppVo = projectMapper.selectAppProject(projectId, year, month);
        List<FnProjectStatData> fnProjectStatDatas = fnProjectStatDataMapper.selectFirstAndLastByProjectId(projectId);
        if (null == fnProjectStatDatas || fnProjectStatDatas.isEmpty()) {
            logger.info("getAppFnProjectInfo failed...");
            throw new ParamException("project have not stat data");
        }

        FnProjectInfoAppVo fnProjectInfoAppVo = new FnProjectInfoAppVo();

        //制作人分析功能
        FnProjectInfoAppVo.ManagerAnalysis managerAnalysis = new FnProjectInfoAppVo.ManagerAnalysis();
        managerAnalysis.setAttachments(new ArrayList<>());
        PmFinanceReport condition = new PmFinanceReport();
        YearMonthDto yearMonthDto = MyDateUtils.yearMonthCalculation(year, month, 1);
        condition.setYear(yearMonthDto.getYear());
        condition.setMonth(yearMonthDto.getMonth());
        condition.setProjectId(projectId);
        condition.setStatus(PmFinanceReport.Status.PUBLISHED);
        List<PmFinanceReport> financeReports = pmFinanceReportMapper.selectByCond(condition);
        Boolean analysisPublishedFlag = false;
        if (!financeReports.isEmpty()) {
            analysisPublishedFlag = true;
            PmFinanceReport report = financeReports.get(0);
            managerAnalysis.setCost(report.getCostFloatReason());
            managerAnalysis.setIncome(report.getIncomeFloatReason());
            managerAnalysis.setProfit(report.getProfitFloatReason());
            managerAnalysis.getAttachments().addAll(pmAttachmentMapper.selectByPmFinanceReportId(report.getId()));
        }
        fnProjectInfoAppVo.setManagerAnalysis(managerAnalysis);
        fnProjectInfoAppVo.setAnalysisPublishedFlag(analysisPublishedFlag);

        JSONObject requestParam = new JSONObject();
        requestParam.put("id", projectId);
        requestParam.put("name", project.getName());
        requestParam.put("year", year);
        requestParam.put("month", month);
        fnProjectInfoAppVo.setRequestParam(requestParam);

        FnProjectInfoAppVo.BaseInfo baseInfo = new FnProjectInfoAppVo.BaseInfo();
        // 设置最早年月和最晚年月
        Integer startYear, startMonth, endYear, endMonth;
        startYear = fnProjectStatDatas.get(0).getYear();
        startMonth = fnProjectStatDatas.get(0).getMonth();
        if (fnProjectStatDatas.size() == 1) {
            endYear = startYear;
            endMonth = startMonth;
        } else {
            endYear = fnProjectStatDatas.get(1).getYear();
            endMonth = fnProjectStatDatas.get(1).getMonth();
        }
        baseInfo.setStartYear(startYear);
        baseInfo.setStartMonth(startMonth);
        baseInfo.setEndYear(endYear);
        baseInfo.setEndMonth(endMonth);
        if (Project.Status.developing.equals(project.getStatus())) {
            if (null != projectAppVo.getTotalCost()) {
                baseInfo.setTotalCost(ReportHelper.formatNumberByScale(projectAppVo.getTotalCost(), 1));
            } else {
                baseInfo.setTotalCost(0F);
            }
        } else {
            if (null != projectAppVo.getExpectIncome()) {
                baseInfo.setAnnualIncomeBudget(ReportHelper.formatNumberByScale(projectAppVo.getExpectIncome(), 1));
            } else {
                baseInfo.setAnnualIncomeBudget(0F);
            }
        }
        fnProjectInfoAppVo.setBaseInfo(baseInfo);

        if (null == project.getStatus() || Project.Status.operating.equals(project.getStatus()) || "西山居世游".equals(project.getName())) {
            fnProjectInfoAppVo.setProfitInfo(new ArrayList<>());
            fnProjectInfoAppVo.setPerCapitaProfitInfo(new ArrayList<>());
            fnProjectInfoAppVo.setIncomeInfo(new ArrayList<>());
        }
        fnProjectInfoAppVo.setCostInfo(new ArrayList<>());
        fnProjectInfoAppVo.setDirectCostDetail(new ArrayList<>());
        fnProjectInfoAppVo.setIndirectCostDetail(new ArrayList<>());

        FnProjectSchedData fnProjectSchedData = fnProjectSchedDataMapper.selectByProjectIdAndYearAndMonth(projectId, year, month);
        if (null != fnProjectSchedData) {
            if (Project.Status.developing.equals(project.getStatus())) {
                if (null != fnProjectSchedData.getTotalBudget()) {
                    fnProjectInfoAppVo.getBaseInfo().setTotalBudget(ReportHelper.formatNumberByScale(fnProjectSchedData.getTotalBudget(), 1));
                } else {
                    fnProjectInfoAppVo.getBaseInfo().setTotalBudget(0F);
                }
            }
        }

        List<FnProjectStatDataDto> fnProjectStatDataDtos = fnProjectStatDataMapper.selectLeftJoinFnStatByYearAndMonthAndProjectId(year, month, projectId);

        // 初始化需要的费用项；区分直接费用，直接费用子集，间接费用
        Map<Long, FnProjectInfoAppVo.CostDetail> costDetailMap = new HashMap<>();
        List<FnProjectStatDataDto> directionCostFnProjectStatDataDtos = new ArrayList<>();
        List<FnProjectStatDataDto> indirectionCostFnProjectStatDataDtos = new ArrayList<>();
        List<FnProjectStatDataDto> directionCostChildrenFnProjectStatDataDtos = new ArrayList<>();
        for (FnProjectStatDataDto fnProjectStatDataDto : fnProjectStatDataDtos) {
            Long fnStatId = fnProjectStatDataDto.getFnStatId();

            String fnStatName = null;
            if (fnProjectStatDataDto.getName() != null) {
                fnStatName = fnProjectStatDataDto.getName().trim();
            }

            if (costDetailMap.containsKey(fnStatId)) {
                continue;
            }

            FnProjectInfoAppVo.CostDetail costDetail = new FnProjectInfoAppVo.CostDetail();
            costDetail.setTwoMonthAgoCost(0F);
            costDetail.setLastMonthCost(0F);
            costDetail.setCurrentMonthCost(0F);
            costDetail.setDetailFlag(false);

            if ("产品-运营成本".equals(fnStatName) || "产品-销售费用".equals(fnStatName)
                    || "研发费".equals(fnStatName) // 2017年大表中叫“研发费”
                    || "部门成本".equals(fnStatName)) // 2018年大表中叫“部门成本”
            {
                costDetail.setChildren(new ArrayList<>());
                costDetailMap.put(fnStatId, costDetail);
                directionCostFnProjectStatDataDtos.add(fnProjectStatDataDto);
            } else if ("产品-运营成本".equals(fnProjectStatDataDto.getParentName()) || "产品-销售费用".equals(fnProjectStatDataDto.getParentName())
                    || "研发费".equals(fnProjectStatDataDto.getParentName()) // 2017年大表中叫“研发费”
                    || "部门成本".equals(fnProjectStatDataDto.getParentName())) // 2018年大表中叫“部门成本”
            {
                costDetailMap.put(fnStatId, costDetail);
                directionCostChildrenFnProjectStatDataDtos.add(fnProjectStatDataDto);
            } else if ("分摊费用".equals(fnProjectStatDataDto.getParentName()) || "外包费用".equals(fnStatName)
                    || "大连平台管理费用分摊".equals(fnStatName) || "大连平台研发费用分摊".equals(fnStatName)) { // 补充大连项目的分摊费用： modify by xionghaitao 2017年11月25日
                costDetailMap.put(fnStatId, costDetail);
                indirectionCostFnProjectStatDataDtos.add(fnProjectStatDataDto);
            }
        }

        int currentYear;
        int currentMonth;
        Calendar currentDate = Calendar.getInstance();
        currentDate.set(year, month - 1, 1);
        //前两月
        currentDate.add(Calendar.MONTH, -2);
        currentYear = currentDate.get(Calendar.YEAR);
        currentMonth = currentDate.get(Calendar.MONTH) + 1;
        setFnProjectInfoAppVo(fnProjectInfoAppVo, fnProjectStatDataDtos, costDetailMap, currentYear, currentMonth, MonthType.twoMonthAgo, projectId);
        //上月
        currentDate.add(Calendar.MONTH, 1);
        currentYear = currentDate.get(Calendar.YEAR);
        currentMonth = currentDate.get(Calendar.MONTH) + 1;
        setFnProjectInfoAppVo(fnProjectInfoAppVo, fnProjectStatDataDtos, costDetailMap, currentYear, currentMonth, MonthType.last, projectId);
        //当月
        setFnProjectInfoAppVo(fnProjectInfoAppVo, fnProjectStatDataDtos, costDetailMap, year, month, MonthType.current, projectId);


        //直接费用明细
        for (FnProjectStatDataDto fnProjectStatDataDto : directionCostFnProjectStatDataDtos) {
            FnProjectInfoAppVo.CostDetail costDetail = costDetailMap.get(fnProjectStatDataDto.getFnStatId());
            // 去除3个月为0的项
            if (costDetail.getTwoMonthAgoCost() == 0 && costDetail.getLastMonthCost() == 0 && costDetail.getCurrentMonthCost() == 0) {
                continue;
            }
            for (FnProjectStatDataDto childrenFnProjectStatDataDto : directionCostChildrenFnProjectStatDataDtos) {
                if (childrenFnProjectStatDataDto.getParentId().equals(fnProjectStatDataDto.getFnStatId())) {
                    FnProjectInfoAppVo.CostDetail childCostDetail = costDetailMap.get(childrenFnProjectStatDataDto.getFnStatId());
                    // 去除3个月为0的项
                    if (childCostDetail.getTwoMonthAgoCost() == 0 && childCostDetail.getLastMonthCost() == 0 && childCostDetail.getCurrentMonthCost() == 0) {
                        continue;
                    }
                    costDetail.getChildren().add(childCostDetail);
                }
            }
            fnProjectInfoAppVo.getDirectCostDetail().add(costDetail);
        }
        //间接费用明细
        for (FnProjectStatDataDto fnProjectStatDataDto : indirectionCostFnProjectStatDataDtos) {
            FnProjectInfoAppVo.CostDetail costDetail = costDetailMap.get(fnProjectStatDataDto.getFnStatId());
            // 去除3个月为0的项
            if (costDetail.getTwoMonthAgoCost() == 0 && costDetail.getLastMonthCost() == 0 && costDetail.getCurrentMonthCost() == 0) {
                continue;
            }
            fnProjectInfoAppVo.getIndirectCostDetail().add(costDetail);
        }
        fnProjectInfoAppVo.getDirectCostDetail().sort((x, y) -> y.getCurrentMonthCost().compareTo(x.getCurrentMonthCost()));
        fnProjectInfoAppVo.getIndirectCostDetail().sort((x, y) -> y.getCurrentMonthCost().compareTo(x.getCurrentMonthCost()));

        // 项目明细
        List<FnProjectInfoDto> fnProjectInfoDtos = projectMapper.selectAllWithFnInfo(year, month);
        if (null != fnProjectInfoDtos && !fnProjectInfoDtos.isEmpty()) {
            Map<Long, FnProjectInfoDto> fnProjectInfoDtoMap = fnProjectInfoDtos.stream().collect(Collectors.toMap(p -> p.getId(), p -> p));
            Map<Long, List<FnProjectInfoDto>> subProjectsMap = fnProjectInfoDtos.stream().collect(Collectors.groupingBy(p -> p.getParentFnSumId()));
            List<FnProjectInfoDto> subProjects = getFnProjectsBySum(projectId, subProjectsMap);
            if (!subProjects.isEmpty()) {
                fnProjectInfoAppVo.setIncomeProjectDetail(new ArrayList<>());
                fnProjectInfoAppVo.setCostProjectDetial(new ArrayList<>());
                fnProjectInfoAppVo.setProfitProjectDetail(new ArrayList<>());
                for (FnProjectInfoDto subProject : subProjects) {
                    Float addIncome = 0F, addLastMonthIncome = 0F, addTwoMonthAgoIncome = 0F;
                    Float addCost = 0F, addLastMonthCost = 0F, addTwoMonthAgoCost = 0F;
                    Float addProfit = 0F, addLastMonthProfit = 0F, addTwoMonthAgoProfit = 0F;
                    // 处理需要加总的项目
                    if (ReportHelper.AddToProjectMap.containsKey(subProject.getId())) {
                        Long addProjectId = ReportHelper.AddToProjectMap.get(subProject.getId());
                        if (fnProjectInfoDtoMap.containsKey(addProjectId)) {
                            FnProjectInfoDto addProject = fnProjectInfoDtoMap.get(addProjectId);
                            addIncome = addProject.getIncome();
                            addLastMonthIncome = addProject.getLastMonthIncome();
                            addTwoMonthAgoIncome = addProject.getTwoMonthAgoIncome();
                            addCost = addProject.getCost();
                            addLastMonthCost = addProject.getLastMonthCost();
                            addTwoMonthAgoCost = addProject.getTwoMonthAgoCost();
                            addProfit = addProject.getProfit();
                            addLastMonthProfit = addProject.getLastMonthProfit();
                            addTwoMonthAgoProfit = addProject.getTwoMonthAgoProfit();
                        }
                    }
                    if (null != subProject.getIncome() && subProject.getIncome() > 0) {
                        FnProjectInfoAppVo.FnProjectInfo fnProjectInfo = new FnProjectInfoAppVo.FnProjectInfo();
                        fnProjectInfo.setName(subProject.getName());
                        fnProjectInfo.setCurrentMonthValue(ReportHelper.formatNumberByScale(subProject.getIncome() + addIncome, 1));
                        fnProjectInfo.setLastMonthValue(ReportHelper.formatNumberByScale(subProject.getLastMonthIncome() + addLastMonthIncome, 1));
                        fnProjectInfo.setTwoMonthAgoValue(ReportHelper.formatNumberByScale(subProject.getTwoMonthAgoIncome() + addTwoMonthAgoIncome, 1));
                        fnProjectInfoAppVo.getIncomeProjectDetail().add(fnProjectInfo);
                    }
                    if (null != subProject.getCost() && subProject.getCost() > 0) {
                        FnProjectInfoAppVo.FnProjectInfo fnProjectInfo = new FnProjectInfoAppVo.FnProjectInfo();
                        fnProjectInfo.setName(subProject.getName());
                        fnProjectInfo.setCurrentMonthValue(ReportHelper.formatNumberByScale(subProject.getCost() + addCost, 1));
                        fnProjectInfo.setLastMonthValue(ReportHelper.formatNumberByScale(subProject.getLastMonthCost() + addLastMonthCost, 1));
                        fnProjectInfo.setTwoMonthAgoValue(ReportHelper.formatNumberByScale(subProject.getTwoMonthAgoCost() + addTwoMonthAgoCost, 1));
                        fnProjectInfoAppVo.getCostProjectDetial().add(fnProjectInfo);
                    }
                    if (null != subProject.getProfit() && subProject.getProfit() > 0) {
                        FnProjectInfoAppVo.FnProjectInfo fnProjectInfo = new FnProjectInfoAppVo.FnProjectInfo();
                        fnProjectInfo.setName(subProject.getName());
                        fnProjectInfo.setCurrentMonthValue(ReportHelper.formatNumberByScale(subProject.getProfit() + addProfit, 1));
                        fnProjectInfo.setLastMonthValue(ReportHelper.formatNumberByScale(subProject.getLastMonthProfit() + addLastMonthProfit, 1));
                        fnProjectInfo.setTwoMonthAgoValue(ReportHelper.formatNumberByScale(subProject.getTwoMonthAgoProfit() + addTwoMonthAgoProfit, 1));
                        fnProjectInfoAppVo.getProfitProjectDetail().add(fnProjectInfo);
                    }
                }
                fnProjectInfoAppVo.getIncomeProjectDetail().sort((x, y) -> y.getCurrentMonthValue().compareTo(x.getCurrentMonthValue()));
                fnProjectInfoAppVo.getCostProjectDetial().sort((x, y) -> y.getCurrentMonthValue().compareTo(x.getCurrentMonthValue()));
                fnProjectInfoAppVo.getProfitProjectDetail().sort((x, y) -> y.getCurrentMonthValue().compareTo(x.getCurrentMonthValue()));
            }
        }
        Integer previousMonth = month - 1;
        Integer previousYear = year;
        if (month == 1) {
            previousMonth = 12;
            previousYear = year - 1;
        }
        if (fnProjectInfoAppVo.getIncomeInfo() != null) {
            for (FnProjectInfoAppVo.IncomeInfo incomeInfo : fnProjectInfoAppVo.getIncomeInfo()) {
                if (year.equals(incomeInfo.getYear()) && month.equals(incomeInfo.getMonth())) {
                    for (FnProjectInfoAppVo.IncomeInfo info : fnProjectInfoAppVo.getIncomeInfo()) {
                        if (previousYear.equals(info.getYear()) && previousMonth.equals(info.getMonth()) && info.getIncome() != 0) {
                            FnProjectInfoAppVo.BaseInfo baseInfo1 = fnProjectInfoAppVo.getBaseInfo();
                            baseInfo1.setIncomeMOM((incomeInfo.getIncome() - info.getIncome()) / info.getIncome() * 100);
                            fnProjectInfoAppVo.setBaseInfo(baseInfo1);
                        }
                    }
                }
            }
        }
        if (fnProjectInfoAppVo.getProfitInfo() != null) {
            for (FnProjectInfoAppVo.ProfitInfo profitInfo : fnProjectInfoAppVo.getProfitInfo()) {
                if (year.equals(profitInfo.getYear()) && month.equals(profitInfo.getMonth())) {
                    for (FnProjectInfoAppVo.ProfitInfo info : fnProjectInfoAppVo.getProfitInfo()) {
                        if (previousYear.equals(info.getYear()) && previousMonth.equals(info.getMonth()) && info.getProfit() != 0) {
                            FnProjectInfoAppVo.BaseInfo baseInfo1 = fnProjectInfoAppVo.getBaseInfo();
                            baseInfo1.setProfitMOM((profitInfo.getProfit() - info.getProfit()) / info.getProfit() * 100);
                            fnProjectInfoAppVo.setBaseInfo(baseInfo1);
                        }
                    }
                }
            }
        }
        if (fnProjectInfoAppVo.getCostInfo() != null) {
            for (FnProjectInfoAppVo.CostInfo costInfo : fnProjectInfoAppVo.getCostInfo()) {
                if (year.equals(costInfo.getYear()) && month.equals(costInfo.getMonth())) {
                    for (FnProjectInfoAppVo.CostInfo info : fnProjectInfoAppVo.getCostInfo()) {
                        if (previousYear.equals(info.getYear()) && previousMonth.equals(info.getMonth()) && (info.getDirectCost() + info.getIndirectCost()) != 0) {
                            FnProjectInfoAppVo.BaseInfo baseInfo1 = fnProjectInfoAppVo.getBaseInfo();
                            baseInfo1.setCostMOM(((costInfo.getDirectCost() + costInfo.getIndirectCost())
                                    - (info.getDirectCost() + info.getIndirectCost()))
                                    / (info.getDirectCost() + info.getIndirectCost()) * 100);
                            fnProjectInfoAppVo.setBaseInfo(baseInfo1);
                        }
                    }
                }
            }
        }


        // 2018架构调整后：处理研发费的名称映射，以便前端可以兼容新老架构的名称显示
        int firstDupIndex = -1, secondDupIndex = -1;
        for (FnProjectInfoAppVo.CostDetail directCostDetail : fnProjectInfoAppVo.getDirectCostDetail()) {
            if ("研发费".equals(directCostDetail.getName()) || "部门成本".equals(directCostDetail.getName())) {
                if (firstDupIndex != -1) {
                    secondDupIndex = fnProjectInfoAppVo.getDirectCostDetail().indexOf(directCostDetail);
                } else {
                    firstDupIndex = fnProjectInfoAppVo.getDirectCostDetail().indexOf(directCostDetail);
                }

            }
            if ("研发费".equals(directCostDetail.getName())) {
                directCostDetail.setName("部门成本");
                directCostDetail.setProjectStatDataId(1800L);
                for (FnProjectInfoAppVo.CostDetail children : directCostDetail.getChildren()) {
                    if ("人力成本".equals(children.getName())) {
                        children.setName("人员费用");
                        children.setProjectStatDataId(1801L);
                    }
                }
            }
        }

        // 2018架构调整后：处理研发费的费用值映射
        if (firstDupIndex != -1 && secondDupIndex != -1) {
            FnProjectInfoAppVo.CostDetail oldFirst = fnProjectInfoAppVo.getDirectCostDetail().get(firstDupIndex);
            FnProjectInfoAppVo.CostDetail oldSecond = fnProjectInfoAppVo.getDirectCostDetail().get(secondDupIndex);
            try {
                // 获取重复项
                FnProjectInfoAppVo.CostDetail firstDupItem = (FnProjectInfoAppVo.CostDetail) MyListUtils.deepClone(fnProjectInfoAppVo.getDirectCostDetail().get(firstDupIndex));
                FnProjectInfoAppVo.CostDetail secondDupItem = (FnProjectInfoAppVo.CostDetail) MyListUtils.deepClone(fnProjectInfoAppVo.getDirectCostDetail().get(secondDupIndex));
                // 1. 合并重复的父项
                firstDupItem.setCurrentMonthCost(firstDupItem.getCurrentMonthCost() + secondDupItem.getCurrentMonthCost());
                firstDupItem.setLastMonthCost(firstDupItem.getLastMonthCost() + secondDupItem.getLastMonthCost());
                firstDupItem.setTwoMonthAgoCost(firstDupItem.getTwoMonthAgoCost() + secondDupItem.getTwoMonthAgoCost());
                // 2. 合并重复的子项
                // 2.1 second中存在的，累加
                for (FnProjectInfoAppVo.CostDetail firstTemp : firstDupItem.getChildren()) {
                    for (FnProjectInfoAppVo.CostDetail secondTemp : secondDupItem.getChildren()) {
                        if (firstTemp.getName().equals(secondTemp.getName())) {
                            firstTemp.setCurrentMonthCost(firstTemp.getCurrentMonthCost() + secondTemp.getCurrentMonthCost());
                            firstTemp.setLastMonthCost(firstTemp.getLastMonthCost() + secondTemp.getLastMonthCost());
                            firstTemp.setTwoMonthAgoCost(firstTemp.getTwoMonthAgoCost() + secondTemp.getTwoMonthAgoCost());
                        }
                    }
                }

                // 2.2 second中没有的item,补充
                for (FnProjectInfoAppVo.CostDetail secondTemp : secondDupItem.getChildren()) {
                    boolean isInFirst = false;
                    for (FnProjectInfoAppVo.CostDetail firstTemp : firstDupItem.getChildren()) {
                        if (firstTemp.getName().equals(secondTemp.getName())) {
                            isInFirst = true;
                            break;
                        }
                    }
                    if (!isInFirst) {
                        firstDupItem.getChildren().add(secondTemp);
                    }
                }
                fnProjectInfoAppVo.getDirectCostDetail().remove(oldFirst);
                fnProjectInfoAppVo.getDirectCostDetail().remove(oldSecond);
                fnProjectInfoAppVo.getDirectCostDetail().add(firstDupItem);

            } catch (Exception e) {
                logger.error("新架构的数据合并失败");
                e.printStackTrace();
            }
        }


        return fnProjectInfoAppVo;
    }

    private List<FnProjectInfoDto> getFnProjectsBySum(Long projectId, Map<Long, List<FnProjectInfoDto>> subProjectsMap) {
        List<FnProjectInfoDto> list = new ArrayList<>();
        if (subProjectsMap.containsKey(projectId)) {
            List<FnProjectInfoDto> subProjects = subProjectsMap.get(projectId);
            for (FnProjectInfoDto project : subProjects) {
                if (subProjectsMap.containsKey(project.getId())) {
                    List<FnProjectInfoDto> subList = getFnProjectsBySum(project.getId(), subProjectsMap);
                    if (!subList.isEmpty()) {
                        list.addAll(subList);
                    }
                } else if (!"汇总".equals(project.getServiceLine())) {
                    list.add(project);
                }
            }
        }
        return list;
    }

    private void setFnProjectInfoAppVo(FnProjectInfoAppVo fnProjectInfoAppVo, List<FnProjectStatDataDto> fnProjectStatDataDtos,
                                       Map<Long, FnProjectInfoAppVo.CostDetail> costDetailMap, Integer year, Integer month, MonthType type, Long projectId) {
        FnProjectInfoAppVo.ProfitInfo profitInfo = new FnProjectInfoAppVo.ProfitInfo();
        profitInfo.setYear(year);
        profitInfo.setMonth(month);
        FnProjectInfoAppVo.PerCapitaProfitInfo perCapitaProfitInfo = new FnProjectInfoAppVo.PerCapitaProfitInfo();
        perCapitaProfitInfo.setYear(year);
        perCapitaProfitInfo.setMonth(month);
        FnProjectInfoAppVo.IncomeInfo incomeInfo = new FnProjectInfoAppVo.IncomeInfo();
        incomeInfo.setYear(year);
        incomeInfo.setMonth(month);
        FnProjectInfoAppVo.CostInfo costInfo = new FnProjectInfoAppVo.CostInfo();
        costInfo.setYear(year);
        costInfo.setMonth(month);
        if (MonthType.current.equals(type)) {
            costInfo.setDirectCostDetail(new ArrayList<>());
            costInfo.setIndirectCostDetail(new ArrayList<>());
        }
        Float income = 0F, taxReturn = 0F, turnoverTax = 0F, afterTaxIncome = 0F, cost = 0F, internalSettlementCost = 0F,
                directCost = 0F, indirectCost = 0F, profit = 0F, profitability = 0F, perCapitaProfit = 0F, outsourcingCost = 0F;

        // 取出外包费用
        for (FnProjectStatDataDto fnProjectStatDataDto : fnProjectStatDataDtos) {
            if (!year.equals(fnProjectStatDataDto.getYear()) || !month.equals(fnProjectStatDataDto.getMonth())) {
                continue;
            }

            if ("外包费用".equals(fnProjectStatDataDto.getName())) {
                outsourcingCost = fnProjectStatDataDto.getValue();
                break;
            }
        }

        for (FnProjectStatDataDto fnProjectStatDataDto : fnProjectStatDataDtos) {
            if (!year.equals(fnProjectStatDataDto.getYear()) || !month.equals(fnProjectStatDataDto.getMonth())) {
                continue;
            }
            String fnStatName = fnProjectStatDataDto.getName();
            Long fnStatId = fnProjectStatDataDto.getFnStatId();
            switch (fnStatName) {
                case "收入":
                    if (!ReportHelper.IncomeChangeProjects.contains(projectId)) {
                        income += fnProjectStatDataDto.getValue();
                    }
                    break;
                case "产品运营收入":
                    if (ReportHelper.IncomeChangeProjects.contains(projectId)) {
                        income += fnProjectStatDataDto.getValue();
                    }
                    break;
                case "增值税退税收入":
                    taxReturn += fnProjectStatDataDto.getValue();
                    break;
                case "流转税金及附加(含内部)":
                    turnoverTax += fnProjectStatDataDto.getValue();
                    break;
                case "税后收入":
                    afterTaxIncome += fnProjectStatDataDto.getValue();
                    break;
                case "内部结算成本":
                    internalSettlementCost += fnProjectStatDataDto.getValue();
                    break;
                case "费用合计(直接+分摊)":
                    cost += fnProjectStatDataDto.getValue();
                    break;
                case "税前利润":
                    profit += fnProjectStatDataDto.getValue();
                    break;
                case "税前利润率":
                    profitability += fnProjectStatDataDto.getValue() * 100;
                    break;
                case "人均利润(人数含分摊)":
                    perCapitaProfit += fnProjectStatDataDto.getValue();
                    break;
                case "直接费用合计":
                    directCost += fnProjectStatDataDto.getValue();
                    break;
                case "分摊费用":
                    indirectCost += fnProjectStatDataDto.getValue();
                    break;
                case "大连平台研发费用分摊":
                    indirectCost += fnProjectStatDataDto.getValue();
                    break;
                case "大连平台管理费用分摊":
                    indirectCost += fnProjectStatDataDto.getValue();
                    break;
                default:
            }

            // 当月的设置费用明细
            if (MonthType.current.equals(type)) {
                if ("产品-运营成本".equals(fnStatName) || "产品-销售费用".equals(fnStatName)
                        || "研发费".equals(fnStatName) // 2017年的大表中叫 “研发费”
                        || "部门成本".equals(fnStatName)) // 2018年的大表中叫 “部门成本”
                {
                    FnProjectInfoAppVo.CostDetail costDetail = new FnProjectInfoAppVo.CostDetail();
                    costDetail.setName(getFnStatReplaceName(fnStatName));

                    // 研发费 需要减去 外包费用
                    if ("研发费".equals(fnStatName)) {
                        costDetail.setCurrentMonthCost(ReportHelper.formatNumberByScale(fnProjectStatDataDto.getValue() - outsourcingCost, 1));
                    } else {
                        costDetail.setCurrentMonthCost(ReportHelper.formatNumberByScale(fnProjectStatDataDto.getValue(), 1));
                    }
                    costInfo.getDirectCostDetail().add(costDetail);

                } else if ("分摊费用".equals(fnProjectStatDataDto.getParentName()) || "外包费用".equals(fnStatName)) {
                    FnProjectInfoAppVo.CostDetail costDetail = new FnProjectInfoAppVo.CostDetail();
                    costDetail.setName(getFnStatReplaceName(fnStatName));
                    costDetail.setCurrentMonthCost(ReportHelper.formatNumberByScale(fnProjectStatDataDto.getValue(), 1));
                    costInfo.getIndirectCostDetail().add(costDetail);
                }
            }

            if (costDetailMap.containsKey(fnStatId)) {
                costDetailMap.get(fnStatId).setName(getFnStatReplaceName(fnStatName));

                Float value = fnProjectStatDataDto.getValue();
                // 研发费，直接费用 需要减去 外包费用
                if ("研发费".equals(fnStatName)) {
                    value = value - outsourcingCost;
                }

                switch (type) {
                    case twoMonthAgo:
                        costDetailMap.get(fnStatId).setTwoMonthAgoCost(ReportHelper.formatNumberByScale(value, 1));
                        break;
                    case last:
                        costDetailMap.get(fnStatId).setLastMonthCost(ReportHelper.formatNumberByScale(value, 1));
                        break;
                    case current:
                        // 返回明细状况,以便前端可以查询 (目前包含的明细有：广告费，劳务费，市场费)
                        costDetailMap.get(fnStatId).setDetailFlag(fnProjectStatDataDto.getDetailFlag() != null && fnProjectStatDataDto.getDetailFlag());
                        costDetailMap.get(fnStatId).setProjectStatDataId(fnProjectStatDataDto.getId());
                        costDetailMap.get(fnStatId).setCurrentMonthCost(ReportHelper.formatNumberByScale(value, 1));
                        break;

                    default:
                }
            }
        }

        if (MonthType.current.equals(type)) {
            fnProjectInfoAppVo.getBaseInfo().setIncome(ReportHelper.formatNumberByScale(income, 1));
            fnProjectInfoAppVo.getBaseInfo().setCost(ReportHelper.formatNumberByScale(cost, 1));
            fnProjectInfoAppVo.getBaseInfo().setProfit(ReportHelper.formatNumberByScale(profit, 1));
            fnProjectInfoAppVo.getBaseInfo().setProfitability(ReportHelper.formatNumberByScale(profitability, 1));
            fnProjectInfoAppVo.getBaseInfo().setPerCapitaProfit(ReportHelper.formatNumberByScale(perCapitaProfit, 1));
        }

        if (null != fnProjectInfoAppVo.getProfitInfo()) {
            profitInfo.setProfit(ReportHelper.formatNumberByScale(profit, 1));
            profitInfo.setProfitability(ReportHelper.formatNumberByScale(profitability, 1));
            fnProjectInfoAppVo.getProfitInfo().add(profitInfo);
        }

        if (null != fnProjectInfoAppVo.getPerCapitaProfitInfo()) {
            perCapitaProfitInfo.setPerCapitaProfit(ReportHelper.formatNumberByScale(perCapitaProfit, 1));
            fnProjectInfoAppVo.getPerCapitaProfitInfo().add(perCapitaProfitInfo);
        }

        if (null != fnProjectInfoAppVo.getIncomeInfo()) {
            incomeInfo.setIncome(ReportHelper.formatNumberByScale(income, 1));
            incomeInfo.setTaxReturn(ReportHelper.formatNumberByScale(taxReturn, 1));
            incomeInfo.setTurnoverTax(ReportHelper.formatNumberByScale(-turnoverTax, 1));
            incomeInfo.setAfterTaxIncome(ReportHelper.formatNumberByScale(afterTaxIncome, 1));
            incomeInfo.setInternalSettlementCost(ReportHelper.formatNumberByScale(-internalSettlementCost, 1));
            incomeInfo.setNetIncome(ReportHelper.formatNumberByScale(afterTaxIncome - internalSettlementCost, 1));
            fnProjectInfoAppVo.getIncomeInfo().add(incomeInfo);
        }

        costInfo.setDirectCost(ReportHelper.formatNumberByScale(directCost - outsourcingCost, 1));  // 直接费用 需要减去 外包费用
        costInfo.setIndirectCost(ReportHelper.formatNumberByScale(indirectCost + outsourcingCost, 1));  // 间接费用 需要加上 外包费用
        fnProjectInfoAppVo.getCostInfo().add(costInfo);
    }

    private String getFnStatReplaceName(String fnStatName) {
        String replaceName = fnStatName;
        if (ReportHelper.FnStatReplaceNameMap.containsKey(fnStatName)) {
            replaceName = ReportHelper.FnStatReplaceNameMap.get(fnStatName);
        }
        return replaceName;
    }

    @Override
    public FnProjectShareAppVo getAppProjectShare(Long projectId, Integer year, Integer month) {
        if (null == projectId || null == year || null == month) {
            logger.info("getAppProjectShare failed...");
            throw new ParamException("projectId or year or month can not be null");
        }
        Project project = projectMapper.selectByPrimaryKey(projectId);
        if (null == project) {
            logger.info("getAppProjectShare failed...");
            throw new ParamException("project not found");
        }

        // 读取分摊数据的起始结束时间
        List<DateRangeVo> dateRangeVoList = fnSumShareConfigMapper.selectFirstAndLastDateByProjectId(projectId);
        if (null == dateRangeVoList || dateRangeVoList.isEmpty()) {
            logger.info("getAppProjectShare failed...");
            //throw new ParamException("该项目没有分摊数据");
            return null;
        }

        FnProjectShareAppVo fnProjectShareAppVo = new FnProjectShareAppVo();

        JSONObject requestParam = new JSONObject();
        requestParam.put("id", projectId);
        requestParam.put("name", project.getName());
        requestParam.put("year", year);
        requestParam.put("month", month);
        fnProjectShareAppVo.setRequestParam(requestParam);

        //设置起始结束日期
        BasePeriodAppVo baseInfo = new BasePeriodAppVo();
        Integer startYear, startMonth, endYear, endMonth;
        startYear = dateRangeVoList.get(0).getYear();
        startMonth = dateRangeVoList.get(0).getMonth();
        if (dateRangeVoList.size() > 1) {
            endYear = dateRangeVoList.get(1).getYear();
            endMonth = dateRangeVoList.get(1).getMonth();
        } else {
            endYear = startYear;
            endMonth = startMonth;
        }

        //判断传入的年月是否在有效的范围内
        if ((year > startYear || (year.equals(startYear) && month >= startMonth)) &&
                (year < endYear || (year.equals(endYear) && month <= endMonth))) {
            //在日期范围内，继续往下走
            DateRangeVo dateRangeVo = fnShareTemplateMapper.selectEndYearMonth();

            //DateRangeVo dateRangeVo = fnShareDataMapper.selectEndYearMonthOfProject(projectId);

            //传入的年月小于数据的结束年月，则日期结束年月以日期结束为准
            if (dateRangeVo.getYear() < endYear || (dateRangeVo.getYear() == endYear && dateRangeVo.getMonth() < endMonth)) {
                endYear = dateRangeVo.getYear();
                endMonth = dateRangeVo.getMonth();
            }

            if (endYear < year || (endYear.equals(year) && endMonth < month)) {
                year = endYear;
                month = endMonth;
            }

            baseInfo.setStartYear(startYear);
            baseInfo.setStartMonth(startMonth);
            baseInfo.setEndYear(endYear);
            baseInfo.setEndMonth(endMonth);
            fnProjectShareAppVo.setBaseInfo(baseInfo);
        } else {
            logger.info("该项目没有分摊数据");

            baseInfo.setStartYear(startYear);
            baseInfo.setStartMonth(startMonth);
            baseInfo.setEndYear(endYear);
            baseInfo.setEndMonth(endMonth);
            fnProjectShareAppVo.setBaseInfo(baseInfo);

            return fnProjectShareAppVo;
        }

        fnProjectShareAppVo.setStatistics(new ArrayList<>());
        fnProjectShareAppVo.setDetail(new ArrayList<>());

        //读取分摊汇总信息
        List<FnSumShareConfigDto> fnSumShareConfigDtoList = fnSumShareConfigMapper.selectByYearAndMonthAndProjectId(projectId, year, month);
        if (fnSumShareConfigDtoList.size() == 0) {
            logger.info("该项目没有分摊数据");

            return fnProjectShareAppVo;
        }
        //用来存有分摊的平台的ID
        List<Long> platIdList = new ArrayList<>();

        //建立平台映射
        Map<Long, FnProjectShareAppVo.ShareDetail> shareDetailMap = new HashMap<>();
        for (FnSumShareConfigDto fnSumShareConfigDto : fnSumShareConfigDtoList) {
            Long platIdKey = fnSumShareConfigDto.getPlatId();
            String platName = fnSumShareConfigDto.getPlatName();
            if (null != fnSumShareConfigDto.getPlatParentHrId()) {
                platIdKey = fnSumShareConfigDto.getPlatParentHrId();
                platName = fnSumShareConfigDto.getPlatParentHrName();
            }
            if (!platIdList.contains(platIdKey)) {
                platIdList.add(platIdKey);
            }

            if (!shareDetailMap.containsKey(platIdKey)) {
                FnProjectShareAppVo.ShareDetail shareDetail = new FnProjectShareAppVo.ShareDetail();
                shareDetail.setProjectName(platName);
                shareDetail.setTwoMonthAgoShareNumber(0F);
                shareDetail.setLastMonthShareNumber(0F);
                shareDetail.setCurrentMonthShareNumber(0F);
                shareDetail.setTwoMonthAgoFixedNumber(0F);
                shareDetail.setLastMonthFixedNumber(0F);
                shareDetail.setCurrentMonthFixedNumber(0F);
                shareDetailMap.put(platIdKey, shareDetail);
            }
        }
        //计算有分摊的所有平台的人力人数
        List<UserCountOfProjectDto> userCountOfProjectDtoList = hrReportService.getHrUserTransactionByProjectsWithSubPlat(platIdList, year, month);

        int currentYear;
        int currentMonth;
        Calendar currentDate = Calendar.getInstance();
        currentDate.set(year, month - 1, 1);
        //前两月
        currentDate.add(Calendar.MONTH, -2);
        currentYear = currentDate.get(Calendar.YEAR);
        currentMonth = currentDate.get(Calendar.MONTH) + 1;
        setFnShareAppVo(fnProjectShareAppVo, fnSumShareConfigDtoList, userCountOfProjectDtoList, shareDetailMap, currentYear, currentMonth, MonthType.twoMonthAgo);
        //上月
        currentDate.add(Calendar.MONTH, 1);
        currentYear = currentDate.get(Calendar.YEAR);
        currentMonth = currentDate.get(Calendar.MONTH) + 1;
        setFnShareAppVo(fnProjectShareAppVo, fnSumShareConfigDtoList, userCountOfProjectDtoList, shareDetailMap, currentYear, currentMonth, MonthType.last);
        //当月
        setFnShareAppVo(fnProjectShareAppVo, fnSumShareConfigDtoList, userCountOfProjectDtoList, shareDetailMap, year, month, MonthType.current);

        // 明细
        List<FnProjectShareAppVo.ShareDetail> shareDetails = shareDetailMap.values().stream().filter(ShareDetail -> ShareDetail.getTwoMonthAgoShareNumber() != 0 || ShareDetail.getTwoMonthAgoFixedNumber() != 0
                || ShareDetail.getLastMonthShareNumber() != 0 || ShareDetail.getLastMonthFixedNumber() != 0
                || ShareDetail.getCurrentMonthShareNumber() != 0 || ShareDetail.getCurrentMonthFixedNumber() != 0
        ).collect(Collectors.toList());
        shareDetails.sort((x, y) -> y.getCurrentMonthShareNumber().compareTo(x.getCurrentMonthShareNumber()));
        fnProjectShareAppVo.setDetail(shareDetails);

        //判断要不要显示详情按钮
        List<FnProjectShareDetailVo> shareDetailInfos = getAppProjectShareDetail(projectId, year, month);
        boolean blIsProjectContainShare = shareDetailInfos.size() > 0;
        fnProjectShareAppVo.setProjectShareDetailFlag(blIsProjectContainShare);

        return fnProjectShareAppVo;
    }

    private void setFnShareAppVo(FnProjectShareAppVo fnProjectShareAppVo, List<FnSumShareConfigDto> fnSumShareConfigDtoList, List<UserCountOfProjectDto> userCountOfProjectDtoList,
                                 Map<Long, FnProjectShareAppVo.ShareDetail> shareDetailMap,
                                 Integer year, Integer month, MonthType type) {
        Float shareNumber = 0F;
        for (FnSumShareConfigDto fnSumShareConfigDto : fnSumShareConfigDtoList) {
            if (!year.equals(fnSumShareConfigDto.getYear()) || !month.equals(fnSumShareConfigDto.getMonth())) {
                continue;
            }

            UserCountOfProjectDto userCountOfProjectDto = userCountOfProjectDtoList.stream().filter(item -> item.getProjectId().equals(fnSumShareConfigDto.getPlatId())).findFirst().orElse(null);
            if (null == userCountOfProjectDto) {
                //这里其实不会发生，为了安心写上
                continue;
            }
            UserCountOfProjectDto.UserCountByYearMonth userCountByYearMonth = userCountOfProjectDto.getUserCount().stream().filter(item -> item.getYear().equals(year) && item.getMonth().equals(month)).findFirst().orElse(null);
            if (null == userCountByYearMonth) {
                //这里其实不会发生，为了安心写上
                continue;
            }
            //平台（子分摊项）的人力人数
            int shareNumberThisPlat = userCountByYearMonth.getUserCount();

            //取父分摊项，以便于按父分摊项汇总
            Long platId = fnSumShareConfigDto.getPlatId();
            if (null != fnSumShareConfigDto.getPlatParentHrId()) {
                platId = fnSumShareConfigDto.getPlatParentHrId();
            }

            FnProjectShareAppVo.ShareDetail shareDetail = shareDetailMap.get(platId);
            switch (type) {
                case twoMonthAgo:
                    Float valueTwoMonthAgo = shareDetail.getTwoMonthAgoShareNumber();
                    shareDetail.setTwoMonthAgoShareNumber(fnSumShareConfigDto.getSharePro().floatValue() * shareNumberThisPlat + valueTwoMonthAgo);
                    break;
                case last:
                    Float valueLastMonth = shareDetail.getLastMonthShareNumber();
                    shareDetail.setLastMonthShareNumber(fnSumShareConfigDto.getSharePro().floatValue() * shareNumberThisPlat + valueLastMonth);
                    break;
                case current:
                    Float valueCurrentMonth = shareDetail.getCurrentMonthShareNumber();
                    shareDetail.setCurrentMonthShareNumber(fnSumShareConfigDto.getSharePro().floatValue() * shareNumberThisPlat + valueCurrentMonth);
                    break;
                default:
            }
            shareDetailMap.put(platId, shareDetail);

            //计算这月总分摊人数
            shareNumber += fnSumShareConfigDto.getSharePro().floatValue() * shareNumberThisPlat;
        }

        FnProjectShareAppVo.ShareNumberOfYearMonth ShareNumberOfYearMonth = new FnProjectShareAppVo.ShareNumberOfYearMonth();
        ShareNumberOfYearMonth.setYear(year);
        ShareNumberOfYearMonth.setMonth(month);
        ShareNumberOfYearMonth.setShareNumber(ReportHelper.formatNumberByScale(shareNumber, 1));
        fnProjectShareAppVo.getStatistics().add(ShareNumberOfYearMonth);
    }

    @Override
    public List<FnProjectShareDetailVo> getAppProjectShareDetail(Long projectId, Integer year, Integer month) {

        // 检查该项目的平台填写情况（是否为分摊明细）
        Map<String, Boolean> platShareDetailMap = this.getPlatShareDetailInfo(projectId);

        // 查询该项目下所有分摊详情
        List<FnProjectShareDetailVo> detailVos = fnPlatShareConfigMapper.selectProjectShareDetail(projectId, year, month);

        // 查询所有的parent_hr_id，并分组
        List<ProjectParentHrVo> parentHrProjects = projectMapper.selectAllParentHrProject();
        Map<Long, ProjectParentHrVo> parentHrVoMap = parentHrProjects.stream().collect(Collectors.toMap(p -> p.getId(), p -> p));

        // 将含有parent_hr_id的平台，名称重置为父平台
        detailVos.forEach(detailVo -> {
            if (null != parentHrVoMap.get(detailVo.getPlatId())) {
                detailVo.setPlatName(parentHrVoMap.get(detailVo.getPlatId()).getParentHrProjectName());
            }
        });

        // 去除不是分摊明细的记录
        List<FnProjectShareDetailVo> result = detailVos.stream()
                .filter(d -> {
                    Boolean isPlatShareDetail = platShareDetailMap.get(this.buildPlatYearMonthKey(d.getPlatId(), year, month));
                    return null != isPlatShareDetail && isPlatShareDetail;
                })
                .collect(Collectors.toList());

        return result;
    }


    // 创建key
    private String buildPlatYearMonthKey(Long platId, Integer year, Integer month) {
        return platId + "-" + year + "-" + month;
    }


    /**
     * 功能：判断该项目，在该年该月，是否有平台填写明细
     *
     * @param platDetailMap ： 需调用getPlatShareDetailInfo生成
     * @param year
     * @param month
     * @return flag
     */
    private boolean isProjectContainsPlatDetail(Map<String, Boolean> platDetailMap, Integer year, Integer month) {
        return platDetailMap.keySet().stream()
                .anyMatch((k -> k.split("-")[1].equals(year.toString()) // 存在该年
                        && k.split("-")[2].equals(month.toString())));// 存在该月
    }

    /**
     * 功能：检查该项目下，各平台，各月份，是否为分摊明细填写方式
     *
     * @param projectId
     * @return map：{platId-year-month,shareDetailFlag }
     */
    private Map<String, Boolean> getPlatShareDetailInfo(Long projectId) {

        // 检查该项目下，各平台的月份是否为分摊明细填写方式
        List<CfgPlatAttr> platAttrVos = cfgPlatAttrMapper.selectAllDetailPlat();
        List<FnSumShareConfigGroupVo> projectSumShareInfo = fnSumShareConfigMapper.selectByProjectIdAndGroup(projectId);
        for (FnSumShareConfigGroupVo fnSumShareConfigGroupVo : projectSumShareInfo) {
            if (platAttrVos.stream().anyMatch(p -> p.getPlatId().equals(fnSumShareConfigGroupVo.getPlatId())  // 是该平台
                    && p.getShareDetailFlag()  // 且平台开启了分摊明细
                    && (p.getShareStartYear() < fnSumShareConfigGroupVo.getYear() || // 且平台在开启分摊明细的月份之后
                    (p.getShareStartYear().equals(fnSumShareConfigGroupVo.getYear()) &&
                            p.getShareStartMonth() <= fnSumShareConfigGroupVo.getMonth())))) {
                fnSumShareConfigGroupVo.setShareDetailFlag(true);
            } else {
                fnSumShareConfigGroupVo.setShareDetailFlag(false);
            }
        }

        // 整理为map
        Map<String, Boolean> platShareDetailMap = projectSumShareInfo.stream()
                .collect(Collectors.toMap(p -> this.buildPlatYearMonthKey(p.getPlatId(), p.getYear(), p.getMonth()), p -> p.getShareDetailFlag()));
        return platShareDetailMap;
    }

    @Override
    public List<PlatAppVo> getAppPlats() {
        PermissionProjectDto permissionProjectDto = PermissionHelper.getPermissionByProject();

        List<PlatAppVo> result = new ArrayList<>();

        Calendar today = Calendar.getInstance();
        FnProjectStatData fnProjectStatData = fnProjectStatDataMapper.selectByNew();
        if (null != fnProjectStatData) {
            today.set(fnProjectStatData.getYear(), fnProjectStatData.getMonth() - 1, 1);
        }
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH) + 1;
        today.add(Calendar.MONTH, -1);
        int lastYear = today.get(Calendar.YEAR);
        int lastMonth = today.get(Calendar.MONTH) + 1;

        /// 重构前
//        List<PlatAppVo> platAppVos = projectMapper.selectAllAppPlat(year, month, lastYear, lastMonth);
//
//        // 根据parent_share_id,重新计算平台的分摊费用
//        List<Project> parentSharePlats = projectMapper.selectAllParentSharePlat(); // 缓存所有父分摊项平台
//        List<IdValueDto> sumAmountList = fnShareDataMapper.countParentFromSumColumn(year, month);// 缓存所有汇总项列的值
//        for (PlatAppVo platAppVo : platAppVos) {
//
//            boolean isInParentSharePlat = parentSharePlats.stream().anyMatch(project -> project.getId().equals(platAppVo.getId()));
//
//            // 不存在子分摊项，或使用了新架构，则直接取汇总值
//            if (!isInParentSharePlat || (isInParentSharePlat && year >= 2018)) {
//                for (IdValueDto idValueDto : sumAmountList) {
//                    if (idValueDto.getId().equals(platAppVo.getId())) {
//                        platAppVo.setCost(idValueDto.getValue());
//                        break;
//                    }
//                }
//            }
//            // 存在子分摊项，则将所有子分摊项的分摊金额相加。
//            else {
//                // todo: can be refactor, multi-db-query
//                float sumAmount = fnShareDataMapper.countChildShareCostByPlatIdAndYearAndMonth(platAppVo.getId(), year, month);
//                platAppVo.setCost(sumAmount);
//            }
//        }
//
//        for (PlatAppVo platAppVo : platAppVos) {
//            if (!permissionProjectDto.isAllPerm() && !permissionProjectDto.getPermProjectIds().contains(platAppVo.getId())) {
//                continue;
//            }
//
//            if (null != platAppVo.getCost()) {
//                platAppVo.setCost(ReportHelper.formatNumberByScale(platAppVo.getCost(), 1));
//            } else {
//                platAppVo.setCost(0F);
//            }
//            if (null != platAppVo.getLastMonthCost()) {
//                platAppVo.setLastMonthCost(ReportHelper.formatNumberByScale(platAppVo.getLastMonthCost(), 1));
//            } else {
//                platAppVo.setLastMonthCost(0F);
//            }
//
//            retPlatAppVos.add(platAppVo);
//        }

        // 重构后
        List<PlatAppDto> platAppDtos = projectMapper.selectAllPlatAppDto();
        List<IdValueDto> currentMonthCosts = fnShareDataMapper.countParentFromSumColumn(year, month);
        List<IdValueDto> lastMonthCosts = fnShareDataMapper.countParentFromSumColumn(lastYear, lastMonth);
        List<PlatIdAndProjectIdDto> platIdAndProjectIdDtos = fnSumShareConfigMapper.selectPlatIdAndProjectIdByYearAndMonth(year, month);

        Map<Long, Float> currentMonthShareAmountByPlatIdMap = currentMonthCosts.stream().collect(Collectors.toMap(x -> x.getId(), x -> x.getValue()));
        Map<Long, Float> lastMonthShareAmountByPlatIdMap = lastMonthCosts.stream().collect(Collectors.toMap(x -> x.getId(), x -> x.getValue()));
        Map<Long, List<Long>> projectIdsByParentHrIdMap = new HashMap<>();
        Map<Long, Set<Long>> projectIdSetByPlatIdMap = new HashMap<>();
        Map<Long, PlatAppDto> platAppDtoMap = new HashMap<>();

        for (PlatAppDto item : platAppDtos) {
            if (item.getParentHrId() == null) {
                if (item.getActiveFlag() && Project.ServiceLine.plat.equals(item.getServiceLine()) && (Project.AppShowMode.all.equals(item.getAppShowMode()) || Project.AppShowMode.project.equals(item.getAppShowMode()) || Project.AppShowMode.financeAndProject.equals(item.getAppShowMode()))) {
                    result.add(item);
                }
            } else {
                List<Long> projectIds = projectIdsByParentHrIdMap.get(item.getParentHrId());
                if (projectIds != null) {
                    projectIds.add(item.getId());
                } else {
                    projectIds = new ArrayList<>();
                    projectIds.add(item.getId());
                    projectIdsByParentHrIdMap.put(item.getParentHrId(), projectIds);
                }
            }
            platAppDtoMap.put(item.getId(),item);
        }

        for (PlatIdAndProjectIdDto item : platIdAndProjectIdDtos) {
            Set<Long> projectIdSet = projectIdSetByPlatIdMap.get(item.getPlatId());
            if (projectIdSet != null) {
                projectIdSet.add(item.getProjectId());
            } else {
                projectIdSet = new HashSet<>();
                projectIdSet.add(item.getProjectId());
                projectIdSetByPlatIdMap.put(item.getPlatId(), projectIdSet);
            }
        }


        for (PlatAppVo platAppVo : result) {
            platAppVo.setYear(year);
            platAppVo.setMonth(month);

            Float currentMonthCost = 0f;
            Float lastMonthCost = 0f;
            Integer memberCount = 0;

            Set<Long> serverProjectIdSet = new HashSet<>();

            Long shareProjectId = ReportHelper.PlatInShareProjectMap.get(platAppVo.getId());
            boolean hasSumShareDataFlag = false;

            Float topPlatShareAmount = currentMonthShareAmountByPlatIdMap.get(platAppVo.getId());
            Float topPlatLastMonthShareAmount = lastMonthShareAmountByPlatIdMap.get(platAppVo.getId());
            Set<Long> projectIdSet = projectIdSetByPlatIdMap.get(platAppVo.getId());
            if (projectIdSet != null) {
                serverProjectIdSet.addAll(projectIdSet);
            }
            if (topPlatShareAmount != null) {
                currentMonthCost = topPlatShareAmount;
            }
            if (topPlatLastMonthShareAmount != null) {
                lastMonthCost = topPlatLastMonthShareAmount;
            }

            List<Long> projectIds = projectIdsByParentHrIdMap.get(platAppVo.getId());
            if (projectIds != null) {
                for (Long childProjectId : projectIds) {
                    Float childProjectShareAmount = currentMonthShareAmountByPlatIdMap.get(childProjectId);
                    Float childProjectLastMonthShareAmount = lastMonthShareAmountByPlatIdMap.get(childProjectId);
                    Set<Long> subProjectIdSet = projectIdSetByPlatIdMap.get(childProjectId);
                    if (subProjectIdSet != null) {
                        serverProjectIdSet.addAll(subProjectIdSet);
                    }
                    if (childProjectShareAmount != null) {
                        currentMonthCost += childProjectShareAmount;
                    }
                    if (childProjectLastMonthShareAmount != null) {
                        lastMonthCost += childProjectLastMonthShareAmount;
                    }

                    // 合并子平台的人数
                    memberCount = memberCount + platAppDtoMap.get(childProjectId).getMemberNumber();
                }
            }else {
                memberCount = platAppVo.getMemberNumber();
            }
            if (lastMonthCost != 0f) {
                hasSumShareDataFlag = true;
            }

            platAppVo.setCost(MyCellUtils.formatFloatNumber(currentMonthCost, 1));
            platAppVo.setLastMonthCost(MyCellUtils.formatFloatNumber(lastMonthCost, 1));
            platAppVo.setServeNumber(serverProjectIdSet.size());
            platAppVo.setMemberNumber(memberCount);
            platAppVo.setHasSumShareData(hasSumShareDataFlag);
            if (shareProjectId != null && serverProjectIdSet.contains(shareProjectId)) {
                platAppVo.setServeNumber(platAppVo.getServeNumber() - 1);
            }
        }
        result.sort((x, y) -> y.getCity().compareTo(x.getCity()));
        return result;
    }

    @Override
    public FnPlatCostAppVo getAppPlatCost(Long platId, Integer year, Integer month) {
        if (null == platId || null == year || null == month) {
            logger.info("getAppPlatCost failed...");
            throw new ParamException("platId or year or month can not be null");
        }
        Project platform = projectMapper.selectByPrimaryKey(platId);
        if (null == platform) {
            logger.info("getAppPlatCost failed...");
            throw new ParamException("platform not found");
        }

        List<FnShareData> fnShareDatas = fnShareDataMapper.selectFirstAndLastByPlatId(platId);
        if (null == fnShareDatas || fnShareDatas.isEmpty()) {
            logger.info("getAppPlatCost failed...");
            throw new ParamException("project have not share data");
        }

        BasePeriodAppVo baseInfo = new BasePeriodAppVo();
        // 设置最早年月和最晚年月
        Integer startYear, startMonth, endYear, endMonth;
        startYear = fnShareDatas.get(0).getYear();
        startMonth = fnShareDatas.get(0).getMonth();
        if (fnShareDatas.size() > 1) {
            endYear = fnShareDatas.get(1).getYear();
            endMonth = fnShareDatas.get(1).getMonth();
        } else {
            endYear = startYear;
            endMonth = startMonth;
        }
        baseInfo.setStartYear(startYear);
        baseInfo.setStartMonth(startMonth);
        baseInfo.setEndYear(endYear);
        baseInfo.setEndMonth(endMonth);

        List<FnShareDataDto> fnShareDataDtos = fnShareDataMapper.selectByYearAndMonthAndProjectId(year, month, null, platId);

        FnPlatCostAppVo fnPlatCostAppVo = new FnPlatCostAppVo();

        JSONObject requestParam = new JSONObject();
        requestParam.put("id", platId);
        requestParam.put("name", platform.getName());
        requestParam.put("year", year);
        requestParam.put("month", month);
        fnPlatCostAppVo.setRequestParam(requestParam);

        fnPlatCostAppVo.setBaseInfo(baseInfo);

        List<FnPlatCostAppVo.Statistic> statistics = new ArrayList<>();
        FnPlatCostAppVo.Statistic statistic;
        Calendar now = Calendar.getInstance();
        now.set(year, month - 1, 1);

        List<Project> parentSharePlats = projectMapper.selectAllParentSharePlat(); // 缓存所有父分摊项平台
        boolean isInParentSharePlat = parentSharePlats.stream().anyMatch(project -> project.getId().equals(platId));

        //前两月
        now.add(Calendar.MONTH, -2);
        statistic = getPlatCostAppVoStatistic(isInParentSharePlat, now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, fnShareDataDtos);
        statistics.add(statistic);
        //上月
        now.add(Calendar.MONTH, 1);
        statistic = getPlatCostAppVoStatistic(isInParentSharePlat, now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, fnShareDataDtos);
        statistics.add(statistic);
        //当前月
        statistic = getPlatCostAppVoStatistic(isInParentSharePlat, year, month, fnShareDataDtos);
        statistics.add(statistic);

        fnPlatCostAppVo.setStatistics(statistics);

        return fnPlatCostAppVo;
    }

    private FnPlatCostAppVo.Statistic getPlatCostAppVoStatistic(boolean parentShareFlag, Integer year, Integer month, List<FnShareDataDto> fnShareDataDtos) {
        Float internalCost = 0F, summaryCost = 0F;
        for (FnShareDataDto fnShareDataDto : fnShareDataDtos) {
            if (!year.equals(fnShareDataDto.getYear()) || !month.equals(fnShareDataDto.getMonth())) {
                continue;
            }

            if (null == fnShareDataDto.getShareAmount()) {
                continue;
            }

            if (fnShareDataDto.getProjectId().equals(PlatInShareProjectMap.get(fnShareDataDto.getPlatId()))) {
                internalCost += fnShareDataDto.getShareAmount();
            }
        }


        /**
         * 重新通过子分摊项计算分摊费用
         */
        // 若有子分摊项，则需要汇总子分摊项的分摊金额
        if (parentShareFlag && year < 2018) {
            summaryCost = fnShareDataMapper.countChildShareCostByPlatIdAndYearAndMonth(fnShareDataDtos.get(0).getPlatId(), year, month);
        }
        // 若没有子分摊项，则直接取汇总列的值
        else {
            for (FnShareDataDto fnShareDataDto : fnShareDataDtos) {
                if (fnShareDataDto.getMonth().intValue() == month.intValue() && fnShareDataDto.getProjectId().equals(ReportHelper.FnShareDataSummaryItemProjectId)) {
                    summaryCost = fnShareDataDto.getShareAmount();
                    break;
                }
            }
        }


        FnPlatCostAppVo.Statistic statistic = new FnPlatCostAppVo.Statistic();
        statistic.setYear(year);
        statistic.setMonth(month);
        statistic.setInternalCost(ReportHelper.formatNumberByScale(internalCost, 1));
        statistic.setShareCost(ReportHelper.formatNumberByScale(summaryCost - internalCost, 1));
        return statistic;
    }

    @Override
    public FnPlatShareAppVo getAppPlatShare(Long platId, Integer year, Integer month) {
        if (null == platId || null == year || null == month) {
            logger.info("getAppPlatShare failed...");
            throw new ParamException("platId or year or month can not be null");
        }
        Project platform = projectMapper.selectByPrimaryKey(platId);
        if (null == platform) {
            logger.info("getAppPlatShare failed...");
            throw new ParamException("platform not found");
        }

        // 读取分摊数据的起始结束时间
        List<DateRangeVo> dateRangeVoList = fnSumShareConfigMapper.selectFirstAndLastDateByPlatId(platId);
        if (null == dateRangeVoList || dateRangeVoList.isEmpty()) {
            logger.info("getAppPlatShare failed...");
            //throw new ParamException("该平台没有分摊数据");
            return null;
        }

        FnPlatShareAppVo fnPlatShareAppVo = new FnPlatShareAppVo();

        //回传参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("id", platId);
        requestParam.put("name", platform.getName());
        requestParam.put("year", year);
        requestParam.put("month", month);
        fnPlatShareAppVo.setRequestParam(requestParam);

        fnPlatShareAppVo.setStatistics(new ArrayList<>());
        fnPlatShareAppVo.setShareDetailOfProjectList(new ArrayList<>());
        fnPlatShareAppVo.setFnPlatShareConfigOfUserVoList(new ArrayList<>());

        //设置起始结束日期
        BasePeriodAppVo baseInfo = new BasePeriodAppVo();
        Integer startYear, startMonth, endYear, endMonth;
        startYear = dateRangeVoList.get(0).getYear();
        startMonth = dateRangeVoList.get(0).getMonth();
        if (dateRangeVoList.size() > 1) {
            endYear = dateRangeVoList.get(1).getYear();
            endMonth = dateRangeVoList.get(1).getMonth();
        } else {
            endYear = startYear;
            endMonth = startMonth;
        }

        //判断传入的年月是否在有效的范围内
        if ((year > startYear || (year.equals(startYear) && month >= startMonth)) &&
                (year < endYear || (year.equals(endYear) && month <= endMonth))) {
            //在日期范围内，继续往下走
            /*
            DateRangeVo dateRangeVo = fnShareDataMapper.selectEndYearMonthOfPlat(platId);

            //传入的年月小于数据的结束年月，则日期结束年月以传入的为准
            if (dateRangeVo.getYear() < endYear || (dateRangeVo.getYear() == endYear && dateRangeVo.getMonth() < endMonth)) {
                endYear = dateRangeVo.getYear();
                endMonth = dateRangeVo.getMonth();
            }
            */
            if (endYear < year || (endYear.equals(year) && endMonth < month)) {
                year = endYear;
                month = endMonth;
            }

            baseInfo.setStartYear(startYear);
            baseInfo.setStartMonth(startMonth);
            baseInfo.setEndYear(endYear);
            baseInfo.setEndMonth(endMonth);
            fnPlatShareAppVo.setBaseInfo(baseInfo);
        } else {
            logger.info("该平台没有分摊数据");

            baseInfo.setStartYear(startYear);
            baseInfo.setStartMonth(startMonth);
            baseInfo.setEndYear(endYear);
            baseInfo.setEndMonth(endMonth);
            fnPlatShareAppVo.setBaseInfo(baseInfo);

            return fnPlatShareAppVo;
        }

        //读取分摊汇总信息
        List<FnSumShareConfigDto> fnSumShareConfigDtoList = fnSumShareConfigMapper.selectByYearAndMonthAndPlatId(platId, year, month);

        // 初始化按项目汇总的分摊明细(会跳过分摊给自己的)
        Long selfPlatId = -1L;
        if (PlatInShareProjectMap.containsKey(platId)) {
            selfPlatId = PlatInShareProjectMap.get(platId);
        }

        //计算分摊人数
        FnPlatShareUserCountListDto fnPlatShareUserCountListDto = getFnPlatShareUserCount(platId, selfPlatId, fnSumShareConfigDtoList, year, month);
        for (FnPlatShareUserCountDto fnPlatShareUserCountDto : fnPlatShareUserCountListDto.getFnPlatShareUserCountDtoList()) {
            FnPlatShareAppVo.ShareNumberOfYearMonth ShareNumberOfYearMonth = new FnPlatShareAppVo.ShareNumberOfYearMonth();
            ShareNumberOfYearMonth.setYear(fnPlatShareUserCountDto.getYear());
            ShareNumberOfYearMonth.setMonth(fnPlatShareUserCountDto.getMonth());
            ShareNumberOfYearMonth.setShareNumber(fnPlatShareUserCountDto.getShareNumber());
            ShareNumberOfYearMonth.setTotalNumber(fnPlatShareUserCountDto.getTotalNumber());
            fnPlatShareAppVo.getStatistics().add(ShareNumberOfYearMonth);
        }

        //建立各项目的分摊比例（三个月）的Map
        Map<Long, FnPlatShareAppVo.ShareDetailOfProject> shareDetailOfProjectMap = new HashMap<>();
        for (FnSumShareConfigDto fnSumShareConfigDto : fnSumShareConfigDtoList) {
            if (fnSumShareConfigDto.getProjectId().equals(selfPlatId)) {
                //每个平台建了一个名字跟平台一样的分摊项，表示分摊给自己的（没分摊出去的），这种的分摊比例不计
                continue;
            }
            if (!shareDetailOfProjectMap.containsKey(fnSumShareConfigDto.getProjectId())) {
                FnPlatShareAppVo.ShareDetailOfProject shareDetailOfProject = new FnPlatShareAppVo.ShareDetailOfProject();
                shareDetailOfProject.setProjectName(fnSumShareConfigDto.getProjectName());
                shareDetailOfProject.setTwoMonthAgoSharePro(0F);
                shareDetailOfProject.setLastMonthSharePro(0F);
                shareDetailOfProject.setCurrentMonthSharePro(0F);
                shareDetailOfProjectMap.put(fnSumShareConfigDto.getProjectId(), shareDetailOfProject);
            }
        }

        //计算分摊比例，分别计算三个月的各项目的分摊比例
        int currentYear;
        int currentMonth;
        Calendar currentDate = Calendar.getInstance();
        currentDate.set(year, month - 1, 1);
        //前两月
        currentDate.add(Calendar.MONTH, -2);
        currentYear = currentDate.get(Calendar.YEAR);
        currentMonth = currentDate.get(Calendar.MONTH) + 1;

        Boolean isContainSubHrProject = projectMapper.selectAllSubHrPlat(platId).size() > 0;
        getShareProOfProjectMap(isContainSubHrProject, fnPlatShareUserCountListDto, fnSumShareConfigDtoList, shareDetailOfProjectMap, currentYear, currentMonth, MonthType.twoMonthAgo);
        //上月
        currentDate.add(Calendar.MONTH, 1);
        currentYear = currentDate.get(Calendar.YEAR);
        currentMonth = currentDate.get(Calendar.MONTH) + 1;
        getShareProOfProjectMap(isContainSubHrProject, fnPlatShareUserCountListDto, fnSumShareConfigDtoList, shareDetailOfProjectMap, currentYear, currentMonth, MonthType.last);
        //当月
        getShareProOfProjectMap(isContainSubHrProject, fnPlatShareUserCountListDto, fnSumShareConfigDtoList, shareDetailOfProjectMap, year, month, MonthType.current);

        //去掉为0后按当月分摊比例降序排列
        List<FnPlatShareAppVo.ShareDetailOfProject> shareDetailOfProjectList = shareDetailOfProjectMap.values().stream().filter(shareDetailOfProject ->
                shareDetailOfProject.getTwoMonthAgoSharePro() != 0 ||
                        shareDetailOfProject.getLastMonthSharePro() != 0 ||
                        shareDetailOfProject.getCurrentMonthSharePro() != 0
        ).collect(Collectors.toList());
        shareDetailOfProjectList.sort((x, y) -> y.getCurrentMonthSharePro().compareTo(x.getCurrentMonthSharePro()));
        fnPlatShareAppVo.setShareDetailOfProjectList(shareDetailOfProjectList);

        // 获取按项目分组的人员分摊明细，并按ProjectId排序
        List<FnPlatShareConfigOfUserVo> fnPlatShareConfigOfUserVoList = new ArrayList<>();
        /*
        // 暂时不需要处理明细
        fnPlatShareConfigOfUserVoList = fnPlatShareConfigMapper.selectFnPlatShareConfigByUserByPlatIdAndYearAndMonth(platId, year, month);
        fnPlatShareConfigOfUserVoList.sort(Comparator.comparing(FnPlatShareConfigOfUserVo::getProjectId));
        */
        fnPlatShareAppVo.setFnPlatShareConfigOfUserVoList(fnPlatShareConfigOfUserVoList);

        return fnPlatShareAppVo;
    }

    private FnPlatShareUserCountListDto getFnPlatShareUserCount(Long platId, Long platIdOfSelf, List<FnSumShareConfigDto> fnSumShareConfigDtoList, Integer year, Integer month) {
        FnPlatShareUserCountListDto fnPlatShareUserCountListDto = new FnPlatShareUserCountListDto();
        fnPlatShareUserCountListDto.setFnPlatShareUserCountDtoList(new ArrayList<>());

        List<UserCountOfProjectDto> userCountOfProjectDtoList = hrReportService.getHrUserTransactionByProjectWithSubPlat(platId, year, month);
        //获取子分摊项分摊人数
        for (UserCountOfProjectDto userCountOfProjectDto : userCountOfProjectDtoList) {
            if (userCountOfProjectDto.getProjectId().equals(platId)) {
                //先计算子分摊项的人数
                continue;
            }

            for (UserCountOfProjectDto.UserCountByYearMonth userCountByYearMonth : userCountOfProjectDto.getUserCount()) {
                //获取人力总人数
                int totalNumber = userCountByYearMonth.getUserCount();

                FnPlatShareUserCountDto fnPlatShareUserCountDto = fnPlatShareUserCountListDto.getFnPlatShareUserCount(userCountByYearMonth.getYear(), userCountByYearMonth.getMonth());
                if (null == fnPlatShareUserCountDto) {
                    fnPlatShareUserCountDto = new FnPlatShareUserCountDto();
                    fnPlatShareUserCountDto.setYear(userCountByYearMonth.getYear());
                    fnPlatShareUserCountDto.setMonth(userCountByYearMonth.getMonth());
                    fnPlatShareUserCountDto.setUserCountOfProjectMap(new HashMap<>());

                    fnPlatShareUserCountListDto.getFnPlatShareUserCountDtoList().add(fnPlatShareUserCountDto);
                }

                //获取子分摊项分摊到父分摊项的分摊信息
                FnSumShareConfig fnSumShareConfig = fnSumShareConfigDtoList.stream().filter(item ->
                        item.getPlatId().equals(userCountOfProjectDto.getProjectId()) &&
                                item.getProjectId().equals(platIdOfSelf > 0 ? platIdOfSelf : platId) &&
                                item.getYear().equals(userCountByYearMonth.getYear()) &&
                                item.getMonth().equals(userCountByYearMonth.getMonth())
                ).findFirst().orElse(null);

                int shareNumberOfThisSubPlat = 0;
                if (null != fnSumShareConfig) {
                    //有分摊到父分摊项时，子分摊项的分摊人数按照公式人力总人数*（1-分摊到本平台比例）来算
                    shareNumberOfThisSubPlat = BigDecimal.valueOf(totalNumber).multiply((BigDecimal.ONE.subtract(fnSumShareConfig.getSharePro()))).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                } else {
                    List<FnSumShareConfig> fnSumShareConfigList = fnSumShareConfigDtoList.stream().filter(item -> {
                        return item.getPlatId().equals(userCountOfProjectDto.getProjectId()) && item.getYear().equals(userCountByYearMonth.getYear()) &&
                                item.getMonth().equals(userCountByYearMonth.getMonth());
                    }).collect(Collectors.toList());
                    if (fnSumShareConfigList.size() == 0) {
                        //没有分摊数据时不算分摊人数
                        continue;
                    }
                    //没有分摊到父分摊项时，子分摊项的分摊人数等于此分摊项平台人力总人数
                    shareNumberOfThisSubPlat = totalNumber;
                }

                fnPlatShareUserCountDto.getUserCountOfProjectMap().put(userCountOfProjectDto.getProjectId(), shareNumberOfThisSubPlat);
            }
        }

        //计算父分摊项分摊人数
        UserCountOfProjectDto userCountOfProjectDto = userCountOfProjectDtoList.stream().filter(item -> platId.equals(item.getProjectId())).findFirst().orElse(null);

        for (UserCountOfProjectDto.UserCountByYearMonth userCountByYearMonth : userCountOfProjectDto.getUserCount()) {
            int shareNumber = 0, totalNumber = 0;
            //取父分摊项人力人数
            totalNumber = userCountByYearMonth.getUserCount();

            FnPlatShareUserCountDto fnPlatShareUserCountDto = fnPlatShareUserCountListDto.getFnPlatShareUserCount(userCountByYearMonth.getYear(), userCountByYearMonth.getMonth());
            if (null == fnPlatShareUserCountDto) {
                fnPlatShareUserCountDto = new FnPlatShareUserCountDto();
                fnPlatShareUserCountDto.setYear(userCountByYearMonth.getYear());
                fnPlatShareUserCountDto.setMonth(userCountByYearMonth.getMonth());
                fnPlatShareUserCountDto.setUserCountOfProjectMap(new HashMap<>());

                fnPlatShareUserCountListDto.getFnPlatShareUserCountDtoList().add(fnPlatShareUserCountDto);
            }
            fnPlatShareUserCountDto.setTotalNumber(totalNumber);

            //取平台分摊给自己的分摊信息（美术中心会分摊给美术中心平台，技术中心没有分摊给技术中心平台的，只能用技术中心子分摊项分摊给技术中心来算）
            FnSumShareConfig fnSumShareConfig = fnSumShareConfigDtoList.stream().filter(item -> {
                return item.getPlatId().equals(platId) && item.getProjectId().equals(platIdOfSelf > 0 ? platIdOfSelf : platId) &&
                        item.getYear().equals(userCountByYearMonth.getYear()) && item.getMonth().equals(userCountByYearMonth.getMonth());
            }).findFirst().orElse(null);

            if (null != fnSumShareConfig) {
                //当有分摊给自己的比例时，按照人力总人数*（1-分摊到本平台比例）来算
                shareNumber = BigDecimal.valueOf(totalNumber).multiply((BigDecimal.ONE.subtract(fnSumShareConfig.getSharePro()))).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
            } else {
                //没有分摊给自己的比例时，按照每个子分摊项的分摊人数汇总
                HashMap<Long, Integer> userCountOfProjectMap = fnPlatShareUserCountDto.getUserCountOfProjectMap();
                if (userCountOfProjectMap.size() == 1) {
                    //没有子分摊项时，分摊人数就等于自己平台的人数
                    for (HashMap.Entry<Long, Integer> entry : userCountOfProjectMap.entrySet()) {
                        shareNumber += entry.getValue();
                    }
                } else {
                    for (Map.Entry<Long, Integer> entry : userCountOfProjectMap.entrySet()) {
                        if (!entry.getKey().equals(platId)) {
                            shareNumber += entry.getValue();
                        }
                    }
                }
            }

            fnPlatShareUserCountDto.setShareNumber(shareNumber);
        }

        return fnPlatShareUserCountListDto;
    }

    private void getShareProOfProjectMap(Boolean isContainSubHrProject, FnPlatShareUserCountListDto fnPlatShareUserCountListDto,
                                         List<FnSumShareConfigDto> fnSumShareConfigDtoList,
                                         Map<Long, FnPlatShareAppVo.ShareDetailOfProject> shareDetailMap,
                                         Integer year, Integer month, MonthType type) {
        FnPlatShareUserCountDto fnPlatShareUserCountDto = fnPlatShareUserCountListDto.getFnPlatShareUserCount(year, month);
        if (null == fnPlatShareUserCountDto) {
            return;
        }
        Integer shareNumber = fnPlatShareUserCountDto.getShareNumber();
        HashMap<Long, Integer> userCountOfProjectDtoList = fnPlatShareUserCountDto.getUserCountOfProjectMap();
        //按项目计算分摊比例汇总
        for (FnSumShareConfigDto fnSumShareConfigDto : fnSumShareConfigDtoList) {
            if (!year.equals(fnSumShareConfigDto.getYear()) || !month.equals(fnSumShareConfigDto.getMonth())) {
                continue;
            }

            //拿到分摊到的项目的Id
            Long projectIdOfSharedTo = fnSumShareConfigDto.getProjectId();

            if (shareDetailMap.containsKey(projectIdOfSharedTo)) {
                //去除掉分摊给自己平台的
                Integer shareNumberOfThisPlat = shareNumber;
                //获取[这个]分摊项(平台)指定月的总分摊人数
                for (HashMap.Entry<Long, Integer> entry : userCountOfProjectDtoList.entrySet()) {
                    if (fnSumShareConfigDto.getPlatId().equals(entry.getKey())) {
                        shareNumberOfThisPlat = userCountOfProjectDtoList.get(entry.getKey());
                        break;
                    }
                }

                FnPlatShareAppVo.ShareDetailOfProject shareDetailOfProject = shareDetailMap.get(projectIdOfSharedTo);
                switch (type) {
                    case twoMonthAgo:
                        Float valueTwoMonthAgo = shareDetailOfProject.getTwoMonthAgoSharePro();
                        if (!isContainSubHrProject) {
                            shareDetailOfProject.setTwoMonthAgoSharePro(fnSumShareConfigDto.getSharePro().floatValue());
                        } else {
                            shareDetailOfProject.setTwoMonthAgoSharePro((shareNumber > 0 ? fnSumShareConfigDto.getSharePro().floatValue() * shareNumberOfThisPlat / shareNumber : 0) + valueTwoMonthAgo);
                        }
                        break;
                    case last:
                        Float valueLast = shareDetailOfProject.getLastMonthSharePro();
                        if (!isContainSubHrProject) {
                            shareDetailOfProject.setLastMonthSharePro(fnSumShareConfigDto.getSharePro().floatValue());
                        } else {
                            shareDetailOfProject.setLastMonthSharePro((shareNumber > 0 ? fnSumShareConfigDto.getSharePro().floatValue() * shareNumberOfThisPlat / shareNumber : 0) + valueLast);
                        }
                        break;
                    case current:
                        Float valueCurrent = shareDetailOfProject.getCurrentMonthSharePro();
                        if (!isContainSubHrProject) {
                            shareDetailOfProject.setCurrentMonthSharePro(fnSumShareConfigDto.getSharePro().floatValue());
                        } else {
                            shareDetailOfProject.setCurrentMonthSharePro((shareNumber > 0 ? fnSumShareConfigDto.getSharePro().floatValue() * shareNumberOfThisPlat / shareNumber : 0) + valueCurrent);
                        }
                        break;
                    default:
                }
                shareDetailMap.put(projectIdOfSharedTo, shareDetailOfProject);
            }
        }
    }

    @Override
    public OrgProjectInfoAppVo getAppOrgProjectInfo(Long projectId) {
        if (null == projectId) {
            logger.info("getAppOrgProjectInfo failed...");
            throw new ParamException("projectId can not be null");
        }
        ProjectVo projectVo = projectMapper.selectWithOrderCodesStrAndUsedNamesStrByPrimaryKey(projectId);
        if (null == projectVo) {
            logger.info("getAppOrgProjectInfo failed...");
            throw new ParamException("projectId not found");
        }

        //项目负责人
        Map<String, OrgWorkGroupMemberAppVo> projectManagerMap = new HashMap<>();
        if (null != projectVo.getManagerIds() && !projectVo.getManagerIds().isEmpty()) {
            String[] userIds = projectVo.getManagerIds().split(",");
            List<OrgWorkGroupMemberAppVo> projectManagers = userMapper.selectAllUserWithNameAndLoginIdAndPostByIds(userIds);
            for (OrgWorkGroupMemberAppVo projectManager : projectManagers) {
                projectManager.setLeaderFlag(true);
                projectManagerMap.put(projectManager.getLoginId(), projectManager);
            }
        }

        List<OrgWorkGroupMemberAppVo> members = userMapper.selectAllEntityWithOrgWorkGroupSimpleByProjectId(projectId);
        List<WorkGroup> workGroups = workGroupService.getWorkGroupTreeByProjectId(projectId);

        OrgProjectInfoAppVo orgProjectInfoAppVo = new OrgProjectInfoAppVo();
        orgProjectInfoAppVo.setId(projectVo.getId());
        orgProjectInfoAppVo.setName(projectVo.getName());
        orgProjectInfoAppVo.setMemberNumber(members.size());

        orgProjectInfoAppVo.setMembers(new ArrayList<>());
        if (!projectManagerMap.isEmpty()) {
            orgProjectInfoAppVo.getMembers().addAll(projectManagerMap.values());
        }
        for (OrgWorkGroupMemberAppVo member : members) {
            if (!projectManagerMap.isEmpty() && projectManagerMap.containsKey(member.getLoginId())) {
                continue;
            }
            orgProjectInfoAppVo.getMembers().add(member);
        }

        FnShareTemplate fnShareTemplate = fnShareTemplateMapper.selectTemplateLast();
        if (null != fnShareTemplate) {
            //当分摊比例数据导出了的时候，认为分摊的数据才是最终的数据
            List<FnPlatShareMemberVo> fnPlatShareMemberVoList = fnPlatShareConfigMapper.selectMemberByProjectIdAndYearAndMonth(projectId, fnShareTemplate.getYear(), fnShareTemplate.getMonth());
            orgProjectInfoAppVo.setShareMembers(fnPlatShareMemberVoList);
            orgProjectInfoAppVo.setShareMemberCount(fnPlatShareMemberVoList.size());
        }

        List<OrgProjectInfoAppVo.Group> rootGroups = new ArrayList<>();
        for (WorkGroup workGroup : workGroups) {
            OrgProjectInfoAppVo.Group group = new OrgProjectInfoAppVo.Group();
            group.setId(workGroup.getId());
            group.setName(workGroup.getName());
            group.setMemberNumber(workGroup.getMemberNumber());
            rootGroups.add(group);
        }
        orgProjectInfoAppVo.setGroups(rootGroups);

        return orgProjectInfoAppVo;
    }

    @Override
    public OrgProjectWorkGroupInfoAppVo getAppOrgProjectWorkGroupInfo(Long projectId, Long groupId) {
        if (null == projectId || null == groupId) {
            logger.info("getAppOrgProjectWorkGroupInfo failed...");
            throw new ParamException("projectId or groupId can not be null");
        }

        List<OrgWorkGroupMemberAppVo> leaders = userMapper.selectAllGroupLeaderWithOrgWorkGroupSimpleByProjectId(projectId);
        List<OrgWorkGroupMemberAppVo> members = userMapper.selectAllEntityWithOrgWorkGroupSimpleByProjectId(projectId);
        WorkGroup workGroup = workGroupService.getWorkGroupTreeByProjectIdAndGroupId(projectId, groupId);

        OrgProjectWorkGroupInfoAppVo orgProjectWorkGroupInfoAppVo = new OrgProjectWorkGroupInfoAppVo();
        addMemberToWorkGroup(orgProjectWorkGroupInfoAppVo, workGroup, members, leaders);
        return orgProjectWorkGroupInfoAppVo;
    }

    private void addMemberToWorkGroup(OrgProjectWorkGroupInfoAppVo orgProjectWorkGroupInfoAppVo, WorkGroup workGroup,
                                      List<OrgWorkGroupMemberAppVo> members, List<OrgWorkGroupMemberAppVo> leaders) {
        orgProjectWorkGroupInfoAppVo.setId(workGroup.getId());
        orgProjectWorkGroupInfoAppVo.setName(workGroup.getName());
        orgProjectWorkGroupInfoAppVo.setMemberNumber(workGroup.getMemberNumber());
        if (null != workGroup.getChildren() && !workGroup.getChildren().isEmpty()) {
            List<OrgProjectWorkGroupInfoAppVo> children = new ArrayList<>();
            for (WorkGroup childGroup : workGroup.getChildren()) {
                OrgProjectWorkGroupInfoAppVo childOrgProjectWorkGroupInfoAppVo = new OrgProjectWorkGroupInfoAppVo();
                addMemberToWorkGroup(childOrgProjectWorkGroupInfoAppVo, childGroup, members, leaders);
                children.add(childOrgProjectWorkGroupInfoAppVo);
            }
            orgProjectWorkGroupInfoAppVo.setChildrens(children);
        }
        List<String> memberInGroupList = new ArrayList<>();
        for (OrgWorkGroupMemberAppVo member : leaders) {
            if (!workGroup.getId().equals(member.getWorkGroupId())) {
                continue;
            }
            if (memberInGroupList.contains(member.getLoginId())) {
                continue;
            }

            if (null == orgProjectWorkGroupInfoAppVo.getMembers()) {
                orgProjectWorkGroupInfoAppVo.setMembers(new ArrayList<>());
            }
            member.setLeaderFlag(true);
            orgProjectWorkGroupInfoAppVo.getMembers().add(member);
            memberInGroupList.add(member.getLoginId());
        }
        for (OrgWorkGroupMemberAppVo member : members) {
            if (!workGroup.getId().equals(member.getWorkGroupId())) {
                continue;
            }
            if (memberInGroupList.contains(member.getLoginId())) {
                continue;
            }

            if (null == orgProjectWorkGroupInfoAppVo.getMembers()) {
                orgProjectWorkGroupInfoAppVo.setMembers(new ArrayList<>());
            }
            orgProjectWorkGroupInfoAppVo.getMembers().add(member);
            memberInGroupList.add(member.getLoginId());
        }
    }

    @Override
    public List<FnSummaryProjectAppVo> getAppSummaryProjects() {
        PermissionProjectDto permissionProjectDto = PermissionHelper.getPermissionByFinance();

        Calendar today = Calendar.getInstance();
        FnProjectStatData fnProjectStatData = fnProjectStatDataMapper.selectByNew();
        if (null != fnProjectStatData) {
            today.set(fnProjectStatData.getYear(), fnProjectStatData.getMonth() - 1, 1);
        }
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH) + 1;

        Map<Long, FnSummaryProjectAppVo> fnSummaryProjectAppVoParentMap = new HashMap<>();
        Map<Long, FnSummaryProjectAppVo> fnSummaryProjectAppVoChildrenMap = new HashMap<>();
        List<FnSummaryProjectAppVo> fnSummaryProjectAppVos = projectMapper.selectAllAppSummaryProject(year, month);
        for (FnSummaryProjectAppVo fnSummaryProjectAppVo : fnSummaryProjectAppVos) {
            if (!permissionProjectDto.isAllPerm() && !permissionProjectDto.getPermProjectIds().contains(fnSummaryProjectAppVo.getId())) {
                continue;
            }

            if (null != fnSummaryProjectAppVo.getProfit()) {
                fnSummaryProjectAppVo.setProfit(ReportHelper.formatNumberByScale(fnSummaryProjectAppVo.getProfit(), 1));
            } else {
                fnSummaryProjectAppVo.setProfit(0F);
            }
            if (null != fnSummaryProjectAppVo.getTotalProfit()) {
                fnSummaryProjectAppVo.setTotalProfit(ReportHelper.formatNumberByScale(fnSummaryProjectAppVo.getTotalProfit(), 1));
            } else {
                fnSummaryProjectAppVo.setTotalProfit(0F);
            }

            if (null == fnSummaryProjectAppVo.getParentShareId()) {
                fnSummaryProjectAppVoParentMap.put(fnSummaryProjectAppVo.getId(), fnSummaryProjectAppVo);
            } else {
                fnSummaryProjectAppVoChildrenMap.put(fnSummaryProjectAppVo.getId(), fnSummaryProjectAppVo);
            }
        }

        for (FnSummaryProjectAppVo fnSummaryProjectAppVo : fnSummaryProjectAppVoChildrenMap.values()) {
            Long parentId = fnSummaryProjectAppVo.getParentShareId();
            if (fnSummaryProjectAppVoParentMap.containsKey(parentId)) {
                if (null == fnSummaryProjectAppVoParentMap.get(parentId).getChildren()) {
                    fnSummaryProjectAppVoParentMap.get(parentId).setChildren(new ArrayList<>());
                }
                fnSummaryProjectAppVoParentMap.get(parentId).getChildren().add(fnSummaryProjectAppVo);
            } else {
                fnSummaryProjectAppVoParentMap.put(fnSummaryProjectAppVo.getId(), fnSummaryProjectAppVo);
            }
        }

        List<FnSummaryProjectAppVo> result = fnSummaryProjectAppVoParentMap.values().stream().collect(Collectors.toList());
        result.sort((x, y) -> x.getId().compareTo(y.getId()));
        return result;
    }

    private void setFnProjectStatYoYVOListField(FnProjectStatYoYVO fnProjectStatYoYVO) {
        fnProjectStatYoYVO.getCostInfo().add(new ArrayList<>());
        fnProjectStatYoYVO.getProfitInfo().add(new ArrayList<>());
        fnProjectStatYoYVO.getIncomeInfo().add(new ArrayList<>());
        fnProjectStatYoYVO.getPerCapitaProfitInfo().add(new ArrayList<>());
    }

    @Override
    public FnProjectStatYoYVO getAppFnProjectStatYoY(Long id, Integer year) {
        FnProjectStatYoYVO result = new FnProjectStatYoYVO();
        result.setCostInfo(new ArrayList<>());
        result.setIncomeInfo(new ArrayList<>());
        result.setPerCapitaProfitInfo(new ArrayList<>());
        result.setProfitInfo(new ArrayList<>());

        FnProjectStatData lastData = fnProjectStatDataMapper.selectByNew();
        // 数据库最新年份财务比查询年份晚，默认切换到数据库最新年份
        if (lastData.getYear().compareTo(year) < 1) {
            year = lastData.getYear();
        }

        List<FnProjectStatData> fnProjectStatDataList = fnProjectStatDataMapper.selectTwoYearDataByProjectIdAndYear(id, year);
        if (!fnProjectStatDataList.isEmpty()) {
            Map<Integer, List<FnProjectStatData>> fnProjectStatDataByYearMap = fnProjectStatDataList.stream().collect(Collectors.groupingBy(x -> x.getYear()));
            // 排序
            List<Integer> years = new ArrayList<>(fnProjectStatDataByYearMap.keySet());
            Collections.sort(years);

            if (years.size() == 1) {
                // 只有一年，加个list结构
                setFnProjectStatYoYVOListField(result);
            }

            for (Integer i : years) {
                List<FnProjectInfoAppVo.PerCapitaProfitInfo> perCapitaProfitInfos = new ArrayList<>();
                List<FnProjectInfoAppVo.IncomeInfo> incomeInfos = new ArrayList<>();
                List<FnProjectInfoAppVo.CostInfo> costInfos = new ArrayList<>();
                List<FnProjectInfoAppVo.ProfitInfo> profitInfos = new ArrayList<>();

                List<FnProjectStatData> fnProjectStatData = fnProjectStatDataByYearMap.get(i);
                // 排序
                Map<Integer, List<FnProjectStatData>> fnProjectStatDataByMonthMap = fnProjectStatData.stream().collect(Collectors.groupingBy(x -> x.getMonth()));
                List<Integer> months = new ArrayList<>(fnProjectStatDataByMonthMap.keySet());
                Collections.sort(months);

                for (Integer month : months) {
                    if (month.equals(0)) {
                        continue;
                    }
                    FnProjectInfoAppVo.IncomeInfo incomeInfo = new FnProjectInfoAppVo.IncomeInfo();
                    FnProjectInfoAppVo.CostInfo costInfo = new FnProjectInfoAppVo.CostInfo();
                    FnProjectInfoAppVo.ProfitInfo profitInfo = new FnProjectInfoAppVo.ProfitInfo();
                    FnProjectInfoAppVo.PerCapitaProfitInfo perCapitaProfitInfo = new FnProjectInfoAppVo.PerCapitaProfitInfo();

                    incomeInfo.setYear(i);
                    incomeInfo.setMonth(month);
                    costInfo.setYear(i);
                    costInfo.setMonth(month);
                    profitInfo.setYear(i);
                    profitInfo.setMonth(month);
                    perCapitaProfitInfo.setYear(i);
                    perCapitaProfitInfo.setMonth(month);

                    float directCost = 0f, outsourcingCost = 0f, income = 0f, taxReturn = 0f, turnoverTax = 0f, afterTaxIncome = 0f, internalSettlementCost = 0f,
                            indirectCost = 0f, profit = 0f, profitability = 0f, perCapitaProfit = 0f;
                    ;
                    for (FnProjectStatData projectStatData : fnProjectStatDataByMonthMap.get(month)) {

                        if (projectStatData.getValue() == null) {
                            projectStatData.setValue(0f);
                        }

                        Float value = projectStatData.getValue();
                        // 收入
                        if (FnStat.id.income.equals(projectStatData.getFnStatId())) {
                            income = value;
                            continue;
                        }
                        if (FnStat.id.taxReturn.equals(projectStatData.getFnStatId())) {
                            taxReturn = value;
                            continue;
                        }
                        if (FnStat.id.internalSettlementCost.equals(projectStatData.getFnStatId())) {
                            internalSettlementCost = value;
                            continue;
                        }
                        if (FnStat.id.turnoverTax.equals(projectStatData.getFnStatId())) {
                            turnoverTax = value;
                            continue;
                        }
                        if (FnStat.id.afterTaxIncome.equals(projectStatData.getFnStatId())) {
                            afterTaxIncome = value;
                            continue;
                        }

                        // 费用
                        if (FnStat.id.directCost.equals(projectStatData.getFnStatId())) {
                            directCost = value;
                            continue;
                        }
                        if (FnStat.id.shareCost.equals(projectStatData.getFnStatId())) {
                            indirectCost = value;
                            continue;
                        }
                        if (FnStat.id.outsrouceCost.equals(projectStatData.getFnStatId())) {
                            outsourcingCost = value;
                            continue;
                        }

                        // 利润
                        if (FnStat.id.profit.equals(projectStatData.getFnStatId())) {
                            profit = value;
                            continue;
                        }
                        if (FnStat.id.profitability.equals(projectStatData.getFnStatId())) {
                            profitability = value;
                            continue;
                        }

                        // 人均利润
                        if (FnStat.id.perCapitaProfit.equals(projectStatData.getFnStatId())) {
                            perCapitaProfit = value;
                        }
                    }
                    incomeInfo.setIncome(ReportHelper.formatNumberByScale(income, 1));
                    incomeInfo.setTaxReturn(ReportHelper.formatNumberByScale(taxReturn, 1));
                    incomeInfo.setTurnoverTax(ReportHelper.formatNumberByScale(-turnoverTax, 1));
                    incomeInfo.setInternalSettlementCost(ReportHelper.formatNumberByScale(-internalSettlementCost, 1));
                    //  净收入，税后收入-内部结算成本
                    incomeInfo.setNetIncome(ReportHelper.formatNumberByScale((afterTaxIncome - internalSettlementCost), 1));
                    incomeInfo.setAfterTaxIncome(ReportHelper.formatNumberByScale(afterTaxIncome, 1));
                    incomeInfos.add(incomeInfo);
                    // 直接费用 需要减去 外包费用
                    costInfo.setDirectCost(ReportHelper.formatNumberByScale(directCost - outsourcingCost, 1));
                    // 间接费用 需要加上 外包费用
                    costInfo.setIndirectCost(ReportHelper.formatNumberByScale(indirectCost + outsourcingCost, 1));
                    costInfos.add(costInfo);

                    profitInfo.setProfit(ReportHelper.formatNumberByScale(profit, 1));
                    profitInfo.setProfitability(ReportHelper.formatNumberByScale(profitability * 100, 1));
                    profitInfos.add(profitInfo);

                    perCapitaProfitInfo.setPerCapitaProfit(ReportHelper.formatNumberByScale(perCapitaProfit, 1));
                    perCapitaProfitInfos.add(perCapitaProfitInfo);
                }
                result.getIncomeInfo().add(incomeInfos);
                result.getCostInfo().add(costInfos);
                result.getProfitInfo().add(profitInfos);
                result.getPerCapitaProfitInfo().add(perCapitaProfitInfos);
            }
        } else {
            // 无数据，给前端两个list结构
            setFnProjectStatYoYVOListField(result);
            setFnProjectStatYoYVOListField(result);
        }
        return result;
    }
}