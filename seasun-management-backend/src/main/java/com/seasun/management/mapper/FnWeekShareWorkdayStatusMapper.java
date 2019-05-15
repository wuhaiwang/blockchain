package com.seasun.management.mapper;

import com.seasun.management.model.FnWeekShareWorkdayStatus;
import com.seasun.management.vo.WorkdayStatusVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

public interface FnWeekShareWorkdayStatusMapper {
    @Delete({
            "delete from fn_week_share_workday_status",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into fn_week_share_workday_status (year, week, ",
            "workday, status, operator_id, ",
            "create_time, update_time, ",
            "month, day, end_month, ",
            "end_day)",
            "values (#{year,jdbcType=INTEGER}, #{week,jdbcType=INTEGER}, ",
            "#{workday,jdbcType=DECIMAL}, #{status,jdbcType=BIT}, #{operatorId,jdbcType=BIGINT}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP}, ",
            "#{month,jdbcType=INTEGER}, #{day,jdbcType=INTEGER}, #{endMonth,jdbcType=INTEGER}, ",
            "#{endDay,jdbcType=INTEGER})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FnWeekShareWorkdayStatus record);

    int insertSelective(FnWeekShareWorkdayStatus record);

    @Select({
            "select",
            "id, year, week, workday, status, operator_id, create_time, update_time, month, ",
            "day, end_month, end_day",
            "from fn_week_share_workday_status",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FnWeekShareWorkdayStatusMapper.BaseResultMap")
    FnWeekShareWorkdayStatus selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FnWeekShareWorkdayStatus record);

    @Update({
            "update fn_week_share_workday_status",
            "set year = #{year,jdbcType=INTEGER},",
            "week = #{week,jdbcType=INTEGER},",
            "workday = #{workday,jdbcType=DECIMAL},",
            "status = #{status,jdbcType=BIT},",
            "operator_id = #{operatorId,jdbcType=BIGINT},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP},",
            "month = #{month,jdbcType=INTEGER},",
            "day = #{day,jdbcType=INTEGER},",
            "end_month = #{endMonth,jdbcType=INTEGER},",
            "end_day = #{endDay,jdbcType=INTEGER}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FnWeekShareWorkdayStatus record);

    /* the flowing are user defined ... */
    @Select({"select * from fn_week_share_workday_status where year=#{0} and week=#{1} limit 1"})
    FnWeekShareWorkdayStatus selectByYearAndWeek(Integer year, Integer week);

    @Select({"select workday from fn_week_share_workday_status where year=#{0} and week=#{1} limit 1"})
    BigDecimal selectWorkdayByYearAndWeek(Integer year, Integer week);

    @Select({"select status from fn_week_share_workday_status where year=#{0} and week=#{1} limit 1"})
    Boolean selectStatusByYearAndWeek(Integer year, Integer week);

    @Select({"select id,year,week,workday,status from fn_week_share_workday_status where year=#{0} order by week"})
    List<WorkdayStatusVo> selectVoByYear(Integer year);

    @Select({"select * from fn_week_share_workday_status where (year=#{0} and week=#{1}) or (year=#{2} and week =#{3})"})
    List<FnWeekShareWorkdayStatus> selectByDate(Integer year, Integer week, int copyYear, Integer lastWeek);
}