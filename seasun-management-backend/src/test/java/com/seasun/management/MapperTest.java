package com.seasun.management;

import com.seasun.management.dto.WorkGroupHrDto;
import com.seasun.management.dto.YearMonthDto;
import com.seasun.management.mapper.*;
import com.seasun.management.model.*;
import com.seasun.management.vo.CrashLogQueryConditionVo;
import com.seasun.management.vo.ProjectVo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
public class MapperTest {

    @Autowired
    private UserPerformanceMapper userPerformanceMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private CrashLogMapper crashLogMapper;

    @Autowired
    private ProjectUsedNameMapper projectUsedNameMapper;

    @Autowired
    private FnProjectStatDataDetailMapper fnProjectStatDataDetailMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private CostCenterMapper costCenterMapper;

    @Autowired
    private FloorMapper floorMapper;

    @Autowired
    private SubcompanyMapper subcompanyMapper;

    @Autowired
    private FnTaskMapper fnTaskMapper;

    @Autowired
    private UserChildrenInfoMapper userChildrenInfoMapper;

    @Autowired
    private FnProjectStatDataMapper fnProjectStatDataMapper;

    @Autowired
    private FnProjectSchedDataMapper fnProjectSchedDataMapper;

    @Autowired
    private FnSumShareConfigMapper fnSumShareConfigMapper;

    @Autowired
    private FnStatMapper fnStatMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SchoolMapper schoolMapper;

    @Autowired
    private RUserProjectPermMapper rUserProjectPermMapper;

    @Autowired
    private WorkGroupMapper workGroupMapper;

    @Autowired
    private PerformanceWorkGroupMapper performanceWorkGroupMapper;

    private static final Logger logger = LoggerFactory.getLogger(MapperTest.class);

    @Before
    public void before() {
        logger.debug("do some thing before test here...");
    }

    @Test
    public void testBaseCRUD() {
        // test delete
        departmentMapper.disableByPrimaryKey(13L);
        Department department = new Department();
        department.setId(13L);
        department.setName("seasunTestDepartment");
        department.setCity("珠海");
        department.setDescription("this is 描述");
        department.setActiveFlag(true);
        department.setCreateTime(new Date());
        department.setUpdateTime(new Date());

        // test insert
        departmentMapper.insert(department);

        // test find
        Department department1 = departmentMapper.selectByPrimaryKey(13L);
        logger.debug("find by id result is:{}", department1.getName());

        // test findAll
        // 注意：sqlbuilder 中 params，必须使用 #{param1} 的格式调用,以防sql注入
        List<Department> departmentList = departmentMapper.selectByCondition(new Department());
        logger.debug("total size is:{}", departmentList.size());

        // test update
        departmentMapper.updateNameById(13, "新部门");
        Department departmentCond = new Department();
        departmentCond.setName("新部门");
        List<Department> departments = departmentMapper.selectByCondition(departmentCond);
        logger.debug("find by name result is:{}", departments.get(0).getName());
    }

    @Test
    public void testAutoIncreaseId() {
        floorMapper.deleteByPrimaryKey(12L);
        Floor floor = new Floor();
        floor.setName("珠海一楼");
        floor.setCity("珠海");
        floorMapper.insert(floor);
        // here, id will auto increase .
        logger.debug("new floor id is:{}", floor.getId());
    }

    @Test
    public void testXmlDymSql() {
        Subcompany subcompany = new Subcompany();
        subcompany.setId(1L);
        subcompany.setName("dym名称");
        subcompany.setCode("oo8");
        subcompanyMapper.updateByPrimaryKeySelective(subcompany);
        Subcompany subcompany1 = subcompanyMapper.selectByPrimaryKey(1L);
        logger.debug("subcompany after update :{}", subcompany1.getName());
    }

    @Test
    public void testCriteriaQuery() {
        SchoolExample schoolExample = new SchoolExample();
        schoolExample.createCriteria().andIdEqualTo(1821L);
        List<School> schoolList = schoolMapper.selectByExample(schoolExample);
        logger.debug("school name is:{}", schoolList.get(0).getName());
    }

    @Test
    public void testDeleteByIn() {
        String[] codes = {"223", "225", "228"};
        //costCenterMapper.deleteByCodesNotIn(codes);
    }

    @Test
    public void testSelectByIn() {
        String[] codes = {"223", "225", "228"};
        //List<CostCenter> costCenters = costCenterMapper.selectByCodesIn(codes);
    }

    @Test
    public void testBatchDelete() {
        String[] userIds = {"223", "225", "228"};
        Map<String, Object> params = new HashMap<>();
        params.put("projectId", 1029L);
        params.put("userIds", userIds);

        rUserProjectPermMapper.deleteByProjectIdAndUserIdsNotIn(params);
    }

    @Test
    public void testBatchInsert() {
        List<UserChildrenInfo> userChildrenInfos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            UserChildrenInfo userChildrenInfo = new UserChildrenInfo();
            userChildrenInfo.setName("小明" + i);
            userChildrenInfo.setBirthday(new Date());
            userChildrenInfo.setGender(UserChildrenInfo.Gender.male);
            userChildrenInfo.setSchool("第五中学");
            userChildrenInfo.setUserId(1011L);
            userChildrenInfos.add(userChildrenInfo);
        }
        userChildrenInfoMapper.batchInsert(userChildrenInfos);
    }

    @Test
    public void testSqlProvider() {
        School school = new School();
        school.setId(1436L);
        school.setName("三峡大学");
        List<School> schools = schoolMapper.selectByCondition(school);
        logger.debug("result size:{}", schools.size());
    }

    @Test
    public void testManyRelationQuery() {
        List<ProjectUsedName> testProjectUsedName = projectUsedNameMapper.selectAll();
        List<Project> projects = projectMapper.selectAllWithRelation();
        for (Project project : projects) {
            if (project.getUsedNames().size() > 0) {
                logger.debug("used name:{}", project.getUsedNames().get(0).getName());
            }
        }
        logger.debug("result size:{}", projects.size());
    }

    @Test
    public void testJoinQuery() {
        List<ProjectVo> projects = projectMapper.selectAllWithOrderCodesStrAndUsedNamesStr();
    }

    @Test
    public void testLongWordInsert() {
        String message = "12345678901234567890123456789012345678901234567890" +
                "12345678901234567890123456789012345678901234567890" +
                "12345678901234567890123456789012345678901234567890" +
                "12345678901234567890123456789012345678901234567890" +
                "12345678901234567890123456789012345678901234567890";
        String message2 = "123456";// 1234567 will cause exception
        FnTask fnTask = new FnTask();
        fnTask.setStatus(FnTask.TaskStatus.discard);
        fnTask.setResultMessage(message + message2);
        fnTaskMapper.insert(fnTask);
    }

    @Test
    public void testAutoGenInsert() {
        FnTask fnTask = new FnTask();
        fnTask.setId(5000L);
        fnTask.setStatus(FnTask.TaskStatus.processing);
        fnTask.setType(FnTask.Type.project_excel_import);
        fnTaskMapper.insert(fnTask);
        logger.debug("new id is:{}", fnTask.getId());
    }

    @Test
    public void testViewSelect() {
        List<ProjectVo> projectVos = projectMapper.selectProjectView();
        logger.debug("find result size:{}", projectVos.size());
    }

    @Test
    public void testParamAnnotation() {
        /*
        @Delete("delete from fn_project_stat_data where year = #{year} and project_id = #{projectId}")
        int deleteByYearAndProjectId(@Param("year") int year,@Param("projectId") Long projectId);
        */
        fnProjectStatDataMapper.deleteByYearAndProjectId(2, 33L);
    }


    /**
     * 批量更新
     */
    @Test
    public void testBatchUpdate() {
        List<FnProjectSchedData> batchUpdateData = new ArrayList<>();
        FnProjectSchedData tmp = new FnProjectSchedData();
        tmp.setId(1L);
        tmp.setProjectId(1L);
        tmp.setTotalCost(45.2F);
        tmp.setHumanNumber(77.77F);
        tmp.setYear(2016);
        tmp.setMonth(6);
        batchUpdateData.add(tmp);
        FnProjectSchedData tmp1 = new FnProjectSchedData();
        tmp1.setId(2L);
        tmp1.setProjectId(1L);
        tmp1.setTotalCost(111.1F);
        tmp1.setHumanNumber(66.6F);
        tmp1.setYear(2016);
        tmp1.setMonth(10);
        batchUpdateData.add(tmp1);
        fnProjectSchedDataMapper.batchUpdateByPks(batchUpdateData);
    }

    @Test
    public void testProviderQueryByCustomParam() {
        fnProjectStatDataMapper.selectLeftJoinFnStatByYearAndMonthAndProjectId(2016, 10, 1000L);
    }

    @Test
    public void testRemoveByDepartmentIdAndCodesNotIn() {
        String[] codes = new String[]{"123", "456"};
        Map<String, Object> params = new HashMap<>();
        params.put("departmentId", 1029);
        params.put("codes", codes);
        costCenterMapper.removeByDepartmentIdAndCodesNotIn(params);
    }

    @Test
    public void testCustomSelectResultInXml() {
        CrashLogQueryConditionVo cond = new CrashLogQueryConditionVo();
        cond.setSystem("true");
        crashLogMapper.selectByCondition(cond);
    }

    @Test
    public void testCustomMapInSelectInXml() {
        List<Long> ids = new ArrayList<>();
        ids.add(12L);
        ids.add(14L);
        ids.add(13L);
        fnProjectStatDataDetailMapper.getDetailByProjectIdsAndYearAndMonthAndStatId(ids, 2017, 6, 20063L);
    }

    @Test
    public void otherTests() {
        logger.debug("do some other test here...");
        List<Long> ids = new ArrayList<>();
        ids.add(2L);
        ids.add(3L);
        List<YearMonthDto> result = userPerformanceMapper.selectYearMonthByPerformanceWorkGroupIds(ids);
        logger.debug("finish...result size is:" + result.size());
    }

    @Test // 没有满足的条件下，会返回空集合，而非null集合
    public void testEmptyCollectionSelect(){
        List<WorkGroupHrDto> workGroups = workGroupMapper.selectActiveWorkGroupHrDtoByProjectId(123L);
        logger.debug("finish...result size is:" + workGroups.size());
    }
    @After
    public void after() {
        logger.debug("do some thing after test here...");
    }

}

