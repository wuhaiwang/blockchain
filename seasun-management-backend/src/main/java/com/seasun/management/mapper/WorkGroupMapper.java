package com.seasun.management.mapper;

import java.util.List;

import com.seasun.management.dto.*;
import com.seasun.management.model.IdNameBaseObject;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import com.seasun.management.model.WorkGroup;
import com.seasun.management.util.MyStringUtils;
import com.seasun.management.vo.WorkGroupOrgVo;
import com.seasun.management.vo.WorkGroupPerformanceVo;
import org.springframework.cache.annotation.Cacheable;

public interface WorkGroupMapper {

    @Delete({
            "delete from work_group",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into work_group (name, parent, ",
            "active_flag, share_week_flag)",
            "values (#{name,jdbcType=VARCHAR}, #{parent,jdbcType=BIGINT}, ",
            "#{activeFlag,jdbcType=BIT}, #{shareWeekFlag,jdbcType=BIT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(WorkGroup record);

    int insertSelective(WorkGroup record);

    @Select({
            "select",
            "id, name, parent, active_flag, share_week_flag",
            "from work_group",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.WorkGroupMapper.BaseResultMap")
    WorkGroup selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(WorkGroup record);

    @Update({
            "update work_group",
            "set name = #{name,jdbcType=VARCHAR},",
            "parent = #{parent,jdbcType=BIGINT},",
            "active_flag = #{activeFlag,jdbcType=BIT},",
            "share_week_flag = #{shareWeekFlag,jdbcType=BIT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(WorkGroup record);

    /* the flowing are user defined ... */
    @Insert({
            "insert into work_group (id, name, parent, ",
            "active_flag, share_week_flag)",
            "values (#{id,jdbcType=BIGINT}, #{name,jdbcType=VARCHAR}, #{parent,jdbcType=BIGINT}, ",
            "#{activeFlag,jdbcType=BIT},#{shareWeekFlag,jdbcType=BIT})"
    })
    int insertWithId(WorkGroup record);

    @Select({"select * from work_group ",
            "where id in ",
            "(select work_group_id from user where order_center_id in",
            "(select id from order_center where project_id in ",
            "(select id from project where name = #{projectName})) and work_group_id is not null group by work_group_id",
            " )"})
    List<WorkGroup> getGroupListByProject(String projectName);

    @Select("SELECT * FROM work_group where active_flag =1 and id in (select work_group_id from user where active_flag =1 group by work_group_id )")
    List<WorkGroup> selectValidGroup();

    @Select("select * from work_group where active_flag = 1")
    List<WorkGroup> selectAllByActive();

    @Select("select * from work_group where id not in (select work_group_id from user where active_flag =1 group by work_group_id)")
    List<WorkGroup> selectNoUserGroup();

    @Select({"select *, ",
            "(select count(*) from user where work_group_id = g.id and order_center_id in",
            "(select id from order_center where project_id in (select id from project where id = #{projectId} or parent_hr_id = #{projectId}))",
            ") as member_number",
            "from work_group g",
            "where g.id in",
            "(select work_group_id from user where order_center_id in",
            "(select id from order_center where project_id in (select id from project where id = #{projectId} or parent_hr_id = #{projectId}))",
            "and work_group_id is not null group by work_group_id)",
            "or g.parent in",
            "(select work_group_id from user where order_center_id in",
            "(select id from order_center where project_id in (select id from project where id = #{projectId} or parent_hr_id = #{projectId}))",
            "and work_group_id is not null group by work_group_id)",
            "order by g.name"
    })
    List<WorkGroup> selectAllGroupByProjectId(Long projectId);

    @Select({"select *, ",
            "(select count(*) from user where active_flag=1 and virtual_flag=0 and work_group_id = g.id and order_center_id in",
            "(select id from order_center where project_id in (select id from project where id = #{projectId} or parent_hr_id = #{projectId}))",
            ") as member_number",
            "from work_group g",
            "where g.id in",
            "(select work_group_id from user where active_flag=1 and virtual_flag=0 and order_center_id in",
            "(select id from order_center where project_id in (select id from project where id = #{projectId} or parent_hr_id = #{projectId}))",
            "and work_group_id is not null group by work_group_id)",
            "or g.parent in",
            "(select work_group_id from user where active_flag=1 and virtual_flag=0 and order_center_id in",
            "(select id from order_center where project_id in (select id from project where id = #{projectId} or parent_hr_id = #{projectId}))",
            "and work_group_id is not null group by work_group_id)",
            "order by g.name"
    })
    List<WorkGroup> selectAllActiveGroupByProjectId(Long projectId);

    @Select({"select * from work_group where parent=#{parent}"})
    List<WorkGroup> selectAllGroupByParentId(long parent);

    @Select({"select id, parent from work_group WHERE active_flag =1"})
    List<WorkGroup> selectAllIdParentWorkGroup();

    @Select({"SELECT count(*) FROM",
            "(SELECT * from r_user_work_group_perm ",
            "where user_id=#{userId})",
            "x LEFT JOIN work_group w ON w.id=x.work_group_id ",
            "WHERE performance_flag=0",
            "and active_flag=1"})
    int queryLegalPerformanceGroupCount(long userId);

    @Select({"SELECT work_group_id FROM",
            "(select work_group_id,user_id FROM r_user_work_group_perm )",
            "ru LEFT JOIN work_group w on ru.work_group_id=w.id",
            "where user_id= #{userId}",
            "and performance_flag=0",
            "and active_flag=1"})
    List<Long> selectDirectPerformanceLegalWorkGroup(long userId);

    @Select({"SELECT work_group_id FROM",
            "(select work_group_id,user_id FROM r_user_work_group_perm )",
            "ru LEFT JOIN work_group w on ru.work_group_id=w.id",
            "where user_id= #{userId}",
            "and salary_change_flag=0",
            "and active_flag=1"})
    List<Long> selectDirectSalaryLegalWorkGroup(long userId);

    @Select({"SELECT count(*) FROM",
            "(SELECT * from r_user_work_group_perm ",
            "where user_id=#{userId})",
            "x LEFT JOIN work_group w ON w.id=x.work_group_id ",
            "WHERE salary_change_flag=0",
            "and active_flag=1"})
    int queryLegalSalaryGroupCount(long userId);

    @Select({
            "select w.* ",
            "from work_group w",
            "left join r_user_work_group_perm r on r.work_group_id=w.id",
            "where r.work_group_role_id = #{groupRoleId} and w.active_flag = 1 and r.user_id = #{userId}"
    })
    List<WorkGroup> selectAllByLeaderAndGroupRoleId(@Param("userId") long userId, @Param("groupRoleId") long groupRoleId);

    @Select("select w.*, r.user_id as leaderId from work_group w left join r_user_work_group_perm r on w.id = r.work_group_id and r.work_group_role_id = #{groupRoleId}")
    List<WorkGroupDto> selectAllWithLeaderByGroupRoleId(@Param("groupRoleId") Long groupRoleId);

//    @Cacheable(value = "allWorkGroupGroup")
    @Select({
            "select w.*, (select group_concat(user_id) from r_user_work_group_perm r where r.work_group_id = w.id and r.work_group_role_id = 3) as leader_ids",
            "from work_group w ",
            "where w.active_flag = 1"
    })
    List<HrWorkGroupDto> selectAllActiveWithLeaderByHR();

    @Select({
            "select w.*, (select group_concat(user_id) from r_user_work_group_perm r where r.work_group_id = w.id and r.work_group_role_id = 3) as leader_ids",
            "from work_group w ",
            "where w.active_flag = 1 and (w.id in",
            "(select work_group_id from user where order_center_id in",
            "(select id from order_center where project_id in (select id from project where id = #{projectId} or parent_hr_id = #{0}))",
            "and work_group_id is not null group by work_group_id)",
            "or w.parent in",
            "(select work_group_id from user where order_center_id in",
            "(select id from order_center where project_id in (select id from project where id = #{projectId} or parent_hr_id = #{0}))",
            "and work_group_id is not null group by work_group_id))"
    })
    List<HrWorkGroupDto> selectActiveWithLeaderByProjectId(Long projectId); // 返回所有组，跟是否配置人力负责人无关。

    @Select({
            "select w.*, (select group_concat(user_id) from r_user_work_group_perm r where r.work_group_id = w.id and r.work_group_role_id = 3) as leader_ids",
            "from work_group w ",
            "where w.active_flag = 1 and w.id in",
            "(select work_group_id from user where active_flag=1 and virtual_flag=0 and order_center_id in",
            "(select id from order_center where project_id in (select id from project where id = #{projectId} ))",
            "and work_group_id is not null group by work_group_id)",
    })
    // 返回当前平台下的人力组(不包含子平台的人力组)
    List<HrWorkGroupDto> selectHrWorkGroupDtoByProjectId(Long projectId);

    @Select({"select* FROM(select w.*, r.user_id as leaderId ,r.work_group_role_id from work_group w left join r_user_work_group_perm r ",
            "on w.id = r.work_group_id)s WHERE s.work_group_role_id = #{groupRoleId}"})
    List<WorkGroupDto> selectAllSelectiveGroupByRole(@Param("groupRoleId") Long groupRoleId);

    @Select({
            "select w.*, r.user_id leader_id",
            "from work_group w",
            "left join r_user_work_group_perm r on w.id = r.work_group_id and r.work_group_role_id = #{workGroupRoleId}",
            "where w.id=#{workGroupId}"
    })
    WorkGroupDto selectWithLeaderByPrimaryKey(@Param("workGroupId") Long workGroupId, @Param("workGroupRoleId") Long workGroupRoleId);

    @Select("select d.*,concat(u.last_name,u.first_name) as manager from (select w.*,r.user_id from (select *, (select count(*) from user where work_group_id = g.id ) as member_count from work_group g where g.active_flag = 1 order by g.name) w left join r_user_work_group_perm r on w.id = r.work_group_id and r.work_group_role_id =3 \n" +
            "group by id ) d left join user u on d.user_id = u.id;")
    List<WorkGroupOrgVo> selectWithGroupInfo();

    @Select({"select (select name from work_group where id = m.parent) as parentName, m.* from (select a.*,group_concat(login_id) as login_id , group_concat(full_name) as user_name from \n" +
            "            (select w.*,r.user_id,work_group_role_id from work_group w left join r_user_work_group_perm r on w.id = r.work_group_id \n" +
            "            union \n" +
            "            select  w.*,r.user_id,work_group_role_id from work_group w right join r_user_work_group_perm r on w.id = r.work_group_id) \n" +
            "             a left join (select s.id ,s.login_id,concat(s.last_name,s.first_name) full_name from user s) u on a.user_id = u.id  \n" +
            "             where a.id = #{0} group by id,work_group_role_id) m;"})
    List<WorkGroupPerformanceVo> selectAllGroupWithManagerInfoById(Long workGroupId);

    WorkGroupDto selectWithManagerByPrimaryKey(Long workGroupId, Long roleId);

    @Select("SELECT work_group_id FROM user WHERE work_group_id IS NOT NULL AND active_flag=1 AND perf_work_group_id IS NULL ")
    List<String> test();

    @SelectProvider(type = WorkGroupSqlBuilder.class, method = "buildSelectParentIdByIds")
    List<WorkGroup> selectWorkGroupsByIds(@Param("ids") List<Long> ids);

    List<WorkGroupHrDto> selectWorkGroupHrDtoByCond(WorkGroup cond);

    @Select({"select u.work_group_id id ,p.id parent from user u left join order_center o on o.id=u.order_center_id left join project p on p.id=o.project_id where u.active_flag=1 and u.virtual_flag=0 and u.work_group_id is not null and p.id is not null group by u.work_group_id,p.id "})
    List<BaseParentDto> selectActiveWorkGroupIdWithProjectId();

    @Select({"select g.*,r.user_id hrId ",
            "from work_group g left join r_user_work_group_perm r on g.id=r.work_group_id",
            "where r.work_group_role_id = 3 and g.active_flag=1 and ( g.id in",
            "(select work_group_id from user where order_center_id in",
            "(select id from order_center where project_id in (select id from project where id = #{projectId} )",
            "and work_group_id is not null group by work_group_id)"
    })
    // 只返回有人管理的组(不包含子平台的人力组)，适合按人力负责人查询。
    List<WorkGroupHrDto> selectActiveWorkGroupHrDtoByProjectId(Long projectId);


    class WorkGroupSqlBuilder {
        public String buildSelectParentIdByIds(List<Long> ids) {
            return new SQL() {{
                SELECT("*");
                FROM("work_group");
                WHERE("id in (" + String.join(",", MyStringUtils.listConvertArray(ids)) + ")");
                WHERE("active_flag = 1");
            }}.toString();
        }
    }

    @Select({"select w.*,u.total from (select id,parent from work_group where active_flag=1) w left join (select work_group_id,count(1) total from user where active_flag=1 and virtual_flag=0 group by work_group_id) u on u.work_group_id=w.id"})
    List<WorkGroupMemberDto> selectAllActiveWorkGroupMember();

    @Select({"select w.*,r.user_id hrId from (select * from work_group where active_flag=1) w left join (select * from r_user_work_group_perm where work_group_role_id=#{roleId}) r on r.work_group_id=w.id ORDER BY id"})
    List<WorkGroupHrDto> getActiveWorkGroupByRole(Long roleId);

    @Select({"select w.*,r.user_id hrId from work_group w left join r_user_work_group_perm r on r.work_group_id=w.id where w.active_flag=1 and r.work_group_role_id=3 and r.user_id=#{0}"})
    List<WorkGroupHrDto> selectActiveWorkGroupHrDtoByHrId(Long hrId);

    @Select({
            "select",
            "id, name, parent",
            "from work_group",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.WorkGroupMapper.BaseResultMap")
    WorkGroup selectById(Long id);

    @Select({
            "select",
            "id, name, parent",
            "from work_group"
    })
    List<WorkGroup> selectAll();


    @Select({
            "select * ",
            "from work_group"
    })
    List<WorkGroup> selectAllGroup();
}