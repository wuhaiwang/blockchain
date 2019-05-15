package com.seasun.management.mapper;

import com.seasun.management.constant.CpConstant;
import com.seasun.management.dto.IdValueDto;
import com.seasun.management.dto.SimpleBudgetDto;
import com.seasun.management.model.CpBudget;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

public interface CpBudgetMapper {
    @Delete({
        "delete from cp_budget",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into cp_budget (cp_project_id, budget_year, ",
        "budget_amount, create_time, ",
        "update_time)",
        "values (#{cpProjectId,jdbcType=BIGINT}, #{budgetYear,jdbcType=INTEGER}, ",
        "#{budgetAmount,jdbcType=DECIMAL}, #{createTime,jdbcType=TIMESTAMP}, ",
        "#{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(CpBudget record);

    int insertSelective(CpBudget record);

    @Select({
        "select",
        "id, cp_project_id, budget_year, budget_amount, create_time, update_time",
        "from cp_budget",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.CpBudgetMapper.BaseResultMap")
    CpBudget selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CpBudget record);

    @Update({
        "update cp_budget",
        "set cp_project_id = #{cpProjectId,jdbcType=BIGINT},",
          "budget_year = #{budgetYear,jdbcType=INTEGER},",
          "budget_amount = #{budgetAmount,jdbcType=DECIMAL},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(CpBudget record);

    /* the flowing are user defined ... */

    @Select({
            "select id,cp_project_id cpProjectId,budget_year budgetYear,budget_amount budgetAmount from cp_budget b where b.cp_project_id= #{0,jdbcType=BIGINT} and b.budget_year=#{1,jdbcType=INTEGER} LIMIT 0,1",
    })
    CpBudget selectCpBudget(Long cpProjectId, Integer budgetYear);

    @Update({
            "update cp_budget set budget_amount = #{budgetAmount,jdbcType=DECIMAL}, update_time = #{updateTime,jdbcType=TIMESTAMP} where id = #{id,jdbcType=BIGINT}"
    })
    int updateCpBudget(CpBudget record);

    List<IdValueDto> selectByProjectIdsAndYear(@Param("list") List<Integer> gameProjectIds, @Param("year")Integer year);

    @Select({"select sum(ifNull(b.budget_amount,0)) from cp_budget b left join cp.gameproject cg on cg.id=b.cp_project_id where cg.Active=1 and b.budget_year=#{0}"})
    BigDecimal selectAllActiveProjectBudgetByYear(Integer year);

    @Update({
            "update cp_budget",
            "set budget_amount =  #{budgetAmount,jdbcType=DECIMAL},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateCpBudgetAmount(CpBudget cpBudget);

    BigDecimal selectSumBudgetByProjectIdsAndYear(@Param("list") List<Integer> gameProjectIds, @Param("year")Integer year);

    @Select({"select ifNull(a.budget_amount,0) from cp_budget a left join cp.gameproject b on a.cp_project_id=b.id where b.code='"+ CpConstant.SEASUN_CP_PROJECT_CODE+"' and a.budget_year=#{0} limit 1"})
    BigDecimal selectSeasunBudgetByYear(Integer year);
}