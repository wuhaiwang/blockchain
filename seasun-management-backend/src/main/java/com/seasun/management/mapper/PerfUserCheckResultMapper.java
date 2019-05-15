package com.seasun.management.mapper;

import com.seasun.management.model.PerfUserCheckResult;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface PerfUserCheckResultMapper {
    @Delete({
        "delete from perf_user_check_result",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into perf_user_check_result (type, user_id, ",
        "remark, create_by, ",
        "create_time)",
        "values (#{type,jdbcType=VARCHAR}, #{userId,jdbcType=BIGINT}, ",
        "#{remark,jdbcType=VARCHAR}, #{createBy,jdbcType=BIGINT}, ",
        "#{createTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(PerfUserCheckResult record);

    int insertSelective(PerfUserCheckResult record);

    @Select({
        "select",
        "id, type, user_id, remark, create_by, create_time",
        "from perf_user_check_result",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.PerfUserCheckResultMapper.BaseResultMap")
    PerfUserCheckResult selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PerfUserCheckResult record);

    @Update({
        "update perf_user_check_result",
        "set type = #{type,jdbcType=VARCHAR},",
          "user_id = #{userId,jdbcType=BIGINT},",
          "remark = #{remark,jdbcType=VARCHAR},",
          "create_by = #{createBy,jdbcType=BIGINT},",
          "create_time = #{createTime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(PerfUserCheckResult record);

    @Select({"select * from perf_user_check_result"})
    List<PerfUserCheckResult> selectAll();

   // <!-- the following are user defined...-->

    void batchInsert(List<PerfUserCheckResult> batchInsert);

    void batchUpdate(List<PerfUserCheckResult> batchUpdate);

    void deleteByUserIds(List<Long> deleteIds);
}