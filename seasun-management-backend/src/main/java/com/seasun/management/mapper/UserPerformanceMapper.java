package com.seasun.management.mapper;

import com.seasun.management.dto.*;
import com.seasun.management.model.UserPerformance;
import com.seasun.management.vo.UserPerformanceVo;
import com.seasun.management.vo.UserPhotoWallVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserPerformanceMapper {
    @Delete({
            "delete from user_performance",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into user_performance (work_group_id, sub_group, ",
            "parent_group, user_id, ",
            "year, month, final_performance, ",
            "status, month_goal, ",
            "month_goal_last_modify_time, direct_manager_comment, ",
            "manager_comment, last_modify_user, ",
            "self_comment, self_performance, ",
            "fm_project_id, fm_confirmed_status, ",
            "post, work_age, work_age_in_ks, ",
            "work_group_name, work_status, ",
            "create_time, update_time, ",
            "sub_b_flag)",
            "values (#{workGroupId,jdbcType=BIGINT}, #{subGroup,jdbcType=VARCHAR}, ",
            "#{parentGroup,jdbcType=BIGINT}, #{userId,jdbcType=BIGINT}, ",
            "#{year,jdbcType=INTEGER}, #{month,jdbcType=INTEGER}, #{finalPerformance,jdbcType=VARCHAR}, ",
            "#{status,jdbcType=VARCHAR}, #{monthGoal,jdbcType=VARCHAR}, ",
            "#{monthGoalLastModifyTime,jdbcType=TIMESTAMP}, #{directManagerComment,jdbcType=VARCHAR}, ",
            "#{managerComment,jdbcType=VARCHAR}, #{lastModifyUser,jdbcType=BIGINT}, ",
            "#{selfComment,jdbcType=VARCHAR}, #{selfPerformance,jdbcType=VARCHAR}, ",
            "#{fmProjectId,jdbcType=BIGINT}, #{fmConfirmedStatus,jdbcType=VARCHAR}, ",
            "#{post,jdbcType=VARCHAR}, #{workAge,jdbcType=INTEGER}, #{workAgeInKs,jdbcType=INTEGER}, ",
            "#{workGroupName,jdbcType=VARCHAR}, #{workStatus,jdbcType=VARCHAR}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP}, ",
            "#{subBFlag,jdbcType=BIT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(UserPerformance record);

    int insertSelective(UserPerformance record);

    @Select({
            "select",
            "id, work_group_id, sub_group, parent_group, user_id, year, month, final_performance, ",
            "Status, month_goal, month_goal_last_modify_time, direct_manager_comment, manager_comment, ",
            "last_modify_user, self_comment, self_performance, fm_project_id, fm_confirmed_status, ",
            "post, work_age, work_age_in_ks, work_group_name, work_status, create_time, update_time, ",
            "sub_b_flag",
            "from user_performance",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserPerformanceMapper.BaseResultMap")
    UserPerformance selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserPerformance record);

    @Update({
            "update user_performance",
            "set work_group_id = #{workGroupId,jdbcType=BIGINT},",
            "sub_group = #{subGroup,jdbcType=VARCHAR},",
            "parent_group = #{parentGroup,jdbcType=BIGINT},",
            "user_id = #{userId,jdbcType=BIGINT},",
            "year = #{year,jdbcType=INTEGER},",
            "month = #{month,jdbcType=INTEGER},",
            "final_performance = #{finalPerformance,jdbcType=VARCHAR},",
            "status = #{status,jdbcType=VARCHAR},",
            "month_goal = #{monthGoal,jdbcType=VARCHAR},",
            "month_goal_last_modify_time = #{monthGoalLastModifyTime,jdbcType=TIMESTAMP},",
            "direct_manager_comment = #{directManagerComment,jdbcType=VARCHAR},",
            "manager_comment = #{managerComment,jdbcType=VARCHAR},",
            "last_modify_user = #{lastModifyUser,jdbcType=BIGINT},",
            "self_comment = #{selfComment,jdbcType=VARCHAR},",
            "self_performance = #{selfPerformance,jdbcType=VARCHAR},",
            "fm_project_id = #{fmProjectId,jdbcType=BIGINT},",
            "fm_confirmed_status = #{fmConfirmedStatus,jdbcType=VARCHAR},",
            "post = #{post,jdbcType=VARCHAR},",
            "work_age = #{workAge,jdbcType=INTEGER},",
            "work_age_in_ks = #{workAgeInKs,jdbcType=INTEGER},",
            "work_group_name = #{workGroupName,jdbcType=VARCHAR},",
            "work_status = #{workStatus,jdbcType=VARCHAR},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP},",
            "sub_b_flag = #{subBFlag,jdbcType=BIT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserPerformance record);


    /* the flowing are user defined ... */

    @Deprecated
    @Select({"SELECT x.id,x.user_id,x.year,x.month,x.name,x.work_group,x.final_performance,x.status,CONCAT(u.last_name,u.first_name)manager,x.self_performance FROM",
            "(SELECT p.*,r.user_id m_id FROM",
            "(SELECT v.*,w.name work_group FROM",
            "(SELECT p.id,",
            "p.user_id, p.year, p.month, p.final_performance, p.status, p.self_performance ,CONCAT(u.last_name,u.first_name) name,u.work_group_id ",
            "from user_performance p join user u on u.id=p.user_id)",
            "v LEFT JOIN work_group w ON v.work_group_id=w.id)p",
            "LEFT JOIN r_user_work_group_perm r on r.work_group_id=p.work_group_id)",
            "x left join user u on x.m_id=u.id",
            "where year= #{year}",
            "and month= #{month}",
            "and user_id= #{userId}"})
    List<PerformanceDto> selectPersonalPerformanceList(@Param("userId") long userId, @Param("year") int year, @Param("month") int month);

    @Deprecated
    List<PerformanceDto> selectPerformanceList(@Param("year") int year, @Param("month") int month, @Param("list") List<Long> list);

    @Deprecated
    List<UserStatusDto> selectStatus(@Param("year") int year, @Param("month") int month, @Param("list") List<Long> list);

    @Deprecated
    List<UserPerformanceLevelDto> selectPerformancePro(@Param("year") int year, @Param("month") int month, @Param("list") List<Long> list);

    @Deprecated
    @Select({"SELECT r.id ,r.user_id, r.year ,r.month ,r.name ,r.work_group ,r.final_performance ,r.status,GROUP_CONCAT(CONCAT(user.last_name,user.first_name)) manager, r.self_performance FROM",
            "(SELECT z.*,ru.user_id manger_id FROM",
            "(SELECT y.*,work_group.name work_group FROM",
            "(SELECT x.* ,user.work_group_id,CONCAT(user.last_name,user.first_name)name from",
            "(SELECT id, user_id,year,month,final_performance,status,self_performance from user_performance)",
            "x LEFT JOIN user on x.user_id=user.id)",
            "y LEFT JOIN work_group ON y.work_group_id=work_group.id)",
            "z LEFT JOIN r_user_work_group_perm ru ON z.work_group_id=ru.work_group_id)",
            "r LEFT JOIN user ON r.manger_id=user.id",
            "where year= #{year}",
            "and month= #{month}",
            "GROUP BY id"
    })
    List<PerformanceDto> selectAllPerformanceList(@Param("year") int year, @Param("month") int month);

    @Deprecated
    @Select({"SELECT status,count(*) num from user_performance",
            "where year= #{year}",
            "and month= #{month}",
            "GROUP BY status"})
    List<UserStatusDto> selectAllStatus(@Param("year") int year, @Param("month") int month);

    @Deprecated
    @Select({"SELECT final_performance level ,count(*) num from user_performance",
            "where year= #{year}",
            "and month= #{month}",
            "GROUP BY final_performance"})
    List<UserPerformanceLevelDto> selectAllPerformancePro(@Param("year") int year, @Param("month") int month);

    @Deprecated
    @Select({"SELECT  x.name,x.employee_no,x.join_post_date join_time ,x.year,x.month,  ",
            "floor(DATEDIFF(NOW(),x.join_post_date)/365+x.work_age_out_ks) work_age,x.post ,  ",
            "wg.name work_group ,x.final_performance performance,x.last_performance,x.month_goal,x.manager_comment,x.self_comment,x.status,x.manager  FROM   ",
            "(SELECT r.user_id,r.name,r.employee_no, r.work_group_id, r.year, r.month ,r.manager, r.status,",
            "r.join_post_date ,r.work_age_out_ks ,r.post,r.final_performance,r.last_performance,r.month_goal,r.manager_comment,r.self_comment from  ",
            "(select p.user_id, p.name, p.employee_no,  p.year, p.month ,p.status,",
            "p.work_group_id,ud.join_post_date ,ud.work_age_out_ks ,ud.post,p.final_performance,p.last_performance,p.month_goal,p.manager_comment,p.self_comment,p.manager FROM  ",
            "(SELECT q.*,GROUP_CONCAT(user.last_name,user.first_name) manager FROM ",
            "(SELECT y.*,ru.user_id man_id FROM ",

            "(SELECT w.* , up.final_performance last_performance FROM",
            "(select up.id,up.user_id,CONCAT(u.last_name,u.first_name) name , year,month, status,",
            "u.employee_no,u.work_group_id, up.final_performance,up.month_goal,up.manager_comment,up.self_comment  ",
            "FROM user_performance up join user u on up.user_id=u.id ",
            "WHERE up.id= #{id})",
            "w LEFT JOIN user_performance up ON w.user_id=up.user_id",
            "AND CASE WHEN w.month=1 THEN (up.year=w.year-1 AND up.month=12)",
            "ELSE (up.year=w.year AND up.month=w.month-1)",
            "END)",

            "y LEFT JOIN r_user_work_group_perm ru on y.work_group_id=ru.work_group_id) ",
            "q LEFT JOIN user on q.man_id=user.id)",
            "p LEFT JOIN user_detail ud on p.user_id=ud.user_id )",
            "r LEFT JOIN work_group wg ON r.work_group_id=wg.id)  ",
            "x LEFT JOIN work_group wg ON wg.id=x.work_group_id ",
            "limit 1"
    })
    UserPerformanceDetailDto selectUserInfo(long id);

    @Deprecated
    @Insert({"INSERT INTO user_performance(user_id,year,month,status) ",
            "(SELECT id user_id,#{year} year,#{month} month,'未提交' status ",
            "FROM user",
            "WHERE active_flag=0",
            "group by id)"})
    int generatePerformances(@Param("year") int year, @Param("month") int month);


    @Deprecated
    List<UserPerformanceDetailDto> selectAllPerformanceDetailList(@Param("year") int year, @Param("month") int month);

    @Deprecated
    List<UserPerformanceDetailDto> selectPerformanceDetailList(@Param("year") int year, @Param("month") int month, @Param("list") List<Long> list);

    @Select({
            "select concat(u.last_name,u.first_name) as user_name, u.login_id, u.employee_no, u.in_date, p.*, g.performance_manager_id as leader_id",
            "from user_performance p",
            "left join user u on p.user_id = u.id",
            "left join performance_work_group g on g.id = u.perf_work_group_id",
            "where p.id = #{id}"
    })
    UserPerformanceDto selectWithUserNameByPrimaryKey(Long id);

    @Select({
            "select concat(u.last_name,u.first_name) as user_name, u.login_id, u.employee_no, u.in_date, p.*, g.performance_manager_id as leader_id",
            "from user_performance p",
            "left join user u on p.user_id = u.id",
            "left join performance_work_group g on g.id = u.work_group_id",
            "where year = #{year} and month = #{month} order by p.work_group_name"
    })
    List<UserPerformanceDto> selectAllByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Select({
            "select concat(u.last_name,u.first_name) as user_name, u.login_id, u.employee_no, u.in_date, p.*, g.performance_manager_id as leader_id",
            "from user_performance p",
            "left join user u on p.user_id = u.id",
            "left join performance_work_group g on g.id = u.work_group_id",
            "where year = #{year} and month = #{month} order by p.work_group_name and status='未完成'"
    })
    List<UserPerformanceDto> selectPerfByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Select({
            "select us.*,u.login_id from user_performance us left join user u on u.id=us.user_id where year = #{year} and month = #{month} and status = '已完成'"
    })
    List<UserPerformanceVo> selectPureModelByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Select({
            "select p.year, p.month, p.status from user_performance p",
            "left join performance_work_group g on g.id = p.work_group_id",
            "where g.performance_manager_id = #{manageId}",
            "group by p.year, p.month, p.status",
            "order by p.year, p.month"
    })
    List<HistoryStatusDto> selectHistoryStatusByManagerId(long manageId);

    @Select({
            "select p.year, p.month, p.status from user_performance p",
            "where p.work_group_id = #{workGroupId}",
            "group by p.year, p.month, p.status",
            "order by p.year, p.month"
    })
    List<HistoryStatusDto> selectHistoryStatusByWorkGroupId(long workGroupId);

    @Select({
            "select year, month from user_performance",
            "where status = '" + UserPerformance.Status.complete + "'",
            "group by year, month",
            "order by year desc, month desc",
            "limit 1"
    })
    YearMonthDto selectWithYearMonthByLastComplete();

    @Select({
            "select year, month from user_performance",
            "group by year, month",
            "order by year, month"
    })
    List<YearMonthDto> selectAllWithYearMonth();

    List<YearMonthDto> selectYearMonthByPerformanceWorkGroupIds(List<Long> ids);

    @Select({
            "select year, month from user_performance",
            "where status = '" + UserPerformance.Status.complete + "'",
            "group by year, month",
            "order by year, month"
    })
    List<YearMonthDto> selectAllWithYearMonthByComplete();

    @Select("select * from user_performance where user_id = #{userId} and year = #{year} and month = #{month}")
    UserPerformance selectByUserIdAndYearAndMonth(@Param("userId") long userId, @Param("year") int year, @Param("month") int month);

    List<UserPerformance> selectByUserIdsAndYearAndMonth(@Param("list") List<Long> userIds, @Param("year") int year, @Param("month") int month);

    @Select({
            "select * from user_performance",
            "where user_id = #{userId}",
            "order by year, month"
    })
    List<UserPerformance> selectAllByUserId(@Param("userId") long userId);

    @Update({
            "update user_performance",
            "set status = '" + UserPerformance.Status.complete + "'",
            "where year = #{year} and month = #{month}"
    })
    int updateStatusToCompleteByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Select({
            "select count(*) from user_performance p",
            "left join performance_work_group g on g.id = p.work_group_id",
            "where p.year = #{year} and p.month = #{month}",
            "and ((g.performance_manager_id = #{managerId} and p.status = '" + UserPerformance.Status.locked + "') or p.status = '" + UserPerformance.Status.complete + "')"
    })
    int selectCountByManagerAndLockedOrComplete(@Param("managerId") long managerId, @Param("year") int year, @Param("month") int month);

    @Select({
            "select count(*) from user_performance p",
            "where p.year = #{year} and p.month = #{month}",
            "and (p.status = '" + UserPerformance.Status.locked + "' or p.status = '" + UserPerformance.Status.complete + "')"
    })
    int selectCountByLockedOrComplete(@Param("year") int year, @Param("month") int month);

    @Select({
            "select count(*) from user_performance p",
            "where p.year = #{year} and p.month = #{month}",
            "and p.status = '" + UserPerformance.Status.complete + "'"
    })
    int selectCountByComplete(@Param("year") int year, @Param("month") int month);

    @Select({
            "select count(*) from user_performance",
            "where year = #{year} and month = #{month}",
            "and ((work_group_id = #{workGroupId} and status = '" + UserPerformance.Status.locked + "') or status = '" + UserPerformance.Status.complete + "')"
    })
    int selectCountByWorkGroupAndLockedOrComplete(@Param("workGroupId") long workGroupId, @Param("year") int year, @Param("month") int month);

    @Select("select * from user_performance where work_group_id = #{workGroupId} and status != '" + UserPerformance.Status.complete + "' limit 1")
    UserPerformance selectUnfinishedRecordByWorkGroupId(Long workGroupId);

    int batchInsertByLeaderSubmit(List<UserPerformanceDto> userPerformanceDtos);

    int batchUpdateStatusByPks(List<UserPerformanceDto> userPerformanceDtos);

    int batchInsertUserPerformance(List<UserPerformance> userPerformanceList);

    @Delete("delete from user_performance where year = #{0} and month = #{1}")
    void deleteByYearAndMonth(int year, int month);

    @Delete("delete from user_performance where user_id = #{0} and status != '" + UserPerformance.Status.complete + "'")
    int deleteUnfinishedRecordByUserId(Long userId);

    int deleteByWorkGroupIdAndYearAndMonth(@Param("year") int year, @Param("month") int month, @Param("workGroupIds") List<Long> workGroupIds);

    int selectCountByWorkGroupIdAndYearAndMonth(@Param("year") int year, @Param("month") int month, @Param("workGroupIds") List<Long> workGroupIds);

    List<UserPerformance> selectByWorkGroupIdAndYearAndMonth(@Param("year") int year, @Param("month") int month, @Param("workGroupIds") List<Long> workGroupIds);

    int updateFinalPerformanceByUserIdAndYearMonth(@Param("year") int year, @Param("month") int month, @Param("list") List<UserPerformanceDto> userPerformanceDtos);

    int updateFPAndMCById(@Param("list") List<UserPerformanceDto> userPerformanceDtoList);


    List<UserPerformanceDto> selectPerformancesByPKs(List<Long> list);

    // 更新
    void batchUpdateUserPerformanceInfoByPks(List<UserPerformance> list);

    @Select({"SELECT * FROM user_performance WHERE year= #{year} and month = #{month} AND work_group_id in(",
            "SELECT id FROM performance_work_group WHERE performance_manager_id = #{userId})"})
    List<UserPerformance> selectDMUserPerformance(@Param("userId") long userId, @Param("year") int year, @Param("month") int month);

    @Select({"SELECT u.id ,rp.project_id  FROM user ",
            "LEFT JOIN order_center oc ON user.order_center_id =oc .id ",
            "LEFT JOIN r_user_project_perm rp ON rp.project_id = oc.project_id",
            "LEFT JOIN user u ON u.id = rp.user_id  ",
            "LEFT JOIN project_role ON project_role.id = rp.project_role_id",
            "WHERE project_role.name ='项目负责人'",
            "AND `user`.id = #{userId}"})
    Long selectPlatLeaderIdByUserId(Long userId);

    List<UserPerformanceDto> selectDtoByWorkGroupIdsAndDate(@Param("year") Integer year, @Param("month") Integer month, @Param("list") List<Long> managerGroupIds);

    List<UserPerformanceDto> selectDtoByUserIdsAndDate(@Param("year") Integer year, @Param("month") Integer month, @Param("list") List<Long> managerGroupIds);

    List<UserPerformanceDto> selectLeavedByGroupIdsAndUserIds(@Param("year") Integer year, @Param("groupIds") List<Long> allExportPerfWorkGoupIds, @Param("userIds") List<Long> allSubUserIds);

    @Select({"select * from user_performance where user_id=#{0} order by year desc,month desc limit 1"})
    UserPerformance selectUserLastPerformance(Long memberId);

    @Select({"select year,month from user_performance where status !='"+UserPerformance.Status.complete+"' order by year desc,month desc limit 1"})
    YearMonthDto selectLastWriteYearMonth();

    class UserPerformanceSqlBuilder {
        public String buildSelectUserPerformance(Integer year,Integer month,String floorId) {
            StringBuffer sql = new StringBuffer();
            sql.append("select tb.id,tb.loginId,tb.name,tb.photo,tb.source,tb.year,tb.month,tb.projectName,left(IFNULL(f.name,'OTHER'),1)floorNo from (");
            sql.append("select ");
            sql.append("u.id,");
            sql.append("u.floor_id,");
            sql.append("u.login_id loginId,");
            sql.append("concat(u.last_name, u.first_name) name,");
            sql.append("concat(u.login_id, '.png') photo,");
            sql.append("concat(u.login_id, '.png') source,");
            sql.append("up.year year,");
            sql.append("up.month month,");
            sql.append("IFNULL((select pc.name from project pc where pc.id = p.parent_hr_id),p.NAME) projectName ");
            sql.append("from (");
            sql.append("select u.id id,ifnull(sd.disp_city, s.city) as location ");
            sql.append("from user u ");
            sql.append("left join user_detail d on u.id = d.user_id ");
            sql.append("left join order_center o on o.id = u.order_center_id ");
            sql.append("left join cost_center c on u.cost_center_id = c.id ");
            sql.append("left join subcompany s on s.code = substr(c.code, 1, 4) ");
            sql.append("left join user_special_disp sd on sd.user_id = u.id ");
            sql.append("where u.virtual_flag = 0 and u.active_flag = 1");
            sql.append(")USER_VIEW,");
            sql.append("user_performance up,");
            sql.append("USER u,");
            sql.append("project p,");
            sql.append("order_center c ");
            sql.append("WHERE ");
            sql.append("USER_VIEW.location='珠海' ");
            sql.append("and USER_VIEW.id=U.id ");
            sql.append("AND u.id = up.user_id ");
            sql.append("AND p.id = c.project_id ");
            sql.append("AND c.id = u.order_center_id ");
            sql.append("AND up.final_performance = 'S' ");
            sql.append("AND up.`year` = "+year+" and up.`month`="+month+" ");
            sql.append("ORDER BY u.login_id");
            sql.append(")tb left join floor f on tb.floor_id = f.id ");
            sql.append("WHERE 1=1 ");
            if(StringUtils.isNotEmpty(floorId) && (floorId.equals("1") || floorId.equals("2") || floorId.equals("3"))){
                sql.append("AND f.`name` like '"+floorId+"栋%' ");
            }else if("other".equalsIgnoreCase(floorId)){
                sql.append("AND F.`name` IS NULL OR (F.`name` IS NOT NULL  AND (f.`name` NOT LIKE '1栋%' AND f.`name` NOT LIKE '2栋%' AND f.`name` NOT LIKE '3栋%'))");//为空或者(不为空但也不在1,2,3)层里面的
            }
            return sql.toString();
        }
    }
    @Select({"SELECT up.year,up.month FROM user_performance up,USER u,floor f WHERE u.id = up.user_id AND u.floor_id = f.id " +
            "AND up.final_performance = 'S' AND (f.name LIKE '1栋%' OR f.name LIKE '2栋%' OR f.name LIKE '3栋%')ORDER BY up.year DESC,up.month DESC LIMIT 0,1"})
    UserPhotoWallVo selectUserPerformanceDate();

    @SelectProvider(type = UserPerformanceSqlBuilder.class, method = "buildSelectUserPerformance")
    List<UserPhotoWallVo> selectUserPerformance(Integer year,Integer month,String floorId);

    @Select({
            "select us.*,u.login_id from user_performance us left join user u on u.id=us.user_id where year = #{year} and month = #{month} and status = '已完成'\n" +
                    "and NOT EXISTS (select 1 from perf_appeal_info ap WHERE ap.year = us.year and ap.month = us.month and ap.user_id = us.user_id)"
    })
    List<UserPerformanceVo> selectUserPerByYearAndMonth(@Param("year") int year, @Param("month") int month);
    int updateUserPerByYearAndMonth(@Param("year") int year, @Param("month") int month,@Param("list") List<UserPerformanceVo> list);
}