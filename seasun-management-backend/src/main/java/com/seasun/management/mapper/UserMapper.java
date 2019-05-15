package com.seasun.management.mapper;

import com.seasun.management.dto.*;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.User;
import com.seasun.management.util.MyStringUtils;
import com.seasun.management.vo.*;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public interface UserMapper {
    @Delete({
            "delete from user",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into user (order_center_id, cost_center_id, ",
            "work_group_id, perf_work_group_id, ",
            "employee_no, middle_name, ",
            "first_name, last_name, ",
            "login_id, floor_id, ",
            "label, photo, email, ",
            "work_place, active_flag, ",
            "phone, in_date, virtual_flag, ",
            "virtual_expire_time, virtual_manager_id, ",
            "hr_id, grade, evaluate_type, couple_user_id,",
            "create_time, update_time)",
            "values (#{orderCenterId,jdbcType=BIGINT}, #{costCenterId,jdbcType=BIGINT}, ",
            "#{workGroupId,jdbcType=BIGINT}, #{perfWorkGroupId,jdbcType=BIGINT}, ",
            "#{employeeNo,jdbcType=BIGINT}, #{middleName,jdbcType=VARCHAR}, ",
            "#{firstName,jdbcType=VARCHAR}, #{lastName,jdbcType=VARCHAR}, ",
            "#{loginId,jdbcType=VARCHAR}, #{floorId,jdbcType=BIGINT}, ",
            "#{label,jdbcType=VARCHAR}, #{photo,jdbcType=VARCHAR}, #{email,jdbcType=VARCHAR}, ",
            "#{workPlace,jdbcType=VARCHAR}, #{activeFlag,jdbcType=BIT}, ",
            "#{phone,jdbcType=VARCHAR}, #{inDate,jdbcType=DATE}, #{virtualFlag,jdbcType=BIT}, ",
            "#{virtualExpireTime,jdbcType=DATE}, #{virtualManagerId,jdbcType=BIGINT}, ",
            "#{hrId,jdbcType=BIGINT}, #{grade,jdbcType=VARCHAR}, #{evaluateType,jdbcType=VARCHAR},#{coupleUserId,jdbcType=BIGINT}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(User record);

    int insertSelective(User record);

    @Select({
            "select",
            "id, order_center_id, cost_center_id, work_group_id, perf_work_group_id, employee_no, ",
            "middle_name, first_name, last_name, login_id, floor_id, label, photo, email, ",
            "work_place, active_flag, phone, in_date, virtual_flag, virtual_expire_time, ",
            "virtual_manager_id, hr_id, grade, evaluate_type, couple_user_id,create_time, update_time",
            "from user",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserMapper.BaseResultMap")
    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    @Update("update user set perf_work_group_id = null where id = #{0}")
    int clearPerfWorkGroupIdByUserId(Long userId);

    void clearUserPerformanceGroupIdInList(List<Long> performanceWorkGroupIds);

    @Update({
            "update user",
            "set order_center_id = #{orderCenterId,jdbcType=BIGINT},",
            "cost_center_id = #{costCenterId,jdbcType=BIGINT},",
            "work_group_id = #{workGroupId,jdbcType=BIGINT},",
            "perf_work_group_id = #{perfWorkGroupId,jdbcType=BIGINT},",
            "employee_no = #{employeeNo,jdbcType=BIGINT},",
            "middle_name = #{middleName,jdbcType=VARCHAR},",
            "first_name = #{firstName,jdbcType=VARCHAR},",
            "last_name = #{lastName,jdbcType=VARCHAR},",
            "login_id = #{loginId,jdbcType=VARCHAR},",
            "floor_id = #{floorId,jdbcType=BIGINT},",
            "label = #{label,jdbcType=VARCHAR},",
            "photo = #{photo,jdbcType=VARCHAR},",
            "email = #{email,jdbcType=VARCHAR},",
            "work_place = #{workPlace,jdbcType=VARCHAR},",
            "active_flag = #{activeFlag,jdbcType=BIT},",
            "phone = #{phone,jdbcType=VARCHAR},",
            "in_date = #{inDate,jdbcType=DATE},",
            "virtual_flag = #{virtualFlag,jdbcType=BIT},",
            "virtual_expire_time = #{virtualExpireTime,jdbcType=DATE},",
            "virtual_manager_id = #{virtualManagerId,jdbcType=BIGINT},",
            "hr_id = #{hrId,jdbcType=BIGINT},",
            "grade = #{grade,jdbcType=VARCHAR},",
            "evaluate_type = #{evaluateType,jdbcType=VARCHAR},",
            "couple_user_id = #{coupleUserId,jdbcType=BIGINT},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(User record);

    /* the flowing are user defined ... */
    @Insert({
            "insert into user (id, order_center_id, cost_center_id, ",
            "work_group_id, employee_no, ",
            "middle_name, first_name, ",
            "last_name, login_id, ",
            "floor_id, label, photo, ",
            "email, work_place, ",
            "active_flag, phone, in_date, ",
            "virtual_flag, virtual_expire_time, ",
            "virtual_manager_id, hr_id, ",
            "create_time, update_time)",
            "values (#{id,jdbcType=BIGINT}, #{orderCenterId,jdbcType=BIGINT}, #{costCenterId,jdbcType=BIGINT}, ",
            "#{workGroupId,jdbcType=BIGINT}, #{employeeNo,jdbcType=BIGINT}, ",
            "#{middleName,jdbcType=VARCHAR}, #{firstName,jdbcType=VARCHAR}, ",
            "#{lastName,jdbcType=VARCHAR}, #{loginId,jdbcType=VARCHAR}, ",
            "#{floorId,jdbcType=BIGINT}, #{label,jdbcType=VARCHAR}, #{photo,jdbcType=VARCHAR}, ",
            "#{email,jdbcType=VARCHAR}, #{workPlace,jdbcType=VARCHAR}, ",
            "#{activeFlag,jdbcType=BIT}, #{phone,jdbcType=VARCHAR}, #{inDate,jdbcType=DATE}, ",
            "#{virtualFlag,jdbcType=BIT}, #{virtualExpireTime,jdbcType=DATE}, ",
            "#{virtualManagerId,jdbcType=BIGINT}, #{hrId,jdbcType=BIGINT}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})"
    })
    int insertWithId(User record);

    @Results(
            {
                    @Result(column = "first_name", property = "firstName"),
                    @Result(column = "last_name", property = "lastName")
            }
    )
    @Select("select first_name,last_name from user where id = #{id}")
    User selectNameByPrimaryKey(Long id);

    @SelectProvider(type = UserSqlBuilder.class, method = "buildSelectAllActiveUsers")
    List<UserMiniVo> selectAllActiveUser(Boolean virtualFlag);

    @SelectProvider(type = UserSqlBuilder.class, method = "buildSelectAllActiveUsersInGroup")
    List<WorkGroupCompVo.UserSimVo> selectAllActiveUserInWorkGroup(String groupColumn, List<Long> groupIds);

    @Select("SELECT u.id, d.gender,u.login_id, concat(u.last_name,u.first_name) as name,u.photo, d.post, p.name as departmentName \n" +
            "                FROM user u \n" +
            "                left outer join  user_detail d on u.id = d.user_id \n" +
            "                left outer join cost_center c on u.cost_center_id = c.id \n" +
            "                left outer join department p on c.department_id = p.id \n" +
            "                WHERE u.active_flag = 1 AND u.virtual_flag = 0 and (d.work_status = '正式' or d.work_status = '试用');")
    List<UserMiniVo> selectAllPerformanceUser();


    @Select({"select id, concat(u.last_name,u.first_name) as name, login_id from user u where active_flag=1"})
    List<UserSelectVo> selectAllActiveUserSelectVo();

    @Select("select * from user")
    List<User> selectAll();

    @Select("select * from user where login_id = #{loginId} and active_flag =1 limit 1")
    User selectActiveUserByLoginId(String loginId);

    @Select("select * from user where login_id = #{loginId} limit 1")
    User selectUserByLoginId(String loginId);

    @Select("select * from user where concat(last_name,first_name) = #{name} limit 1")
    User selectUserByName(String name);

    @Select("select * from user where employee_no = #{employNo} limit 1")
    User selectUserByEmployeeNo(Long employeeNo);

    List<User> selectUsersByEmployeeNos(@Param("employee_nos") List<Long> employeeNos);

    @Select("select * from user where id < 1000 order by id desc limit 1")
    User selectMaxTestUser();

    @Select({
            "select u.id as user_id, concat(u.last_name,u.first_name) as name, u.login_id, u.work_group_id, d.post, u.photo,u.employee_no",
            "from user u ",
            "left join user_detail d on u.id = d.user_id",
            "where u.order_center_id in (select id from order_center where project_id in (select id from project where id = #{projectId} or parent_hr_id = #{projectId}))",
            "and u.active_flag = 1 and u.virtual_flag = 0 ",
            "order by u.login_id"
    })
    List<OrgWorkGroupMemberAppVo> selectAllEntityWithOrgWorkGroupSimpleByProjectId(Long projectId);

    @Select({
            "select u.id as user_id, concat(u.last_name,u.first_name) as name, u.login_id, u.work_group_id, d.post, u.photo, d.work_status",
            "from user u ",
            "left join user_detail d on u.id = d.user_id",
            "where u.order_center_id in (select id from order_center where project_id in (select id from project where id = #{projectId} or parent_hr_id = #{projectId}))",
            "and u.active_flag = 1 and u.virtual_flag = 0 and (d.work_status = '正式' or d.work_status = '试用' or d.work_status = '实习')",
            "order by u.login_id"
    })
    List<OrgWorkGroupMemberAppVo> selectAllUserByProjectId(Long projectId);

    @Select({
            "select u.id as user_id,u.photo as userPhoto, concat(u.last_name,u.first_name) as name, u.login_id, u.work_group_id, d.post, u.photo",
            "from user u ",
            "left join user_detail d on u.id = d.user_id",
            "where u.order_center_id in (select id from order_center where project_id in (select id from project where id = #{projectId} or parent_hr_id = #{projectId}))",
            "and u.active_flag = 1 and u.virtual_flag = 0 and (d.work_status = '正式' or d.work_status = '试用')",
            "order by u.login_id"
    })
    List<OrgWorkGroupMemberAppVo> selectAllUserByProjectIdWithoutTrainee(Long projectId);

    @Select({
            "select count(*) ",
            "from user u ",
            "left join user_detail d on u.id = d.user_id",
            "where u.order_center_id in (select id from order_center where project_id in (select id from project where id = #{projectId} or parent_hr_id = #{projectId}))",
            "and u.active_flag = 1 and u.virtual_flag = 0 and (d.work_status = '正式' or d.work_status = '试用')",
            "order by u.login_id"
    })
    int countAllUserByProjectIdWithoutTrainee(Long projectId);


    @SelectProvider(type = UserSqlBuilder.class, method = "buildSelectByUserIds")
    List<OrgWorkGroupMemberAppVo> selectAllUserWithNameAndLoginIdAndPostByIds(@Param("userIds") String[] userIds);

    @Select({"select u.id as user_id, concat(u.last_name,u.first_name) as name, u.login_id, r.work_group_id, d.post, u.photo",
            "from r_user_work_group_perm r",
            "left join user u on r.user_id = u.id",
            "left join user_detail d on r.user_id = d.user_id",
            "where r.work_group_role_id = 3 and r.work_group_id in ",
            "(select work_group_id from user where order_center_id in ",
            "(select id from order_center where project_id in (select id from project where id = #{projectId} or parent_hr_id = #{projectId}))",
            "and active_flag = 1 and virtual_flag = 0)",
            "and r.user_id in ",
            "(select id from user where order_center_id in ",
            "(select id from order_center where project_id in (select id from project where id = #{projectId} or parent_hr_id = #{projectId})))",
    })
    List<OrgWorkGroupMemberAppVo> selectAllGroupLeaderWithOrgWorkGroupSimpleByProjectId(Long projectId);

    @Select({"select u.id as user_id, concat(u.last_name,u.first_name) as name, u.login_id, u.work_group_id, d.post, u.photo",
            "from r_user_work_group_perm r",
            "left join user u on r.user_id = u.id",
            "left join user_detail d on r.user_id = d.user_id",
            "where r.work_group_role_id = 3 and r.work_group_id = #{workGroupId}",
            "and u.active_flag = 1 and u.virtual_flag = 0"})
    List<OrgWorkGroupMemberAppVo> selectLeadersByWorkGroupId(Long workGroupId);

    @Select({"select u.id as user_id, concat(u.last_name,u.first_name) as name, u.login_id, u.work_group_id, d.post, u.photo",
            "from r_user_project_perm r",
            "left join user u on r.user_id = u.id",
            "left join user_detail d on r.user_id = d.user_id",
            "where r.project_role_id = 1 and r.project_id = #{projectId}",
            "and u.active_flag = 1 and u.virtual_flag = 0"})
    List<OrgWorkGroupMemberAppVo> selectLeadersByProjectId(Long projectId);

    @Select({
            "select u.id as user_id, concat(u.last_name,u.first_name) as name, u.login_id, u.work_group_id, d.post, u.photo, d.work_status",
            "from user u ",
            "left join user_detail d on d.user_id = u.id",
            "where u.active_flag = 1 and u.virtual_flag = 0 and d.work_status in ('正式', '试用')",
            "order by u.login_id"
    })
    List<OrgWorkGroupMemberAppVo> selectAllEntityWithOrgWorkGroupSimple();

    //    @Cacheable(value = "allActiveUser")
    @Select({
            "select u.id as user_id, concat(u.last_name,u.first_name) as name, u.login_id, u.work_group_id, d.post, u.photo, d.work_status",
            "from user u ",
            "left join user_detail d on d.user_id = u.id",
            "where u.active_flag = 1 and u.virtual_flag = 0 and d.work_status in ('正式', '试用', '实习')",
            "order by u.login_id"
    })
    List<OrgWorkGroupMemberAppVo> selectAllEntityWithOrgWorkGroupSimpleWithIntern();

    List<IdNameBaseObject> selectUserByIds(String[] userIds);

    @Select("select count(*) from user where active_flag = 1 and virtual_flag = 0")
    int selectActiveEntityCount();

    @Select("select count(*) from user where datediff(in_date, #{beginDate}) >= 0 and datediff(in_date, #{endDate}) <= 0 and active_flag = 1 and virtual_flag = 0")
    int selectCountByInDate(@Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    @Select({"select count(*) from user u left join user_detail d on u.id = d.user_id ",
            "where u.virtual_flag = 0 and u.active_flag = 0 and datediff(d.leave_date, #{beginDate}) >= 0 and datediff(d.leave_date, #{endDate}) <= 0"})
    int selectCountByLeaveDate(@Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    @Select({
            "select u.*, d.birthday, d.leave_date, d.work_status, d.leave_type, o.code as order_center_code, ifnull(sd.disp_city, s.city) as location",
            "from user u",
            "left join user_detail d on u.id = d.user_id",
            "left join order_center o on o.id = u.order_center_id",
            "left join cost_center c on u.cost_center_id = c.id",
            "left join subcompany s on s.code = substr(c.code, 1, 4)",
            "left join user_special_disp sd on sd.user_id = u.id",
            "where u.virtual_flag = 0"
    })
    List<UserDto> selectAllEntityWidthDetail();

    @Select({
            "select u.*, d.birthday, d.leave_date, d.work_status, d.leave_type, o.code as order_center_code, ifnull(sd.disp_city, s.city) as location",
            "from user u",
            "left join user_detail d on u.id = d.user_id",
            "left join order_center o on o.id = u.order_center_id",
            "left join cost_center c on u.cost_center_id = c.id",
            "left join subcompany s on s.code = substr(c.code, 1, 4)",
            "left join user_special_disp sd on sd.user_id = u.id",
            "where u.virtual_flag = 0 and ((d.work_status != '实习' and d.work_status != '离职') or (d.work_status = '离职' and d.leave_type != '实习生返校' and d.leave_type != '实习生离职'))"
    })
    List<UserDto> selectAllEntityWidthDetailWithoutTrainee();

    @Select({
            "select u.id as user_id, concat(u.last_name,u.first_name) as name, u.login_id, u.work_group_id,",
            "(select count(*) from r_user_work_group_perm r where r.user_id = u.id and r.work_group_role_id = #{groupRoleId}) > 0 as leader_flag,",
            "(select group_concat(r.work_group_id separator ',') from r_user_work_group_perm r where r.user_id = u.id and r.work_group_role_id = 1) as sub_group",
            "from user u ",
            "where u.active_flag = 1 and u.virtual_flag = 0 ",
            "order by u.login_id"
    })
    List<WorkGroupUserDto> selectAllEntityWithWorkGroupSimple(@Param("groupRoleId") Long groupRoleId);

    @Select({
            "select u.id as user_id, concat(u.last_name,u.first_name) as name, u.login_id, u.work_group_id, w.name as work_group_name, u.employee_no,d.work_status, d.post, d.work_status,",
            "floor((datediff(now(), u.in_date) + d.work_age_out_ks) / 365) as work_age,",
            "floor(datediff(now(), u.in_date) / 365) as work_age_in_ks,",
            "(select count(*) from r_user_work_group_perm r where r.user_id = u.id and r.work_group_role_id = #{groupRoleId}) > 0 as leader_flag,",
            "ifnull((select group_concat(r.work_group_id separator ',') from r_user_work_group_perm r where r.user_id = u.id and r.work_group_role_id = #{groupRoleId}), '') as sub_group",
            "from user u",
            "left join user_detail d on d.user_id = u.id",
            "left join work_group w on w.id = u.work_group_id",
            "where u.active_flag = 1 and u.virtual_flag = 0 and d.work_status != '离职' and d.work_status != '实习' ",
            "order by u.login_id"
    })
    List<PerformanceUserDto> selectAllEntityWithPerformance(@Param("groupRoleId") Long groupRoleId);

    @Select({
            "select u.id as user_id, concat(u.last_name,u.first_name) as name, u.login_id, u.employee_no, d.post, d.work_status,",
            "u.perf_work_group_id as work_group_id, g.parent as parent_work_group_id, g.name as work_group_name,",
            "floor((datediff(now(), u.in_date) + d.work_age_out_ks) / 365) as work_age,",
            "floor(datediff(now(), u.in_date) / 365) as work_age_in_ks,",
            "(select count(*) from performance_work_group g where g.performance_manager_id = u.id) > 0 as leader_flag",
            "from user u",
            "left join user_detail d on d.user_id = u.id",
            "left join performance_work_group g on g.id = u.perf_work_group_id",
            "where u.active_flag = 1 and u.virtual_flag = 0 ",
            "order by u.login_id"
    })
    List<PerformanceUserDto> selectAllEntityWithPerformanceByPerformanceWorkGroup();

    @Select({
            "select u.id as user_id, concat(u.last_name,u.first_name) as name, u.login_id, u.work_group_id, w.name as work_group_name, u.employee_no, d.post, d.work_status,",
            "floor((datediff(now(), u.in_date) + d.work_age_out_ks) / 365) as work_age,",
            "floor(datediff(now(), u.in_date) / 365) as work_age_in_ks,",
            "(select count(*) from r_user_work_group_perm r where r.user_id = u.id and r.work_group_role_id = #{groupRoleId}) > 0 as leader_flag,",
            "ifnull((select group_concat(r.work_group_id separator ',') from r_user_work_group_perm r where r.user_id = u.id and r.work_group_role_id = #{groupRoleId}), '') as sub_group",
            "from user u",
            "left join user_detail d on d.user_id = u.id",
            "left join work_group w on w.id = u.work_group_id",
            "where u.id = #{userId}"
    })
    PerformanceUserDto selectWithPerformance(@Param("userId") Long userId, @Param("groupRoleId") Long groupRoleId);

    @Select({
            "select u.id as user_id, concat(u.last_name,u.first_name) as name, u.login_id, u.employee_no, d.post, d.work_status,",
            "u.perf_work_group_id as work_group_id, g.parent as parent_work_group_id, g.name as work_group_name,g.performance_manager_id as leaderId,",
            "floor((datediff(now(), u.in_date) + d.work_age_out_ks) / 365) as work_age,",
            "floor(datediff(now(), u.in_date) / 365) as work_age_in_ks,",
            "(select count(*) from performance_work_group g where g.performance_manager_id = u.id) > 0 as leader_flag",
            "from user u",
            "left join user_detail d on d.user_id = u.id",
            "left join performance_work_group g on g.id = u.perf_work_group_id",
            "where u.id = #{userId}"
    })
    PerformanceUserDto selectWithPerformanceByPerformanceWorkGroup(@Param("userId") Long userId);

    List<PerformanceUserDto> selectWithPerformanceByPWGUserIdInList(List<Long> list);

    @Select({"SELECT",
            "u.id userId,",
            "CONCAT(u.last_name, u.first_name) name,",
            "u.login_id, u.work_group_id,",
            "(select count(*) from r_user_work_group_perm r where r.user_id = u.id and r.work_group_role_id = #{groupRoleId}) > 0 as leader_flag,",
            "u.grade,",
            "u.evaluate_type evaluate_type,",
            "FLOOR((DATEDIFF(NOW(), u.in_date) + ud.work_age_out_ks) / 365) work_age,",
            "FLOOR(DATEDIFF(NOW(), u.in_date) / 365) work_age_in_ks",
            "FROM user u LEFT JOIN user_detail ud ON u.id = ud.user_id",
            "where u.virtual_flag = 0 and u.active_flag = 1"
    })
    List<UserGradeDto> selectAllUserGradeInfo(@Param("groupRoleId") Long groupRoleId);

    @Select({"SELECT x.user_id ,x.post,x.work_age,x.work_age_in_ks,work_group.name FROM",
            "(SELECT u.id user_id, FLOOR((DATEDIFF(NOW(), u.in_date) + ud.work_age_out_ks) / 365) work_age,",
            "FLOOR(DATEDIFF(NOW(), u.in_date) / 365) work_age_in_ks, post,work_group_id",
            "FROM user u LEFT JOIN user_detail ud ON u.id = ud.user_id",
            "where u.virtual_flag = 0 and u.active_flag = 1)x LEFT JOIN work_group",
            "ON x.work_group_id =work_group.id"})
    List<IndividualSalaryChangeVo.UserInfo> selectAllUserPositionInfo();

    @Select({"SELECT x.user_id ,x.post,x.work_age,x.work_age_in_ks,work_group.name ,x.employee_no FROM",
            "(SELECT u.id user_id, FLOOR((DATEDIFF(NOW(), u.in_date) + ud.work_age_out_ks) / 365) work_age,",
            "FLOOR(DATEDIFF(NOW(), u.in_date) / 365) work_age_in_ks, post,work_group_id,employee_no",
            "FROM user u LEFT JOIN user_detail ud ON u.id = ud.user_id",
            "where u.virtual_flag = 0 and u.active_flag = 1)x LEFT JOIN work_group",
            "ON x.work_group_id =work_group.id WHERE user_id=#{userId}"})
    IndividualSalaryChangeVo.UserInfo selectUserPositionInfoByUserId(Long userId);

    @Select({"SELECT",
            "u.id userId,",
            "CONCAT(u.last_name, u.first_name) name,",
            "u.login_id, u.work_group_id,",
            "(select count(*) from r_user_work_group_perm r where r.user_id = u.id and r.work_group_role_id = #{groupRoleId}) > 0 as leader_flag,",
            "u.grade,",
            "u.evaluate_type evaluate_type,",
            "FLOOR((DATEDIFF(NOW(), u.in_date) + ud.work_age_out_ks) / 365) work_age,",
            "FLOOR(DATEDIFF(NOW(), u.in_date) / 365) work_age_in_ks",
            "FROM user u LEFT JOIN user_detail ud ON u.id = ud.user_id",
            "where u.virtual_flag = 0 and u.active_flag = 1 and u.work_group_id=#{workGroupId}"
    })
    List<UserGradeDto> selectAllUserGradeInfoByWorkGroupId(@Param("workGroupId") Long workGroupId, @Param("groupRoleId") Long groupRoleId);

    @Select({"SELECT",
            "u.id userId, u.login_id, u.work_group_id,",
            "CONCAT(u.last_name, u.first_name) name,",
            "(select count(*) from r_user_work_group_perm r where r.user_id = u.id and r.work_group_role_id = #{groupRoleId}) > 0 as leader_flag,",
            "u.grade,",
            "u.evaluate_type evaluateType,",
            "FLOOR((DATEDIFF(NOW(), u.in_date) + ud.work_age_out_ks) / 365) workAge,",
            "FLOOR(DATEDIFF(NOW(), u.in_date) / 365) workAgeInKs,",
            "ud.post,",
            "u.employee_no,",
            "w.name work_group",
            "FROM user u LEFT JOIN user_detail ud ON u.id = ud.user_id",
            "left join work_group w on u.work_group_id=w.id",
            "where u.id=#{userId}"
    })
    UserGradeDetailDto selectUserGradeDetailInfo(@Param("userId") Long userId, @Param("groupRoleId") Long groupRoleId);

    @Update({
            "update user",
            "set grade=#{grade}, evaluate_Type=#{evaluateType}",
            "where id=#{userId}"
    })
    int updateUserGradeInfo(@Param("userId") Long userId, @Param("grade") String grade, @Param("evaluateType") String evaluateType);

    @Select("select * from user where work_group_id = #{workGroupId}")
    List<User> selectByWorkGroupId(@Param("workGroupId") Long workGroupId);

    @Select("select * from user where perf_work_group_id = #{workGroupId}")
    List<User> selectByPerfWorkGroupId(@Param("workGroupId") Long workGroupId);

    @Select("select id user_Id, employee_no, concat(last_name, first_name) user_name from user where employee_no is not null")
    List<UserEmployeeNoDto> selectUserWithEmployeeInfo();

    @Select({
            "select u.id userId,concat(u.last_name, u.first_name) name, u.perf_work_group_id, d.gender, u.employee_no, u.login_id, u.phone, u.email, u.work_group_id, w.name work_group, d.post, u.photo",
            "from user u",
            "left join user_detail d on d.user_id = u.id",
            "left join work_group w on w.id = u.work_group_id",
            "where u.id=#{userId}"
    })
    UserInfoAppVo selectByUserId(Long userId);

    @Select("select * from user where perf_work_group_id is not null")
    List<User> selectAllByPerformance();

    int batchUpdatePerfWorkGroupIdByPks(List<User> users);

    int batchUpdateUserPhoto(List<UserMiniDto> userPhotoDtoList);

    int batchUpdateUserPhone(List<UserMiniDto> userPhotoDtoList);

    @Update("update user set perf_work_group_id = null")
    int clearAllPerfWorkGroupId();

    @Update("update user set perf_work_group_id = null where perf_work_group_id = #{0}")
    int clearPrefWorkGroupIdById(Long perfWorkGroupId);

    @Update("update user set perf_work_group_id = #{0} where work_group_id = #{1}")
    int syncPrefWorkGroupId(Long perfWorkGroupId, Long workGroupId);

    @Select("select o.project_id from user u left join order_center o on o.id = u.order_center_id where u.id = #{0}")
    Long selectUserProjectId(Long userId);

    @Select("select login_id from (select u.order_center_id,u.login_id,u.id from user u left join user_detail d on u.id = d.user_id where d.work_status in('正式','试用') and u.virtual_flag = 0 and u.active_flag =1) active_user_info " +
            "where order_center_id in (select id from order_center where project_id in (select id from project where city = #{0} and active_flag = 1))")
    List<User> selectHrUserByCity(String city);

    @Select({
            "select u.login_id, concat(u.last_name, u.first_name) name, f.name floor_name, if (p.parent_hr_id is null, p.name, (select name from project where id = p.parent_hr_id)) project_name",
            "from user u",
            "left join user_detail d on d.user_id = u.id",
            "LEFT JOIN FLOOR f ON f.id = u.floor_id",
            "LEFT JOIN order_center o ON o.id = u.order_center_id",
            "LEFT JOIN project p ON p.id = o.project_id",
            "WHERE u.active_flag = 1 AND u.virtual_flag = 0 AND p.city = #{city} AND (d.work_status IN ('正式','试用') OR (d.work_status = '实习' AND p.id <> 1066))",
            "order by u.login_id"
    })
    List<UserCheckInInfoVo> selectAllWithPubByCity(String city);

    @Select({
            "SELECT user.id user_id, CONCAT(user.last_name,user.first_name) user_name ,    ",
            "user.login_id , user.perf_work_group_id performance_work_group_id , ",
            "pwg.id manage_perf_WG_id , pwg.name manage_perf_WG_name ",
            "FROM user  ",
            "LEFT JOIN performance_work_group pwg ON pwg.performance_manager_id = `user`.id ",
            "WHERE CONCAT(last_name,first_name) LIKE CONCAT(#{keyword},'%') ",
            "OR login_id LIKE CONCAT(#{keyword},'%')  "

    })
    List<UserPerfWorkGroupDto> selectByUserNameKeyword(String keyword);

    List<Long> selectActiveUserIdByPerfWorkGroup(List<Long> perfWorkGroupIds);

    @Select({"SELECT * FROM user WHERE active_flag=1 AND virtual_flag=0 AND concat(last_name,first_name) LIKE CONCAT('%',#{keyword},'%')",
            "OR login_id LIKE CONCAT('%',#{keyword},'%') ORDER BY login_id"})
    List<User> getWorkGroupUserByUserNameKeyword(@Param("keyword") String keyword);

    @Select({"select u.*,d.work_status workStatus from user u left join user_detail d on u.id=d.user_id where active_flag = 1 and virtual_flag = 0"})
    List<UserDto> selectActiveEntity();

    List<SimUserCoupleInfoVo> selectSimUserCoupleInfoVoBYUserIds(@Param("list") List<Long> userIds);

    List<User> selectBYUserLoginIds(@Param("list") List<String> loginIds);

    List<User> selectBYUserIds(@Param("list") List<Long> userIds);

    @Select("select u.id,u.employee_no from user u left join user_detail d on u.id=d.user_id where u.active_flag = 1 and u.virtual_flag = 0 and d.work_status in('正式', '试用', '实习') and u.work_group_id is not null")
    List<User> selectActiveEntityUserIdAndEmployeeNo();

    @Select({"select * from user u left join order_center o on u.order_center_id=o.id left join user_detail d on u.id=d.user_id left join project p on o.project_id=p.id where u.active_flag=1 and u.virtual_flag=0 and d.work_status =#{0} and d.gender=1 and p.city='珠海' and p.id!=1066 order by u.employee_no"})
    List<User> selectAllWomanByWorkStatus(String workStatus);

    @Select({"select u.id userId,CONCAT(u.last_name,u.first_name) name,u.employee_no,u.login_id,u.photo,u.couple_user_id,d.post,d.gender,FLOOR(DATEDIFF(NOW(), u.in_date) / 365) workAgeInKs,u.work_group_id , w.name workGroup,p.city from user u left join user_detail d on u.id=d.user_id left join work_group w  on u.work_group_id=w.id left join order_center o on o.id=u.order_center_id left join project p on p.id=o.project_id where u.active_flag=1 and u.virtual_flag=0 and u.couple_user_id is not null"})
    List<SimUserCoupleInfoVo> selectAllMarriedUser();

    void batchUpdateUserCouple(List<SimUserCoupleInfoVo> users);

    List<SimUserCoupleInfoVo> selectMarriedUsersByCitys(List<String> citys);

    List<SimUserCoupleInfoVo> selectUserIdsByEmployeeNos(List<Long> employeeNos);

    @Select({"select u.*,w.name workGroupName,d.work_status workStatus from user u left join order_center o on u.order_center_id=o.id left join project p on p.id =o.project_id left join work_group w on u.work_group_id=w.id left join user_work_experience uw on uw.user_id=u.id where u.active_flag=1 and u.virtual_flag=0 and p.city=#{0} "})
    List<UserDto> selectByCity(String city);

    @Select({"select u.id from user u left join performance_work_group p on u.perf_work_group_id=p.id where u.active_flag = 1 and u.virtual_flag = 0 and p.active_flag=1"})
    List<Long> selectAllPerfUserIds();

    @Select({"select u.id,concat(u.last_name, u.first_name) name,u.phone mobile,d.certificate_type certificateType,d.certification_no idCode,IF(d.gender=0,1,2)gender,concat(p.name,'/',w.`name`) departmentName,p.id projectId,p.name projectName,s.id companyId,s.name companyName " +
            "from user u left join user_detail d on d.user_id=u.id left join order_center o on o.id=u.order_center_id left join project p on p.id=o.project_id left join cost_center c on c.id=u.cost_center_id left join work_group w on w.id=u.work_group_id left join subcompany s on substr(c.code,1,4)=s.code " +
            "where u.login_id=#{0}"})
    UserMiniVo selectUserMiniVoByLoginId(String loginId);

    List<UserMiniVo> selectUserMiniVoByCertificationNo(List<String> CertificationNos);

    @Select({"select u.id user_Id, u.employee_no, concat(u.last_name, u.first_name) user_name from user u ,user_detail ud " +
            "where ud.user_id = u.id and ud.work_status in ('试用','正式') and u.active_flag=1 and u.virtual_flag=0 and u.order_center_id in (select id from order_center where project_id=#{0})"})
    List<UserEmployeeNoDto> selectActiveUserEmployeeNoDtoByProjectId(Long projectId);

    List<UserEmployeeNoDto> selectActiveUserEmployeeNoDtoByWorkGroupIds(List<Long> workGroupIds);

    List<UserShareTemplateDto> selectActiveUserTemplateDtoByWorkGroupIds(List<Long> workGroupIds);

    List<Long> selectUserIdsByWorkGroupIds(List<Long> groupIds);

    @Select({"select u.id user_Id, u.employee_no, concat(u.last_name, u.first_name) user_name,u.login_id loginId,u.work_group_id workGroupId from user u,user_detail ud " +
            "where u.id = ud.user_id and ud.work_status in ('正式','试用') and u.active_flag=1 and  u.virtual_flag=0 and u.order_center_id in (select id from order_center where project_id=#{0})"})
    List<UserEmployeeNoDto> selectUserEmployeeNoDtoByProjectId(Long platId);

    @Select({"select u.id createBy,u.employee_no,concat(u.last_name,u.first_name) userName,u.work_group_id workGroupId,u.work_group_id workGroupId,CASE d.gender WHEN 0 THEN '男' ELSE '女' END gender, d.work_status,date_format(u.in_date,'%Y/%c/%e') inDate,w.`name` workGroupName,d.post " +
            "from user u left join user_detail d on u.id=d.user_id left join work_group w on u.work_group_id = w.id" +
            " where u.active_flag=1 and u.virtual_flag=0 and d.work_status in( '试用','正式') and u.order_center_id in (select id from order_center where project_id =#{0})"})
    List<FnPlatShareConfigUserDTO> selectActiveShareConfigUserDTOByPlatId(Long platId);

    List<FnPlatShareConfigUserDTO> selectActiveShareConfiUserDTOByWorkGroupIds(List<Long> allSubGroupIds);

    List<Long> selectActiveUserIdsByPlatIds(List<Long> projectIds);

    class UserSqlBuilder {
        public String buildSelectByUserIds(String[] userIds) {
            return new SQL() {{
                SELECT("u.id as user_id, concat(u.last_name,u.first_name) as name, u.login_id, u.work_group_id, d.post");
                FROM("user u");
                LEFT_OUTER_JOIN("user_detail d on u.id = d.user_id");
                WHERE("u.id in (" + String.join(",", userIds) + ")");
                WHERE("u.active_flag = 1");
                WHERE("u.virtual_flag = 0");
            }}.toString();
        }

        public String buildSelectAllActiveUsers(Boolean virtualFlag) {
            return new SQL() {{
                SELECT("u.id, u.login_id, concat(u.last_name,u.first_name) as name,u.photo, d.post, p.name as departmentName");
                FROM("user u");
                LEFT_OUTER_JOIN("user_detail d on u.id = d.user_id");
                LEFT_OUTER_JOIN("cost_center c on u.cost_center_id = c.id");
                LEFT_OUTER_JOIN("department p on c.department_id = p.id");
                WHERE("u.active_flag = 1");
                if (null != virtualFlag) {
                    WHERE("u.virtual_flag = " + virtualFlag);
                }
            }}.toString();
        }

        public String buildSelectAllActiveUsersInGroup(String groupColumn, List<Long> groupIds) {
            return new SQL() {{
                SELECT("id ,CONCAT(last_name,first_name) name ,login_id, " + groupColumn);
                FROM("user");
                WHERE(groupColumn + " in (" + String.join(",", MyStringUtils.listConvertArray(groupIds)) + ")");
            }}.toString();
        }

        public String buildSelectUserInfo(String loginId) {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT u.id,concat(u.last_name, u.first_name) name,u.phone mobile,d.certification_no idCode,IF(d.gender=0,1,2)gender,");
            sql.append("CONCAT('西山居',");
            sql.append("(");
            sql.append("SELECT ");
            sql.append("p.name ");
            sql.append("FROM ");
            sql.append("project p,");
            sql.append("order_center c ");
            sql.append("WHERE ");
            sql.append("p.id = c.project_id ");
            sql.append("AND c.id = u.order_center_id ");
            sql.append("),'/',");
            sql.append("(");
            sql.append("SELECT ");
            sql.append("g.NAME ");
            sql.append("FROM ");
            sql.append("work_group g ");
            sql.append("WHERE ");
            sql.append(" g.id = u.work_group_id ");
            sql.append(")");
            sql.append(") as departmentName ");
            sql.append("FROM USER u,user_detail d WHERE u.id = d.user_id AND login_id = '" + loginId + "'");
            return sql.toString();
        }
    }

    @SelectProvider(type = UserSqlBuilder.class, method = "buildSelectUserInfo")
    UserMiniVo selectUserInfo(String loginId);

    @Select({"select u.id id,concat(u.last_name,u.first_name) name,s.id companyId,o.project_id projectId ,d.gender gender,d.certificate_type certificateType, u.phone mobile,d.certification_no idCode from user u left join user_detail d on d.user_id=u.id left join order_center o on o.id=u.order_center_id left join cost_center c on u.cost_center_id = c.id left join subcompany s on s.code = substr(c.code, 1, 4) where u.active_flag=1 and u.virtual_flag=0"})
    List<UserMiniVo> selectAllActiveUserMiniVo();

    @Select({"select u.id id,concat(u.last_name,u.first_name) name,s.id companyId,o.project_id projectId ,d.gender gender,d.certificate_type certificateType, u.phone mobile,d.certification_no idCode from user u left join user_detail d on d.user_id=u.id left join order_center o on o.id=u.order_center_id left join cost_center c on u.cost_center_id = c.id left join subcompany s on s.code = substr(c.code, 1, 4) "})
    List<UserMiniVo> selectAllUserMiniVo();

    @Select({"SELECT * FROM user WHERE photo_status=0 and active_flag=1 and virtual_flag=0"})
    List<User> findUsersNeedSyncPhoto();

    @Update("update user set photo_status = 1 where id = #{0}")
    int updatePhotoStatus(Long userId);

    List<UserGroupManagerDto> findUserGroupManagers(@Param("user_ids") List<Long> userId);

    @Select({"select u.* from r_user_work_group_perm perm join user u on u.id = perm.user_id and perm.work_group_id = #{group_id} and perm.work_group_role_id = #{role};"})
    List<User> selectUsersByWorkGroupIdAndRole(@Param("group_id") Long groupId, @Param("role") Long role);

    List<User> selectUsersInWorkGroupIdAndRole(@Param("group_id_list") List<Long> groupIdList, @Param("role") Long role);

    List<User> selectUsersByFullName (@Param("full_names") List<String> fullName);

    @Select({"select * from user where active_flag = 1 and virtual_flag = 0 and work_group_id =1010 limit 1;"})
    User selectRandomActive ();

    @Select({"select CONCAT(u.last_name,u.first_name) as userName,ud.certificate_type certificeteType,ud.certification_no certificationNo,ud.work_status workStatus\n" +
            "from user  u \n" +
            "LEFT JOIN user_detail ud on u.id=ud.user_id\n" +
            "where ud.work_status='正式'"})
    List<UserCertificateVo> selectUserCertificateInfo();

    List<UserBaseInfoDto> selectUserBaseInfoList (@Param("requestVo") UserBaseInfoRequestVo requestVo);

    Long countUserBaseInfoList(@Param("requestVo")UserBaseInfoRequestVo requestVo);


    @Select({
            "select user.*, user_detail.* from user left join user_detail on user.id = user_detail.user_id  where user.id = #{user_id};"
    })
    UserDetailBaseInfoDto selectUserJoinUserDetailByUserId (@Param("user_id") Long userId);


    List<UserBaseInfoExportDto> selectUserJoinUserDetailAndDeparemtnyByUserId (@Param("requestVo") UserBaseInfoRequestVo userBaseInfoExportDto);


    @Select({
            "select user.*, user_detail.*, subcompany.name as subcompany, floor((datediff(now(), user.in_date) + user_detail.work_age_out_ks) / 365) as work_age," +
                    " floor(datediff(now(), user.in_date) / 365) as work_age_in_ks,   cc.code as cost_center_code, oc.code as order_center_code  from user left join user_detail on user.id = user_detail.user_id " +
                    " left join cost_center cc on cc.id = user.cost_center_id " +
                    " left join order_center oc on oc.id = user.order_center_id " +
                    " left join subcompany on subcompany.id = user_detail.subcompany_id " +
                    "where user.id = #{user_id};"
    })
    UserWorkInfoDto selectUserWorkInfoByUserId (@Param("user_id") Long userId);
}