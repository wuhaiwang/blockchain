package com.seasun.management.mapper;

import com.seasun.management.model.FnWeekShareStatus;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface FnWeekShareStatusMapper {
    @Delete({
        "delete from fn_week_share_status",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into fn_week_share_status (year, week, ",
        "work_group_id, lock_flag, ",
        "operator_id, create_time)",
        "values (#{year,jdbcType=INTEGER}, #{week,jdbcType=INTEGER}, ",
        "#{workGroupId,jdbcType=BIGINT}, #{lockFlag,jdbcType=BIT}, ",
        "#{operatorId,jdbcType=BIGINT}, #{createTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(FnWeekShareStatus record);

    int insertSelective(FnWeekShareStatus record);

    @Select({
        "select",
        "id, year, week, work_group_id, lock_flag, operator_id, create_time",
        "from fn_week_share_status",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FnWeekShareStatusMapper.BaseResultMap")
    FnWeekShareStatus selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FnWeekShareStatus record);

    @Update({
        "update fn_week_share_status",
        "set year = #{year,jdbcType=INTEGER},",
          "week = #{week,jdbcType=INTEGER},",
          "work_group_id = #{workGroupId,jdbcType=BIGINT},",
          "lock_flag = #{lockFlag,jdbcType=BIT},",
          "operator_id = #{operatorId,jdbcType=BIGINT},",
          "create_time = #{createTime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FnWeekShareStatus record);

    /* the flowing are user defined ... */
    List<FnWeekShareStatus> selectLockedGroupByYearAndWeekAndGroupIds(@Param("year") Integer year,@Param("week") Integer week,@Param("groupIds") List<Long> shareGroupIds);

    int batchInsert(List<FnWeekShareStatus> fnWeekShareStatusList);

    @Select("select * from fn_week_share_status where year = #{0} and week = #{1}")
    List<FnWeekShareStatus> selectByYearAndWeek(Integer year,Integer week);

    @Select("select work_group_id from fn_week_share_status where year = #{0} and week = #{1}")
    List<Long> selectWorkGroupIdsByYearAndWeek(Integer year,Integer week);

    @Select("select work_group_id from fn_week_share_status where year = #{0} and week = #{1} and lock_flag = #{2}")
    List<Long> selectWorkGroupIdsByYearAndWeekAndLockFlag(Integer year,Integer week,Boolean lockFlag);

    int updateByWorkGroupIdsAndYearAndWeek(@Param("year") Integer year,@Param("week") Integer week, @Param("lockFlag") Boolean lockFlag, @Param("operatorId") Long operatorId,
                                           @Param("groupIds") List<Long> shareGroupIds);

    @Select({"select * from fn_week_share_status where year=#{0} and week=#{1} and work_group_id=#{2} limit 1"})
    FnWeekShareStatus selectLockedGroupByYearAndWeekAndGroupId(Integer year, Integer week, Long workGroupId);


    @Select({"select lock_flag from fn_week_share_status where year=#{0} and week=#{1} and work_group_id =(select work_group_id from user where id=#{2}) limit 1"})
    Boolean selectGroupLockFlagByYearAndWeekAndUserId(Integer year, Integer week, Long createBy);

}