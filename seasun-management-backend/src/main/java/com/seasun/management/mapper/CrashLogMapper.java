package com.seasun.management.mapper;

import com.seasun.management.model.CrashLog;
import com.seasun.management.vo.CrashLogQueryConditionVo;
import com.seasun.management.vo.CrashLogVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

public interface CrashLogMapper {
    @Delete({
            "delete from crash_log",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into crash_log (user_id, system, ",
            "version, model, ",
            "app_version, create_time, ",
            "description)",
            "values (#{userId,jdbcType=BIGINT}, #{system,jdbcType=VARCHAR}, ",
            "#{version,jdbcType=VARCHAR}, #{model,jdbcType=VARCHAR}, ",
            "#{appVersion,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, ",
            "#{description,jdbcType=LONGVARCHAR})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(CrashLog record);

    int insertSelective(CrashLog record);

    @Select({
            "select",
            "id, user_id, system, version, model, app_version, create_time, description",
            "from crash_log",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.CrashLogMapper.ResultMapWithBLOBs")
    CrashLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CrashLog record);

    @Update({
            "update crash_log",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "system = #{system,jdbcType=VARCHAR},",
            "version = #{version,jdbcType=VARCHAR},",
            "model = #{model,jdbcType=VARCHAR},",
            "app_version = #{appVersion,jdbcType=VARCHAR},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "description = #{description,jdbcType=LONGVARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKeyWithBLOBs(CrashLog record);

    @Update({
            "update crash_log",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "system = #{system,jdbcType=VARCHAR},",
            "version = #{version,jdbcType=VARCHAR},",
            "model = #{model,jdbcType=VARCHAR},",
            "app_version = #{appVersion,jdbcType=VARCHAR},",
            "create_time = #{createTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(CrashLog record);

    /* the flowing are user defined ... */
    List<CrashLogVo> selectByCondition(CrashLogQueryConditionVo crashLogQueryConditionVo);

    double selectCountByCondition(CrashLogQueryConditionVo crashLogQueryConditionVo);
}