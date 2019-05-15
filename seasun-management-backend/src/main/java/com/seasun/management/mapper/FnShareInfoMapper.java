package com.seasun.management.mapper;

import com.seasun.management.model.FnShareInfo;
import org.apache.ibatis.annotations.*;

public interface FnShareInfoMapper {
    @Delete({
            "delete from fn_share_info",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into fn_share_info (year, month, ",
            "status, create_time, ",
            "update_time)",
            "values (#{year,jdbcType=INTEGER}, #{month,jdbcType=INTEGER}, ",
            "#{status,jdbcType=VARCHAR}, #{createTime,jdbcType=DATE}, ",
            "#{updateTime,jdbcType=DATE})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FnShareInfo record);

    int insertSelective(FnShareInfo record);

    @Select({
            "select",
            "id, year, month, status, create_time, update_time",
            "from fn_share_info",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FnShareInfoMapper.BaseResultMap")
    FnShareInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FnShareInfo record);

    @Update({
            "update fn_share_info",
            "set year = #{year,jdbcType=INTEGER},",
            "month = #{month,jdbcType=INTEGER},",
            "status = #{status,jdbcType=VARCHAR},",
            "create_time = #{createTime,jdbcType=DATE},",
            "update_time = #{updateTime,jdbcType=DATE}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FnShareInfo record);

    /* the flowing are user defined ... */

    @Select("select * from fn_share_info where year = #{year} and month = #{month} limit 1")
    FnShareInfo selectByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Select("select * from fn_share_info order by id desc limit 1")
    FnShareInfo selectLatestProcessingRecord();

    @Select({"select status from fn_share_info where year = #{0} and month = #{1} limit 1"})
    String selectStatusByYearAndMonth(Integer year, Integer month);
}