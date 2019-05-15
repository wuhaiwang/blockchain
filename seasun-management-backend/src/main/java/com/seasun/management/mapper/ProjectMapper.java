package com.seasun.management.mapper;

import com.seasun.management.dto.FnProjectInfoDto;
import com.seasun.management.dto.PlatAppDto;
import com.seasun.management.dto.ProjectMaxMemberDto;
import com.seasun.management.helper.ReportHelper;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.Project;
import com.seasun.management.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;

public interface ProjectMapper {
    @Delete({
            "delete from project",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into project (name, short_name, ",
            "en_short_name, logo, ",
            "status, type, service_line, ",
            "description, contact_name, ",
            "active_flag, virtual_flag, ",
            "share_flag, finance_flag, ",
            "hr_flag, org_flag, app_show_mode, ",
            "parent_share_id, parent_hr_id, ",
            "parent_fn_sum_id, hr_list, ",
            "city, establish_time, ",
            "max_member, work_group_id, ",
            "create_time, update_time)",
            "values (#{name,jdbcType=VARCHAR}, #{shortName,jdbcType=VARCHAR}, ",
            "#{enShortName,jdbcType=VARCHAR}, #{logo,jdbcType=VARCHAR}, ",
            "#{status,jdbcType=VARCHAR}, #{type,jdbcType=VARCHAR}, #{serviceLine,jdbcType=VARCHAR}, ",
            "#{description,jdbcType=VARCHAR}, #{contactName,jdbcType=VARCHAR}, ",
            "#{activeFlag,jdbcType=BIT}, #{virtualFlag,jdbcType=BIT}, ",
            "#{shareFlag,jdbcType=BIT}, #{financeFlag,jdbcType=BIT}, ",
            "#{hrFlag,jdbcType=BIT}, #{orgFlag,jdbcType=BIT}, #{appShowModel,jdbcType=TINYINT}, ",
            "#{parentShareId,jdbcType=BIGINT}, #{parentHrId,jdbcType=BIGINT}, ",
            "#{parentFnSumId,jdbcType=BIGINT}, #{hrList,jdbcType=VARCHAR}, ",
            "#{city,jdbcType=VARCHAR}, #{establishTime,jdbcType=DATE}, ",
            "#{maxMember,jdbcType=INTEGER}, #{workGroupId,jdbcType=BIGINT}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(Project record);

    int insertSelective(Project record);

    @Select({
            "select",
            "id, name, short_name, en_short_name, logo, status, type, service_line, description, ",
            "contact_name, active_flag, virtual_flag, share_flag, finance_flag, hr_flag, ",
            "org_flag, app_show_mode, parent_share_id, parent_hr_id, parent_fn_sum_id, hr_list, ",
            "city, establish_time, max_member, work_group_id, create_time, update_time",
            "from project",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.ProjectMapper.BaseResultMap")
    Project selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Project record);

    @Update({
            "update project",
            "set name = #{name,jdbcType=VARCHAR},",
            "short_name = #{shortName,jdbcType=VARCHAR},",
            "en_short_name = #{enShortName,jdbcType=VARCHAR},",
            "logo = #{logo,jdbcType=VARCHAR},",
            "status = #{status,jdbcType=VARCHAR},",
            "type = #{type,jdbcType=VARCHAR},",
            "service_line = #{serviceLine,jdbcType=VARCHAR},",
            "description = #{description,jdbcType=VARCHAR},",
            "contact_name = #{contactName,jdbcType=VARCHAR},",
            "active_flag = #{activeFlag,jdbcType=BIT},",
            "virtual_flag = #{virtualFlag,jdbcType=BIT},",
            "share_flag = #{shareFlag,jdbcType=BIT},",
            "finance_flag = #{financeFlag,jdbcType=BIT},",
            "hr_flag = #{hrFlag,jdbcType=BIT},",
            "org_flag = #{orgFlag,jdbcType=BIT},",
            "app_show_mode = #{appShowMode,jdbcType=TINYINT},",
            "parent_share_id = #{parentShareId,jdbcType=BIGINT},",
            "parent_hr_id = #{parentHrId,jdbcType=BIGINT},",
            "parent_fn_sum_id = #{parentFnSumId,jdbcType=BIGINT},",
            "hr_list = #{hrList,jdbcType=VARCHAR},",
            "city = #{city,jdbcType=VARCHAR},",
            "establish_time = #{establishTime,jdbcType=DATE},",
            "max_member = #{maxMember,jdbcType=INTEGER},",
            "work_group_id = #{workGroupId,jdbcType=BIGINT},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(Project record);

    /* the flowing are user defined ... */
    int insertSelectiveWithId(Project record);

    // 项目负责人角色id固定为1
    @Select({"select p.*, m.usedNamesStr ,n.join_name as orderCenterCodesStr, r.userIds managerIds, pr.name parentShareName, g.name workGroupName from project p",
            "left join project pr on pr.id=p.parent_share_id",
            "left join v_project_used_name m on p.id = m.project_id",
            "left join (select project_id,group_concat(b.code) as join_name from order_center b group by project_id) n on p.id = n.project_id",
            "left join (select project_id, group_concat(user_id) userIds from r_user_project_perm where project_role_id=1 group by project_id) r on r.project_id=p.id",
            "left join work_group g on g.id = p.work_group_id",
            "where p.id=#{id, jdbcType=BIGINT}"})
    ProjectVo selectWithOrderCodesStrAndUsedNamesStrByPrimaryKey(Long id);

    // 项目负责人角色id固定为1
    @Select({"select p.*, m.usedNamesStr,n.join_name as orderCenterCodesStr, r.userIds managerIds, pr.name parentShareName",
            "from project p",
            "left join project pr on pr.id=p.parent_hr_id",
            "left join v_project_used_name m on p.id = m.project_id",
            "left join (select project_id,group_concat(b.code) as join_name from order_center b group by project_id) n on p.id = n.project_id",
            "left join (select project_id, group_concat(user_id) userIds from r_user_project_perm where project_role_id=1 group by project_id) r on r.project_id=p.id "})
    List<ProjectVo> selectAllWithOrderCodesStrAndUsedNamesStr();

    //选择所有平台
    @Select({"select p.*, m.usedNamesStr,n.join_name as orderCenterCodesStr, r.userIds managerIds, pr.name parentShareName",
            "from project p",
            "left join project pr on pr.id=p.parent_hr_id",
            "left join v_project_used_name m on p.id = m.project_id",
            "left join (select project_id,group_concat(b.code) as join_name from order_center b group by project_id) n on p.id = n.project_id",
            "left join (select project_id, group_concat(user_id) userIds from r_user_project_perm where project_role_id=1 group by project_id) r on r.project_id=p.id WHERE p.active_flag = 1 AND p.service_line= '平台'"})
    List<ProjectVo> selectAllPlatWithOrderCodesStrAndUsedNamesStr();

    // 项目负责人角色id固定为1
    @Select({"select p.*, m.usedNamesStr,(select group_concat(code) from order_center o left join project op on o.project_id = op.id where op.id = p.id or op.parent_hr_id = p.id) as orderCenterCodesStr, r.userIds managerIds, pr.name parentShareName",
            "from project p",
            "left join project pr on pr.id=p.parent_hr_id",
            "left join v_project_used_name m on p.id = m.project_id",
            "left join (select project_id, group_concat(user_id) userIds from r_user_project_perm where project_role_id=1 group by project_id) r on r.project_id=p.id"})
    List<ProjectVo> selectAllWithOrderCodesStrAndUsedNamesStrByParent();

    @Select("select m.usedNamesStr,n.join_name as orderCenterCodesStr,p.* from project p left join " +
            "v_project_used_name m on p.id = m.project_id left join" +
            "(select project_id,group_concat(b.code) as join_name from order_center b group by project_id) n on p.id = n.project_id")
    @Results(
            {
                    @Result(property = "id", column = "id"),
                    @Result(property = "orders", column = "id",
                            many = @Many(select = "com.seasun.management.mapper.OrderCenterMapper.selectByProject")),
                    @Result(property = "usedNames", column = "id",
                            many = @Many(select = "com.seasun.management.mapper.ProjectUsedNameMapper.selectByProject")),
            })
    List<Project> selectAllWithRelation();

    @Select("select distinct(a.id) from project a , project b where b.parent_share_id = a.id;")
    List<Project> selectAllParentSharePlat();

    @Select("select * from project where parent_share_id=#{parentSharePlatId}")
    List<Project> selectAllSubSharePlat(Long parentSharePlatId);

    @Select("select * from project where parent_hr_id=#{parentHrPlatId} and active_flag=1")
    List<Project> selectAllSubHrPlat(Long parentHrPlatId);

    @Select("select * from project where (parent_hr_id=#{projectId} and active_flag=1) or id=#{projectId}")
        //@Select("select * from project where (id=#{projectId})")
    List<Project> selectAllSubHrPlatWithParent(Long projectId);

    List<Project> selectByIdsOrParentHrIds(List<Long> projectIds);

    List<Project> selectByIds(List<Long> projectIds);

    @Select("select * from v_project_name_info where active_flag = 1 and share_flag =1 and service_line = '平台'")
    List<ProjectVo> selectAllSharePlat();

    @Select("select * from v_project_name_info where active_flag = 1 and share_flag =1 and service_line != '平台' and service_line != '发行'")
    List<ProjectVo> selectAllShareProject();

    @Select("select * from v_project_name_info where active_flag = 1 and finance_flag =1 and (service_line != '平台' or (service_line = '平台' and name = '西山居世游'))")
    List<ProjectVo> selectAllFNProject();

    @Select("select * from v_project_name_info where active_flag = 1 and share_flag =1 ")
    List<ProjectVo> selectAllProjectAndPlat();

    @Select("select * from v_project_name_info where active_flag = 1")
    List<ProjectVo> selectAllActiveProject();

    @Select("select * from v_project_name_info where active_flag = 0")
    List<ProjectVo> selectAllInActiveProject();

    @Select("select * from project a left join project_used_name b on a.id = b.project_id " +
            "where a.active_flag = 1 and a.share_flag=1 and (a.name= #{name} or a.short_name = #{name} or b.name =  #{name}) limit 1")
    Project selectByUsedName(String name);

    @Select("select * from v_project_name_info")
    List<ProjectVo> selectProjectView();

    @Update("update project set name = concat(name,'-废弃'), active_flag = 0 where id = #{id}")
    int disableByPrimaryKey(Long id);

    @Update("update project set hr_flag=0 where id=#{id}")
    int diableHrProjectByPrimaryKey(Long id);

    @SelectProvider(type = ProjectSqlBuilder.class, method = "buildSelectByCondition")
    List<Project> selectByCondition(Project project);

    // 项目负责人角色id固定为1
    @Select({"select #{year} as year, #{month} as month, a.id, a.name, a.establish_time, a.type,a.max_member, a.member_number, a.logo, a.work_group_id, a.city,",
            "(select group_concat(concat(u.last_name, u.first_name)) from r_user_project_perm r left join user u on u.id = r.user_id where r.project_id = a.id and r.project_role_id = 1) as contact_name,",
            "(select sum(share_number) from fn_share_data where project_id = a.id and year = #{year} and month = #{month}) as plat_share_number,",
            "(select sum(value) from fn_project_stat_data b left join fn_stat c on b.fn_stat_id = c.id where c.name = '收入' and b.project_id = a.id and b.year = #{year} and b.month = #{month}) as income,",
            "(select sum(value) from fn_project_stat_data b left join fn_stat c on b.fn_stat_id = c.id where c.name = '收入' and b.project_id = a.id and b.year = #{lastYear} and b.month = #{lastMonth}) as last_month_income,",
            "(select sum(value) from fn_project_stat_data b left join fn_stat c on b.fn_stat_id = c.id where c.name = '费用合计(直接+分摊)' and b.project_id = a.id and b.year = #{year} and b.month = #{month}) as cost,",
            "(select sum(value) from fn_project_stat_data b left join fn_stat c on b.fn_stat_id = c.id where c.name = '费用合计(直接+分摊)' and b.project_id = a.id and b.year = #{lastYear} and b.month = #{lastMonth}) as last_month_cost,",
            "((select ifnull(sum(value), 0) from fn_project_stat_data b left join fn_stat c on b.fn_stat_id = c.id where c.name = '费用合计(直接+分摊)' and b.project_id = a.id and b.year >= 2017 and b.month <> 0 and (b.year < #{year} or (b.year = #{year} and b.month <= #{month}))) + ",
            "(select ifnull(max(total_cost), 0) from fn_project_sched_data where project_id = a.id and year = 2017 and month = 0)) as total_cost,",
            "(select ifnull(max(expect_income), 0) from fn_project_sched_data where project_id = a.id and year = #{year} and month = 0) as expect_income,",
            "(SELECT COUNT(*) FROM fn_sum_share_config WHERE (project_id=a.id AND YEAR=#{lastYear} AND MONTH=#{lastMonth})) > 0  AS has_sum_share_data,",
            "(select ifnull(sum(budget_amount),0) from cp_budget where budget_year=#{year} and cp_project_id in (select c.cp_project_id from cp_project_relation c left join cp.gameproject g on g.ID=c.cp_project_id where g.Active=1 and c.it_project_id=a.id)) as budgetAmount,",
            "(select GROUP_CONCAT(c.cp_project_id) from cp_project_relation c left join cp.gameproject g on g.ID=c.cp_project_id where g.Active=1 and c.it_project_id=a.id GROUP BY c.it_project_id) as cPProjectIdsStr,",
            "(select count(1) from pm_quality_project_relation where it_project_id = a.id LIMIT 0,1)hasQualityFlag ",
            "from v_project_member_info a",
            "where a.active_flag = 1 and (a.app_show_mode = 1 or a.app_show_mode = 2 or a.app_show_mode = 5) and a.service_line != '平台' and " +
                    " (a.service_line != '汇总' or (a.service_line = '汇总' and a.app_show_mode = 5) ) and a.service_line != '分摊项' and a.status = #{status}",
            "order by a.id"
    })
    List<ProjectAppVo> selectAllAppProject(@Param("status") String status, @Param("year") int year, @Param("month") int month, @Param("lastYear") int lastYear, @Param("lastMonth") int lastMonth);

    @Select({"select #{year} as year, #{month} as month, a.id, a.name, a.establish_time, a.max_member, a.member_number, a.logo, a.work_group_id,",
            "(select group_concat(concat(u.last_name, u.first_name)) from r_user_project_perm r left join user u on u.id = r.user_id where r.project_id = a.id and r.project_role_id = 1) as contact_name,",
            "(select sum(share_number) from fn_share_data where project_id = a.id and year = #{year} and month = #{month}) as plat_share_number,",
            "(select sum(value) from fn_project_stat_data b left join fn_stat c on b.fn_stat_id = c.id where c.name = '收入' and b.project_id = a.id and b.year = #{year} and b.month = #{month}) as income,",
            "(select sum(value) from fn_project_stat_data b left join fn_stat c on b.fn_stat_id = c.id where c.name = '费用合计(直接+分摊)' and b.project_id = a.id and b.year = #{year} and b.month = #{month}) as cost,",
            "((select ifnull(sum(value), 0) from fn_project_stat_data b left join fn_stat c on b.fn_stat_id = c.id where c.name = '费用合计(直接+分摊)' and b.project_id = a.id and b.year >= 2017 and b.month <> 0 and (b.year < #{year} or (b.year = #{year} and b.month <= #{month}))) + ",
            "(select ifnull(max(total_cost), 0) from fn_project_sched_data where project_id = a.id and year = 2017 and month = 0)) as total_cost,",
            "(select ifnull(max(expect_income), 0) from fn_project_sched_data where project_id = a.id and year = #{year} and month = 0) as expect_income",
            "from v_project_member_info a",
            "where a.id = #{projectId}"
    })
    ProjectAppVo selectAppProject(@Param("projectId") Long projectId, @Param("year") int year, @Param("month") int month);


    @SelectProvider(type = ProjectSqlBuilder.class, method = "buildSelectAllAppPlat")
    List<PlatAppVo> selectAllAppPlat(@Param("year") int year, @Param("month") int month, @Param("lastYear") int lastYear, @Param("lastMonth") int lastMonth);


    @Select({
            "select #{year} as year, #{month} as month, a.id, a.name, a.parent_share_id,",
            "(select sum(value) from fn_project_stat_data b left join fn_stat c on b.fn_stat_id = c.id where c.name = '税前利润' and b.project_id = a.id and b.year = #{year} and b.month = #{month}) as profit,",
            "(select sum(value) from fn_project_stat_data b left join fn_stat c on b.fn_stat_id = c.id where c.name = '税前利润' and b.project_id = a.id and b.year = #{year} and b.month = 0) as total_profit",
            "from v_project_member_info a",
            "where a.active_flag = 1 and (name = '西山居世游' or (a.app_show_mode = 1 or a.app_show_mode = 4  or a.app_show_mode = 5) and a.service_line = '汇总')",
            "order by a.id"
    })
    List<FnSummaryProjectAppVo> selectAllAppSummaryProject(@Param("year") int year, @Param("month") int month);

    @Select("select count(*) from project where active_flag = 1 and (app_show_mode = 1 or app_show_mode = 2) and service_line != '平台' and service_line != '汇总' and service_line != '分摊项' and status = #{status}")
    int selectAppProjectCount(@Param("status") String status);

    @Select("select count(*) from project where active_flag = 1 and (app_show_mode = 1 or app_show_mode = 2) and service_line = '平台' and isnull(parent_hr_id)")
    int selectAppPlatCount();

    @SelectProvider(type = ProjectSqlBuilder.class, method = "buildSelectAllWithFnInfo")
    List<FnProjectInfoDto> selectAllWithFnInfo(@Param("year") int year, @Param("month") int month);

    @Select({
            "select p.id, p.name, p.member_number as current_member, p.max_member, p.service_line, concat(u.last_name, u.first_name) as manager_name",
            "from v_project_member_info p",
            "left join r_user_project_perm r on p.id = r.project_id and r.project_role_id = 1",
            "left join user u on u.id = r.user_id",
            "where r.user_id = #{managerId} and r.project_role_id = 1",
            "and p.active_flag = 1 and (app_show_mode is not null and app_show_mode <> 0) and p.hr_flag = 1"
    })
    List<ProjectMaxMemberVo> selectAllWithMaxMemberByManagerAndShowApp(Long managerId);

    @Select({
            "select p.id, p.name, p.member_number as current_member, p.max_member, p.service_line, concat(u.last_name, u.first_name) as manager_name",
            "from v_project_member_info p",
            "left join r_user_project_perm r on p.id = r.project_id and r.project_role_id = 1",
            "left join user u on u.id = r.user_id",
            "where r.user_id = #{managerId} and r.project_role_id = 1",
            "and p.active_flag = 1 and p.hr_flag = 1"
    })
    List<ProjectMaxMemberVo> selectAllWithMaxMemberByManager(Long managerId);

    @Select({
            "select p.id, p.name, p.member_number as current_member, p.max_member, p.service_line,",
            "(select u.id from user u left join r_user_project_perm r on r.user_id = u.id and r.project_role_id = 1 where r.project_id = p.id limit 1) as manager_id,",
            "(select concat(u.last_name, u.first_name) from user u left join r_user_project_perm r on r.user_id = u.id and r.project_role_id = 1 where r.project_id = p.id limit 1) as manager_name",
            "from v_project_member_info p",
            "where p.active_flag = 1 and p.hr_flag = 1"
    })
    List<ProjectMaxMemberVo> selectAllWithMaxMember();

    @Select({
            "select p.id, p.name, p.member_number as current_member, p.max_member, p.service_line,",
            "(select u.id from user u left join r_user_project_perm r on r.user_id = u.id and r.project_role_id = 1 where r.project_id = p.id limit 1) as manager_id,",
            "(select concat(u.last_name, u.first_name) from user u left join r_user_project_perm r on r.user_id = u.id and r.project_role_id = 1 where r.project_id = p.id limit 1) as manager_name",
            "from v_project_member_info p",
            "where p.active_flag = 1 and (app_show_mode is not null and (app_show_mode =3 or app_show_mode=1)) and p.hr_flag = 1",
            "order by p.id"
    })
    List<ProjectMaxMemberVo> selectAllWithMaxMemberAndShowApp();

    @Select({
            "select p.id, p.name, p.member_number as current_member, p.max_member, p.service_line, u.id as manager_id, concat(u.last_name, u.first_name) as manager_name",
            "from v_project_member_info p",
            "left join r_user_project_perm r on p.id = r.project_id and r.project_role_id = 1",
            "left join user u on u.id = r.user_id",
            "where p.id = #{projectId}",
            "limit 1"
    })
    ProjectMaxMemberVo selectWithMaxMemberByProjectId(Long projectId);

    @Select({
            "SELECT DISTINCT " +
                    "p.id, " +
                    "p. NAME, " +
                    "p.service_line as serviceLine, " +
                    "p.max_member as maxMember, " +
                    "u.id as managerId, " +
                    "p.contact_name AS managerName " +
                    "FROM " +
                    "`project` p " +
                    "RIGHT JOIN user u ON p.contact_name = CONCAT(u.last_name,u.first_name) " +
                    "where p.id=#{0} "+
                    "order by max_member DESC "
    })
    ProjectMaxMemberVo selectMaxMemberByProjectId(Long projectId);

    @Select("select p1.id, p1.name,p2.id as parentHrProjectId,p2.name as parentHrProjectName from project p1 left join project p2 on p1.parent_hr_id = p2.id where p1.parent_hr_id is not null")
    List<ProjectParentHrVo> selectAllParentHrProject();

    @Select("select concat(ifnull(name,''),',',ifnull(usedNamesStr,'')) from v_project_name_info where active_flag = 1 and share_flag =1 and service_line = '平台'")
    List<String> selectAllSharePlatName();

    @Select({"select p.id,p.name,p.status,p.type,p.service_line,GROUP_CONCAT(CONCAT(u.last_name,u.first_name) SEPARATOR ',') contactName from project p left join r_user_project_perm r  on p.id=r.project_id left join user u on u.id=r.user_id where r.project_role_id=1 and p.id=#{0} group by p.id;"})
    PmPlanDetailAppVo selectPmPlanDetailAppVoByPrimaryKey(Long projectId);

    @Select({"select * from project"})
    List<Project> selectAll();

    @Select({"select p.id,p.name from subcompany s left join cost_center c on substr(c.code, 1, 4)=s.code left join user u on u.cost_center_id=c.id left join order_center o  on u.order_center_id=o.id left join project p on p.id=o.project_id where p.active_flag=1 and s.id=#{0} group by p.id"})
    List<IdNameBaseObject> selectIdNameByCompanyId(Long companyId);

    @Select("select v.* from cfg_plat_attr c left join  v_project_name_info v on v.id=c.plat_id ")
    List<ProjectVo> selectCfgAllSharePlat();

    @Select({"select * from project where active_flag=0"})
    List<Project> selectUnActiveProject();

    @Select({"select id,name from project where active_flag = 1 and share_flag =1 and service_line != '平台' and service_line != '发行' and id not in( select p.id from fn_plat_favor_project f left join project p on f.favor_project_id = p.id where f.plat_id =#{0}  union select id from project where id in (select project_id from fn_plat_week_share_config where plat_id=#{0} and year=#{1} and week=#{2}))"})
    List<IdNameBaseObject> selectUnfavorShareProjectIdAndNamesByPlatId(Long platId, Integer year, Integer week);

    @Select({"select p.id,p.name,p.max_member,p.work_group_id,p.city,a.memberNumber,p.active_flag,p.app_show_mode,p.parent_hr_id,p.service_line from project p left join (select p.id id,count(1) memberNumber from project p left join order_center o on o.project_id=p.id left join user u on u.order_center_id=o.id left join user_detail d on d.user_id=u.id where u.active_flag=1 and u.virtual_flag=0 and d.work_status in ('正式','试用') GROUP BY p.id) a on a.id=p.id"})
    List<PlatAppDto> selectAllPlatAppDto();

    class ProjectSqlBuilder {
        public String buildSelectByCondition(Project project) {
            return new SQL() {{
                SELECT("*");
                FROM("project");
                if (null != project.getStatus()) {
                    WHERE("status = #{status}");
                }

                if (null != project.getFinanceFlag()) {
                    WHERE("finance_flag = #{financeFlag}");
                }

                if (null != project.getShareFlag()) {
                    WHERE("share_flag = #{shareFlag}");
                }

                if (null != project.getHrFlag()) {
                    WHERE("hr_flag = #{hrFlag}");
                }

                if (null != project.getAppShowMode()) {
                    WHERE("app_show_mode = #{appShowModel}");
                }

                WHERE("active_flag = 1");
            }}.toString();

        }

        public String buildSelectAllAppPlat(Map<String, Object> params) {
            int year = (int) params.get("year");
            int month = (int) params.get("month");
            int lastYear = (int) params.get("lastYear");
            int lastMonth = (int) params.get("lastMonth");
            return new SQL() {{
                SELECT(year + " as year, " + month + " as month");
                SELECT("a.id, a.name, a.member_number, a.max_member, a.work_group_id, a.city");
                StringBuilder columnsServeNumber = new StringBuilder();
                columnsServeNumber.append("(select count(*) from fn_share_data where plat_id = a.id and project_id <> 1 ");
                for (Map.Entry<Long, Long> entry : ReportHelper.PlatInShareProjectMap.entrySet()) {
                    columnsServeNumber.append(" and if (plat_id = ").append(entry.getKey()).append(", project_id <> ").append(entry.getValue()).append(", 1=1)");
                }
                columnsServeNumber.append(" and year = ").append(year);
                columnsServeNumber.append(" and month = ").append(month);
                columnsServeNumber.append(" and share_number <> 0) as serve_number");
                SELECT(columnsServeNumber.toString());
//                SELECT("(select sum(share_amount) from fn_share_data where plat_id = a.id and project_id = 1 and year = " + year + " and month = " + month + ") as cost");
                SELECT("(select sum(share_amount) from fn_share_data where plat_id = a.id and project_id = 1 and year = " + lastYear + " and month = " + lastMonth + ") as last_month_cost");
                SELECT("(SELECT COUNT(*) FROM fn_sum_share_config WHERE ((plat_id=a.id OR plat_id IN (SELECT id FROM project WHERE parent_hr_id=a.id AND active_flag=1)) AND YEAR=" + lastYear + " AND MONTH=" + lastMonth + ")) > 0  AS has_sum_share_data");
                FROM("v_project_member_info a ");
                WHERE("a.active_flag = 1 and (a.app_show_mode = 1 or a.app_show_mode = 2 or a.app_show_mode = 5) and a.service_line = '平台' and isnull(a.parent_hr_id)");
                ORDER_BY("a.id");
            }}.toString();
        }

        public String buildSelectAllWithFnInfo(Map<String, Object> params) {
            int year = (int) params.get("year");
            int month = (int) params.get("month");
            int lastYear;
            int lastMonth;
            int twoMonthAgoYear;
            int twoMonthAgoMonth;
            if (month == 1) {
                lastYear = year - 1;
                lastMonth = 12;
            } else {
                lastYear = year;
                lastMonth = month - 1;
            }
            if (lastMonth == 1) {
                twoMonthAgoYear = lastYear - 1;
                twoMonthAgoMonth = 12;
            } else {
                twoMonthAgoYear = lastYear;
                twoMonthAgoMonth = lastMonth - 1;
            }
            return new SQL() {{
                SELECT("p.*");
                SELECT("(select ifnull(sum(value),0) from fn_project_stat_data d left join fn_stat s on d.fn_stat_id = s.id where s.name = '收入' and d.project_id = p.id and d.year = " + year + " and d.month = " + month + ") as income");
                SELECT("(select ifnull(sum(value),0) from fn_project_stat_data d left join fn_stat s on d.fn_stat_id = s.id where s.name = '费用合计(直接+分摊)' and d.project_id = p.id and d.year = " + year + " and d.month = " + month + ") as cost");
                SELECT("(select ifnull(sum(value),0) from fn_project_stat_data d left join fn_stat s on d.fn_stat_id = s.id where s.name = '税前利润' and d.project_id = p.id and d.year = " + year + " and d.month = " + month + ") as profit");
                SELECT("(select ifnull(sum(value),0) from fn_project_stat_data d left join fn_stat s on d.fn_stat_id = s.id where s.name = '收入' and d.project_id = p.id and d.year = " + lastYear + " and d.month = " + lastMonth + ") as lastMonthIncome");
                SELECT("(select ifnull(sum(value),0) from fn_project_stat_data d left join fn_stat s on d.fn_stat_id = s.id where s.name = '费用合计(直接+分摊)' and d.project_id = p.id and d.year = " + lastYear + " and d.month = " + lastMonth + ") as lastMonthCost");
                SELECT("(select ifnull(sum(value),0) from fn_project_stat_data d left join fn_stat s on d.fn_stat_id = s.id where s.name = '税前利润' and d.project_id = p.id and d.year = " + lastYear + " and d.month = " + lastMonth + ") as lastMonthProfit");
                SELECT("(select ifnull(sum(value),0) from fn_project_stat_data d left join fn_stat s on d.fn_stat_id = s.id where s.name = '收入' and d.project_id = p.id and d.year = " + twoMonthAgoYear + " and d.month = " + twoMonthAgoMonth + ") as twoMonthAgoIncome");
                SELECT("(select ifnull(sum(value),0) from fn_project_stat_data d left join fn_stat s on d.fn_stat_id = s.id where s.name = '费用合计(直接+分摊)' and d.project_id = p.id and d.year = " + twoMonthAgoYear + " and d.month = " + twoMonthAgoMonth + ") as twoMonthAgoCost");
                SELECT("(select ifnull(sum(value),0) from fn_project_stat_data d left join fn_stat s on d.fn_stat_id = s.id where s.name = '税前利润' and d.project_id = p.id and d.year = " + twoMonthAgoYear + " and d.month = " + twoMonthAgoMonth + ") as twoMonthAgoProfit");
                FROM("project p");
                WHERE("p.id in (select project_id from fn_project_stat_data where year = " + year + " and month = " + month + ") and parent_fn_sum_id is not null");
                ORDER_BY("p.id");
            }}.toString();
        }

        /**
         * 查询IT细目，外包映射使用
         * @param projectName
         * @return
         */
        public String buildSelectItProject(String projectName) {
            StringBuffer sql = new StringBuffer("select id,name from project where (service_line!='平台' and active_flag =1)");
            if (StringUtils.isNotBlank(projectName)) {
                sql.append(" and name like '" + projectName + "%'");
            }
            return sql.toString();
        }
    }

    @Select("select id , name, service_line from project where service_line != '分摊项' and active_flag =1 and virtual_flag =0")
    List<MiniProjectVo> selectAllMiniProject();


    @Select("select * from project where service_line != '分摊项' and active_flag =1 and virtual_flag =0")
    List<Project> selectAllProject();

    /**
     * 获取IT项目，跟外包项目做映射关系使用
     * @return
     */
    @SelectProvider(type = ProjectSqlBuilder.class, method = "buildSelectItProject")
    public List<ProjectVo> selectItProject(@Param("projectName") String projectName);

    @Update({
            "update project set max_member=#{maxMember} where id=#{projectId}"
    })
    int updateMaxMemberByProjectId(@Param("projectId")Long projectId,@Param("maxMember")Integer maxMember);

    @Select({
           "select p.id, p.name, p.member_number as current_member, p.max_member, p.service_line,  GROUP_CONCAT(DISTINCTROW concat(u.id,':',u.last_name, u.first_name)) as manager_name ,\n" +
                   "\n" +
                   "GROUP_CONCAT(concat(oc.code,':',IFNULL(oc.city,' '))) as order_str, \n" +
                   "p.active_flag \n" +
                   "from v_project_member_info p\n" +
                   "left join r_user_project_perm r on p.id = r.project_id and r.project_role_id = 1\n" +
                   "left join user u on u.id = r.user_id\n" +
                   "left join order_center oc on oc.project_id = p.id \n" +
                   "where hr_flag=1\n" +
                   "GROUP BY p.id\n" +
                   "ORDER BY current_member desc,max_member desc"
    })
    List<ProjectMaxMemberDto> getProjectMaxMemberVos();

    @Select({
            "select * from project where id=#{0}"
    })
    Project getProjectDateByProjectId(Long id);

    @Select({
            "select id, name from project where active_flag =1 and virtual_flag = 0 order  by id asc;"
    })
    List<IdNameBaseObject> selectAllBaseInfo ();

    int simpleUpdate (@Param("project") ProjectVo projectVo);

}