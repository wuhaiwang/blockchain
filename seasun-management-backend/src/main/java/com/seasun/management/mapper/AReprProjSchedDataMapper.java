package com.seasun.management.mapper;

import com.seasun.management.model.AReprProjSchedData;
import com.seasun.management.model.AReprShareData;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface AReprProjSchedDataMapper {
    @Delete({
            "delete from a_repr_proj_sched_data",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into a_repr_proj_sched_data (project_name, project_id, ",
            "month, year, total_cost, ",
            "rest_budget, create_time, ",
            "update_time, anticipated_revenue, ",
            "hr_numbers)",
            "values (#{projectName,jdbcType=VARCHAR}, #{projectId,jdbcType=VARCHAR}, ",
            "#{month,jdbcType=INTEGER}, #{year,jdbcType=INTEGER}, #{totalCost,jdbcType=REAL}, ",
            "#{restBudget,jdbcType=REAL}, #{createTime,jdbcType=TIMESTAMP}, ",
            "#{updateTime,jdbcType=TIMESTAMP}, #{anticipatedRevenue,jdbcType=REAL}, ",
            "#{hrNumbers,jdbcType=REAL})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(AReprProjSchedData record);

    int insertSelective(AReprProjSchedData record);

    @Select({
            "select",
            "id, project_name, project_id, month, year, total_cost, rest_budget, create_time, ",
            "update_time, anticipated_revenue, hr_numbers",
            "from a_repr_proj_sched_data",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.AReprProjSchedDataMapper.BaseResultMap")
    AReprProjSchedData selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AReprProjSchedData record);

    @Update({
            "update a_repr_proj_sched_data",
            "set project_name = #{projectName,jdbcType=VARCHAR},",
            "project_id = #{projectId,jdbcType=VARCHAR},",
            "month = #{month,jdbcType=INTEGER},",
            "year = #{year,jdbcType=INTEGER},",
            "total_cost = #{totalCost,jdbcType=REAL},",
            "rest_budget = #{restBudget,jdbcType=REAL},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP},",
            "anticipated_revenue = #{anticipatedRevenue,jdbcType=REAL},",
            "hr_numbers = #{hrNumbers,jdbcType=REAL}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(AReprProjSchedData record);

     /* the flowing are user defined ... */

    @Select("select * from a_repr_proj_sched_data")
    List<AReprProjSchedData> selectAll();

    @Select("select * from a_repr_proj_sched_data where year = #{0} and month = #{1}")
    List<AReprProjSchedData> selectByYearAndMonth(int year, int month);
}