package com.seasun.management.mapper;

import com.seasun.management.dto.UserGradeHistoryDto;
import com.seasun.management.model.UserGradeChange;
import com.seasun.management.vo.OrdinateSalaryChangeAppVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

public interface UserGradeChangeMapper {
    @Delete({
            "delete from user_grade_change",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into user_grade_change (user_id, year, ",
            "month, old_grade, ",
            "old_evaluate_type, new_grade, ",
            "new_evaluate_type, status, ",
            "create_time, update_time)",
            "values (#{userId,jdbcType=BIGINT}, #{year,jdbcType=INTEGER}, ",
            "#{month,jdbcType=INTEGER}, #{oldGrade,jdbcType=VARCHAR}, ",
            "#{oldEvaluateType,jdbcType=VARCHAR}, #{newGrade,jdbcType=VARCHAR}, ",
            "#{newEvaluateType,jdbcType=VARCHAR}, #{status,jdbcType=VARCHAR}, ",
            "#{createTime,jdbcType=DATE}, #{updateTime,jdbcType=DATE})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(UserGradeChange record);

    int insertSelective(UserGradeChange record);

    @Select({
            "select",
            "id, user_id, year, month, old_grade, old_evaluate_type, new_grade, new_evaluate_type, ",
            "status, create_time, update_time",
            "from user_grade_change",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserGradeChangeMapper.BaseResultMap")
    UserGradeChange selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserGradeChange record);

    @Update({
            "update user_grade_change",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "year = #{year,jdbcType=INTEGER},",
            "month = #{month,jdbcType=INTEGER},",
            "old_grade = #{oldGrade,jdbcType=VARCHAR},",
            "old_evaluate_type = #{oldEvaluateType,jdbcType=VARCHAR},",
            "new_grade = #{newGrade,jdbcType=VARCHAR},",
            "new_evaluate_type = #{newEvaluateType,jdbcType=VARCHAR},",
            "status = #{status,jdbcType=VARCHAR},",
            "create_time = #{createTime,jdbcType=DATE},",
            "update_time = #{updateTime,jdbcType=DATE}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserGradeChange record);

    /* the flowing are user defined ... */
    @Select({
            "select year, month, new_grade grade",
            "from user_grade_change",
            "where user_id=#{userId}",
            "and new_grade is not null",
            "order by year desc, month desc"
    })
    List<UserGradeHistoryDto> selectUserGradeHistory(Long userId);


    @Select({"SELECT * FROM user_grade_change",
            "WHERE user_id= #{userId} ORDER BY year DESC,month DESC",
            "LIMIT 1"})
    UserGradeChange getUserGradeChangeByUserId(Long userId);

    @Select({"SELECT * FROM(SELECT * FROM user_grade_change ORDER BY year DESC ,month DESC)x",
            "GROUP BY user_id"})
    List<UserGradeChange> getAllUserLastChangeTime();
}