package com.seasun.management.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

import com.seasun.management.model.ApDraw;

public interface ApDrawMapper {

    @Insert({
            "insert into ap_draw (year, code, user_id, employee_no, user_name, award_name, create_time, update_time) ",
            "values (#{year,jdbcType=VARCHAR}, #{code,jdbcType=VARCHAR}, ",
            "#{userId,jdbcType=BIGINT}, #{employeeNo,jdbcType=BIGINT}, ",
            "#{userName,jdbcType=VARCHAR}, #{awardName,jdbcType=VARCHAR},",
            "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(ApDraw apDraw);

    @Select({"select * from ap_draw where user_id = #{userId} order by 1 desc limit 1"})
	List<ApDraw> selectByUserId(Long userId);
    
    @Select({"select * from ap_draw where code = #{code} limit 1"})
	ApDraw selectByCode(String code);
}
