package com.seasun.management.mapper;

import com.seasun.management.model.FnSumWeekShareConfig;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface FnSumWeekShareConfigMapper {
    @Delete({
        "delete from fn_sum_week_share_config",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into fn_sum_week_share_config (year, week, plat_id,",
        "user_id, remark, create_time, ",
        "update_time)",
        "values (#{year,jdbcType=INTEGER}, #{week,jdbcType=INTEGER}, #{platId,jdbcType=BIGINT},",
        "#{userId,jdbcType=BIGINT}, #{remark,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, ",
        "#{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(FnSumWeekShareConfig record);

    int insertSelective(FnSumWeekShareConfig record);

    @Select({
        "select",
        "id, year, week, plat_id, user_id, remark, create_time, update_time",
        "from fn_sum_week_share_config",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FnSumWeekShareConfigMapper.BaseResultMap")
    FnSumWeekShareConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FnSumWeekShareConfig record);

    @Update({
        "update fn_sum_week_share_config",
        "set year = #{year,jdbcType=INTEGER},",
          "week = #{week,jdbcType=INTEGER},",
          "plat_id = #{platId,jdbcType=BIGINT},",
          "user_id = #{userId,jdbcType=BIGINT},",
          "remark = #{remark,jdbcType=VARCHAR},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FnSumWeekShareConfig record);

    /* the flowing are user defined ... */
    @Select({"select * from fn_sum_week_share_config where year=#{0} and week=#{1} and plat_id=#{2}"})
    List<FnSumWeekShareConfig> selectByYearAndWeek(Integer year, Integer week,Long platId);

    @Select({"select * from fn_sum_week_share_config where user_id=#{2} and year=#{0} and week=#{1} limit 1"})
    FnSumWeekShareConfig selectByYearAndWeekAndUserId(Integer year, Integer week,Long userId);

    List<FnSumWeekShareConfig> selectByYearAndWeekAndUserIds(@Param("year") Integer year,@Param("week") Integer week,@Param("userIds") List<Long> userIds);
}
