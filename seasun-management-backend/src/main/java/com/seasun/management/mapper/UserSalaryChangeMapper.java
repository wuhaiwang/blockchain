package com.seasun.management.mapper;

import com.seasun.management.dto.UserSalaryChangeDetailDto;
import com.seasun.management.vo.OrdinateSalaryChangeAppVo;
import com.seasun.management.dto.UserSalaryChangeDto;
import com.seasun.management.model.UserSalaryChange;
import com.seasun.management.vo.SalaryChangeCharacterVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserSalaryChangeMapper {
    @Delete({
            "delete from user_salary_change",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into user_salary_change (work_group_id, sub_group, ",
            "user_id, year, quarter, ",
            "old_salary, increase_salary, ",
            "score, performance_count, ",
            "evaluate_type, grade, ",
            "status, last_salary_change_date, ",
            "last_salary_change_amount, last_grade_change_date, ",
            "create_time, update_time, ",
            "work_age, work_age_in_ks, ",
            "post, work_group_name)",
            "values (#{workGroupId,jdbcType=BIGINT}, #{subGroup,jdbcType=VARCHAR}, ",
            "#{userId,jdbcType=BIGINT}, #{year,jdbcType=INTEGER}, #{quarter,jdbcType=INTEGER}, ",
            "#{oldSalary,jdbcType=INTEGER}, #{increaseSalary,jdbcType=INTEGER}, ",
            "#{score,jdbcType=INTEGER}, #{performanceCount,jdbcType=INTEGER}, ",
            "#{evaluateType,jdbcType=VARCHAR}, #{grade,jdbcType=VARCHAR}, ",
            "#{status,jdbcType=VARCHAR}, #{lastSalaryChangeDate,jdbcType=DATE}, ",
            "#{lastSalaryChangeAmount,jdbcType=INTEGER}, #{lastGradeChangeDate,jdbcType=DATE}, ",
            "#{createTime,jdbcType=DATE}, #{updateTime,jdbcType=DATE}, ",
            "#{workAge,jdbcType=INTEGER}, #{workAgeInKs,jdbcType=INTEGER}, ",
            "#{post,jdbcType=VARCHAR}, #{workGroupName,jdbcType=VARCHAR})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(UserSalaryChange record);

    int insertSelective(UserSalaryChange record);

    @Select({
            "select",
            "id, work_group_id, sub_group, user_id, year, quarter, old_salary, increase_salary, ",
            "score, performance_count, evaluate_type, grade, status, last_salary_change_date, ",
            "last_salary_change_amount, last_grade_change_date, create_time, update_time, ",
            "work_age, work_age_in_ks, post, work_group_name",
            "from user_salary_change",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserSalaryChangeMapper.BaseResultMap")
    UserSalaryChange selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserSalaryChange record);

    @Update({
            "update user_salary_change",
            "set work_group_id = #{workGroupId,jdbcType=BIGINT},",
            "sub_group = #{subGroup,jdbcType=VARCHAR},",
            "user_id = #{userId,jdbcType=BIGINT},",
            "year = #{year,jdbcType=INTEGER},",
            "quarter = #{quarter,jdbcType=INTEGER},",
            "old_salary = #{oldSalary,jdbcType=INTEGER},",
            "increase_salary = #{increaseSalary,jdbcType=INTEGER},",
            "score = #{score,jdbcType=INTEGER},",
            "performance_count = #{performanceCount,jdbcType=INTEGER},",
            "evaluate_type = #{evaluateType,jdbcType=VARCHAR},",
            "grade = #{grade,jdbcType=VARCHAR},",
            "status = #{status,jdbcType=VARCHAR},",
            "last_salary_change_date = #{lastSalaryChangeDate,jdbcType=DATE},",
            "last_salary_change_amount = #{lastSalaryChangeAmount,jdbcType=INTEGER},",
            "last_grade_change_date = #{lastGradeChangeDate,jdbcType=DATE},",
            "create_time = #{createTime,jdbcType=DATE},",
            "update_time = #{updateTime,jdbcType=DATE},",
            "work_age = #{workAge,jdbcType=INTEGER},",
            "work_age_in_ks = #{workAgeInKs,jdbcType=INTEGER},",
            "post = #{post,jdbcType=VARCHAR},",
            "work_group_name = #{workGroupName,jdbcType=VARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserSalaryChange record);

    /* the flowing are user defined ... */

    @Select({"SELECT y.id,y.year,y.quarter,y.user_id,y.name,y.score,y.grade,y.evaluate_type,GROUP_CONCAT(CONCAT(user.last_name,user.first_name))manager,y.work_group,y.status,y.increase_salary FROM ",
            "(SELECT x.*,p.user_id manager_id FROM",
            "(SELECT w.* ,work_group.name work_group FROM ",
            "(SELECT us.*,user.work_group_id, CONCAT(user.last_name,user.first_name)name FROM ",
            "(SELECT *from user_salary_change) ",
            "us LEFT JOIN user ON us.user_id=user.id) ",
            "w LEFT JOIN work_group ON w.work_group_id=work_group.id) ",
            "x LEFT JOIN r_user_work_group_perm p on x.work_group_id=p.work_group_id) ",
            "y LEFT JOIN user ON y.manager_id=user.id",
            "WHERE year= #{year}",
            "AND quarter= #{quarter}",
            "GROUP BY id"})
    List<UserSalaryChangeDto> selectAllSalaryChangeList(@Param("year") int year, @Param("quarter") int quarter);


    List<UserSalaryChangeDto> selectSalaryChangeList(@Param("year") int year, @Param("list") List<Long> list, @Param("quarter") int quarter);


    //The followed is defined for the app service

    @Select({"SELECT  u.id,u.year,u.quarter,u.user_id,u.name,u.score,u.evaluate_type,u.work_group,GROUP_CONCAT(CONCAT(user.last_name,user.first_name)) manager",
            ",u.grade,u.last_salary_change_amount,u.last_grade_change_date,u.last_salary_change_date",
            ",u.old_salary,u.increase_salary ,u.sub_group,u.status,u.history_work_group_id  work_group_id,",
            "u.work_age , u.work_age_in_ks ,u.post ,u.work_group_name,u.performance_count FROM",
            "(SELECT z.*,r.user_id manager_id FROM (SELECT y.*,work_group.name work_group FROM",
            "(SELECT x.*,CONCAT(user.last_name,user.first_name)name, user.work_group_id FROM",
            "(SELECT us.id,us.year,us.quarter,us.status,us.sub_group,us.work_group_id history_work_group_id,us.user_id,us.score ,us.evaluate_type",
            " ,us.old_salary,us.increase_salary,us.grade,us.last_salary_change_amount,us.last_grade_change_date,us.performance_count,",
            "us.last_salary_change_date ,us.work_age , us.work_age_in_ks ,us.post ,us.work_group_name FROM user_salary_change us",
            "WHERE year =#{year} AND quarter= #{quarter})",
            "x LEFT JOIN user ON x.user_id=user.id) y LEFT JOIN work_group ON y.work_group_id=work_group.id)",
            "z LEFT JOIN (SELECT * FROM r_user_work_group_perm WHERE work_group_role_id=3)",
            "r ON z.work_group_id=r.work_group_id) u LEFT JOIN user ON u.manager_id=user.id",
            "GROUP BY user_id"})
    List<OrdinateSalaryChangeAppVo> selectAllSalaryChangeByYearAndQuarter(@Param("year") int year, @Param("quarter") int quarter);

    @Select({"SELECT x.*,work_group.name work_group_name FROM",
            "(SELECT work_group_id FROM r_user_work_group_perm WHERE user_id= #{userId}",
            "AND work_group_role_id=2)",
            "x LEFT JOIN work_group ON x.work_group_id=work_group.id"})
    List<SalaryChangeCharacterVo.GroupManagerIdentity> getCharacter(Long userId);


    List<OrdinateSalaryChangeAppVo> getGroupSimpleInfoList(List<Long> list);

    @Select({"SELECT  u.id,u.year,u.quarter,u.user_id,u.name,u.score,u.evaluate_type ,u.grade,u.employee_no,",
            "u.old_salary,u.increase_salary ,u.last_salary_change_amount,u.last_grade_change_date,u.last_salary_change_date,u.performance_count ,",
            "u.work_age , u.work_age_in_ks ,u.post ,u.work_group_name work_group  FROM",
            "(SELECT x.*,CONCAT(user.last_name,user.first_name)name,user.employee_no FROM",
            "(SELECT us.id,us.year,us.quarter,us.user_id,us.score ,us.evaluate_type,us.grade",
            ",us.old_salary,us.increase_salary,us.last_salary_change_amount,us.last_grade_change_date,us.last_salary_change_date,us.performance_count",
            ",us.work_age , us.work_age_in_ks ,us.post ,us.work_group_name FROM user_salary_change us",
            "WHERE year= #{year} AND quarter= #{quarter} AND user_id=#{userId})x LEFT JOIN user ON x.user_id=user.id)u"})
    OrdinateSalaryChangeAppVo getIndividualSalaryDetail(@Param("userId") Long userId, @Param("year") Integer year, @Param("quarter") Integer quarter);

    @Update("update user_salary_change set status='已完成' ,update_time =now() where year= #{year} and quarter =#{quarter}")
    int updateAllEmployee(@Param("year") int year, @Param("quarter") int quarter);

    @Select({"SELECT  u.id,u.year,u.quarter,u.user_id,u.name,u.score,u.grade,u.evaluate_type,u.work_group,GROUP_CONCAT(CONCAT(user.last_name,user.first_name)) manager",
            ",u.old_salary,u.increase_salary,u.performance_count ,u.sub_group,u.status,u.history_work_group_id work_group_id FROM",
            "(SELECT z.*,r.user_id manager_id FROM (SELECT y.*,work_group.name work_group FROM",
            "(SELECT x.*,CONCAT(user.last_name,user.first_name)name, user.grade,user.work_group_id FROM",
            "(SELECT us.id,us.year,us.quarter,us.status,us.sub_group,us.work_group_id history_work_group_id,us.user_id,us.score ,us.evaluate_type",
            ",us.old_salary,us.increase_salary,us.performance_count,us.work_age , us.work_age_in_ks ,us.post ,us.work_group_name FROM user_salary_change us",
            "WHERE work_group_id=#{workGroupId} AND year= #{year} AND quarter= #{quarter})",
            "x LEFT JOIN user ON x.user_id=user.id) y LEFT JOIN work_group ON y.work_group_id=work_group.id)",
            "z LEFT JOIN (SELECT * FROM r_user_work_group_perm WHERE work_group_role_id=3)",
            "r ON z.work_group_id=r.work_group_id) u LEFT JOIN user ON u.manager_id=user.id",
            "GROUP BY user_id"})
    List<OrdinateSalaryChangeAppVo> getHistorySubLeader(@Param("workGroupId") long workGroupId, @Param("year") int year, @Param("quarter") int quarter);

    @Select("select * from user_salary_change where work_group_id = #{workGroupId} and status !='已完成' limit 1")
    UserSalaryChange selectUnfinishedRecordByWorkGroupId(Long workGroupId);

    @Select("SELECT count(*)FROM (SELECT *FROM user_performance where year= #{year} AND month> 3* #{quarter}-3 AND month<=3* #{quarter} AND status='已完成' GROUP BY year,month)x ")
    int performanceMonthCount(@Param("year") Integer year, @Param("quarter") Integer quarter);

    @Select({"SELECT uu.*,ss.manager,ss.work_group FROM (SELECT s.*,(CASE WHEN xy.work_group_role_id IS NULL THEN 0 ELSE 1 END) leader_flag FROM ",
            "(SELECT z.id,z.user_id,z.year,z.quarter,z.score,z.performance_count,z.status,CONCAT(user.last_name,user.first_name) name,user.grade,user.evaluate_type ,",
            "(CASE WHEN z.work_group_id IS NULL THEN user.work_group_id ELSE z.work_group_id END) work_group_id, user.login_id ",
            ",z.increase_salary,z.last_grade_change_date,z.last_salary_change_amount,z.last_salary_change_date FROM",
            "(SELECT usc.id,y.user_id,y.year,y.quarter,y.score,y.performance_count ,usc.status,usc.work_group_id ,usc.increase_salary,usc.last_grade_change_date,",
            "usc.last_salary_change_amount,usc.last_salary_change_date FROM (SELECT x.id,x.user_id,x.year,x.quarter,SUM(score) score,count(*) performance_count FROM",
            "(SELECT id,user_id, year,(CASE WHEN (month>0 AND month<=3) THEN 1 WHEN (month>3 AND month<=6) THEN 2 WHEN (month>6 AND month<=9) THEN 3",
            "ELSE 4 END)as quarter,(CASE WHEN final_performance='S' THEN 4 WHEN final_performance='A' THEN 3 WHEN final_performance='B' THEN 2",
            "WHEN final_performance='C' THEN 1 ELSE 0 END) as score FROM user_performance where year=#{year} AND month>#{quarter}*3-3 AND month<=#{quarter}*3 AND status= '已完成')x",
            "GROUP BY user_id)y LEFT JOIN user_salary_change usc ON y.user_id =usc.user_id AND y.year =usc.year AND y.quarter=usc.quarter)z",
            "LEFT JOIN user ON z.user_id=user.id)s LEFT JOIN(SELECT * FROM r_user_work_group_perm r WHERE work_group_role_id=2)xy ON s.user_id=xy.user_id)uu",
            "LEFT JOIN (SELECT res.work_group_id,res.manager,work_group.name work_group FROM (SELECT ru.work_group_id,GROUP_CONCAT(CONCAT(user.last_name,user.first_name))manager FROM",
            "(SELECT * FROM r_user_work_group_perm WHERE work_group_role_id=3)ru LEFT JOIN user ON user_id=user.id GROUP BY work_group_id)",
            "res LEFT JOIN work_group ON res.work_group_id=work_group.id)ss ON uu.work_group_id=ss.work_group_id"})
    List<OrdinateSalaryChangeAppVo> getQuarterRangePerformance(@Param("year") Integer year, @Param("quarter") Integer quarter);

    @Select({"SELECT uu.user_id,uu.year,uu.quarter,uu.score,uu.performance_count,uu.employee_no,",
            "uu.name,uu.grade,uu.evaluate_type ,w.name work_group FROM (SELECT z.user_id,z.year,z.quarter,z.score,z.performance_count,",
            "CONCAT(user.last_name,user.first_name) name,user.grade,user.evaluate_type ,",
            "user.work_group_id ,user.employee_no FROM (SELECT x.user_id,x.year,x.quarter,SUM(score) score ,count(*) performance_count FROM",
            "(SELECT user_id, year,(CASE WHEN (month>0 AND month<=3) THEN 1 WHEN (month>3 AND month<=6) THEN 2",
            "WHEN (month>6 AND month<=9) THEN 3 ELSE 4 END)as quarter,(CASE WHEN final_performance='S' THEN 4",
            "WHEN final_performance='A' THEN 3 WHEN final_performance='B' THEN 2",
            "WHEN final_performance='C' THEN 1 ELSE 0 END) as score",
            "FROM user_performance WHERE user_id =#{userId} AND year=#{year} AND month>#{quarter}*3-3 AND month<=#{quarter}*3)x GROUP BY user_id)z LEFT JOIN user ON z.user_id=user.id)uu",
            "LEFT JOIN work_group w ON uu.work_group_id=w.id"})
    OrdinateSalaryChangeAppVo combineSalaryInfo(@Param("userId") Long userId, @Param("year") Integer year, @Param("quarter") Integer quarter);

    @Select({"SELECT * FROM user_salary_change WHERE user_id = #{userId} AND quarter= #{quarter} AND year= #{year}"})
    UserSalaryChange selectByUserIdYearAndQuarter(@Param("userId") Long userId, @Param("year") Integer year, @Param("quarter") Integer quarter);

    @Select({"SELECT grade,evaluate_type,x.* FROM (SELECT u.user_id,u.year,sum(score) from (SELECT user_id, year,(CASE WHEN (month>0 AND month<=3) THEN 1 WHEN (month>3 AND month<=6) THEN 2",
            "WHEN (month>6 AND month<=9) THEN 3 ELSE 4 END)as quarter,(CASE WHEN final_performance='S' THEN 4 WHEN final_performance='A' THEN 3 WHEN final_performance='B' THEN 2",
            "WHEN final_performance='C' THEN 1 ELSE 0 END) as score FROM user_performance where year=#{year} AND month>#{quarter}*3-3 AND month<=3*#{quarter}",
            "AND user_id=#{userId})u GROUP BY user_id)x LEFT JOIN user ON user.id=x.user_id"})
    UserSalaryChange selectPersonalInfo(@Param("userId") Long userId, @Param("year") Integer year, @Param("quarter") Integer quarter);

    void batchUpdateSelective(List<UserSalaryChange> list);

    void batchInsertSelective(List<UserSalaryChange> list);

    @Select("select * from user_salary_change where work_group_id=#{workGroupId} GROUP BY year,quarter")
    List<UserSalaryChange> getAllExistByWorkGroupId(Long workGroupId);

    @Select("select * from user_salary_change where status='已完成' ORDER BY year DESC,quarter DESC LIMIT 1")
    UserSalaryChange getLatestSystemFinish();

    @Delete("delete from user_salary_change where year = #{0} and quarter = #{1}")
    void deleteByYearAndQuarter(int year, int quarter);
//
//    @Select("select * from user_salary_change;")
//    List<UserSalaryChange> selectAll();
}
