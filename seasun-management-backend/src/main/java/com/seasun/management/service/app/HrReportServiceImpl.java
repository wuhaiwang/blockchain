package com.seasun.management.service.app;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.seasun.management.model.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.alibaba.fastjson.JSONObject;
import com.seasun.management.dto.HrWorkGroupDto;
import com.seasun.management.dto.OrderCenterWithParentHrIdDto;
import com.seasun.management.dto.UserCountOfProjectDto;
import com.seasun.management.dto.UserDto;
import com.seasun.management.dto.UserTransactionDto;
import com.seasun.management.exception.ParamException;
import com.seasun.management.helper.ReportHelper;
import com.seasun.management.mapper.OrderCenterMapper;
import com.seasun.management.mapper.ProjectMapper;
import com.seasun.management.mapper.UserMapper;
import com.seasun.management.mapper.UserTransferPostMapper;
import com.seasun.management.mapper.WorkGroupMapper;
import com.seasun.management.service.WorkGroupService;
import com.seasun.management.util.MyListUtils;
import com.seasun.management.vo.ContactsAppVo;
import com.seasun.management.vo.ContactsAppVo.Group;
import com.seasun.management.vo.HrOverviewAppVo;
import com.seasun.management.vo.HrPersonnelAnalysisAppVo;
import com.seasun.management.vo.HrUserTransactionAppVo;
import com.seasun.management.vo.OrgWorkGroupMemberAppVo;
import com.seasun.management.vo.ProjectVo;

@Service
public class HrReportServiceImpl implements HrReportService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HrReportServiceImpl.class);

    @Autowired
    UserMapper userMapper;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    OrderCenterMapper orderCenterMapper;

    @Autowired
    UserTransferPostMapper userTransferPostMapper;

    @Autowired
    WorkGroupMapper workGroupMapper;

    @Autowired
    WorkGroupService workGroupService;

    private UserTransactionDto getUserTransaction(List<UserDto> users, Calendar beginDate, Calendar endDate) {
        UserTransactionDto userTransactionDto = new UserTransactionDto();
        Long totalPeople = 0L, servingPeople = 0L, leavingPeople = 0L, totalIntern = 0L;
        for (UserDto user : users) {
            if (!user.getActiveFlag() && null == user.getLeaveDate()) {
                continue;
            }

            if ("实习".equals(user.getWorkStatus())) {
                totalIntern++;
                continue;
            }
            if (("离职".equals(user.getWorkStatus()) && ("实习生离职".equals(user.getLeaveType()) || "实习生返校".equals(user.getLeaveType())))) {
                continue;
            }

            if (null != user.getInDate() && ReportHelper.compareTwoDatesByGreaterOrEqual(beginDate.getTime(), user.getInDate()) && ReportHelper.compareTwoDatesByGreaterOrEqual(user.getInDate(), endDate.getTime())) {
                servingPeople++;
            }

            if (null != user.getLeaveDate() && ReportHelper.compareTwoDatesByGreaterOrEqual(beginDate.getTime(), user.getLeaveDate()) && ReportHelper.compareTwoDatesByGreaterOrEqual(user.getLeaveDate(), endDate.getTime())) {
                leavingPeople++;
            }

            if (null != user.getInDate() && ReportHelper.compareTwoDatesByGreater(endDate.getTime(), user.getInDate())) {
                continue;
            }
            if (!user.getActiveFlag() && null != user.getLeaveDate() && ReportHelper.compareTwoDatesByGreater(user.getLeaveDate(), beginDate.getTime())) {
                continue;
            }

            totalPeople++;
        }
        userTransactionDto.setTotalPeople(totalPeople - leavingPeople);
        userTransactionDto.setServingPeople(servingPeople);
        userTransactionDto.setLeavingPeople(leavingPeople);
        userTransactionDto.setTotalIntern(totalIntern);
        return userTransactionDto;
    }

    private List<UserDto> deepClonedUsers(List<UserDto> users) {
        List<UserDto> clonedAllUsers = new ArrayList<>();
        try {
            clonedAllUsers = (List<UserDto>) MyListUtils.deepClone(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clonedAllUsers;
    }

    private List<UserDto> clonedUsers(List<UserDto> users) {
        List<UserDto> clonedAllUsers = new ArrayList<>();
        for (UserDto userDto : users) {
            UserDto clonedUser = userDto.clone();
            clonedAllUsers.add(clonedUser);
        }
        return clonedAllUsers;
    }

    @Override
    public List<HrOverviewAppVo> getAppHrOverview() {
        Map<String, List<UserDto>> userLocationMap = new HashMap<>();
        List<UserDto> users = userMapper.selectAllEntityWidthDetail();
        userLocationMap.put("大西山居", users);
        for (UserDto user : users) {
            if (null == user.getLocation() || user.getLocation().isEmpty()) {
                continue;
            }

            String location = user.getLocation();
            if (!userLocationMap.containsKey(location)) {
                userLocationMap.put(location, new ArrayList<>());
            }
            userLocationMap.get(location).add(user);
        }

        List<HrOverviewAppVo> hrOverviewAppVos = getAppHrOverviewList(userLocationMap, null);

        return hrOverviewAppVos;
    }

    @Override
    public List<HrOverviewAppVo> getAppHrProject() {
        Map<String, List<UserDto>> userProjectMap = new HashMap<>();
        Map<String, ProjectVo> projectMap = new HashMap<>();

        List<UserDto> users = userMapper.selectAllEntityWidthDetail();
        List<ProjectVo> projectVos = projectMapper.selectAllWithOrderCodesStrAndUsedNamesStrByParent();
        for (ProjectVo projectVo : projectVos) {
            if (!projectVo.getActiveFlag() || !projectVo.getHrFlag()) {
                continue;
            }
            if (!Project.AppShowMode.all.equals(projectVo.getAppShowMode()) && !Project.AppShowMode.hr.equals(projectVo.getAppShowMode())) {
                continue;
            }
            if (null != projectVo.getId() &&
                    (Project.ServiceLine.plat.equals(projectVo.getServiceLine()) ||
                            Project.ServiceLine.summary.equals(projectVo.getServiceLine()) ||
                            Project.ServiceLine.share.equals(projectVo.getServiceLine()))) {
                continue;
            }
            if (null == projectVo.getOrderCenterCodesStr()) {
                continue;
            }

            String[] orderCenterCodes = projectVo.getOrderCenterCodesStr().split(",");
            userProjectMap.put(projectVo.getName(), users.stream().filter(userDto -> Arrays.asList(orderCenterCodes).contains(userDto.getOrderCenterCode())).collect(Collectors.toList()));
            projectMap.put(projectVo.getName(), projectVo);
        }

        List<HrOverviewAppVo> hrOverviewAppVos = getAppHrOverviewList(userProjectMap, projectMap);

        hrOverviewAppVos.sort(new Comparator<HrOverviewAppVo>() {
            @Override
            public int compare(HrOverviewAppVo o1, HrOverviewAppVo o2) {
                return Collator.getInstance(Locale.CHINA).compare(o1.getName(), o2.getName());
            }
        });

        // just for app bug hot fix 2017-11-8
        for (HrOverviewAppVo vo : hrOverviewAppVos) {
            if (vo.getWorkGroupId() == null) {
                vo.setWorkGroupId(267l);
            }
        }
        // just for app bug hot fix 2017-11-8

        return hrOverviewAppVos;
    }

    @Override
    public List<HrOverviewAppVo> getAppHrPlat() {
        Map<String, List<UserDto>> projectUsersMap = new HashMap<>();
        Map<String, ProjectVo> projectMap = new HashMap<>();

        List<UserDto> userDtos = userMapper.selectAllEntityWidthDetail();
        List<ProjectVo> projectVos = projectMapper.selectAllWithOrderCodesStrAndUsedNamesStrByParent();
        for (ProjectVo projectVo : projectVos) {
            if (!projectVo.getActiveFlag() || !projectVo.getHrFlag()) {
                continue;
            }
            if (!Project.AppShowMode.all.equals(projectVo.getAppShowMode()) && !Project.AppShowMode.hr.equals(projectVo.getAppShowMode())) {
                continue;
            }
            if (null != projectVo.getId() && !Project.ServiceLine.plat.equals(projectVo.getServiceLine())) {
                continue;
            }
            if (null == projectVo.getOrderCenterCodesStr()) {
                continue;
            }

            String[] orderCenterCodes = projectVo.getOrderCenterCodesStr().split(",");
            projectUsersMap.put(projectVo.getName(), userDtos.stream().filter(userDto -> Arrays.asList(orderCenterCodes).contains(userDto.getOrderCenterCode())).collect(Collectors.toList()));
            projectMap.put(projectVo.getName(), projectVo);
        }

        List<HrOverviewAppVo> hrOverviewAppVos = getAppHrOverviewList(projectUsersMap, projectMap);

        hrOverviewAppVos.sort(new Comparator<HrOverviewAppVo>() {
            @Override
            public int compare(HrOverviewAppVo o1, HrOverviewAppVo o2) {
                return Collator.getInstance(Locale.CHINA).compare(o1.getName(), o2.getName());
            }
        });

        // just for app bug hot fix 2017-11-8
        for (HrOverviewAppVo vo : hrOverviewAppVos) {
            if (vo.getWorkGroupId() == null) {
                vo.setWorkGroupId(267l);
            }
        }
        // just for app bug hot fix 2017-11-8

        return hrOverviewAppVos;
    }

    private List<HrOverviewAppVo> getAppHrOverviewList(Map<String, List<UserDto>> projectUsersMap, Map<String, ProjectVo> projectMap) {
        List<HrOverviewAppVo> hrOverviewAppVos = new ArrayList<>();

        Calendar today = ReportHelper.getDateOnly();
        Integer currentYear = today.get(Calendar.YEAR);
        Integer currentMonth = today.get(Calendar.MONTH) + 1;
        Calendar beginDate = ReportHelper.getDateOnly();
        beginDate.set(Calendar.DAY_OF_MONTH, 1);
        Calendar endDate = ReportHelper.getDateOnly();
        endDate.set(Calendar.DAY_OF_MONTH, 1);
        endDate.add(Calendar.MONTH, 1);
        endDate.add(Calendar.DAY_OF_MONTH, -1);

        for (Map.Entry<String, List<UserDto>> entry : projectUsersMap.entrySet()) {
            List<UserDto> projectUsers = entry.getValue();
            HrOverviewAppVo hrOverviewAppVo = new HrOverviewAppVo();
            hrOverviewAppVo.setYear(currentYear);
            hrOverviewAppVo.setMonth(currentMonth);
            if (null != projectMap && projectMap.containsKey(entry.getKey())) {
                ProjectVo project = projectMap.get(entry.getKey());
                hrOverviewAppVo.setId(project.getId());
                hrOverviewAppVo.setMaxMember(project.getMaxMember());
                hrOverviewAppVo.setWorkGroupId(project.getWorkGroupId());
                hrOverviewAppVo.setCity(project.getCity());
            }
            hrOverviewAppVo.setName(entry.getKey());

            UserTransactionDto userTransactionDto = getUserTransaction(projectUsers, beginDate, endDate);
            hrOverviewAppVo.setTotalPeople(userTransactionDto.getTotalPeople());
            hrOverviewAppVo.setServingPeople(userTransactionDto.getServingPeople());
            hrOverviewAppVo.setLeavingPeople(userTransactionDto.getLeavingPeople());
            hrOverviewAppVo.setTotalIntern(userTransactionDto.getTotalIntern());

            Float sumAge = 0F, averageAge = 0F;
            Integer agePeople = 0;
            Calendar birthday = ReportHelper.getDateOnly();
            for (UserDto user : projectUsers) {

                if ("实习".equals(user.getWorkStatus()) || ("离职".equals(user.getWorkStatus()) && ("实习生离职".equals(user.getLeaveType()) || "实习生返校".equals(user.getLeaveType())))) {
                    continue;
                }
                if (!user.getActiveFlag()) {
                    continue;
                }
                if (null == user.getBirthday()) {
                    continue;
                }
                birthday.setTime(user.getBirthday());
                if (birthday.get(Calendar.YEAR) < 1950) {
                    continue;
                }

                sumAge += ReportHelper.compareTwoDatesToDay(user.getBirthday(), today.getTime()) / 365;
                agePeople++;
            }
            if (agePeople > 0) {
                averageAge = sumAge / agePeople;
            }
            hrOverviewAppVo.setAverageAge(ReportHelper.formatNumberByScale(averageAge, 1));
            hrOverviewAppVos.add(hrOverviewAppVo);
        }
        hrOverviewAppVos.sort((x, y) -> {
            int i = 0;
            if (null != y.getCity()) {
                i = y.getCity().compareTo(x.getCity());
            }
            if (i == 0) {
                i = y.getTotalPeople().compareTo(x.getTotalPeople());
            }
            return i;
        });

        return hrOverviewAppVos;
    }

    @Override
    public HrUserTransactionAppVo getHrUserTransactionByLocation(String location, Integer year, Integer month) {
        HrUserTransactionAppVo hrUserTransactionAppVo = new HrUserTransactionAppVo();

        JSONObject requestParam = new JSONObject();
        requestParam.put("location", location);
        requestParam.put("year", year);
        requestParam.put("month", month);
        hrUserTransactionAppVo.setRequestParam(requestParam);

        hrUserTransactionAppVo.setStatistics(new ArrayList<>());

        HrUserTransactionAppVo.Statistics statistics;

        List<UserDto> users = userMapper.selectAllEntityWidthDetailWithoutTrainee();
        List<UserTransferPost> userTransferPosts = userTransferPostMapper.selectAll();


        Calendar beginDate = ReportHelper.getDateOnly();
        beginDate.set(year, month - 1, 1);
        Calendar endDate = ReportHelper.getDateOnly();
        endDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), 1);
        endDate.add(Calendar.MONTH, 1);
        endDate.add(Calendar.DAY_OF_MONTH, -1);

        statistics = getHrUserTransactionByLocation(location, beginDate, endDate, users, userTransferPosts, true);
        hrUserTransactionAppVo.getStatistics().add(statistics);

        beginDate.add(Calendar.MONTH, -1);
        endDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), 1);
        endDate.add(Calendar.MONTH, 1);
        endDate.add(Calendar.DAY_OF_MONTH, -1);

        statistics = getHrUserTransactionByLocation(location, beginDate, endDate, users, userTransferPosts, true);
        hrUserTransactionAppVo.getStatistics().add(statistics);

        beginDate.add(Calendar.MONTH, -1);
        endDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), 1);
        endDate.add(Calendar.MONTH, 1);
        endDate.add(Calendar.DAY_OF_MONTH, -1);

        statistics = getHrUserTransactionByLocation(location, beginDate, endDate, users, userTransferPosts, true);
        hrUserTransactionAppVo.getStatistics().add(statistics);

        hrUserTransactionAppVo.getStatistics().sort((x, y) -> {
            int i = x.getYear().compareTo(y.getYear());
            if (i == 0) {
                i = x.getMonth().compareTo(y.getMonth());
            }
            return i;
        });


        List<ProjectVo> projectVos = projectMapper.selectAllWithOrderCodesStrAndUsedNamesStrByParent();

        hrUserTransactionAppVo.setProject(getHrProjectUserTrend(users, userTransferPosts, projectVos, location, year, month, Project.ServiceLine.project));

        hrUserTransactionAppVo.setPlatform(getHrProjectUserTrend(users, userTransferPosts, projectVos, location, year, month, Project.ServiceLine.plat));

        return hrUserTransactionAppVo;
    }

    private HrUserTransactionAppVo.Statistics getHrUserTransactionByLocation(String location, Calendar beginDate, Calendar endDate,
                                                                             List<UserDto> users, List<UserTransferPost> userTransferPosts, boolean isNeedClone) {
        List<UserDto> resetUsers = resetUserInfo(users, userTransferPosts, endDate, isNeedClone);

        HrUserTransactionAppVo.Statistics statistics = new HrUserTransactionAppVo.Statistics();
        statistics.setYear(beginDate.get(Calendar.YEAR));
        statistics.setMonth(beginDate.get(Calendar.MONTH) + 1);

        resetUsers = resetUsers.stream().filter(u -> ("大西山居".equals(location) && null != u.getLocation()) || location.equals(u.getLocation())).collect(Collectors.toList());

        Long transInPeople = 0L, transOutPeople = 0L;

        Map<Long, UserTransferPost> transInMap = new HashMap<>();
        Map<Long, UserTransferPost> transOutMap = new HashMap<>();
        for (UserTransferPost userTransferPost : userTransferPosts) {
            if (!ReportHelper.compareTwoDatesByGreater(endDate.getTime(), userTransferPost.getTransferTime()) && !ReportHelper.compareTwoDatesByGreater(userTransferPost.getTransferTime(), beginDate.getTime())) {
                if (location.equals(userTransferPost.getNewWorkPlace()) && !userTransferPost.getNewWorkPlace().equals(userTransferPost.getPreWorkPlace())) {
                    transInMap.put(userTransferPost.getUserId(), userTransferPost);
                }
                if (location.equals(userTransferPost.getPreWorkPlace()) && !userTransferPost.getPreWorkPlace().equals(userTransferPost.getNewWorkPlace())) {
                    transOutMap.put(userTransferPost.getUserId(), userTransferPost);
                }
            }
        }

        for (UserDto user : users) {
            if (transInMap.containsKey(user.getId()) && user.getActiveFlag() && !("实习".equals(user.getWorkStatus()) || ("离职".equals(user.getWorkStatus()) && ("实习生离职".equals(user.getLeaveType()) || "实习生返校".equals(user.getLeaveType()))))) {
                UserTransferPost userTransferPost = transInMap.get(user.getId());
                Calendar transferTime = Calendar.getInstance();
                transferTime.setTime(userTransferPost.getTransferTime());
                Calendar inDate = Calendar.getInstance();
                inDate.setTime(user.getInDate());
                if (transferTime.get(Calendar.YEAR) != inDate.get(Calendar.YEAR) || transferTime.get(Calendar.MONTH) != inDate.get(Calendar.MONTH)) {
                    transInPeople++;
                }
            }
            if (transOutMap.containsKey(user.getId()) && user.getActiveFlag() && !("实习".equals(user.getWorkStatus()) || ("离职".equals(user.getWorkStatus()) && ("实习生离职".equals(user.getLeaveType()) || "实习生返校".equals(user.getLeaveType()))))) {
                UserTransferPost userTransferPost = transOutMap.get(user.getId());
                Calendar transferTime = Calendar.getInstance();
                transferTime.setTime(userTransferPost.getTransferTime());
                Calendar leaveDate = Calendar.getInstance();
                leaveDate.setTime(user.getLeaveDate());
                if (transferTime.get(Calendar.YEAR) != leaveDate.get(Calendar.YEAR) || transferTime.get(Calendar.MONTH) != leaveDate.get(Calendar.MONTH)) {
                    transOutPeople++;
                }
            }
        }

        UserTransactionDto userTransactionDto = getUserTransaction(resetUsers, beginDate, endDate);
        statistics.setTotalPeople(userTransactionDto.getTotalPeople());
        statistics.setServingPeople(userTransactionDto.getServingPeople());
        statistics.setLeavingPeople(userTransactionDto.getLeavingPeople());
        statistics.setTransInPeople(transInPeople);
        statistics.setTransOutPeople(transOutPeople);
        return statistics;
    }

    private List<HrUserTransactionAppVo.Detail> getHrProjectUserTrend(List<UserDto> users, List<UserTransferPost> userTransferPosts,
                                                                      List<ProjectVo> projectVos, String location, Integer year, Integer month, String serviceLine) {
        List<HrUserTransactionAppVo.Detail> details = new ArrayList<>();
        for (ProjectVo projectVo : projectVos) {
            if (!"大西山居".equals(location) && !location.equals(projectVo.getCity())) {
                continue;
            }
            if (!projectVo.getActiveFlag() || !projectVo.getHrFlag()) {
                continue;
            }
            if (!Project.AppShowMode.all.equals(projectVo.getAppShowMode()) && !Project.AppShowMode.hr.equals(projectVo.getAppShowMode())) {
                continue;
            }
            if (Project.ServiceLine.plat.equals(serviceLine)) {
                if (!Project.ServiceLine.plat.equals(projectVo.getServiceLine())) {
                    continue;
                }
            } else {
                if (Project.ServiceLine.plat.equals(projectVo.getServiceLine()) || Project.ServiceLine.summary.equals(projectVo.getServiceLine()) || Project.ServiceLine.share.equals(projectVo.getServiceLine())) {
                    continue;
                }
            }
            if (null == projectVo.getOrderCenterCodesStr()) {
                continue;
            }

            HrUserTransactionAppVo.Detail detail = new HrUserTransactionAppVo.Detail();
            detail.setName(projectVo.getName());

            Calendar beginDate = ReportHelper.getDateOnly();
            beginDate.set(year, month - 1, 1);
            Calendar endDate = ReportHelper.getDateOnly();
            endDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), 1);
            endDate.add(Calendar.MONTH, 1);
            endDate.add(Calendar.DAY_OF_MONTH, -1);

            detail.setCurrentMonthPeople(getTotalPeople(beginDate, endDate, projectVo, users, userTransferPosts, true));

            beginDate.add(Calendar.MONTH, -1);
            endDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), 1);
            endDate.add(Calendar.MONTH, 1);
            endDate.add(Calendar.DAY_OF_MONTH, -1);

            detail.setLastMonthPeople(getTotalPeople(beginDate, endDate, projectVo, users, userTransferPosts, true));

            beginDate.add(Calendar.MONTH, -1);
            endDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), 1);
            endDate.add(Calendar.MONTH, 1);
            endDate.add(Calendar.DAY_OF_MONTH, -1);

            detail.setTwoMonthAgoPeople(getTotalPeople(beginDate, endDate, projectVo, users, userTransferPosts, true));

            details.add(detail);
        }

        return details;
    }

    private Long getTotalPeople(Calendar beginDate, Calendar endDate, ProjectVo projectVo, List<UserDto> users, List<UserTransferPost> userTransferPosts, boolean isNeedCopy) {
        List<UserDto> resetUsers = resetUserInfo(users, userTransferPosts, endDate, isNeedCopy);
        String[] orderCenterCodes = projectVo.getOrderCenterCodesStr().split(",");
        resetUsers = resetUsers.stream().filter(userDto -> Arrays.asList(orderCenterCodes).contains(userDto.getOrderCenterCode())).collect(Collectors.toList());


        UserTransactionDto userTransactionDto = getUserTransaction(resetUsers, beginDate, endDate);
        return userTransactionDto.getTotalPeople();
    }

    @Override
    public HrUserTransactionAppVo getHrUserTransactionByProject(Long projectId, Integer year, Integer month) {
        Project project = projectMapper.selectByPrimaryKey(projectId);
        if (null == project) {
            logger.info("getHrUserTransactionByProject failed...");
            throw new ParamException("project not found");
        }

        HrUserTransactionAppVo hrUserTransactionAppVo = new HrUserTransactionAppVo();

        JSONObject requestParam = new JSONObject();
        requestParam.put("projectId", projectId);
        requestParam.put("name", project.getName());
        requestParam.put("year", year);
        requestParam.put("month", month);
        hrUserTransactionAppVo.setRequestParam(requestParam);

        hrUserTransactionAppVo.setStatistics(new ArrayList<>());

        HrUserTransactionAppVo.Statistics statistics;

        List<UserDto> users = userMapper.selectAllEntityWidthDetailWithoutTrainee();
        List<UserTransferPost> userTransferPosts = userTransferPostMapper.selectAll();
        List<OrderCenter> orderCenters = orderCenterMapper.selectByProject(projectId);
        List<String> orderCenterCodes = new ArrayList<>();
        List<Long> orderCenterIds = new ArrayList<>();

        for (OrderCenter orderCenter : orderCenters) {
            orderCenterCodes.add(orderCenter.getCode());
            orderCenterIds.add(orderCenter.getId());
        }

        Calendar beginDate = ReportHelper.getDateOnly();
        beginDate.set(year, month - 1, 1);
        Calendar endDate = ReportHelper.getDateOnly();
        endDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), 1);
        endDate.add(Calendar.MONTH, 1);
        endDate.add(Calendar.DAY_OF_MONTH, -1);

        statistics = getHrUserTransactionByProject(beginDate, endDate, users, userTransferPosts, orderCenterCodes, orderCenterIds, true);
        hrUserTransactionAppVo.getStatistics().add(statistics);

        beginDate.add(Calendar.MONTH, -1);
        endDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), 1);
        endDate.add(Calendar.MONTH, 1);
        endDate.add(Calendar.DAY_OF_MONTH, -1);

        statistics = getHrUserTransactionByProject(beginDate, endDate, users, userTransferPosts, orderCenterCodes, orderCenterIds, true);
        hrUserTransactionAppVo.getStatistics().add(statistics);

        beginDate.add(Calendar.MONTH, -1);
        endDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), 1);
        endDate.add(Calendar.MONTH, 1);
        endDate.add(Calendar.DAY_OF_MONTH, -1);

        statistics = getHrUserTransactionByProject(beginDate, endDate, users, userTransferPosts, orderCenterCodes, orderCenterIds, false);
        hrUserTransactionAppVo.getStatistics().add(statistics);

        hrUserTransactionAppVo.getStatistics().sort((x, y) -> {
            int i = x.getYear().compareTo(y.getYear());
            if (i == 0) {
                i = x.getMonth().compareTo(y.getMonth());
            }
            return i;
        });

        return hrUserTransactionAppVo;
    }

    @Override
    public List<UserCountOfProjectDto> getHrUserTransactionByProjectsWithSubPlat(List<Long> projectIds, Integer year, Integer month) {
        List<UserDto> users = userMapper.selectAllEntityWidthDetailWithoutTrainee();
        List<UserTransferPost> userTransferPosts = userTransferPostMapper.selectAll();
        List<OrderCenterWithParentHrIdDto> orderCenters = orderCenterMapper.selectByProjectIdsOrParentHrIds(projectIds);

        List<UserCountOfProjectDto> userCountOfProjectDtoList = new ArrayList<>();

        List<Project> projectList = projectMapper.selectByIdsOrParentHrIds(projectIds);
        for (Project project : projectList) {
            UserCountOfProjectDto userCountOfProjectDto = new UserCountOfProjectDto();
            userCountOfProjectDto.setProjectId(project.getId());
            userCountOfProjectDto.setUserCount(new ArrayList<>());

            List<String> orderCenterCodes = new ArrayList<>();
            List<Long> orderCenterIds = new ArrayList<>();

            for (OrderCenterWithParentHrIdDto orderCenter : orderCenters) {
                if (project.getId().equals(orderCenter.getProjectId()) || project.getId().equals(orderCenter.getParentHrId())) {
                    //如果是子分摊项，则只会拿到子分摊项的订单；如果是父分摊项，则会拿到父自己的以及所有子分摊项的
                    orderCenterCodes.add(orderCenter.getCode());
                    orderCenterIds.add(orderCenter.getId());
                }
            }

            Calendar beginDate = ReportHelper.getDateOnly();
            beginDate.set(year, month - 1, 1);
            Calendar endDate = ReportHelper.getDateOnly();
            endDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), 1);
            endDate.add(Calendar.MONTH, 1);
            endDate.add(Calendar.DAY_OF_MONTH, -1);

            HrUserTransactionAppVo.Statistics statistics = getHrUserTransactionByProject(beginDate, endDate, users, userTransferPosts, orderCenterCodes, orderCenterIds, true);
            UserCountOfProjectDto.UserCountByYearMonth userCountByYearMonth = new UserCountOfProjectDto.UserCountByYearMonth();
            userCountByYearMonth.setYear(statistics.getYear());
            userCountByYearMonth.setMonth(statistics.getMonth());
            userCountByYearMonth.setUserCount(statistics.getTotalPeople().intValue());
            userCountOfProjectDto.getUserCount().add(userCountByYearMonth);

            beginDate.add(Calendar.MONTH, -1);
            endDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), 1);
            endDate.add(Calendar.MONTH, 1);
            endDate.add(Calendar.DAY_OF_MONTH, -1);

            statistics = getHrUserTransactionByProject(beginDate, endDate, users, userTransferPosts, orderCenterCodes, orderCenterIds, true);
            userCountByYearMonth = new UserCountOfProjectDto.UserCountByYearMonth();
            userCountByYearMonth.setYear(statistics.getYear());
            userCountByYearMonth.setMonth(statistics.getMonth());
            userCountByYearMonth.setUserCount(statistics.getTotalPeople().intValue());
            userCountOfProjectDto.getUserCount().add(userCountByYearMonth);

            beginDate.add(Calendar.MONTH, -1);
            endDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), 1);
            endDate.add(Calendar.MONTH, 1);
            endDate.add(Calendar.DAY_OF_MONTH, -1);

            statistics = getHrUserTransactionByProject(beginDate, endDate, users, userTransferPosts, orderCenterCodes, orderCenterIds, true);
            userCountByYearMonth = new UserCountOfProjectDto.UserCountByYearMonth();
            userCountByYearMonth.setYear(statistics.getYear());
            userCountByYearMonth.setMonth(statistics.getMonth());
            userCountByYearMonth.setUserCount(statistics.getTotalPeople().intValue());
            userCountOfProjectDto.getUserCount().add(userCountByYearMonth);

            userCountOfProjectDto.getUserCount().sort((x, y) -> {
                int i = x.getYear().compareTo(y.getYear());
                if (i == 0) {
                    i = x.getMonth().compareTo(y.getMonth());
                }
                return i;
            });

            userCountOfProjectDtoList.add(userCountOfProjectDto);
        }

        return userCountOfProjectDtoList;
    }

    @Override
    public List<UserCountOfProjectDto> getHrUserTransactionByProjectWithSubPlat(Long projectId, Integer year, Integer month) {
        List<UserDto> users = userMapper.selectAllEntityWidthDetailWithoutTrainee();
        List<UserTransferPost> userTransferPosts = userTransferPostMapper.selectAll();
        List<OrderCenterWithParentHrIdDto> orderCenters = orderCenterMapper.selectOrderCenterDtoByProjectId(projectId);

        List<UserCountOfProjectDto> userCountOfProjectDtoList = new ArrayList<>();

        List<Project> projectList = projectMapper.selectAllSubHrPlatWithParent(projectId);
        for (Project project : projectList) {
            UserCountOfProjectDto userCountOfProjectDto = new UserCountOfProjectDto();
            userCountOfProjectDto.setProjectId(project.getId());
            userCountOfProjectDto.setUserCount(new ArrayList<>());

            List<String> orderCenterCodes = new ArrayList<>();
            List<Long> orderCenterIds = new ArrayList<>();

            for (OrderCenterWithParentHrIdDto orderCenter : orderCenters) {
                if (project.getId().equals(orderCenter.getProjectId()) || project.getId().equals(orderCenter.getParentHrId())) {
                    //如果是子分摊项，则只会拿到子分摊项的订单；如果是父分摊项，则会拿到父自己的以及所有子分摊项的
                    orderCenterCodes.add(orderCenter.getCode());
                    orderCenterIds.add(orderCenter.getId());
                }
            }

            Calendar beginDate = ReportHelper.getDateOnly();
            beginDate.set(year, month - 1, 1);
            Calendar endDate = ReportHelper.getDateOnly();
            endDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), 1);
            endDate.add(Calendar.MONTH, 1);
            endDate.add(Calendar.DAY_OF_MONTH, -1);

            HrUserTransactionAppVo.Statistics statistics = getHrUserTransactionByProject(beginDate, endDate, users, userTransferPosts, orderCenterCodes, orderCenterIds, true);
            UserCountOfProjectDto.UserCountByYearMonth userCountByYearMonth = new UserCountOfProjectDto.UserCountByYearMonth();
            userCountByYearMonth.setYear(statistics.getYear());
            userCountByYearMonth.setMonth(statistics.getMonth());
            userCountByYearMonth.setUserCount(statistics.getTotalPeople().intValue());
            userCountOfProjectDto.getUserCount().add(userCountByYearMonth);

            beginDate.add(Calendar.MONTH, -1);
            endDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), 1);
            endDate.add(Calendar.MONTH, 1);
            endDate.add(Calendar.DAY_OF_MONTH, -1);

            statistics = getHrUserTransactionByProject(beginDate, endDate, users, userTransferPosts, orderCenterCodes, orderCenterIds, true);
            userCountByYearMonth = new UserCountOfProjectDto.UserCountByYearMonth();
            userCountByYearMonth.setYear(statistics.getYear());
            userCountByYearMonth.setMonth(statistics.getMonth());
            userCountByYearMonth.setUserCount(statistics.getTotalPeople().intValue());
            userCountOfProjectDto.getUserCount().add(userCountByYearMonth);

            beginDate.add(Calendar.MONTH, -1);
            endDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), 1);
            endDate.add(Calendar.MONTH, 1);
            endDate.add(Calendar.DAY_OF_MONTH, -1);

            statistics = getHrUserTransactionByProject(beginDate, endDate, users, userTransferPosts, orderCenterCodes, orderCenterIds, true);
            userCountByYearMonth = new UserCountOfProjectDto.UserCountByYearMonth();
            userCountByYearMonth.setYear(statistics.getYear());
            userCountByYearMonth.setMonth(statistics.getMonth());
            userCountByYearMonth.setUserCount(statistics.getTotalPeople().intValue());
            userCountOfProjectDto.getUserCount().add(userCountByYearMonth);

            userCountOfProjectDto.getUserCount().sort((x, y) -> {
                int i = x.getYear().compareTo(y.getYear());
                if (i == 0) {
                    i = x.getMonth().compareTo(y.getMonth());
                }
                return i;
            });

            userCountOfProjectDtoList.add(userCountOfProjectDto);
        }

        return userCountOfProjectDtoList;
    }

    private HrUserTransactionAppVo.Statistics getHrUserTransactionByProject(Calendar beginDate, Calendar endDate, List<UserDto> users, List<UserTransferPost> userTransferPosts, List<String> orderCenterCodes, List<Long> orderCenterIds, boolean isNeedClone) {
        List<UserDto> resetUsers = resetUserInfo(users, userTransferPosts, endDate, isNeedClone);

        HrUserTransactionAppVo.Statistics statistics = new HrUserTransactionAppVo.Statistics();
        statistics.setYear(beginDate.get(Calendar.YEAR));
        statistics.setMonth(beginDate.get(Calendar.MONTH) + 1);

        resetUsers = resetUsers.stream().filter(u -> orderCenterCodes.contains(u.getOrderCenterCode())).collect(Collectors.toList());
        UserTransactionDto userTransactionDto = getUserTransaction(resetUsers, beginDate, endDate);

        Long transInPeople = 0L, transOutPeople = 0L;

        List<Long> transInUserId = new ArrayList<>();
        List<Long> transOutUserId = new ArrayList<>();
        for (UserTransferPost userTransferPost : userTransferPosts) {
            if (!ReportHelper.compareTwoDatesByGreater(endDate.getTime(), userTransferPost.getTransferTime()) && !ReportHelper.compareTwoDatesByGreater(userTransferPost.getTransferTime(), beginDate.getTime())) {
                if (orderCenterIds.contains(userTransferPost.getNewOrderCenterId())) {
                    transInUserId.add(userTransferPost.getUserId());
                }
                if (orderCenterIds.contains(userTransferPost.getPreOrderCenterId())) {
                    transOutUserId.add(userTransferPost.getUserId());
                }
            }
        }

        for (UserDto user : users) {
            if (transInUserId.contains(user.getId()) && !("实习".equals(user.getWorkStatus()) || ("离职".equals(user.getWorkStatus()) && ("实习生离职".equals(user.getLeaveType()) || "实习生返校".equals(user.getLeaveType()))))) {
                transInPeople++;
            }
            if (transOutUserId.contains(user.getId()) && !("实习".equals(user.getWorkStatus()) || ("离职".equals(user.getWorkStatus()) && ("实习生离职".equals(user.getLeaveType()) || "实习生返校".equals(user.getLeaveType()))))) {
                transOutPeople++;
            }
        }

        statistics.setTotalPeople(userTransactionDto.getTotalPeople());
        statistics.setServingPeople(userTransactionDto.getServingPeople());
        statistics.setLeavingPeople(userTransactionDto.getLeavingPeople());
        statistics.setTransInPeople(transInPeople);
        statistics.setTransOutPeople(transOutPeople);
        return statistics;
    }


    @Override
    public HrPersonnelAnalysisAppVo getHrPersonnelAnalysisByLocation(String location, Integer year, Integer month) {
        HrPersonnelAnalysisAppVo hrPersonnelAnalysisAppVo = new HrPersonnelAnalysisAppVo();

        JSONObject requestParam = new JSONObject();
        requestParam.put("location", location);
        requestParam.put("year", year);
        requestParam.put("month", month);
        hrPersonnelAnalysisAppVo.setRequestParam(requestParam);

        List<UserDto> users = userMapper.selectAllEntityWidthDetailWithoutTrainee();
        List<UserTransferPost> userTransferPosts = userTransferPostMapper.selectAll();

        hrPersonnelAnalysisAppVo.setAge(getPersonnelAnalysisByAgeAndLocation(location, year, month, users, userTransferPosts, true));
        hrPersonnelAnalysisAppVo.setCompanyAge(getPersonnelAnalysisByCompanyAgeAndLocation(location, year, month, users, userTransferPosts, true));
        hrPersonnelAnalysisAppVo.setLeavingCompanyAge(getPersonnelAnalysisByLeavingCompanyAgeAndLocation(location, year, month, users, userTransferPosts, true));

        return hrPersonnelAnalysisAppVo;
    }

    private List<HrPersonnelAnalysisAppVo.PersonnelAnalysis> getPersonnelAnalysisByAgeAndLocation(String location, Integer year, Integer month,
                                                                                                  List<UserDto> users, List<UserTransferPost> userTransferPosts, boolean isNeedClone) {
        Calendar today = ReportHelper.getDateOnly();
        today.set(year, month - 1, 1);
        today.add(Calendar.MONTH, 1);
        today.add(Calendar.DAY_OF_MONTH, -1);

        Calendar beginDate = ReportHelper.getDateOnly();
        beginDate.set(year, month - 1, 1);
        Calendar endDate = ReportHelper.getDateOnly();
        endDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), 1);
        endDate.add(Calendar.MONTH, 1);
        endDate.add(Calendar.DAY_OF_MONTH, -1);

        List<UserDto> resetUsers = resetUserInfo(users, userTransferPosts, endDate, isNeedClone);

        List<UserDto> currentUsers = new ArrayList<>();
        for (UserDto user : resetUsers) {
            if (!"大西山居".equals(location) && !location.equals(user.getLocation())) {
                continue;
            }
            if (!user.getActiveFlag() && null == user.getLeaveDate()) {
                continue;
            }
            if ("实习".equals(user.getWorkStatus()) || ("离职".equals(user.getWorkStatus()) && ("实习生离职".equals(user.getLeaveType()) || "实习生返校".equals(user.getLeaveType())))) {
                continue;
            }
            if (null != user.getLeaveDate() && ReportHelper.compareTwoDatesByGreaterOrEqual(beginDate.getTime(), user.getLeaveDate()) && ReportHelper.compareTwoDatesByGreaterOrEqual(user.getLeaveDate(), endDate.getTime())) {
                continue;
            }
            if (null != user.getInDate() && ReportHelper.compareTwoDatesByGreater(endDate.getTime(), user.getInDate())) {
                continue;
            }
            if (!user.getActiveFlag() && null != user.getLeaveDate() && ReportHelper.compareTwoDatesByGreater(user.getLeaveDate(), beginDate.getTime())) {
                continue;
            }

            currentUsers.add(user);
        }
        return getPersonnelAnalysisByAge(today, currentUsers);
    }

    private List<HrPersonnelAnalysisAppVo.PersonnelAnalysis> getPersonnelAnalysisByCompanyAgeAndLocation(String location, Integer year, Integer month,
                                                                                                         List<UserDto> users, List<UserTransferPost> userTransferPosts, boolean isNeedClone) {
        Calendar today = ReportHelper.getDateOnly();
        today.set(year, month - 1, 1);
        today.add(Calendar.MONTH, 1);
        today.add(Calendar.DAY_OF_MONTH, -1);

        Calendar beginDate = ReportHelper.getDateOnly();
        beginDate.set(year, month - 1, 1);
        Calendar endDate = ReportHelper.getDateOnly();
        endDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), 1);
        endDate.add(Calendar.MONTH, 1);
        endDate.add(Calendar.DAY_OF_MONTH, -1);

        List<UserDto> resetUsers = resetUserInfo(users, userTransferPosts, endDate, isNeedClone);

        List<UserDto> currentUsers = new ArrayList<>();
        for (UserDto user : resetUsers) {
            if (!"大西山居".equals(location) && !location.equals(user.getLocation())) {
                continue;
            }
            if (!user.getActiveFlag() && null == user.getLeaveDate()) {
                continue;
            }
            if ("实习".equals(user.getWorkStatus()) || ("离职".equals(user.getWorkStatus()) && ("实习生离职".equals(user.getLeaveType()) || "实习生返校".equals(user.getLeaveType())))) {
                continue;
            }
            if (null != user.getLeaveDate() && ReportHelper.compareTwoDatesByGreaterOrEqual(beginDate.getTime(), user.getLeaveDate()) && ReportHelper.compareTwoDatesByGreaterOrEqual(user.getLeaveDate(), endDate.getTime())) {
                continue;
            }
            if (null != user.getInDate() && ReportHelper.compareTwoDatesByGreater(endDate.getTime(), user.getInDate())) {
                continue;
            }
            if (!user.getActiveFlag() && null != user.getLeaveDate() && ReportHelper.compareTwoDatesByGreater(user.getLeaveDate(), beginDate.getTime())) {
                continue;
            }

            currentUsers.add(user);
        }

        return getPersonnelAnalysisByCompanyAge(today, currentUsers);
    }

    private List<HrPersonnelAnalysisAppVo.PersonnelAnalysis> getPersonnelAnalysisByLeavingCompanyAgeAndLocation(String location, Integer year, Integer month,
                                                                                                                List<UserDto> users, List<UserTransferPost> userTransferPosts, boolean isNeedClone) {
        Calendar beginDate = ReportHelper.getDateOnly();
        beginDate.set(year, month - 1, 1);
        Calendar endDate = ReportHelper.getDateOnly();
        endDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), 1);
        endDate.add(Calendar.MONTH, 1);
        endDate.add(Calendar.DAY_OF_MONTH, -1);

        List<UserDto> resetUsers = resetUserInfo(users, userTransferPosts, endDate, isNeedClone);

        List<UserDto> currentUsers = new ArrayList<>();
        for (UserDto user : resetUsers) {
            if (!"大西山居".equals(location) && !location.equals(user.getLocation())) {
                continue;
            }
            if (!user.getActiveFlag() && null == user.getLeaveDate()) {
                continue;
            }
            if ("实习".equals(user.getWorkStatus()) || ("离职".equals(user.getWorkStatus()) && ("实习生离职".equals(user.getLeaveType()) || "实习生返校".equals(user.getLeaveType())))) {
                continue;
            }
            if (null != user.getLeaveDate() && ReportHelper.compareTwoDatesByGreaterOrEqual(beginDate.getTime(), user.getLeaveDate()) && ReportHelper.compareTwoDatesByGreaterOrEqual(user.getLeaveDate(), endDate.getTime())) {
                currentUsers.add(user);
            }
        }

        return getPersonnelAnalysisByLeavingCompanyAge(currentUsers);
    }

    @Override
    public HrPersonnelAnalysisAppVo getHrPersonnelAnalysisByProject(Long projectId, Integer year, Integer month) {
        Project project = projectMapper.selectByPrimaryKey(projectId);
        if (null == project) {
            logger.info("getHrPersonnelAnalysisByProject failed...");
            throw new ParamException("project not found");
        }

        HrPersonnelAnalysisAppVo hrPersonnelAnalysisAppVo = new HrPersonnelAnalysisAppVo();

        JSONObject requestParam = new JSONObject();
        requestParam.put("projectId", projectId);
        requestParam.put("name", project.getName());
        requestParam.put("year", year);
        requestParam.put("month", month);
        hrPersonnelAnalysisAppVo.setRequestParam(requestParam);

        List<UserDto> users = userMapper.selectAllEntityWidthDetailWithoutTrainee();
        List<UserTransferPost> userTransferPostLis = userTransferPostMapper.selectAll();
        List<OrderCenter> orderCenters = orderCenterMapper.selectByProject(projectId);
        List<String> orderCenterCodes = new ArrayList<>();

        for (OrderCenter orderCenter : orderCenters) {
            orderCenterCodes.add(orderCenter.getCode());
        }

        hrPersonnelAnalysisAppVo.setAge(getPersonnelAnalysisByAgeAndProject(orderCenterCodes, year, month, users, userTransferPostLis, true));
        hrPersonnelAnalysisAppVo.setCompanyAge(getPersonnelAnalysisByCompanyAgeAndProject(orderCenterCodes, year, month, users, userTransferPostLis, true));
        hrPersonnelAnalysisAppVo.setLeavingCompanyAge(getPersonnelAnalysisByLeavingCompanyAgeAndProject(orderCenterCodes, year, month, users, userTransferPostLis, true));

        return hrPersonnelAnalysisAppVo;
    }

    private List<UserDto> getCurrentNotInternUserDto(List<UserDto> users, List<String> orderCenterCodes, Calendar beginDate, Calendar endDate) {
        List<UserDto> currentUsers = new ArrayList<>();
        for (UserDto user : users) {
            if (!orderCenterCodes.contains(user.getOrderCenterCode())) {
                continue;
            }
            if (!user.getActiveFlag() && null == user.getLeaveDate()) {
                continue;
            }
            if ("实习".equals(user.getWorkStatus()) || ("离职".equals(user.getWorkStatus()) && ("实习生离职".equals(user.getLeaveType()) || "实习生返校".equals(user.getLeaveType())))) {
                continue;
            }
            if (null != user.getLeaveDate() && ReportHelper.compareTwoDatesByGreaterOrEqual(beginDate.getTime(), user.getLeaveDate()) && ReportHelper.compareTwoDatesByGreaterOrEqual(user.getLeaveDate(), endDate.getTime())) {
                continue;
            }
            if (null != user.getInDate() && ReportHelper.compareTwoDatesByGreater(endDate.getTime(), user.getInDate())) {
                continue;
            }
            if (!user.getActiveFlag() && null != user.getLeaveDate() && ReportHelper.compareTwoDatesByGreater(user.getLeaveDate(), beginDate.getTime())) {
                continue;
            }

            currentUsers.add(user);
        }
        return currentUsers;
    }

    private List<HrPersonnelAnalysisAppVo.PersonnelAnalysis> getPersonnelAnalysisByAgeAndProject(List<String> orderCenterCodes, Integer year, Integer month,
                                                                                                 List<UserDto> users, List<UserTransferPost> userTransferPosts, boolean isNeedClone) {
        Calendar today = ReportHelper.getDateOnly();
        today.set(year, month - 1, 1);
        today.add(Calendar.MONTH, 1);
        today.add(Calendar.DAY_OF_MONTH, -1);

        Calendar beginDate = ReportHelper.getDateOnly();
        beginDate.set(year, month - 1, 1);
        Calendar endDate = ReportHelper.getDateOnly();
        endDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), 1);
        endDate.add(Calendar.MONTH, 1);
        endDate.add(Calendar.DAY_OF_MONTH, -1);

        List<UserDto> resetUsers = resetUserInfo(users, userTransferPosts, endDate, isNeedClone);

        List<UserDto> currentUsers = getCurrentNotInternUserDto(resetUsers, orderCenterCodes, beginDate, endDate);

        return getPersonnelAnalysisByAge(today, currentUsers);
    }

    private List<HrPersonnelAnalysisAppVo.PersonnelAnalysis> getPersonnelAnalysisByCompanyAgeAndProject(List<String> orderCenterCodes, Integer year, Integer month,
                                                                                                        List<UserDto> users, List<UserTransferPost> userTransferPosts, boolean isNeedClone) {
        Calendar today = ReportHelper.getDateOnly();
        today.set(year, month - 1, 1);
        today.add(Calendar.MONTH, 1);
        today.add(Calendar.DAY_OF_MONTH, -1);

        Calendar beginDate = ReportHelper.getDateOnly();
        beginDate.set(year, month - 1, 1);
        Calendar endDate = ReportHelper.getDateOnly();
        endDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), 1);
        endDate.add(Calendar.MONTH, 1);
        endDate.add(Calendar.DAY_OF_MONTH, -1);

        List<UserDto> resetUsers = resetUserInfo(users, userTransferPosts, endDate, isNeedClone);

        List<UserDto> currentUsers = getCurrentNotInternUserDto(resetUsers, orderCenterCodes, beginDate, endDate);

        return getPersonnelAnalysisByCompanyAge(today, currentUsers);
    }

    private List<HrPersonnelAnalysisAppVo.PersonnelAnalysis> getPersonnelAnalysisByLeavingCompanyAgeAndProject(List<String> orderCenterCodes, Integer year, Integer month,
                                                                                                               List<UserDto> users, List<UserTransferPost> userTransferPosts, boolean isNeedClone) {
        Calendar beginDate = ReportHelper.getDateOnly();
        beginDate.set(year, month - 1, 1);
        Calendar endDate = ReportHelper.getDateOnly();
        endDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), 1);
        endDate.add(Calendar.MONTH, 1);
        endDate.add(Calendar.DAY_OF_MONTH, -1);

        List<UserDto> resetUsers = resetUserInfo(users, userTransferPosts, endDate, isNeedClone);

        List<UserDto> currentUsers = new ArrayList<>();
        for (UserDto user : resetUsers) {
            if (!orderCenterCodes.contains(user.getOrderCenterCode())) {
                continue;
            }
            if (!user.getActiveFlag() && null == user.getLeaveDate()) {
                continue;
            }
            if ("实习".equals(user.getWorkStatus()) || ("离职".equals(user.getWorkStatus()) && ("实习生离职".equals(user.getLeaveType()) || "实习生返校".equals(user.getLeaveType())))) {
                continue;
            }
            if (null != user.getLeaveDate() && ReportHelper.compareTwoDatesByGreaterOrEqual(beginDate.getTime(), user.getLeaveDate()) && ReportHelper.compareTwoDatesByGreaterOrEqual(user.getLeaveDate(), endDate.getTime())) {
                currentUsers.add(user);
            }
        }

        return getPersonnelAnalysisByLeavingCompanyAge(currentUsers);
    }

    private List<HrPersonnelAnalysisAppVo.PersonnelAnalysis> getPersonnelAnalysisByAge(Calendar today, List<UserDto> users) {
        List<HrPersonnelAnalysisAppVo.PersonnelAnalysis> personnelAnalyses = new ArrayList<>();
        int age21_22 = 0, age22_24 = 0, age24_26 = 0, age26_28 = 0, age28_30 = 0, age30 = 0;
        for (UserDto user : users) {
            if (null == user.getBirthday()) {
                age30++;
                continue;
            }

            Float age = ReportHelper.compareTwoDatesToDay(user.getBirthday(), today.getTime()).longValue() / 365f;

            if (age > 30) {
                age30++;
            } else if (age > 28) {
                age28_30++;
            } else if (age > 26) {
                age26_28++;
            } else if (age > 24) {
                age24_26++;
            } else if (age > 22) {
                age22_24++;
            } else if (age > 20) {
                age21_22++;
            }
        }
        HrPersonnelAnalysisAppVo.PersonnelAnalysis personnelAnalysis = new HrPersonnelAnalysisAppVo.PersonnelAnalysis();
        personnelAnalysis.setName("21-22岁");
        personnelAnalysis.setCount(age21_22);
        personnelAnalyses.add(personnelAnalysis);
        personnelAnalysis = new HrPersonnelAnalysisAppVo.PersonnelAnalysis();
        personnelAnalysis.setName("22-24岁");
        personnelAnalysis.setCount(age22_24);
        personnelAnalyses.add(personnelAnalysis);
        personnelAnalysis = new HrPersonnelAnalysisAppVo.PersonnelAnalysis();
        personnelAnalysis.setName("24-26岁");
        personnelAnalysis.setCount(age24_26);
        personnelAnalyses.add(personnelAnalysis);
        personnelAnalysis = new HrPersonnelAnalysisAppVo.PersonnelAnalysis();
        personnelAnalysis.setName("26-28岁");
        personnelAnalysis.setCount(age26_28);
        personnelAnalyses.add(personnelAnalysis);
        personnelAnalysis = new HrPersonnelAnalysisAppVo.PersonnelAnalysis();
        personnelAnalysis.setName("28-30岁");
        personnelAnalysis.setCount(age28_30);
        personnelAnalyses.add(personnelAnalysis);
        personnelAnalysis = new HrPersonnelAnalysisAppVo.PersonnelAnalysis();
        personnelAnalysis.setName("30岁以上");
        personnelAnalysis.setCount(age30);
        personnelAnalyses.add(personnelAnalysis);
        return personnelAnalyses;
    }

    private List<HrPersonnelAnalysisAppVo.PersonnelAnalysis> getPersonnelAnalysisByCompanyAge(Calendar today, List<UserDto> users) {
        List<HrPersonnelAnalysisAppVo.PersonnelAnalysis> personnelAnalyses = new ArrayList<>();
        int probation = 0, age0_1 = 0, age1_2 = 0, age2_3 = 0, age3_5 = 0, age5_8 = 0, age8_10 = 0, age10 = 0;
        for (UserDto userDto : users) {
            if (null == userDto.getInDate()) {
                age0_1++;
                continue;
            }

            if ("试用".equals(userDto.getWorkStatus())) {
                probation++;
                continue;
            }
            Float age = ReportHelper.compareTwoDatesToDay(userDto.getInDate(), today.getTime()) / 365;
            if (age > 10) {
                age10++;
            } else if (age > 8) {
                age8_10++;
            } else if (age > 5) {
                age5_8++;
            } else if (age > 3) {
                age3_5++;
            } else if (age > 2) {
                age2_3++;
            } else if (age > 1) {
                age1_2++;
            } else {
                age0_1++;
            }
        }
        HrPersonnelAnalysisAppVo.PersonnelAnalysis personnelAnalysis = new HrPersonnelAnalysisAppVo.PersonnelAnalysis();
        personnelAnalysis.setName("试用期");
        personnelAnalysis.setCount(probation);
        personnelAnalyses.add(personnelAnalysis);
        personnelAnalysis = new HrPersonnelAnalysisAppVo.PersonnelAnalysis();
        personnelAnalysis.setName("0-1年");
        personnelAnalysis.setCount(age0_1);
        personnelAnalyses.add(personnelAnalysis);
        personnelAnalysis = new HrPersonnelAnalysisAppVo.PersonnelAnalysis();
        personnelAnalysis.setName("1-2年");
        personnelAnalysis.setCount(age1_2);
        personnelAnalyses.add(personnelAnalysis);
        personnelAnalysis = new HrPersonnelAnalysisAppVo.PersonnelAnalysis();
        personnelAnalysis.setName("2-3年");
        personnelAnalysis.setCount(age2_3);
        personnelAnalyses.add(personnelAnalysis);
        personnelAnalysis = new HrPersonnelAnalysisAppVo.PersonnelAnalysis();
        personnelAnalysis.setName("3-5年");
        personnelAnalysis.setCount(age3_5);
        personnelAnalyses.add(personnelAnalysis);
        personnelAnalysis = new HrPersonnelAnalysisAppVo.PersonnelAnalysis();
        personnelAnalysis.setName("5-8年");
        personnelAnalysis.setCount(age5_8);
        personnelAnalyses.add(personnelAnalysis);
        personnelAnalysis = new HrPersonnelAnalysisAppVo.PersonnelAnalysis();
        personnelAnalysis.setName("8-10年");
        personnelAnalysis.setCount(age8_10);
        personnelAnalyses.add(personnelAnalysis);
        personnelAnalysis = new HrPersonnelAnalysisAppVo.PersonnelAnalysis();
        personnelAnalysis.setName("10年以上");
        personnelAnalysis.setCount(age10);
        personnelAnalyses.add(personnelAnalysis);
        return personnelAnalyses;
    }

    private List<HrPersonnelAnalysisAppVo.PersonnelAnalysis> getPersonnelAnalysisByLeavingCompanyAge(List<UserDto> users) {
        List<HrPersonnelAnalysisAppVo.PersonnelAnalysis> personnelAnalyses = new ArrayList<>();
        int age0_1 = 0, age1_2 = 0, age2_3 = 0, age3_5 = 0, age5_8 = 0, age8_10 = 0, age10 = 0;
        for (UserDto userDto : users) {
            if (null == userDto.getInDate()) {
                age0_1++;
                continue;
            }

            Float age = ReportHelper.compareTwoDatesToDay(userDto.getInDate(), userDto.getLeaveDate()) / 365;
            if (age > 10) {
                age10++;
            } else if (age > 8) {
                age8_10++;
            } else if (age > 5) {
                age5_8++;
            } else if (age > 3) {
                age3_5++;
            } else if (age > 2) {
                age2_3++;
            } else if (age > 1) {
                age1_2++;
            } else {
                age0_1++;
            }
        }
        HrPersonnelAnalysisAppVo.PersonnelAnalysis personnelAnalysis = new HrPersonnelAnalysisAppVo.PersonnelAnalysis();
        personnelAnalysis.setName("0-1年");
        personnelAnalysis.setCount(age0_1);
        personnelAnalyses.add(personnelAnalysis);
        personnelAnalysis = new HrPersonnelAnalysisAppVo.PersonnelAnalysis();
        personnelAnalysis.setName("1-2年");
        personnelAnalysis.setCount(age1_2);
        personnelAnalyses.add(personnelAnalysis);
        personnelAnalysis = new HrPersonnelAnalysisAppVo.PersonnelAnalysis();
        personnelAnalysis.setName("2-3年");
        personnelAnalysis.setCount(age2_3);
        personnelAnalyses.add(personnelAnalysis);
        personnelAnalysis = new HrPersonnelAnalysisAppVo.PersonnelAnalysis();
        personnelAnalysis.setName("3-5年");
        personnelAnalysis.setCount(age3_5);
        personnelAnalyses.add(personnelAnalysis);
        personnelAnalysis = new HrPersonnelAnalysisAppVo.PersonnelAnalysis();
        personnelAnalysis.setName("5-8年");
        personnelAnalysis.setCount(age5_8);
        personnelAnalyses.add(personnelAnalysis);
        personnelAnalysis = new HrPersonnelAnalysisAppVo.PersonnelAnalysis();
        personnelAnalysis.setName("8-10年");
        personnelAnalysis.setCount(age8_10);
        personnelAnalyses.add(personnelAnalysis);
        personnelAnalysis = new HrPersonnelAnalysisAppVo.PersonnelAnalysis();
        personnelAnalysis.setName("10年以上");
        personnelAnalysis.setCount(age10);
        personnelAnalyses.add(personnelAnalysis);
        return personnelAnalyses;
    }

    private List<UserDto> resetUserInfo(List<UserDto> users, List<UserTransferPost> userTransferPosts, Calendar endDate, boolean isNeedClone) {
        List<UserDto> copyUsers;
        if (isNeedClone) {
            copyUsers = clonedUsers(users);
        } else {
            copyUsers = users;
        }
        // 重置调岗人员信息
        Map<Long, UserTransferPost> userTransferPostMap = new HashMap<>();
        for (UserTransferPost userTransferPost : userTransferPosts) {
            if (!ReportHelper.compareTwoDatesByGreater(endDate.getTime(), userTransferPost.getTransferTime())) {
                continue;
            }

            if (userTransferPostMap.containsKey(userTransferPost.getUserId())) {
                continue;
            }

            userTransferPostMap.put(userTransferPost.getUserId(), userTransferPost);
        }
        for (UserDto user : copyUsers) {
            if (userTransferPostMap.containsKey(user.getId())) {
                UserTransferPost userTransferPost = userTransferPostMap.get(user.getId());
                user.setLocation(userTransferPost.getPreWorkPlace());
                user.setOrderCenterCode(userTransferPost.getPreOrderCenterCode());
                user.setOrderCenterId(userTransferPost.getPreOrderCenterId());
            }
        }
        return copyUsers;
    }

    @Override
    public ContactsAppVo getContactsByProjectId(Long projectId, Long groupId) {
        List<OrgWorkGroupMemberAppVo> members = userMapper.selectAllUserByProjectId(projectId);
        Map<Long, List<OrgWorkGroupMemberAppVo>> membersGroupIdMap = getMemberGroupIdMap(members);

        Set<Long> groupIdSet = members.stream().map(member -> member.getWorkGroupId()).collect(Collectors.toSet());
        List<WorkGroup> workGroups = null;
        if (null == groupIdSet || groupIdSet.size() == 0) {
            workGroups = new ArrayList<>();
        } else {
            workGroups = workGroupMapper.selectWorkGroupsByIds(new ArrayList<Long>(groupIdSet));
        }

        if (CollectionUtils.isEmpty(workGroups)) {
            return null;
        }

        Map<Long, WorkGroup> allGroupIdMap = new HashMap<Long, WorkGroup>();
        populateAllParentGroups(workGroups, allGroupIdMap);

        Long baseGroupId = null;
        boolean isRootGroup = false;
        Long rootGroupId = findRootGroupId(allGroupIdMap, membersGroupIdMap);
        if (groupId == null || groupId.equals(rootGroupId)) {
            baseGroupId = rootGroupId;
            isRootGroup = true;
        } else {
            baseGroupId = groupId;
            isRootGroup = false;
        }

        WorkGroup baseGroup = allGroupIdMap.get(baseGroupId);
        Map<List<Integer>, List<OrgWorkGroupMemberAppVo>> totalAndMemberMap = getTotalAndMembers(isRootGroup, projectId, baseGroup.getId(), allGroupIdMap, membersGroupIdMap);
        ContactsAppVo contactsAppVo = new ContactsAppVo();
        contactsAppVo.setWorkGroupId(baseGroup.getId());
        contactsAppVo.setWorkGroupName(baseGroup.getName());
        contactsAppVo.setCurrentGroups(getCurrentGroups(isRootGroup, projectId, baseGroup.getId(), allGroupIdMap, membersGroupIdMap));
        contactsAppVo.setMembers(totalAndMemberMap.values().iterator().next());
        contactsAppVo.setTotal(totalAndMemberMap.keySet().iterator().next().get(0));
        contactsAppVo.setInternTotal(totalAndMemberMap.keySet().iterator().next().get(1));
        return contactsAppVo;
    }

    private List<Group> getCurrentGroups(boolean isRootGroup, Long projectId, Long workGroupId, Map<Long, WorkGroup> allGroupIdMap, Map<Long, List<OrgWorkGroupMemberAppVo>> membersGroupIdMap) {
        List<Group> result = new ArrayList<Group>();
        List<WorkGroup> childrenGroups = allGroupIdMap.values().stream().filter(workGroup -> workGroup != null && workGroup.getParent().equals(workGroupId)).collect(Collectors.toList());
        for (WorkGroup childrenGroup : childrenGroups) {
            Map<List<Integer>, List<OrgWorkGroupMemberAppVo>> totalAndMemberMap = getTotalAndMembers(isRootGroup, projectId, childrenGroup.getId(), allGroupIdMap, membersGroupIdMap);

            Group group = new Group();
            result.add(group);
            group.setGroupId(childrenGroup.getId());
            group.setName(childrenGroup.getName());
            group.setTotal(totalAndMemberMap.keySet().iterator().next().get(0));
            group.setInternTotal(totalAndMemberMap.keySet().iterator().next().get(1));
        }
        return result;
    }

    private Map<List<Integer>, List<OrgWorkGroupMemberAppVo>> getTotalAndMembers(boolean isRootGroup, Long projectId, Long workGoupId, Map<Long, WorkGroup> allGroupIdMap, Map<Long, List<OrgWorkGroupMemberAppVo>> membersGroupIdMap) {
        int regularTotal = 0;
        int internTotal = 0;
        List<OrgWorkGroupMemberAppVo> uniqueMembers = new ArrayList<OrgWorkGroupMemberAppVo>();
        Set<Long> uniqueMemberId = new HashSet<Long>();
        List<OrgWorkGroupMemberAppVo> leaders = userMapper.selectLeadersByWorkGroupId(workGoupId);
        if (isRootGroup) {
            leaders.addAll(userMapper.selectLeadersByProjectId(projectId));
        }
        if (!CollectionUtils.isEmpty(leaders)) {
            for (OrgWorkGroupMemberAppVo leader : leaders) {
                if (!uniqueMemberId.contains(leader.getUserId())) {
                    leader.setLeaderFlag(true);
                    uniqueMemberId.add(leader.getUserId());
                    uniqueMembers.add(leader);
                }
            }
        }
        Set<Long> allChildrenGroupIdSet = new HashSet<Long>();
        allChildrenGroupIdSet.add(workGoupId);
        populateAllChildrenGroupId(workGoupId, allChildrenGroupIdSet, allGroupIdMap);
        for (Long allChildrenGroupId : allChildrenGroupIdSet) {
            if (!CollectionUtils.isEmpty(membersGroupIdMap.get(allChildrenGroupId))) {
                int interns = (int) membersGroupIdMap.get(allChildrenGroupId).stream().filter(member -> "实习".equals(member.getWorkStatus())).count();
                int regulars = membersGroupIdMap.get(allChildrenGroupId).size() - interns;
                internTotal += interns;
                regularTotal += regulars;

                List<OrgWorkGroupMemberAppVo> members = membersGroupIdMap.get(allChildrenGroupId);
                if (!CollectionUtils.isEmpty(members)) {
                    for (OrgWorkGroupMemberAppVo member : members) {
                        if (!uniqueMemberId.contains(member.getUserId())) {
                            member.setLeaderFlag(false);
                            uniqueMemberId.add(member.getUserId());
                            uniqueMembers.add(member);
                        }
                    }
                }
            }
        }
        Map<List<Integer>, List<OrgWorkGroupMemberAppVo>> result = new HashMap<List<Integer>, List<OrgWorkGroupMemberAppVo>>();
        List<Integer> total = new ArrayList<Integer>();
        total.add(regularTotal);
        total.add(internTotal);
        result.put(total, uniqueMembers);
        return result;
    }

    private void populateAllChildrenGroupId(Long workGoupId, Set<Long> allChildrenGroupIdSet, Map<Long, WorkGroup> allGroupIdMap) {
        List<Long> childrenGroupIds = allGroupIdMap.values().stream().filter(workGroup -> workGroup != null && workGroup.getParent().equals(workGoupId)).map(workGroup -> workGroup.getId()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(childrenGroupIds)) {
            return;
        }

        allChildrenGroupIdSet.addAll(childrenGroupIds);

        for (Long childrenGroupId : childrenGroupIds) {
            populateAllChildrenGroupId(childrenGroupId, allChildrenGroupIdSet, allGroupIdMap);
        }
    }

    private Map<Long, List<OrgWorkGroupMemberAppVo>> getMemberGroupIdMap(List<OrgWorkGroupMemberAppVo> members) {
        Map<Long, List<OrgWorkGroupMemberAppVo>> membersGroupIdMap = new HashMap<Long, List<OrgWorkGroupMemberAppVo>>();
        for (OrgWorkGroupMemberAppVo member : members) {
            if (!membersGroupIdMap.containsKey(member.getWorkGroupId())) {
                membersGroupIdMap.put(member.getWorkGroupId(), new ArrayList<OrgWorkGroupMemberAppVo>());
            }
            membersGroupIdMap.get(member.getWorkGroupId()).add(member);
        }
        return membersGroupIdMap;
    }

    private Long findRootGroupId(Map<Long, WorkGroup> allGroupIdMap, Map<Long, List<OrgWorkGroupMemberAppVo>> membersGroupIdMap) {
        Long realRootGroupId = null;
        for (WorkGroup group : allGroupIdMap.values()) {
            if (group.getParent() == null || group.getParent() == 0) {
                realRootGroupId = group.getId();
                break;
            }
        }

        return findRootGroupIdLoop(realRootGroupId, allGroupIdMap, membersGroupIdMap);
    }


    private Long findRootGroupIdLoop(Long realRootGroupId, Map<Long, WorkGroup> allGroupIdMap, Map<Long, List<OrgWorkGroupMemberAppVo>> membersGroupIdMap) {
        if (realRootGroupId == null) {
            return null;
        }
        List<WorkGroup> childrenGroups = allGroupIdMap.values().stream().filter(workGroup -> workGroup.getParent().equals(realRootGroupId)).collect(Collectors.toList());
        if (childrenGroups.size() > 1 || !CollectionUtils.isEmpty(membersGroupIdMap.get(realRootGroupId))) {
            return realRootGroupId;
        } else {
            return findRootGroupIdLoop(childrenGroups.get(0).getId(), allGroupIdMap, membersGroupIdMap);
        }
    }


    private void populateAllParentGroups(List<WorkGroup> groups, Map<Long, WorkGroup> allGroupIdMap) {
        if (CollectionUtils.isEmpty(groups)) {
            return;
        }

        for (WorkGroup group : groups) {
            if (!allGroupIdMap.containsKey(group.getId())) {
                allGroupIdMap.put(group.getId(), group);
            }
        }

        Set<Long> parentGroupIds = groups.stream().map(group -> group.getParent()).collect(Collectors.toSet());

        List<WorkGroup> parentGroups = null;
        if (null == parentGroupIds || parentGroupIds.size() == 0) {
            parentGroups = new ArrayList<>();
        } else {
            parentGroups = workGroupMapper.selectWorkGroupsByIds(new ArrayList<Long>(parentGroupIds));
        }

        populateAllParentGroups(parentGroups, allGroupIdMap);
    }


    private HrWorkGroupDto flatten(Long groupId, List<HrWorkGroupDto> HrWorkGroupDtos) {
        for (HrWorkGroupDto item : HrWorkGroupDtos) {
            if (groupId.equals(item.getId())) {
                return item;
            } else {
                HrWorkGroupDto flatten = flatten(groupId, item.getChildWorkGroups());
                if (flatten != null) {
                    return flatten;
                }
            }
        }
        return null;
    }

    @Override
    public ContactsAppVo getContacts(Long workGroupId) {
        ContactsAppVo result = new ContactsAppVo();
        HrWorkGroupDto currentRootWorkGroup;
        HrWorkGroupDto rootWorkGroupTree = workGroupService.buildHrWorkGroupView();
        // 根组
        if (workGroupId == null || workGroupId.equals(rootWorkGroupTree.getId())) {
            currentRootWorkGroup = rootWorkGroupTree;
            List<ContactsAppVo.Group> allContactsGroups = getAllGroups(currentRootWorkGroup);
            result.setAllGroups(allContactsGroups);
        } else {
            currentRootWorkGroup = flatten(workGroupId, rootWorkGroupTree.getChildWorkGroups());
        }

        // 未找到该组
        if (currentRootWorkGroup == null) {
            logger.info("{} work group not found", workGroupId);
            throw new ParamException("当前组不存在");
        }

        result.setWorkGroupId(currentRootWorkGroup.getId());
        result.setWorkGroupName(currentRootWorkGroup.getName());
        result.setTotal(currentRootWorkGroup.getMemberNumber());
        result.setInternTotal(currentRootWorkGroup.getInternNumber());

        // 当前组
        result.setCurrentGroups(new ArrayList<>());
        List<HrWorkGroupDto> children = currentRootWorkGroup.getChildWorkGroups();
        for (HrWorkGroupDto child : children) {
            ContactsAppVo.Group currentGroup = getContactsGroup(child);
            if (currentGroup.getTotal() > 0) {
                result.getCurrentGroups().add(currentGroup);
            }
        }

        // 组内人员
        List<OrgWorkGroupMemberAppVo> allContactsMembers = new ArrayList<>();
        List<Long> leaderIds = new ArrayList<>();
        if (null != currentRootWorkGroup.getLeaders() && !currentRootWorkGroup.getLeaders().isEmpty()) {
            for (OrgWorkGroupMemberAppVo leader : currentRootWorkGroup.getLeaders()) {
                leader.setLeaderFlag(true);
                allContactsMembers.add(leader);
                leaderIds.add(leader.getUserId());
            }
        }
        allContactsMembers.addAll(getAllContactsMembers(currentRootWorkGroup, leaderIds));
        result.setMembers(allContactsMembers);

        return result;
    }

    private List<ContactsAppVo.Group> getAllGroups(HrWorkGroupDto rootWorkGroup) {
        List<ContactsAppVo.Group> groups = new ArrayList<>();
        ContactsAppVo.Group rootGroup = getContactsGroup(rootWorkGroup);
        groups.add(rootGroup);
        if (!rootWorkGroup.getChildWorkGroups().isEmpty()) {
            List<HrWorkGroupDto> children = rootWorkGroup.getChildWorkGroups();
            for (HrWorkGroupDto child : children) {
                List<ContactsAppVo.Group> subGroups = getAllGroups(child);
                groups.addAll(subGroups);
            }
        }
        return groups;
    }

    private ContactsAppVo.Group getContactsGroup(HrWorkGroupDto groupDto) {
        ContactsAppVo.Group currentGroup = new ContactsAppVo.Group();
        currentGroup.setGroupId(groupDto.getId());
        currentGroup.setName(groupDto.getName());
        currentGroup.setTotal(groupDto.getMemberNumber());
        currentGroup.setInternTotal(groupDto.getInternNumber());
        return currentGroup;
    }

    private List<OrgWorkGroupMemberAppVo> getAllContactsMembers(HrWorkGroupDto rootWorkGroup, List<Long> leaderIds) {
        List<OrgWorkGroupMemberAppVo> allMembers = new ArrayList<>();
        if (null != rootWorkGroup.getMembers() && !rootWorkGroup.getMembers().isEmpty()) {
            for (OrgWorkGroupMemberAppVo member : rootWorkGroup.getMembers()) {
                if (!leaderIds.contains(member.getUserId())) {
                    allMembers.add(member);
                }
            }
        }
        if (null != rootWorkGroup.getChildWorkGroups() && !rootWorkGroup.getChildWorkGroups().isEmpty()) {
            List<HrWorkGroupDto> children = rootWorkGroup.getChildWorkGroups();
            for (HrWorkGroupDto child : children) {
                List<OrgWorkGroupMemberAppVo> subMembers = getAllContactsMembers(child, leaderIds);
                if (!subMembers.isEmpty()) {
                    allMembers.addAll(subMembers);
                }
            }
        }
        allMembers.sort(Comparator.comparing(u -> u.getLoginId()));
        return allMembers;
    }

}
