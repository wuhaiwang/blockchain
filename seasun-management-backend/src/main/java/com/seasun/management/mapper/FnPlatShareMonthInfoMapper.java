package com.seasun.management.mapper;

import com.seasun.management.model.FnPlatShareMonthInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface FnPlatShareMonthInfoMapper {
    @Delete({
        "delete from fn_plat_share_month_info",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into fn_plat_share_month_info (year, month, ",
        "plat_id, workday, workday_period, create_time)",
        "values (#{year,jdbcType=INTEGER}, #{month,jdbcType=INTEGER}, ",
        "#{platId,jdbcType=BIGINT}, #{workDay,jdbcType=DECIMAL}, #{workdayPeriod,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(FnPlatShareMonthInfo record);

    int insertSelective(FnPlatShareMonthInfo record);

    @Select({
        "select",
        "id, year, month, plat_id, workday, workday_period, create_time",
        "from fn_plat_share_month_info",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FnPlatShareMonthInfoMapper.BaseResultMap")
    FnPlatShareMonthInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FnPlatShareMonthInfo record);

    @Update({
        "update fn_plat_share_month_info",
        "set year = #{year,jdbcType=INTEGER},",
          "month = #{month,jdbcType=INTEGER},",
          "plat_id = #{platId,jdbcType=BIGINT},",
          "workday = #{workDay,jdbcType=DECIMAL},",
          "workday_period = #{workdayPeriod,jdbcType=VARCHAR},",
          "create_time = #{createTime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FnPlatShareMonthInfo record);


    /* the flowing are user defined ... */

    @Delete("delete from fn_plat_share_month_info where plat_id=#{0} and year=#{1} and month=#{2}")
    Integer deleteByPlatIdAndYearAndMonth(Long platId, Integer year, Integer month);

    @Select({"select * from fn_plat_share_month_info where plat_id=#{0} and year=#{1} and month=#{2} limit 1"})
    FnPlatShareMonthInfo selectWorkDayByPlatIdAndYearAndMonth(Long platId, Integer year, Integer month);
}