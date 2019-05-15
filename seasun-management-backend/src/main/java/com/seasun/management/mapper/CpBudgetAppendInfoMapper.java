package com.seasun.management.mapper;

import com.seasun.management.dto.SimpleBudgetDto;
import com.seasun.management.model.CpBudgetAppendInfo;
import com.seasun.management.vo.cp.CpBudgetAppendInfoVo;
import com.seasun.management.vo.cp.ProjectBudgetVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CpBudgetAppendInfoMapper {
    @Delete({
        "delete from cp_budget_append_info",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into cp_budget_append_info (cp_budget_id, amount, ",
        "create_time, reason,type)",
        "values (#{cpBudgetId,jdbcType=BIGINT}, #{amount,jdbcType=DECIMAL}, ",
        "#{createTime,jdbcType=TIMESTAMP}, #{reason,jdbcType=VARCHAR}, #{type,jdbcType=VARCHAR})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(CpBudgetAppendInfo record);

    int insertSelective(CpBudgetAppendInfo record);

    @Select({
        "select",
        "id, cp_budget_id, amount, create_time, reason",
        "from cp_budget_append_info",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.CpBudgetAppendInfoMapper.BaseResultMap")
    CpBudgetAppendInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CpBudgetAppendInfo record);

    @Update({
        "update cp_budget_append_info",
        "set cp_budget_id = #{cpBudgetId,jdbcType=BIGINT},",
          "amount = #{amount,jdbcType=DECIMAL},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "reason = #{reason,jdbcType=VARCHAR}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(CpBudgetAppendInfo record);

    @Select({
            "select",
            "id, cp_budget_id, amount, create_time, reason, type",
            "from cp_budget_append_info",
            "where cp_budget_id = #{cpBudgetId,jdbcType=BIGINT} ORDER BY type DESC,id DESC"
    })
    List<CpBudgetAppendInfoVo> selectByCpBudgetId(Long cpBudgetId);

    List<SimpleBudgetDto> selectSimpleBudgetDtoByProjectIdsAndYear(@Param("list") List<Integer> cPProjectIds,@Param("year") Integer year);

    @Delete({
            "delete from cp_budget_append_info",
            "where type ='1' and cp_budget_id = #{cpBudgetId,jdbcType=BIGINT}"
    })
    int deleteByBudgetId(Long cpBudgetId);
}