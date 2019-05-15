package com.seasun.management.service;

import com.seasun.management.Application;
import com.seasun.management.dto.WorkGroupDto;
import com.seasun.management.dto.WorkGroupUserDto;
import com.seasun.management.mapper.UserMapper;
import com.seasun.management.mapper.UserSalaryChangeMapper;
import com.seasun.management.mapper.WorkGroupMapper;
import com.seasun.management.model.UserSalaryChange;
import com.seasun.management.model.WorkGroupRole;
import com.seasun.management.service.performanceCore.groupTrack.GroupTrackService;
import com.seasun.management.vo.IndividualSalaryChangeVo;
import com.seasun.management.vo.OrdinateSalaryChangeAppVo;
import com.seasun.management.vo.SubordinateSalaryChangeAppVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
public class UserSalaryAppServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(FloorServiceTest.class);

    @Autowired
    UserSalaryChangeService userSalaryChangeAppService;

    @Autowired
    GroupTrackService groupTrackService;

    @Autowired
    WorkGroupMapper workGroupMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserSalaryChangeMapper userSalaryChangeMapper;


    @Test
    public void testGetSubSalaryChange() {
//        SubordinateSalaryChangeAppVo vo=userSalaryChangeAppService.getSubSalaryChange(922L,2016,1,6816L);
        SubordinateSalaryChangeAppVo vo2 = userSalaryChangeAppService.getSubSalaryChange(725L, 2016, 4);
//        SubordinateSalaryChangeAppVo vo3=userSalaryChangeAppService.getSubSalaryChange(922L,2017,1,6816L);
        System.out.print(".........");
    }

    @Test
    public void testGetWorkGroupIdAndSubGroupId() {
        List<OrdinateSalaryChangeAppVo> voList = new ArrayList<>();
//        for (int i = 0; i < 15; i++) {
//            OrdinateSalaryChangeAppVo vo = new OrdinateSalaryChangeAppVo();
//            vo.setUserId(6816L + i);
//            voList.add(vo);
//        }
        OrdinateSalaryChangeAppVo vo = new OrdinateSalaryChangeAppVo();
        vo.setUserId(1069L);
        voList.add(vo);
        WorkGroupDto rootGroup = new WorkGroupDto();
        List<WorkGroupDto> allGroups = workGroupMapper.selectAllWithLeaderByGroupRoleId(WorkGroupRole.Role.salary);
        List<OrdinateSalaryChangeAppVo> allUsers = userSalaryChangeMapper.getQuarterRangePerformance(2016, 1);
        rootGroup.setLeaderId(4194L);
        rootGroup.setId(725L);
        groupTrackService.initHrGroupTreeByRootGroup(rootGroup, allGroups, allUsers);
//        userSalaryChangeMapper.getWorkGroupIdAndSubGroupId(voList, rootGroup);
        System.out.println("");
        logger.info("--------------" + rootGroup.getMembers() + "*******" + voList.get(0).toString());
    }

    @Test
    public void testGetCharacter() {
    }

    @Test
    public void testGetMemberSalaryChange() {
//        List<OrdinateSalaryChangeAppVo> vo=userSalaryChangeAppService.getMemberSalaryChange(923L,2016,1,true);
        List<OrdinateSalaryChangeAppVo> vo1 = userSalaryChangeAppService.getMemberSalaryChange(727L, 2016, 4, false);
//        List<OrdinateSalaryChangeAppVo> vo2=userSalaryChangeAppService.getMemberSalaryChange(923L,2017,1,false);
        System.out.print(vo1.size());
    }

    @Test
    public void testGetIndividualDetail() {
        IndividualSalaryChangeVo vo = userSalaryChangeAppService.getIndividualDetail(true, 6821L, 2016, 1);
//        IndividualSalaryChangeVo vo1=userSalaryChangeAppService.getIndividualDetail(false,6821L,2016,4);
//        IndividualSalaryChangeVo vo2=userSalaryChangeAppService.getIndividualDetail(false,6821L,2017,1);
        System.out.print("");
    }

    @Test
    public void testGetSubGroupMembers() {
        List<Long> list = new ArrayList<>();
        list.add(923L);
        list.add(924L);
        List<List<OrdinateSalaryChangeAppVo>> lists = userSalaryChangeAppService.getSubGroupMembers(true, 922L, "good", list, 2016, 1);
//        userSalaryChangeAppService.getSubGroupMembers(false,922L,"good",list,2016,4);
//        userSalaryChangeAppService.getSubGroupMembers(false,922L,"good",list,2017,1);
        System.out.print("");
    }

    @Test
    public void testModifyOrdinateSalaryChange() {
        IndividualSalaryChangeVo vo = new IndividualSalaryChangeVo();
        vo.setUserId(6820L);
        vo.setYear(2016);
        vo.setQuarter(4);
        vo.setIncreaseSalary(200);
        userSalaryChangeAppService.modifyOrdinateSalaryChange(vo, 922L);
    }

    @Test
    public void testRequestReject() {
        userSalaryChangeAppService.requestReject(2016, 4, 925L);
    }

    @Test
    public void testGroupCommit() {
        userSalaryChangeAppService.groupCommit(925L, 2016, 4);
    }

    @Test
    public void testAssistSubmit() {
        userSalaryChangeAppService.assistSubmit(725L, 2016, 4);
    }

    @Test
    public void testLeaderFinish() {
        userSalaryChangeAppService.leaderFinish(2016, 4);
    }

    @Test
    public void testPerformanceMonthCount() {

    }

    @Test
    public void test() {
        int year = 2014;
        int quarter = 1;
        int currentYear = 2017;
        int currentQuarter = 1;

        int j = quarter + 1;
        for (int i = year; i <= currentYear; i++) {
            for (; j <= 4; j++) {
                System.out.println(i + "..." + j);
                if (i == currentYear && j == currentQuarter) break;
            }
            if (i == currentYear && j == currentQuarter) break;
            j = 1;
        }
    }

    @Test
    public void test1() {

        List<SubordinateSalaryChangeAppVo.GroupStatus> groupStatusList = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentQuarter = (int) Math.ceil((Calendar.getInstance().get(Calendar.MONTH) + 1) / 3f);


        List<SubordinateSalaryChangeAppVo.GroupStatus> list = new ArrayList<>();
        list.add(new SubordinateSalaryChangeAppVo.GroupStatus(2016, 1, SubordinateSalaryChangeAppVo.GroupStatus.Status.finished));
        list.add(new SubordinateSalaryChangeAppVo.GroupStatus(2016, 2, SubordinateSalaryChangeAppVo.GroupStatus.Status.finished));
        list.add(new SubordinateSalaryChangeAppVo.GroupStatus(2016, 3, SubordinateSalaryChangeAppVo.GroupStatus.Status.finished));
        list.add(new SubordinateSalaryChangeAppVo.GroupStatus(2016, 4, SubordinateSalaryChangeAppVo.GroupStatus.Status.waitingForCommit));
        list.add(new SubordinateSalaryChangeAppVo.GroupStatus(2017, 1, SubordinateSalaryChangeAppVo.GroupStatus.Status.waitingForCommit));
        list.add(new SubordinateSalaryChangeAppVo.GroupStatus(2017, 2, SubordinateSalaryChangeAppVo.GroupStatus.Status.waitingForCommit));

        for (int i = 0; i < groupStatusList.size(); i++) {
            logger.info("-----filled begin data" + list.get(i).toString());
        }

        for (SubordinateSalaryChangeAppVo.GroupStatus groupStatus : list) {
            if (groupStatus.getYear() == currentYear) {
                if (groupStatus.getQuarter() == currentQuarter) {
                    groupStatusList.add(new SubordinateSalaryChangeAppVo.GroupStatus(groupStatus.getYear(), groupStatus.getQuarter(), SubordinateSalaryChangeAppVo.GroupStatus.StatusVo.underway));
                    break;
                }
            }
            if (groupStatus.getStatus().equals(SubordinateSalaryChangeAppVo.GroupStatus.Status.waitingForCommit)) {
                groupStatusList.add(new SubordinateSalaryChangeAppVo.GroupStatus(groupStatus.getYear(), groupStatus.getQuarter(), SubordinateSalaryChangeAppVo.GroupStatus.StatusVo.delayed));
            } else {
                groupStatusList.add(new SubordinateSalaryChangeAppVo.GroupStatus(groupStatus.getYear(), groupStatus.getQuarter(), SubordinateSalaryChangeAppVo.GroupStatus.StatusVo.finished));
            }
        }

        for (int i = 0; i < groupStatusList.size(); i++) {
            logger.info("-----return data" + groupStatusList.get(i).toString());
        }
    }

    @Test
    public void batchUpdate() {
        List<UserSalaryChange> userSalaryChanges = new ArrayList<>();
        UserSalaryChange userSalaryChange = new UserSalaryChange();
        userSalaryChange.setWorkGroupId(111L);
        userSalaryChange.setSubGroup("12,23");
        userSalaryChange.setId(666L);
        UserSalaryChange userSalaryChange1 = new UserSalaryChange();
        userSalaryChange1.setWorkGroupId(222L);
        userSalaryChange1.setSubGroup("12,23,22");
        userSalaryChange1.setId(777L);
        userSalaryChanges.add(userSalaryChange);
        userSalaryChanges.add(userSalaryChange1);
        userSalaryChangeMapper.batchUpdateSelective(userSalaryChanges);
    }

    @Test
    public void testGetWorkGroupIdAndSub() {
        WorkGroupDto rootGroup = new WorkGroupDto();
        List<WorkGroupDto> allGroups = workGroupMapper.selectAllWithLeaderByGroupRoleId(WorkGroupRole.Role.salary);
        List<WorkGroupUserDto> allUsers = userMapper.selectAllEntityWithWorkGroupSimple(WorkGroupRole.Role.salary);
        for (WorkGroupDto workGroup : allGroups) {
            if (workGroup.getId().equals(267L)) {
                rootGroup = workGroup;
            }
        }

        groupTrackService.initHrGroupTreeByRootGroup(rootGroup, allGroups, allUsers);
        List<OrdinateSalaryChangeAppVo> list = new ArrayList<>();
        OrdinateSalaryChangeAppVo vo = new OrdinateSalaryChangeAppVo();
        vo.setUserId(4197L);
        list.add(vo);
//        groupTrackService.getWorkGroupIdAndSubGroupId(list, rootGroup);
        logger.info("----------" + list.get(0).getWorkGroupId());
        logger.info("++++++++++" + list.get(0).getSubGroup());
    }

    @Test
    public void testLambda() {
        List<Integer> list = new ArrayList<>();
        list.forEach(o -> System.out.println(o));

        WorkGroupDto rootGroup;
        List<WorkGroupDto> allGroups = workGroupMapper.selectAllWithLeaderByGroupRoleId(WorkGroupRole.Role.salary);
        Long workGroupId = 267L;

        final WorkGroupDto[] workGroupDto = new WorkGroupDto[1];
        allGroups.forEach(o -> {if (o.getId().equals(workGroupId)) workGroupDto[0] = o;});

        boolean isTrue=true;
        Callable<Integer> c2 = isTrue ? (() -> 42) : (() -> 24);
    }

    @Test
    public void testSortList(){
        List<OrdinateSalaryChangeAppVo> list=new ArrayList<>();
        OrdinateSalaryChangeAppVo vo1=new OrdinateSalaryChangeAppVo();
        vo1.setLoginId("wangwu");
        OrdinateSalaryChangeAppVo vo2=new OrdinateSalaryChangeAppVo();
        vo2.setLoginId("guoguo");
        OrdinateSalaryChangeAppVo vo3=new OrdinateSalaryChangeAppVo();
        vo3.setLoginId("liling");
        OrdinateSalaryChangeAppVo vo4=new OrdinateSalaryChangeAppVo();
        vo4.setLoginId("help");
        list.add(vo1);
        list.add(vo2);
        list.add(vo3);
        list.add(vo4);
        sortList(list);
        for(OrdinateSalaryChangeAppVo vo:list){
            logger.info(vo.getLoginId());
        }
    }

    private List<OrdinateSalaryChangeAppVo> sortList(List<OrdinateSalaryChangeAppVo> list){
        list.sort(Comparator.comparing(u->u.getLoginId()));
        return list;
    }

    @Test
    public void testGetDirect(){
        WorkGroupDto workGroup=new WorkGroupDto();
        Long userId=1086L;
        List<WorkGroupDto> allGroups = workGroupMapper.selectAllWithLeaderByGroupRoleId(WorkGroupRole.Role.salary);
        List<OrdinateSalaryChangeAppVo> ordinateSalaryChangeList = userSalaryChangeMapper.getQuarterRangePerformance(2016, 4);
        for (WorkGroupDto workGroupDto : allGroups) {
            if (userId.equals(workGroupDto.getLeaderId())) {
                workGroup = workGroupDto;
            }
        }
        groupTrackService.initHrGroupTreeByRootGroup(workGroup, allGroups, ordinateSalaryChangeList);
        List<OrdinateSalaryChangeAppVo> directMembers = groupTrackService.getDirectMember(workGroup);
//        groupTrackService.getWorkGroupIdAndSubGroupId(directMembers, workGroup);
        List<OrdinateSalaryChangeAppVo> list= groupTrackService.getDirectMember(workGroup);
        list.forEach(o->System.out.println(o.getUserId()));
    }
    @Test
    public void testSearch(){
        List<Long> groupIds=new ArrayList<>();
        groupIds.add(725L);
        groupIds.add(801L);
        groupIds.add(2001L);
        groupIds.add(2002L);
        Long begin=System.currentTimeMillis();
        userSalaryChangeAppService.getSubGroupMembers(false,267L,"待淘汰",groupIds,2016, 4);
        Long end=System.currentTimeMillis();
        System.out.println("-------"+(end-begin));
    }

}
