package com.seasun.management.mapper;

import com.seasun.management.model.FInstance;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface FInstanceMapper {
    @Delete({
            "delete from f_instance",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into f_instance (flow_id, business_key, ",
            "process_result, created_by, ",
            "start_time, end_time)",
            "values (#{flowId,jdbcType=BIGINT}, #{businessKey,jdbcType=BIGINT}, ",
            "#{processResult,jdbcType=TINYINT}, #{createdBy,jdbcType=BIGINT}, ",
            "#{startTime,jdbcType=TIMESTAMP}, #{endTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FInstance record);

    int insertSelective(FInstance record);

    @Select({
            "select",
            "id, flow_id, business_key, process_result, created_by, start_time, end_time",
            "from f_instance",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FInstanceMapper.BaseResultMap")
    FInstance selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FInstance record);

    @Update({
            "update f_instance",
            "set flow_id = #{flowId,jdbcType=BIGINT},",
            "business_key = #{businessKey,jdbcType=BIGINT},",
            "process_result = #{processResult,jdbcType=TINYINT},",
            "created_by = #{createdBy,jdbcType=BIGINT},",
            "start_time = #{startTime,jdbcType=TIMESTAMP},",
            "end_time = #{endTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FInstance record);
}