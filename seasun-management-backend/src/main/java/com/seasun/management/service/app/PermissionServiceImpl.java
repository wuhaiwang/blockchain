package com.seasun.management.service.app;

import com.seasun.management.dto.RMenuProjectRolePermDto;
import com.seasun.management.dto.RUserProjectPermDto;
import com.seasun.management.helper.PermissionHelper;
import com.seasun.management.helper.ReportHelper;
import com.seasun.management.mapper.*;
import com.seasun.management.model.FnProjectStatData;
import com.seasun.management.model.Menu;
import com.seasun.management.model.Project;
import com.seasun.management.model.User;
import com.seasun.management.util.MyListUtils;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.HomeInfoAppVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    RUserProjectPermMapper rUserProjectPermMapper;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    FnProjectStatDataMapper fnProjectStatDataMapper;

    @Autowired
    FnShareDataMapper fnShareDataMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    RMenuProjectRolePermMapper rMenuProjectRolePermMapper;

    @Autowired
    PerformanceWorkGroupMapper performanceWorkGroupMapper;

    @Override
    public HomeInfoAppVo getAppHomeInfo() {

        User user = MyTokenUtils.getCurrentUser();

        List<String> permList = getAppPermissionList("home");

        HomeInfoAppVo homeInfoAppVo = new HomeInfoAppVo();

        Integer newFnYear = null;
        Integer newFnMonth = null;
        FnProjectStatData fnProjectStatData = fnProjectStatDataMapper.selectByNew();
        if (null != fnProjectStatData) {
            newFnYear = fnProjectStatData.getYear();
            newFnMonth = fnProjectStatData.getMonth();
        }

        if (permList.contains(PermissionHelper.AppHomeInfoModuleName.project)) {
            HomeInfoAppVo.HomeProjectInfo homeProjectInfo = new HomeInfoAppVo.HomeProjectInfo();
            homeProjectInfo.setYear(newFnYear);
            homeProjectInfo.setMonth(newFnMonth);
            int runProjectNumber = projectMapper.selectAppProjectCount(Project.Status.operating);
            int developProjectNumber = projectMapper.selectAppProjectCount(Project.Status.developing);
            homeProjectInfo.setRunProjectNumber(runProjectNumber);
            homeProjectInfo.setDevelopProjectNumber(developProjectNumber);

            homeInfoAppVo.setProjectInfo(homeProjectInfo);
        }

        if (permList.contains(PermissionHelper.AppHomeInfoModuleName.platform)) {
            HomeInfoAppVo.HomePlatformInfo homePlatformInfo = new HomeInfoAppVo.HomePlatformInfo();
            homePlatformInfo.setYear(newFnYear);
            homePlatformInfo.setMonth(newFnMonth);
            int serveNumber = fnShareDataMapper.selectServeProjectCountByYearAndMonth(newFnYear, newFnMonth);
            int platformNumber = projectMapper.selectAppPlatCount();
            homePlatformInfo.setServeNumber(serveNumber);
            homePlatformInfo.setPlatformNumber(platformNumber);

            homeInfoAppVo.setPlatformInfo(homePlatformInfo);
        }

        if (permList.contains(PermissionHelper.AppHomeInfoModuleName.finance)) {
            HomeInfoAppVo.HomeFinanceInfo homeFinanceInfo = new HomeInfoAppVo.HomeFinanceInfo();
            homeFinanceInfo.setYear(newFnYear);
            homeFinanceInfo.setMonth(newFnMonth);
            Float profit = fnProjectStatDataMapper.selectSumProfitByYearAndMonth(newFnYear, newFnMonth);
            Float totalProfit = fnProjectStatDataMapper.selectSumProfitByYearAndMonth(newFnYear, null);
            homeFinanceInfo.setProfit(ReportHelper.formatNumberByScale(profit, 1));
            homeFinanceInfo.setTotalProfit(ReportHelper.formatNumberByScale(totalProfit, 1));

            homeInfoAppVo.setFinanceInfo(homeFinanceInfo);
        }

        Calendar today = Calendar.getInstance();
        Integer currentYear = today.get(Calendar.YEAR);
        Integer currentMonth = today.get(Calendar.MONTH) + 1;
        Calendar beginDate = Calendar.getInstance();
        beginDate.set(Calendar.DAY_OF_MONTH, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(Calendar.MONTH, 1);
        endDate.set(Calendar.DAY_OF_MONTH, -1);

        if (permList.contains(PermissionHelper.AppHomeInfoModuleName.hr)) {
            HomeInfoAppVo.HomeHrInfo homeHrInfo = new HomeInfoAppVo.HomeHrInfo();
            homeHrInfo.setYear(currentYear);
            homeHrInfo.setMonth(currentMonth);
            int totalPeople = userMapper.selectActiveEntityCount();
            int servingPeople = userMapper.selectCountByInDate(beginDate.getTime(), endDate.getTime());
            int leavingPeople = userMapper.selectCountByLeaveDate(beginDate.getTime(), endDate.getTime());
            homeHrInfo.setTotalPeople(totalPeople);
            homeHrInfo.setServingPeople(servingPeople);
            homeHrInfo.setLeavingPeople(leavingPeople);

            homeInfoAppVo.setHrInfo(homeHrInfo);
        }

        if (permList.contains(PermissionHelper.AppHomeInfoModuleName.outsourcing)) {
            HomeInfoAppVo.HomeOutsourceInfo homeOutsourceInfo = new HomeInfoAppVo.HomeOutsourceInfo();

            homeInfoAppVo.setOutsourceInfo(homeOutsourceInfo);
        }

        int count = performanceWorkGroupMapper.selectCountByManagerId(user.getId());
        HomeInfoAppVo.HomePerformanceInfo homePerformanceInfo = null;
        if (MyTokenUtils.isBoss(user) || null != user.getPerfWorkGroupId() || count > 0) {
            homePerformanceInfo = new HomeInfoAppVo.HomePerformanceInfo();
        }
        homeInfoAppVo.setPerformanceInfo(homePerformanceInfo);

        if (permList.contains(PermissionHelper.AppHomeInfoModuleName.salary)) {
            HomeInfoAppVo.HomeSalaryChangeInfo homeSalaryChangeInfo = new HomeInfoAppVo.HomeSalaryChangeInfo();

            homeInfoAppVo.setSalaryChangeInfo(homeSalaryChangeInfo);
        }
        if (permList.contains(PermissionHelper.AppHomeInfoModuleName.grade)) {
            HomeInfoAppVo.HomeGradeChangeInfo homeGradeChangeInfo = new HomeInfoAppVo.HomeGradeChangeInfo();

            homeInfoAppVo.setGradeChangeInfo(homeGradeChangeInfo);
        }
        if (permList.contains(PermissionHelper.AppHomeInfoModuleName.projectMaxMemberFlow)) {
            // todo: 临时判断hr身份
            if (MyTokenUtils.isBoss(user) || MyTokenUtils.isAppAdmin(user.getId()) || MyTokenUtils.isProjectManager(user.getId()) || (MyTokenUtils.isAppHr(user.getId()) && MyTokenUtils.isHrManager(user))) {
                HomeInfoAppVo.HomeProjectMaxMemberFlowInfo homeProjectMaxMemberFlowInfo = new HomeInfoAppVo.HomeProjectMaxMemberFlowInfo();
                homeInfoAppVo.setProjectMaxMemberFlowInfo(homeProjectMaxMemberFlowInfo);
            }
        }
        return homeInfoAppVo;
    }

    @Override
    public List<String> getAppPermissionList(String module) {
        List<String> permList = new ArrayList<>();
        Long userId = MyTokenUtils.getCurrentUserId();
        List<RUserProjectPermDto> rUserProjectPermVos = rUserProjectPermMapper.selectAppPermByUserId(userId);
        List<RMenuProjectRolePermDto> rMenuProjectRolePermDtos = rMenuProjectRolePermMapper.selectAllByUserId(Menu.Type.mobile, userId);
        Map<String, List<RMenuProjectRolePermDto>> menuPermMap = rMenuProjectRolePermDtos.stream().collect(Collectors.groupingBy(r -> r.getKey()));
        switch (module) {
            case "home":
                for (Map.Entry<String, List<RMenuProjectRolePermDto>> entry : menuPermMap.entrySet()) {
                    for (RUserProjectPermDto rUserProjectPermDto : rUserProjectPermVos) {
                        if (!rUserProjectPermDto.getProjectRoleId().equals(PermissionHelper.AppRole.APP_MANAGEMENT_ID) && PermissionHelper.AppHomeInfoModuleName.project.equals(entry.getKey()) &&
                                null != rUserProjectPermDto.getServiceLine() && Project.ServiceLine.plat.equals(rUserProjectPermDto.getServiceLine())) {
                            continue;
                        }
                        if (!rUserProjectPermDto.getProjectRoleId().equals(PermissionHelper.AppRole.APP_MANAGEMENT_ID) && PermissionHelper.AppHomeInfoModuleName.platform.equals(entry.getKey()) &&
                                null != rUserProjectPermDto.getServiceLine() && !Project.ServiceLine.plat.equals(rUserProjectPermDto.getServiceLine())) {
                            continue;
                        }

                        List<RMenuProjectRolePermDto> menuPremList = entry.getValue();
                        for (RMenuProjectRolePermDto rMenuProjectRolePermDto : menuPremList)
                            if (rMenuProjectRolePermDto.getProjectRoleId().equals(rUserProjectPermDto.getProjectRoleId())) {

                                // todo: 该逻辑也需要重构，应改为正向匹配（当前是反向匹配），便于理解。
                                // 权限是平台负责人，但平台是属性为“appShowMode=3,仅人力显示”，则不添加该权限。
                                if (PermissionHelper.AppHomeInfoModuleName.platform.equals(entry.getKey())
                                        && Project.AppShowMode.hr.equals(rUserProjectPermDto.getAppShowMode())){
                                    continue;
                                }
                                permList.add(entry.getKey());
                                break;
                            }
                    }
                }
                break;
            case PermissionHelper.AppHomeInfoModuleName.project:
                for (Map.Entry<String, List<RMenuProjectRolePermDto>> entry : menuPermMap.entrySet()) {
                    if (!"operating".equals(entry.getKey()) && !"developing".equals(entry.getKey()) && !"stage".equals(entry.getKey())) {
                        continue;
                    }
                    String status = "";
                    switch (entry.getKey()) {
                        case "operating":
                            status = "运营";
                            break;
                        case "developing":
                            status = "在研";
                            break;
//                        case "settingUp":
//                            status = "立项中";
//                            break;
                        default:
                            break;
                    }
                    for (RUserProjectPermDto rUserProjectPermDto : rUserProjectPermVos) {
                        if ((!rUserProjectPermDto.getProjectRoleId().equals(PermissionHelper.AppRole.APP_MANAGEMENT_ID) &&
                                !(null == rUserProjectPermDto.getProjectId() && rUserProjectPermDto.getProjectRoleId().equals(PermissionHelper.AppRole.APP_PROJECT_MANAGEMENT_ID)) &&
                                !rUserProjectPermDto.getProjectRoleId().equals(PermissionHelper.AppRole.APP_PESIDENT_OFFICE) &&
                                !rUserProjectPermDto.getProjectRoleId().equals(PermissionHelper.AppRole.APP_Plat_OB)) &&
                                (!status.equals(rUserProjectPermDto.getProjectStatus()) ||
                                        Project.ServiceLine.plat.equals(rUserProjectPermDto.getServiceLine()) ||
                                        Project.ServiceLine.summary.equals(rUserProjectPermDto.getServiceLine()) ||
                                        Project.ServiceLine.share.equals(rUserProjectPermDto.getServiceLine()))) {
                            continue;
                        }

                        List<RMenuProjectRolePermDto> menuPermList = entry.getValue();
                        for (RMenuProjectRolePermDto rMenuProjectRolePermDto : menuPermList) {
                            if (rMenuProjectRolePermDto.getProjectRoleId().equals(rUserProjectPermDto.getProjectRoleId())) {
                                permList.add(entry.getKey());
                                break;
                            }
                        }
                    }
                }
                break;
            case PermissionHelper.AppHomeInfoModuleName.platform:
                for (Map.Entry<String, List<RMenuProjectRolePermDto>> entry : menuPermMap.entrySet()) {
                    if (!"list".equals(entry.getKey()) && !"cost".equals(entry.getKey()) && !"organization".equals(entry.getKey()) && !"share".equals(entry.getKey())) {
                        continue;
                    }
                    for (RUserProjectPermDto rUserProjectPermDto : rUserProjectPermVos) {
                        if ((!rUserProjectPermDto.getProjectRoleId().equals(PermissionHelper.AppRole.APP_MANAGEMENT_ID) &&
                                !(null == rUserProjectPermDto.getProjectId() && rUserProjectPermDto.getProjectRoleId().equals(PermissionHelper.AppRole.APP_PROJECT_MANAGEMENT_ID))) &&
                                !Project.ServiceLine.plat.equals(rUserProjectPermDto.getServiceLine())) {
                            continue;
                        }

                        List<RMenuProjectRolePermDto> menuPermList = entry.getValue();
                        for (RMenuProjectRolePermDto rMenuProjectRolePermDto : menuPermList) {
                            if (rMenuProjectRolePermDto.getProjectRoleId().equals(rUserProjectPermDto.getProjectRoleId())) {
                                permList.add(entry.getKey());
                                break;
                            }
                        }
                    }

                }
                break;
            default:
                break;
        }

        // todo: 此处需要优化，当类似的汇总项越来越多时，权限这块需要重新设计。
        // 补充财务与看板的权限，西山居世游，西米等特殊汇总项需要独立的看板权限。目前只放开“西山居世游”
        if (!rUserProjectPermVos.isEmpty()) {
            for (RUserProjectPermDto projectPerm : rUserProjectPermVos) {
                if (PermissionHelper.AppRole.APP_PROJECT_MANAGEMENT_ID == projectPerm.getProjectRoleId().intValue()
                        && projectPerm.getProjectId() != null && projectPerm.getProjectId().equals(5163L)) { // 5163为西山居世游的projectId
                    permList.add(PermissionHelper.AppHomeInfoModuleName.finance);
                }
            }
        }
        return MyListUtils.removeDuplicate(permList);
    }
}
