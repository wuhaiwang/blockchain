package com.seasun.management.mapper;

import com.seasun.management.model.FInstanceDetail;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface FInstanceDetailMapper {
    @Delete({
            "delete from f_instance_detail",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into f_instance_detail (instance_id, f_step_define_id, ",
            "previous_detail, end_flag, ",
            "process_result, start_time, ",
            "end_time, manager_id, ",
            "remark)",
            "values (#{instanceId,jdbcType=BIGINT}, #{fStepDefineId,jdbcType=BIGINT}, ",
            "#{previousDetail,jdbcType=BIGINT}, #{endFlag,jdbcType=BIT}, ",
            "#{processResult,jdbcType=TINYINT}, #{startTime,jdbcType=TIMESTAMP}, ",
            "#{endTime,jdbcType=TIMESTAMP}, #{managerId,jdbcType=BIGINT}, ",
            "#{remark,jdbcType=VARCHAR})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FInstanceDetail record);

    int insertSelective(FInstanceDetail record);

    @Select({
            "select",
            "id, instance_id, f_step_define_id, previous_detail, end_flag, process_result, ",
            "start_time, end_time, manager_id, remark",
            "from f_instance_detail",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FInstanceDetailMapper.BaseResultMap")
    FInstanceDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FInstanceDetail record);

    @Update({
            "update f_instance_detail",
            "set instance_id = #{instanceId,jdbcType=BIGINT},",
            "f_step_define_id = #{fStepDefineId,jdbcType=BIGINT},",
            "previous_detail = #{previousDetail,jdbcType=BIGINT},",
            "end_flag = #{endFlag,jdbcType=BIT},",
            "process_result = #{processResult,jdbcType=TINYINT},",
            "start_time = #{startTime,jdbcType=TIMESTAMP},",
            "end_time = #{endTime,jdbcType=TIMESTAMP},",
            "manager_id = #{managerId,jdbcType=BIGINT},",
            "remark = #{remark,jdbcType=VARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FInstanceDetail record);


    /* the flowing are user defined ... */

    @Select("select * from f_instance_detail where previous_instance = #{0}")
    List<FInstanceDetail> selectNextByPreviousInstanceId(Long instanceDetailId);

    @Select("select * from f_instance_detail where instance_id = #{0}")
    List<FInstanceDetail> selectByInstanceId(Long instanceId);

    @Select("select * from f_instance_detail where instance_id = #{0} and f_step_define_id = #{1}  limit 1")
    FInstanceDetail selectByInstanceIdAndStepDefineId(Long instanceId, Long stepDefineId);

    @Select("select * from f_instance_detail where instance_id = #{0} and process_result = 0 limit 1")
    FInstanceDetail selectByInstanceIdAndProcessing(Long instanceId);

    @Update("update f_instance_detail set process_result = #{0} where instance_id = #{1} and f_step_define_id = #{2}")
    void updateProcessResultByInstanceIdAndStepDefineId(int processResult, Long instanceId, Long stepDefineId);

    void batchInsert(List<FInstanceDetail> detailList);

    void batchDelete(List<Long> pks);

}