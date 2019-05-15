package com.seasun.management.mapper;

import com.seasun.management.model.FnProjectSchedData;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface FnProjectSchedDataMapper {
    @Delete({
            "delete from fn_project_sched_data",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into fn_project_sched_data (project_id, year, ",
            "month, total_cost, total_budget, ",
            "expect_income, human_number, ",
            "create_time, update_time)",
            "values (#{projectId,jdbcType=BIGINT}, #{year,jdbcType=INTEGER}, ",
            "#{month,jdbcType=INTEGER}, #{totalCost,jdbcType=REAL}, #{totalBudget,jdbcType=REAL}, ",
            "#{expectIncome,jdbcType=REAL}, #{humanNumber,jdbcType=REAL}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FnProjectSchedData record);

    int insertSelective(FnProjectSchedData record);

    @Select({
            "select",
            "id, project_id, year, month, total_cost, total_budget, expect_income, human_number, ",
            "create_time, update_time",
            "from fn_project_sched_data",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FnProjectSchedDataMapper.BaseResultMap")
    FnProjectSchedData selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FnProjectSchedData record);

    @Update({
            "update fn_project_sched_data",
            "set project_id = #{projectId,jdbcType=BIGINT},",
            "year = #{year,jdbcType=INTEGER},",
            "month = #{month,jdbcType=INTEGER},",
            "total_cost = #{totalCost,jdbcType=REAL},",
            "total_budget = #{totalBudget,jdbcType=REAL},",
            "expect_income = #{expectIncome,jdbcType=REAL},",
            "human_number = #{humanNumber,jdbcType=REAL},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FnProjectSchedData record);

    /* the flowing are user defined ... */
    int batchInsert(List<FnProjectSchedData> fnProjectSchedData);

    int batchUpdateByPks(List<FnProjectSchedData> fnProjectSchedData);

    List<FnProjectSchedData> selectByCondition(FnProjectSchedData fnProjectSchedData);

    @Select("select * from fn_project_sched_data where project_id = #{projectId} and year = #{year} and month = #{month}")
    FnProjectSchedData selectByProjectIdAndYearAndMonth(@Param("projectId") Long projectId, @Param("year") int year, @Param("month") int month);

    @Delete("delete from fn_project_sched_data")
    int deleteAll();

    @Delete("delete from fn_project_sched_data where year = #{0} and month = #{1}")
    int deleteByYearAndMonth(int year, int month);
}