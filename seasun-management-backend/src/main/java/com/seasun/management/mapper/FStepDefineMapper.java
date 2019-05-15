package com.seasun.management.mapper;

import com.seasun.management.model.FStepDefine;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.*;

public interface FStepDefineMapper {
    @Delete({
            "delete from f_step_define",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into f_step_define (flow_id, name, ",
            "manager_id, previous_step, ",
            "end_flag, description)",
            "values (#{flowId,jdbcType=BIGINT}, #{name,jdbcType=VARCHAR}, ",
            "#{managerId,jdbcType=BIGINT}, #{previousStep,jdbcType=BIGINT}, ",
            "#{endFlag,jdbcType=BIT}, #{description,jdbcType=VARCHAR})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FStepDefine record);

    int insertSelective(FStepDefine record);

    @Select({
            "select",
            "id, flow_id, name, manager_id, previous_step, end_flag, description",
            "from f_step_define",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FStepDefineMapper.BaseResultMap")
    FStepDefine selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FStepDefine record);

    @Update({
            "update f_step_define",
            "set flow_id = #{flowId,jdbcType=BIGINT},",
            "name = #{name,jdbcType=VARCHAR},",
            "manager_id = #{managerId,jdbcType=BIGINT},",
            "previous_step = #{previousStep,jdbcType=BIGINT},",
            "end_flag = #{endFlag,jdbcType=BIT},",
            "description = #{description,jdbcType=VARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FStepDefine record);

    /* the flowing are user defined ... */
    @Select("select * from f_step_define where flow_id = #{0} and previous_step is null limit 1")
    FStepDefine selectFirstStep(Long flowId);

    @Select("select * from f_step_define where flow_id = #{0} and previous_step = #{1}")
    List<FStepDefine> selectNextStepsByPreviousStep(Long flowId, Long previousStepId);

    @Select("select * from f_step_define where flow_id = #{0} and name = #{1} limit 1")
    FStepDefine selectStepByName(Long flowId, String stepName);

    @Select("select * from f_step_define where id in (select f_step_define_id from f_instance_detail where id = #{0})")
    FStepDefine selectByInstanceDetailId(Long instanceDetailId);
}