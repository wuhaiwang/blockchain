package com.seasun.management.service.impl;

import com.seasun.management.constant.ErrorMessage;
import com.seasun.management.dto.PmPlanDetailDto;
import com.seasun.management.dto.PmPlanDto;
import com.seasun.management.dto.YearMonthDto;
import com.seasun.management.exception.ParamException;
import com.seasun.management.exception.TokenInvalidException;
import com.seasun.management.exception.UserInvalidOperateException;
import com.seasun.management.mapper.*;
import com.seasun.management.model.*;
import com.seasun.management.service.PmPlanService;
import com.seasun.management.util.MyCellUtils;
import com.seasun.management.util.MyDateUtils;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PmPlanServiceImpl implements PmPlanService {

    @Autowired
    private PmPlanMapper pmPlanMapper;

    @Autowired
    private FnProjectStatDataMapper fnProjectStatDataMapper;

    @Autowired
    private PmPlanDetailMapper pmPlanDetailMapper;

    @Autowired
    private PmAttachmentMapper pmAttachmentMapper;

    @Autowired
    private RUserProjectPermMapper rUserProjectPermMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private PmFinanceReportMapper pmFinanceReportMapper;

    @Autowired
    private PmMilestoneMapper pmMilestoneMapper;

    @Autowired
    private PmFinanceDetailMapper pmFinanceDetailMapper;

    public List<IdNameBaseObject> getPmPlanDetailManagers(String manager) {
        List<IdNameBaseObject> result = new ArrayList<>();
        if (manager != null) {
            String[] split = manager.split(",");
            for (String s : split) {
                String[] idNames = s.split("-");
                IdNameBaseObject idNameBaseObject = new IdNameBaseObject();
                idNameBaseObject.setId(Long.valueOf(idNames[0]));
                idNameBaseObject.setName(idNames[1]);
                result.add(idNameBaseObject);
            }
        }
        return result;
    }

    private PmPlanInfoVo.OperateAnalysis getOperatorAnalysis(PmFinanceReport report, Integer year, Integer month, Map<String, List<FnProjectStatData>> fnProjectStatDatasByYearAndMonthMap, Map<Long, List<PmAttachment>> pmAttachmentsByReportIdMap, Boolean pmFlag) {
        PmPlanInfoVo.OperateAnalysis result = new PmPlanInfoVo.OperateAnalysis();
        PmPlanInfoVo.OperateAnalysis.MoMAnalysis income = new PmPlanInfoVo.OperateAnalysis.MoMAnalysis();
        PmPlanInfoVo.OperateAnalysis.MoMAnalysis cost = new PmPlanInfoVo.OperateAnalysis.MoMAnalysis();
        PmPlanInfoVo.OperateAnalysis.MoMAnalysis profit = new PmPlanInfoVo.OperateAnalysis.MoMAnalysis();
        Boolean editFlag = true;
        result.setAttachments(new ArrayList<>());

        //判断是否有财务
        if (fnProjectStatDatasByYearAndMonthMap != null) {

            //获取上月财务详情
            YearMonthDto newYearMonth = MyDateUtils.yearMonthCalculation(year, month, -1);
            year = newYearMonth.getYear();
            month = newYearMonth.getMonth();
            String key = year + "_" + month;
            if (fnProjectStatDatasByYearAndMonthMap.containsKey(key)) {
                for (FnProjectStatData projectStatData : fnProjectStatDatasByYearAndMonthMap.get(key)) {
                    if (FnStat.id.income.equals(projectStatData.getFnStatId())) {
                        income.setLastMonthAgoValue(MyCellUtils.formatFloatNumber(projectStatData.getValue(), 1));
                        continue;
                    } else if (FnStat.id.cost.equals(projectStatData.getFnStatId())) {
                        cost.setLastMonthAgoValue(MyCellUtils.formatFloatNumber(projectStatData.getValue(), 1));
                        continue;
                    } else if (FnStat.id.profit.equals(projectStatData.getFnStatId())) {
                        profit.setLastMonthAgoValue(MyCellUtils.formatFloatNumber(projectStatData.getValue(), 1));
                        continue;
                    }
                }
            }

            //获取前两月财务详情
            YearMonthDto secondYearMonthDto1 = MyDateUtils.yearMonthCalculation(year, month, -1);
            year = secondYearMonthDto1.getYear();
            month = secondYearMonthDto1.getMonth();
            key = year + "_" + month;
            if (fnProjectStatDatasByYearAndMonthMap.containsKey(key)) {
                for (FnProjectStatData projectStatData : fnProjectStatDatasByYearAndMonthMap.get(key)) {
                    if (FnStat.id.income.equals(projectStatData.getFnStatId())) {
                        income.setTwoMonthAgoValue(MyCellUtils.formatFloatNumber(projectStatData.getValue(), 1));
                        if (income.getLastMonthAgoValue() != null && income.getTwoMonthAgoValue() != 0F) {
                            income.setMoM(MyCellUtils.formatFloatNumber(MyCellUtils.getMoM(income.getLastMonthAgoValue(), income.getTwoMonthAgoValue()), 1) + "%");
                        }
                        continue;
                    } else if (FnStat.id.cost.equals(projectStatData.getFnStatId())) {
                        cost.setTwoMonthAgoValue(MyCellUtils.formatFloatNumber(projectStatData.getValue(), 1));
                        if (cost.getLastMonthAgoValue() != null && cost.getTwoMonthAgoValue() != 0F) {
                            cost.setMoM(MyCellUtils.formatFloatNumber(MyCellUtils.getMoM(cost.getLastMonthAgoValue(), cost.getTwoMonthAgoValue()), 1) + "%");
                        }
                        continue;
                    } else if (FnStat.id.profit.equals(projectStatData.getFnStatId())) {
                        profit.setTwoMonthAgoValue(MyCellUtils.formatFloatNumber(projectStatData.getValue(), 1));
                        if (profit.getLastMonthAgoValue() != null && profit.getTwoMonthAgoValue() != 0F) {
                            profit.setMoM(MyCellUtils.formatFloatNumber(MyCellUtils.getMoM(profit.getLastMonthAgoValue(), profit.getTwoMonthAgoValue()), 1) + "%");
                        }
                        continue;
                    }
                }
            }

            // temp-fix: 之前减去过两次，这次再加上2，恢复到传入参数的年月状态。 todo: zhaijie
            YearMonthDto yearMonthDto = MyDateUtils.yearMonthCalculation(year, month, 2);
            year = yearMonthDto.getYear();
            month = yearMonthDto.getMonth();

            //获取去年的上一个月和今年财务详情  setLastYearMonthVaule getLastYearMonthVaule
            YearMonthDto lastYearMonthDto = MyDateUtils.yearMonthCalculation(year, month, -12);
            year = lastYearMonthDto.getYear();
            month = lastYearMonthDto.getMonth();

           // temp-fix: 年月key错乱，临时处理下。 todo: zhaijie
            if (month - 1 == 0) {
                key = (year - 1) + "_" + 12;
            } else {
                key = year + "_" + (month - 1);
            }

            if (fnProjectStatDatasByYearAndMonthMap.containsKey(key)) {
                for (FnProjectStatData projectStatData : fnProjectStatDatasByYearAndMonthMap.get(key)) {
                    if (FnStat.id.income.equals(projectStatData.getFnStatId())) {
                        income.setLastYearMonthVaule(MyCellUtils.formatFloatNumber(projectStatData.getValue(), 1));
                        if (income.getLastMonthAgoValue() != null && income.getLastYearMonthVaule() != 0F) {
                            income.setYoY(MyCellUtils.formatFloatNumber(MyCellUtils.getMoM(income.getLastMonthAgoValue(), income.getLastYearMonthVaule()), 1) + "%");
                        }
                        continue;
                    } else if (FnStat.id.cost.equals(projectStatData.getFnStatId())) {
                        cost.setLastYearMonthVaule(MyCellUtils.formatFloatNumber(projectStatData.getValue(), 1));
                        if (cost.getLastMonthAgoValue() != null && cost.getLastYearMonthVaule() != 0F) {
                            cost.setYoY(MyCellUtils.formatFloatNumber(MyCellUtils.getMoM(cost.getLastMonthAgoValue(), cost.getLastYearMonthVaule()), 1) + "%");
                        }
                        continue;
                    } else if (FnStat.id.profit.equals(projectStatData.getFnStatId())) {
                        profit.setLastYearMonthVaule(MyCellUtils.formatFloatNumber(projectStatData.getValue(), 1));
                        if (profit.getLastMonthAgoValue() != null && profit.getLastYearMonthVaule() != 0F) {
                            profit.setYoY(MyCellUtils.formatFloatNumber(MyCellUtils.getMoM(profit.getLastMonthAgoValue(), profit.getLastYearMonthVaule()), 1) + "%");
                        }
                        continue;
                    }
                }
            }
        }
        YearMonthDto lastYearMonthDto = MyDateUtils.yearMonthCalculation(year, month, 12);
        year = lastYearMonthDto.getYear();
        month = lastYearMonthDto.getMonth();


        //判断是否填写财务分析
        if (report != null) {
            if (pmAttachmentsByReportIdMap != null) {
                if (pmAttachmentsByReportIdMap.containsKey(report.getId())) {
                    result.getAttachments().addAll(pmAttachmentsByReportIdMap.get(report.getId()));
                }
            }

            //非初始化状态，非项管，不可编辑
            if (!PmFinanceReport.Status.INITIALIZED.equals(report.getStatus()) && !pmFlag) {
                editFlag = false;
            }

            result.setId(report.getId());
            cost.setFloatReason(report.getCostFloatReason());
            income.setFloatReason(report.getIncomeFloatReason());
            profit.setFloatReason(report.getProfitFloatReason());
        }

        result.setYear(year);
        result.setMonth(month);
        result.setProfit(profit);
        result.setIncome(income);
        result.setCost(cost);
        result.setEditFlag(editFlag);
        return result;

    }

    @Override
    public PmPlanInfoVo getPmPlanInfo(Long projectId) {
        PmPlanInfoVo result = new PmPlanInfoVo();
        result.setOperateAnalysis(new ArrayList<>(1));
        PmPlanInfoVo.ProjectEstimateDay projectEstimateDay = new PmPlanInfoVo.ProjectEstimateDay();
        PmPlanDto currentPmplanBranch = new PmPlanDto();
        StatusVo statusVo = null;
        Date lastPublishedTime = null;
        //项目状态 1:在研 2 运营
        int type = 2;
        //是否项管
        Boolean pmFlag = false;
        //是否所有项目的制作人助理
        Boolean allProjectAssistantFlag = false;
        Long currentUserId = MyTokenUtils.getCurrentUserId();

        List<Long> manageProjectIds = new ArrayList<>();
        //判断后台项目管理
        List<RUserProjectPermVo> pmManagerPerms = rUserProjectPermMapper.selectByUserIdAndProjectRoleId(currentUserId, ProjectRole.Role.pm_manager_id);
        if (pmManagerPerms.isEmpty()) {
            //判断项目制作人助理
            List<RUserProjectPermVo> pmAssistantPerms = rUserProjectPermMapper.selectByUserIdAndProjectRoleId(currentUserId, ProjectRole.Role.pm_assistant_id);
            for (RUserProjectPermVo rUserProjectPerm : pmAssistantPerms) {
                manageProjectIds.add(rUserProjectPerm.getProjectId());
                if (rUserProjectPerm.getProjectId() == null) {
                    allProjectAssistantFlag = true;
                    break;
                }
            }
            if (manageProjectIds.isEmpty()) {
                throw new TokenInvalidException("can not found token...");
            }
        } else {
            pmFlag = true;
        }

        //获取下拉框项目状态
        //财务最近一次导入时间
        Date projectFnLastCreateTime = fnProjectStatDataMapper.selectLastCreateTime();
        //获取所有在App显示的 在研/运营 项目的最新两个pm_plan版本.
        List<PmPlanDto> projectsLastTwoPmPlanDtos = pmPlanMapper.selectProjectsLastTwoPmplan();
        Map<Long, List<PmPlanDto>> pmPlanDtoByProjectIdMap = projectsLastTwoPmPlanDtos.stream().collect(Collectors.groupingBy(x -> x.getProjectId()));
        //项目下拉状态框，3个List用来排序
        List<StatusVo> publishedStatus = new ArrayList<>();
        List<StatusVo> submittedStatus = new ArrayList<>();
        List<StatusVo> initializedStatus = new ArrayList<>();
        Map<Long, PmPlanDto> branchByProjectIdMap = new HashMap<>();
        for (Long id : pmPlanDtoByProjectIdMap.keySet()) {
            if (!pmFlag && !allProjectAssistantFlag && !manageProjectIds.contains(id)) {
                continue;
            }
            List<PmPlanDto> pmPlanDtos = pmPlanDtoByProjectIdMap.get(id);
            int pmPlanSize = pmPlanDtos.size();
            PmPlanDto pmPlanDto = pmPlanDtos.get(0);
            StatusVo currentStatusVo = new StatusVo();
            currentStatusVo.setId(id);
            currentStatusVo.setName(pmPlanDto.getProjectName());
            String status = PmPlanInfoVo.Status.INITIALIZED;
            //如果pm_plan版本数量小于2，则存在第一次进入未创建版本或者创建初始版本后未发布
            if (pmPlanSize < 2) {
                branchByProjectIdMap.put(id, pmPlanDto);
                if (PmPlan.Status.SUBMITTED.equals(pmPlanDto.getStatus())) {
                    status = PmPlanInfoVo.Status.SUBMITTED;
                    currentStatusVo.setStatus(status);
                    submittedStatus.add(currentStatusVo);
                    continue;
                }
                currentStatusVo.setStatus(status);
                initializedStatus.add(currentStatusVo);
            } else {
                //如果pm_plan版本数量为2，默认一个版本为已发布，另一版本为未发布
                for (int i = 0; i < pmPlanDtos.size(); i++) {
                    PmPlanDto pmPlan = pmPlanDtos.get(i);
                    //判断是否项管
                    if (pmFlag) {

                        int anotherIndex = 1;
                        if (i == 1) {
                            anotherIndex = 0;
                        }

                        if (!PmPlan.Status.PUBLISHED.equals(pmPlan.getStatus())) {
                            //如果当前版本为未发布，并且另一版本发布时间在最新财报导入时间前，则选中当前版本
                            if (pmPlanDtos.get(anotherIndex).getUpdateTime().before(projectFnLastCreateTime)) {
                                if (PmPlan.Status.SUBMITTED.equals(pmPlan.getStatus())) {
                                    currentStatusVo.setStatus(PmPlanInfoVo.Status.SUBMITTED);
                                    submittedStatus.add(currentStatusVo);
                                } else {
                                    currentStatusVo.setStatus(PmPlanInfoVo.Status.INITIALIZED);
                                    initializedStatus.add(currentStatusVo);
                                }
                                branchByProjectIdMap.put(id, pmPlan);
                                break;
                            }
                        } else {
                            //如果当前版本为已发布，并且发布时间在最新财报导入时间后，则选中当前版本
                            if (pmPlan.getUpdateTime().after(projectFnLastCreateTime)) {
                                currentStatusVo.setStatus(PmPlanInfoVo.Status.PUBLISHED);
                                publishedStatus.add(currentStatusVo);
                                branchByProjectIdMap.put(id, pmPlan);
                                break;
                            }
                        }
                    } else {
                        //非项管，则默认选中未发布(最新)版本
                        if (!PmPlan.Status.PUBLISHED.equals(pmPlan.getStatus())) {
                            if (PmPlan.Status.SUBMITTED.equals(pmPlan.getStatus())) {
                                currentStatusVo.setStatus(PmPlanInfoVo.Status.SUBMITTED);
                                submittedStatus.add(currentStatusVo);
                            } else {
                                currentStatusVo.setStatus(PmPlanInfoVo.Status.INITIALIZED);
                                initializedStatus.add(currentStatusVo);
                            }
                            branchByProjectIdMap.put(id, pmPlan);
                            break;
                        }
                    }
                }
            }
        }

        publishedStatus.addAll(submittedStatus);
        publishedStatus.addAll(initializedStatus);


        //获取选中项目信息
        if (projectId == null) {
            statusVo = publishedStatus.get(0);
            projectId = statusVo.getId();
        } else {
            for (StatusVo status : publishedStatus) {
                if (status.getId().equals(projectId)) {
                    statusVo = status;
                }
            }
        }

        //拿到当前版本信息
        currentPmplanBranch = branchByProjectIdMap.get(projectId);

        //设置在研项目预估上线时间
        if (Project.Status.developing.equals(currentPmplanBranch.getProjectStatus())) {
            projectEstimateDay.setConfirmFlag(false);
            type = 1;
            if (currentPmplanBranch.getId() != null) {
                //如果pm_plan更新时间大于创建时间，则预估上线时间为已确认状态
                if (currentPmplanBranch.getUpdateTime().getTime() > currentPmplanBranch.getCreateTime().getTime()) {
                    projectEstimateDay.setConfirmFlag(true);
                }
                projectEstimateDay.setProjectEstimateDay(currentPmplanBranch.getProjectEstimateDay());
            }
        }

        //设置上次发布时间,如果版本数量大于1,则发布过。
        if (pmPlanDtoByProjectIdMap.get(projectId).size() > 1) {
            if (PmPlan.Status.PUBLISHED.equals(currentPmplanBranch.getStatus())) {
                lastPublishedTime = currentPmplanBranch.getUpdateTime();
            } else {
                lastPublishedTime = currentPmplanBranch.getCreateTime();
            }
        }

        //获取计划节点-根据当前所选pm-plan迭代
        List<PmPlanDetailDto> operatorPlans = pmPlanDetailMapper.selectPmPlanDetailDtoByPmPlanId(currentPmplanBranch.getId());
        //前端排序需求
        LinkedList<PmPlanDetailDto> pmPlanDetailDtos = new LinkedList<>();
        LinkedList<PmPlanDetailDto> doneDetails = new LinkedList<>();
        //按状态排序
        for (PmPlanDetailDto pmPlanDetail : operatorPlans) {
            if (pmPlanDetail.getCreateTime().getTime() > currentPmplanBranch.getCreateTime().getTime()) {
                pmPlanDetail.setNewPlanFlag(true);
            } else {
                pmPlanDetail.setNewPlanFlag(false);
            }
            pmPlanDetail.setManagers(getPmPlanDetailManagers(pmPlanDetail.getManagerName()));
            if (PmPlanDetail.ActualStatus.DONE.equals(pmPlanDetail.getActualStatus())) {
                doneDetails.addFirst(pmPlanDetail);
            } else if (PmPlanDetail.ActualStatus.UNDO.equals(pmPlanDetail.getActualStatus())) {
                pmPlanDetailDtos.addFirst(pmPlanDetail);
            } else if (PmPlanDetail.ActualStatus.DOING.equals(pmPlanDetail.getActualStatus())) {
                pmPlanDetailDtos.addLast(pmPlanDetail);
            } else {
                doneDetails.addLast(pmPlanDetail);
            }
        }
        pmPlanDetailDtos.addAll(doneDetails);

        // 设置当前版本所在的pmPlanId
        if (currentPmplanBranch != null) {
            result.setPmPlanId(currentPmplanBranch.getId());
        }

        //获取运营分析
        result.setOperateAnalysis(getProjectOperateAnalysises(projectId, pmFlag));

        result.setLastPublishedTime(lastPublishedTime);
        result.setProjectList(publishedStatus);
        result.setPmMilestone(projectEstimateDay);
        result.setOperatePlans(pmPlanDetailDtos);
        result.setProjectId(projectId);
        result.setType(type);
        result.setProjectName(statusVo.getName());
        result.setPmManagerFlag(pmFlag);
        result.setStatus(statusVo.getStatus());
        return result;
    }

    /**
     * 获取项目运营分析
     */
    private List<PmPlanInfoVo.OperateAnalysis> getProjectOperateAnalysises(Long projectId, Boolean pmFlag) {
        List<PmPlanInfoVo.OperateAnalysis> result = new ArrayList<>();
        List<PmFinanceReport> reports = pmFinanceReportMapper.selectByProjectId(projectId);
        if (reports.isEmpty()) {
            //如果运营分析为空(未填写过),则默认选中财报最新月份下一个月

            List<FnProjectStatData> lastFnProjectStatDatas = fnProjectStatDataMapper.selectByProjectId(projectId);

            if (!lastFnProjectStatDatas.isEmpty()) {
                Map<String, List<FnProjectStatData>> fnProjectStatDatasByYearAndMonthMap = lastFnProjectStatDatas.stream().collect(Collectors.groupingBy(x -> x.getYear() + "_" + x.getMonth()));

                int analysisYear = 0;
                int analysisMonth = 0;

                for (String key : fnProjectStatDatasByYearAndMonthMap.keySet()) {
                    int year = Integer.parseInt(key.substring(0, 4));
                    int month = Integer.parseInt(key.substring(5));
                    if (year < analysisMonth || month == 0) {
                        continue;
                    }

                    if (year > analysisYear || year == analysisYear && month > analysisMonth) {
                        analysisMonth = month;
                        analysisYear = year;
                    }
                }

                YearMonthDto operatorAnalysisYearAndMonthDto = MyDateUtils.yearMonthCalculation(analysisYear, analysisMonth, 1);
                result.add(getOperatorAnalysis(null, operatorAnalysisYearAndMonthDto.getYear(), operatorAnalysisYearAndMonthDto.getMonth(), fnProjectStatDatasByYearAndMonthMap, null, pmFlag));
            }
        } else {

            //拿到日报开始填写年月
            Integer reportStartYear = reports.get(0).getYear();
            Integer reportStartMonth = reports.get(0).getMonth();
            //获取项目财务查询条件开始年月
            YearMonthDto newYearMonth = MyDateUtils.yearMonthCalculation(reportStartYear, reportStartMonth, -2);
            Integer fnStartYear = newYearMonth.getYear();
            Integer fnStartMonth = newYearMonth.getMonth();
            //获取项目财务
            //  List<FnProjectStatData> fnProjectStatDatas = fnProjectStatDataMapper.selectByProjectIdAndStartTime(projectId, fnStartYear, fnStartMonth-12);
            List<FnProjectStatData> fnProjectStatDatas = fnProjectStatDataMapper.selectByProjectId(projectId);
            List<Long> reportIds = reports.stream().map(x -> x.getId()).collect(Collectors.toList());
            List<PmAttachment> attachments = pmAttachmentMapper.selectByPmFinanceReportIds(reportIds);

            Map<String, List<FnProjectStatData>> fnProjectStatDatasByYearAndMonthMap = fnProjectStatDatas.stream().collect(Collectors.groupingBy(x -> x.getYear() + "_" + x.getMonth()));
            Map<String, PmFinanceReport> reportsByYearAndMonthMap = reports.stream().collect(Collectors.toMap(x -> x.getYear() + "_" + x.getMonth(), x -> x));
            Map<Long, List<PmAttachment>> attachmentsByReportIdMap = attachments.stream().collect(Collectors.groupingBy(x -> x.getPmFinanceReportId()));

            YearMonthDto yearMonthDto = MyDateUtils.yearMonthCalculation(reportStartYear, reportStartMonth, -1);
            //遍历财务yearAndMonth
            for (String key : fnProjectStatDatasByYearAndMonthMap.keySet()) {
                Integer year = Integer.parseInt(key.substring(0, 4));
                Integer month = Integer.parseInt(key.substring(5));


                //如果当前财务信息年月早于运营分析开始填写2个月，continue
                if (year.equals(yearMonthDto.getYear()) && month < yearMonthDto.getMonth() || year < yearMonthDto.getYear() || month == 0) {
                    continue;
                }

                YearMonthDto secondYearMonth = MyDateUtils.yearMonthCalculation(year, month, 1);
                year = secondYearMonth.getYear();
                month = secondYearMonth.getMonth();

                if (reportsByYearAndMonthMap.containsKey(year + "_" + month)) {
                    PmFinanceReport report = reportsByYearAndMonthMap.get(year + "_" + month);
                    result.add(getOperatorAnalysis(report, report.getYear(), report.getMonth(), fnProjectStatDatasByYearAndMonthMap, attachmentsByReportIdMap, pmFlag));
                } else {
                    result.add(getOperatorAnalysis(null, year, month, fnProjectStatDatasByYearAndMonthMap, null, pmFlag));
                }
            }
        }

        // 按年月降序排序
        Collections.sort(result, new Comparator<PmPlanInfoVo.OperateAnalysis>() {
            @Override
            public int compare(PmPlanInfoVo.OperateAnalysis o1, PmPlanInfoVo.OperateAnalysis o2) {
                int i = o2.getYear() - o1.getYear();
                if (i == 0) {
                    return o2.getMonth() - o1.getMonth();
                }
                return i;
            }
        });

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PmPlanDetailDto insertPmPlanDetail(PmPlanDetailDto pmPlanDetailDto) {
        Long currentUserId = MyTokenUtils.getCurrentUserId();

        //判断是否项管
        List<RUserProjectPermVo> pmManagerPerms = rUserProjectPermMapper.selectByUserIdAndProjectRoleId(currentUserId, ProjectRole.Role.pm_manager_id);
        if (pmManagerPerms.isEmpty()) {
            boolean assistantFlag = false;
            //判断是否助理
            List<RUserProjectPermVo> pmAssistantPerms = rUserProjectPermMapper.selectByUserIdAndProjectRoleId(currentUserId, ProjectRole.Role.pm_assistant_id);
            for (RUserProjectPermVo pmAssistantPerm : pmAssistantPerms) {
                if (pmAssistantPerm.getProjectId().equals(pmPlanDetailDto.getProjectId())) {
                    assistantFlag = true;
                    break;
                }
            }

            if (!assistantFlag) {
                throw new UserInvalidOperateException(ErrorMessage.USER_STATE_PERMISSIONS_ERROR);
            }
        }

        long nowDate = System.currentTimeMillis();
        if (pmPlanDetailDto.getPmPlanId() == null) {
            PmPlan insert = new PmPlan();
            insert.setProjectId(pmPlanDetailDto.getProjectId());
            insert.setVersion(0);
            insert.setStatus(PmPlan.Status.INITIALIZED);
            insert.setCreateTime(new Date(nowDate));
            insert.setUpdateTime(new Date(nowDate));
            insert.setYear(Integer.parseInt(new SimpleDateFormat("yyyy").format(nowDate)));
            pmPlanMapper.insertSelective(insert);
            pmPlanDetailDto.setPmPlanId(insert.getId());
        }

        pmPlanDetailDto.setNewFlag(true);
        //为了给此计划节点增加新增标识，所以+1000ms
        pmPlanDetailDto.setCreateTime(new Date(nowDate + 1000));

        pmPlanDetailMapper.insertSelective(pmPlanDetailDto);
        return pmPlanDetailDto;
    }

    @Override
    public int updatePmPlanDetail(Long id, PmPlanDetail pmPlanDetail) {

        if (!PmPlanDetail.ActualStatus.CANCELLED.equals(pmPlanDetail.getActualStatus())) {
            pmPlanDetail.setCancelReason("");
        }
        pmPlanDetail.setUpdateTime(new Date());
        return pmPlanDetailMapper.updateByPrimaryKeySelective(pmPlanDetail);
    }

    @Override
    public int deletePmPlanDetailByPmPlanDetailId(Long id) {
        return pmPlanDetailMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int publishPmPlan(Long projectId) {

        Long currentUserId = MyTokenUtils.getCurrentUserId();
        //判断是否项管
        List<RUserProjectPermVo> pmManagerPerms = rUserProjectPermMapper.selectByUserIdAndProjectRoleId(currentUserId, ProjectRole.Role.pm_manager_id);
        if (pmManagerPerms.isEmpty()) {
            //助理，提交
            List<RUserProjectPermVo> pmAssistantPerms = rUserProjectPermMapper.selectByUserIdAndProjectRoleId(currentUserId, ProjectRole.Role.pm_assistant_id);
            boolean assistantFlag = false;
            for (RUserProjectPermVo pmAssistantPerm : pmAssistantPerms) {
                if (pmAssistantPerm.getProjectId().equals(projectId)) {
                    assistantFlag = true;
                    break;
                }
            }

            if (!assistantFlag) {
                throw new UserInvalidOperateException(ErrorMessage.USER_STATE_PERMISSIONS_ERROR);
            }

            // 校验，最新的数据分析的年月，对应的提交年月已存在未初始化的记录，则本次提交不允许
            Date date = fnProjectStatDataMapper.selectLastInsertDateByProjectId(projectId);
            if (date != null) {
                PmFinanceReport pmFinanceReport = pmFinanceReportMapper.selectLastByProjectId(projectId);
                if (pmFinanceReport == null) {
                    throw new UserInvalidOperateException("您有数据分析尚未填写，请填写后提交。");
                }
                if (PmFinanceReport.Status.PUBLISHED.equals(pmFinanceReport.getStatus()) && date.compareTo(pmFinanceReport.getCreateTime()) == -1) {
                    throw new UserInvalidOperateException("数据分析已发布，请等待最新财报导入后再提交。");
                }
            }

            pmPlanMapper.submitByProjectId(projectId, new Date());
            pmFinanceReportMapper.submitByProjectId(projectId, new Date());
            return 1;
        } else {
            //项管，发布
            Project project = projectMapper.selectByPrimaryKey(projectId);
            pmFinanceReportMapper.publishByProjectId(projectId, new Date());
            PmPlan pmPlan = pmPlanMapper.selectLastVersionByProjectId(projectId);

            if (pmPlan != null && !PmPlan.Status.PUBLISHED.equals(pmPlan.getStatus())) {
                List<PmPlanDetail> details = pmPlanDetailMapper.selectByPmPlanId(pmPlan.getId());

                Date nowDate = new Date();
                pmPlan.setUpdateTime(nowDate);
                int updateCount = pmPlanMapper.publishById(pmPlan.getId(), new Date());
                if (updateCount > 0) {
                    if (Project.Status.developing.equals(project.getStatus())) {
                        PmMilestone pmMilestone = pmMilestoneMapper.selectByProjectId(projectId);
                        if (pmMilestone != null) {
                            pmMilestoneMapper.updateProjectEstimateDayByProjectIdAndDate(projectId, pmPlan.getProjectEstimateDay());
                        } else {
                            pmMilestone = new PmMilestone();
                            pmMilestone.setPublishFlag(false);
                            pmMilestone.setUpdateTime(nowDate);
                            pmMilestone.setCreateTime(nowDate);
                            pmMilestone.setProjectId(projectId);
                            pmMilestone.setProjectEstimateDay(pmPlan.getProjectEstimateDay());
                            pmMilestone.setChannel(MyTokenUtils.Channel.pc);
                            pmMilestoneMapper.insertSelective(pmMilestone);
                        }
                    }

                    pmPlan.setCreateTime(nowDate);
                    pmPlan.setVersion(pmPlan.getVersion() + 1);
                    pmPlan.setStatus(PmPlan.Status.INITIALIZED);
                    pmPlan.setYear(Calendar.getInstance().get(Calendar.YEAR));
                    pmPlan.setId(null);
                    pmPlanMapper.insertSelective(pmPlan);
                    if (pmPlan.getId() != null) {
                        if (!details.isEmpty()) {
                            for (PmPlanDetail pmPlanDetail : details) {
                                if (!PmPlanDetail.ActualStatus.DONE.equals(pmPlanDetail.getActualStatus())) {
                                    pmPlanDetail.setNewFlag(true);
                                } else {
                                    pmPlanDetail.setNewFlag(false);
                                }
                                pmPlanDetail.setUpdateTime(null);
                                pmPlanDetail.setPmPlanId(pmPlan.getId());
                            }
                            pmPlanDetailMapper.batchInsert(details);
                        }
                        return 1;
                    }
                }
            }
        }

        return 0;
    }

    @Override
    public List<IdNameBaseObject> getAppPmPlanList() {
        return pmPlanDetailMapper.selectPublishedProjects();
    }

    @Override
    public PmPlanDetailAppVo getProjectPublishedPlanDetails(Long projectId) {
        PmPlanDetailAppVo result = projectMapper.selectPmPlanDetailAppVoByPrimaryKey(projectId);

        if (result == null) {
            throw new ParamException("没有此项目");
        }

        List<PmPlanDetailVo> pmPlanDetailVos = new ArrayList<>();
        //项目最后一次发布的版本计划
        List<PmPlanDetailDto> pmPlanDetails = pmPlanDetailMapper.selectPublishedDetailsByProjectId(projectId);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        Map<String, List<PmPlanDetailDto>> pmPlanDetailsByYearAndMonthMap = new LinkedHashMap<>();
        for (PmPlanDetailDto detail : pmPlanDetails) {
            String key = simpleDateFormat.format(detail.getStartTime());
            if (pmPlanDetailsByYearAndMonthMap.containsKey(key)) {
                pmPlanDetailsByYearAndMonthMap.get(key).add(detail);
            } else {
                List<PmPlanDetailDto> pmPlanDetailDtos = new ArrayList<>();
                pmPlanDetailDtos.add(detail);
                pmPlanDetailsByYearAndMonthMap.put(key, pmPlanDetailDtos);
            }
        }

        for (String time : pmPlanDetailsByYearAndMonthMap.keySet()) {
            PmPlanDetailVo detailVo = new PmPlanDetailVo();
            detailVo.setTime(time);
            detailVo.setPlans(pmPlanDetailsByYearAndMonthMap.get(time));
            pmPlanDetailVos.add(detailVo);
        }
        result.setPlanDetail(pmPlanDetailVos);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long confirmProjectEstimateDay(Long projectId, Long pmPlanId, Long projectEstimateDay) {
        Project project = projectMapper.selectByPrimaryKey(projectId);
        long nowDate = System.currentTimeMillis();
        if (project != null) {
            if (!Project.Status.developing.equals(project.getStatus())) {
                throw new ParamException("该项目为非在研项目,请重新选择...");
            }
        }
        if (pmPlanId != null) {
            PmPlan pmPlan = pmPlanMapper.selectByPrimaryKey(pmPlanId);
            pmPlan.setProjectEstimateDay(new Date(projectEstimateDay));
            pmPlan.setUpdateTime(new Date(nowDate));
            //如果确认的是已发布版本，则刷新pm_milestone表project_estimate_day字段
            if (PmPlan.Status.PUBLISHED.equals(pmPlan.getStatus())) {
                pmMilestoneMapper.updateProjectEstimateDayByProjectIdAndDate(projectId, new Date(projectEstimateDay));
            }
            pmPlanMapper.updateByPrimaryKeySelective(pmPlan);
            return pmPlanId;
        } else {
            PmPlan insert = new PmPlan();
            insert.setCreateTime(new Date(nowDate));
            insert.setUpdateTime(new Date(nowDate + 1000));
            insert.setProjectEstimateDay(new Date(projectEstimateDay));
            insert.setVersion(0);
            insert.setProjectId(projectId);
            insert.setStatus(PmPlan.Status.INITIALIZED);
            insert.setYear(Calendar.getInstance().get(Calendar.YEAR));
            pmPlanMapper.insertSelective(insert);
            return insert.getId();
        }
    }

    @Override
    public List<PmFinanceDetailVo> getPmFinanceDetail(Integer year, Integer month) {
        PmFinanceDetailVo pmFinanceDetailVo = new PmFinanceDetailVo();
        pmFinanceDetailVo.setYear(year);
        pmFinanceDetailVo.setMonth(month);
        pmFinanceDetailVo.setMonthProfitId(2200L);//当月利润 = 税后收入(2200) - 费用(3000)
        pmFinanceDetailVo.setTotalProfitId(3100L);//累计利润ID
        pmFinanceDetailVo.setIncomeId(2000L);//当月收入ID
        pmFinanceDetailVo.setCostId(3000L);//费用ID
        pmFinanceDetailVo.setInnerSettleId(2300L);//内部结算ID
        pmFinanceDetailVo.setRealProfitId(2200L);//实际利润 = 税后收入(2200) - 费用(3000) - 内部结算成本(2300)
        pmFinanceDetailVo.setMemberCountId(3500L);//人数ID
        pmFinanceDetailVo.setAverageProfitId(3600L);//人均利润ID
        List<PmFinanceDetailVo> list = pmPlanMapper.selectPmFinanceDetail(pmFinanceDetailVo);
        if (list != null) {
            BigDecimal income = null;//当月收入
            BigDecimal preIncome = null;//上个月收入
            BigDecimal incomeMoM = null;//收入环比
            for (PmFinanceDetailVo vo : list) {
                income = vo.getIncome();
                preIncome = vo.getPreIncome();
                if (preIncome != null && BigDecimal.ZERO.compareTo(preIncome) != 0 && income != null) {
                    incomeMoM = (income.subtract(preIncome)).divide(preIncome, 4, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal("100"));
                }
                if (incomeMoM != null) {
                    DecimalFormat df = new DecimalFormat("0.0");
                    vo.setIncomeMoM(df.format(incomeMoM) + "%");
                }
                vo.setMonthProfit(decimalFormat(vo.getMonthProfit()));//当前利润
                vo.setTotalProfit(decimalFormat(vo.getTotalProfit()));//累计利润
                vo.setIncome(decimalFormat(vo.getIncome()));//当月收入
                vo.setPreIncome(decimalFormat(vo.getPreIncome()));//上个月收入
                vo.setCost(decimalFormat(vo.getCost()));//费用
                vo.setInnerSettle(decimalFormat(vo.getInnerSettle()));//内部结算
                vo.setRealProfit(decimalFormat(vo.getRealProfit()));//实际利润
                vo.setMemberCount(vo.getMemberCount());//人数
                vo.setAverageProfit(decimalFormat(vo.getAverageProfit()));//人均利润
            }
        }
        return list;
    }

    private BigDecimal decimalFormat(BigDecimal value) {
        if (value != null) {
            DecimalFormat df = new DecimalFormat("0.0");
            return new BigDecimal(df.format(value));
        }
        return null;
    }

    public Long createFinanceRemark(PmFinanceDetailVo pmFinanceDetailVo) {
        PmFinanceDetail record = new PmFinanceDetail();
        record.setId(pmFinanceDetailVo.getId());
        record.setMonth(pmFinanceDetailVo.getMonth());
        record.setProjectId(pmFinanceDetailVo.getProjectId());
        record.setRemark(pmFinanceDetailVo.getRemark());
        record.setYear(pmFinanceDetailVo.getYear());
        Long id = null;
        int count = 0;
        if (record.getId() != null) {
            count = pmFinanceDetailMapper.updateByPrimaryKey(record);
        } else {
            count = pmFinanceDetailMapper.insert(record);
        }
        id = record.getId();
        return id;
    }

    public static Float subFloat(Float data) {
        return (float) (Math.round(data * 10f)) / 10f;
    }
}



