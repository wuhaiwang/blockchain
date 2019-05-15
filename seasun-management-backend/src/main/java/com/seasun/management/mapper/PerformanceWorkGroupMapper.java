package com.seasun.management.mapper;

import com.seasun.management.dto.PerformanceWorkGroupDto;
import com.seasun.management.dto.PerformanceWorkGroupManagerDto;
import com.seasun.management.dto.WorkGroupDto;
import com.seasun.management.dto.WorkGroupMemberDto;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.PerformanceWorkGroup;
import com.seasun.management.model.WorkGroup;
import com.seasun.management.vo.PerformanceWorkGroupInfo;
import com.seasun.management.vo.PerformanceWorkGroupVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface PerformanceWorkGroupMapper {
    @Delete({
            "delete from performance_work_group",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into performance_work_group (work_group_id, name, ",
            "strict_type, parent, performance_manager_id, ",
            "active_flag, project_confirm_flag)",
            "values (#{workGroupId,jdbcType=BIGINT}, #{name,jdbcType=VARCHAR}, ",
            "#{strictType,jdbcType=BIT}, #{parent,jdbcType=BIGINT}, #{performanceManagerId,jdbcType=BIGINT}, ",
            "#{activeFlag,jdbcType=BIT}, #{projectConfirmFlag,jdbcType=BIT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(PerformanceWorkGroup record);

    int insertSelective(PerformanceWorkGroup record);

    @Select({
            "select",
            "id, work_group_id, name, strict_type, parent, performance_manager_id, active_flag, ",
            "project_confirm_flag",
            "from performance_work_group",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.PerformanceWorkGroupMapper.BaseResultMap")
    PerformanceWorkGroup selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PerformanceWorkGroup record);

    @Update({
            "update performance_work_group",
            "set work_group_id = #{workGroupId,jdbcType=BIGINT},",
            "name = #{name,jdbcType=VARCHAR},",
            "strict_type = #{strictType,jdbcType=BIT},",
            "parent = #{parent,jdbcType=BIGINT},",
            "performance_manager_id = #{performanceManagerId,jdbcType=BIGINT},",
            "active_flag = #{activeFlag,jdbcType=BIT},",
            "project_confirm_flag = #{projectConfirmFlag,jdbcType=BIT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(PerformanceWorkGroup record);

    /* the flowing are user defined ... */

    @Select("select id, work_group_id, name, strict_type, parent, active_flag, performance_manager_id as leader_id from performance_work_group")
    List<WorkGroupDto> selectAll();

    @Select("select id, parent from performance_work_group where active_flag= 1")
    List<WorkGroup> selectAllIdParentWorkGroup();

    @Select("select * from performance_work_group where active_flag =1")
    List<PerformanceWorkGroup> selectAllActiveRecords();

    @Select("select * from performance_work_group")
    List<PerformanceWorkGroup> selectAllPerformanceWorkGroup();

    @Select("select id, work_group_id, name, strict_type, parent, active_flag, project_confirm_flag, performance_manager_id as leader_id from performance_work_group where active_flag = 1")
    List<WorkGroupDto> selectAllByActive();

    @Select("select * from performance_work_group where performance_manager_id = #{userId} and active_flag = 1")
    List<PerformanceWorkGroup> selectAllByManagerId(Long userId);

    @Select("select * from performance_work_group where active_flag = 1")
    List<PerformanceWorkGroup> selectAllActiveWorkGroup();


    @Select("select p.*," +
            "( select count(*) from performance_work_group where parent = p.id) as childGroupCount," +
            "( select perf_work_group_id from user where id = p.performance_manager_id ) as managerPerformanceGroupId," +
            "( select count(*) from user where perf_work_group_id = p.id) as memberCount from performance_work_group p where p.active_flag = 1")
    List<PerformanceWorkGroupInfo> selectAllWorkGroupWithManagerPerfWorkGroupId();

    @Select({
            "select pwg.* ,concat(user.last_name,user.first_name) manager_name, g.name work_group_name",
            "from performance_work_group pwg",
            "left join user on pwg.performance_manager_id = user.id and user.active_flag = 1",
            "left join work_group g on g.id = pwg.work_group_id",
            "where pwg.id=#{id}"
    })
    PerformanceWorkGroupVo selectPerformanceWorkGroupVoById(Long id);

    @Select({
            "select pwg.* ,concat(user.last_name,user.first_name) manager_name",
            "from performance_work_group pwg",
            "left join user on pwg.performance_manager_id = user.id",
            "where pwg.active_flag = 1"
    })
    List<PerformanceWorkGroupVo> selectAllWithLeader();

    void updateActiveAllInList(List<Long> list);

    @Select("SELECT * FROM performance_work_group WHERE name = '西山居'")
    PerformanceWorkGroup selectByRoot();

    @Delete("truncate table performance_work_group")
    int truncateTable();

    @Update("update performance_work_group set performance_manager_id = null where performance_manager_id = #{0}")
    int clearByPerformanceManagerId(Long managerId);

    @Select("select * from performance_work_group where performance_manager_id is null")
    List<PerformanceWorkGroup> selectEmptyManager();

    @Select({
            "select *",
            "from performance_work_group",
            "where id in (select perf_work_group_id from user where work_group_id = #{workGroupId})"
    })
    List<PerformanceWorkGroup> selectAllByUserHrWorkGroupId(Long workGroupId);

    @Select({
            "select pwg.*, p.id project_id, concat(u.last_name,u.first_name) manager_name",
            "from performance_work_group pwg",
            "left join project p on pwg.work_group_id = p.work_group_id",
            "left join user u on u.id = pwg.performance_manager_id",
            "where pwg.id in (select perf_work_group_id from user where work_group_id = #{workGroupId})"
    })
    List<PerformanceWorkGroupDto> selectAllWithProjectIdByUserHrWorkGroupId(Long workGroupId);

    @Select({
            "select pwg.*, p.id project_id, concat(u.last_name,u.first_name) manager_name",
            "from performance_work_group pwg",
            "left join project p on pwg.work_group_id = p.work_group_id",
            "left join user u on u.id = pwg.performance_manager_id",
            "where pwg.project_confirm_flag = 1 and pwg.active_flag = 1"
    })
    List<PerformanceWorkGroupDto> selectAllByProjectConfirm();

    @Select({
            "select pwg.*, p.id project_id, concat(u.last_name,u.first_name) manager_name",
            "from performance_work_group pwg",
            "left join project p on pwg.work_group_id = p.work_group_id",
            "left join user u on u.id = pwg.performance_manager_id",
            "where pwg.performance_manager_id = #{userId} and pwg.active_flag = 1"
    })
    List<PerformanceWorkGroupDto> selectAllWithProjectIdByManagerId(Long userId);

    @Select({
            "select pwg.*, p.id project_id, concat(u.last_name,u.first_name) manager_name",
            "from performance_work_group pwg",
            "left join project p on pwg.work_group_id = p.work_group_id",
            "left join user u on u.id = pwg.performance_manager_id",
            "where p.id = #{projectId} and pwg.active_flag = 1",
            "limit 1"
    })
    PerformanceWorkGroupDto selectWithProjectIdByProjectId(Long projectId);

    List<PerformanceWorkGroupDto> selectWithProjectIdByProjectIds(List<Long> projectIds);

    @Select({
            "select pwg.*, p.id project_id, concat(u.last_name,u.first_name) manager_name",
            "from performance_work_group pwg",
            "left join project p on pwg.work_group_id = p.work_group_id",
            "left join user u on u.id = pwg.performance_manager_id",
            "where pwg.id = #{performanceWorkGroupId} and pwg.active_flag = 1",
            "limit 1"
    })
    PerformanceWorkGroupDto selectWithProjectIdByPerformanceWorkGroupId(Long performanceWorkGroupId);

    @Select({
            "select pwg.*, p.id project_id, concat(u.last_name,u.first_name) manager_name,u.login_id managerLoginId",
            "from performance_work_group pwg",
            "left join project p on pwg.work_group_id = p.work_group_id",
            "left join user u on u.id = pwg.performance_manager_id",
            "where pwg.active_flag = 1"
    })
    List<PerformanceWorkGroupDto> selectAllWithProjectIdByActive();

    void batchDeleteByPks(List<Long> ids);

    @Select({"SELECT p.id ,p.`name` FROM performance_work_group p  WHERE p.id IN ",
            "(SELECT r.performance_work_group_id FROM r_user_performance_perm r ",
            "WHERE r.performance_work_group_role_id = 1 ",
            "AND r.user_id = #{userId})"})
    List<IdNameBaseObject> selectUserDataPermission(Long userId);


    @Select({"SELECT * FROM performance_work_group WHERE performance_manager_id = #{userId} AND active_flag = 1"})
    List<PerformanceWorkGroup> selectByUserId(Long userId);

    @Select({"SELECT p.id  workGroupId, p.name workGroupName ,p.parent parentId , p.performance_manager_id  leaderId , CONCAT(u.last_name,u.first_name) as leaderName , u.login_id as leaderLoginId",
            "FROM performance_work_group p LEFT JOIN user u ON u.id=p.performance_manager_id",
            "WHERE p.active_flag = 1"})
    List<PerformanceWorkGroupManagerDto> selectAllActivePerformanceWorkGroupAndManager();

    @Select({"SELECT COUNT(*) FROM performance_work_group WHERE active_flag = 1 AND performance_manager_id = #{userId}"})
    int selectCountByManagerId(@Param("userId") Long userId);

    @Select({"select w.*,u.total from (select id,parent from performance_work_group where active_flag=1) w left join (select perf_work_group_id,count(1) total from user where active_flag=1 and virtual_flag=0 group by perf_work_group_id) u on u.perf_work_group_id=w.id"})
    List<WorkGroupMemberDto> selectAllActiveWorkGroupMember();

    @Select({"update performance_work_group set work_group_id = null where id =#{0}"})
    void updateWorkGroupById(Long id);

    List<PerformanceWorkGroup> selectByIds(@Param("list") List<Long> perfWorkGroupIds);

    @Select("select id from performance_work_group WHERE performance_manager_id  =#{userId}")
    List<Long> selectWorkGroupIdByUserId(Long userId);

}