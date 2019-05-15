package com.seasun.management.mapper;

import com.seasun.management.model.OperateLog;
import com.seasun.management.vo.LogQueryConditionVo;
import com.seasun.management.vo.OrdinateSalaryChangeAppVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface OperateLogMapper {
    @Delete({
            "delete from operate_log",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into operate_log (type, operate_id, ",
            "create_time, description, ",
            "channel, system, ",
            "version, model, ",
            "app_version, imei, ",
            "code_push_label, code_push_environment)",
            "values (#{type,jdbcType=VARCHAR}, #{operateId,jdbcType=BIGINT}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{description,jdbcType=VARCHAR}, ",
            "#{channel,jdbcType=VARCHAR}, #{system,jdbcType=VARCHAR}, ",
            "#{version,jdbcType=VARCHAR}, #{model,jdbcType=VARCHAR}, ",
            "#{appVersion,jdbcType=VARCHAR}, #{imei,jdbcType=VARCHAR}, ",
            "#{codePushLabel,jdbcType=VARCHAR}, #{codePushEnvironment,jdbcType=VARCHAR})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(OperateLog record);

    int insertSelective(OperateLog record);

    @Select({
            "select",
            "id, type, operate_id, create_time, description, channel, system, version, model, ",
            "app_version, imei, code_push_label, code_push_environment",
            "from operate_log",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.OperateLogMapper.BaseResultMap")
    OperateLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OperateLog record);

    @Update({
            "update operate_log",
            "set type = #{type,jdbcType=VARCHAR},",
            "operate_id = #{operateId,jdbcType=BIGINT},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "description = #{description,jdbcType=VARCHAR},",
            "channel = #{channel,jdbcType=VARCHAR},",
            "system = #{system,jdbcType=VARCHAR},",
            "version = #{version,jdbcType=VARCHAR},",
            "model = #{model,jdbcType=VARCHAR},",
            "app_version = #{appVersion,jdbcType=VARCHAR},",
            "imei = #{imei,jdbcType=VARCHAR},",
            "code_push_label = #{codePushLabel,jdbcType=VARCHAR},",
            "code_push_environment = #{codePushEnvironment,jdbcType=VARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(OperateLog record);

    /* the flowing are user defined ... */

    int batchInsertSelective(List<OperateLog> operateLogList);

    List<OperateLog> selectByCondition(LogQueryConditionVo vo);

    double selectCountByCondition(LogQueryConditionVo vo);
}