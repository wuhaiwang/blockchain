package com.seasun.management.mapper;

import com.seasun.management.model.UseLog;
import com.seasun.management.vo.UserMessageConditionVo;
import com.seasun.management.vo.UseLogVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface UseLogMapper {
    @Delete({
            "delete from use_log",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into use_log (user_id, system, ",
            "version, model, ",
            "app_version, imei, ",
            "create_time)",
            "values (#{userId,jdbcType=BIGINT}, #{system,jdbcType=VARCHAR}, ",
            "#{version,jdbcType=VARCHAR}, #{model,jdbcType=VARCHAR}, ",
            "#{appVersion,jdbcType=VARCHAR}, #{imei,jdbcType=VARCHAR}, ",
            "#{createTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(UseLog record);

    int insertSelective(UseLog record);

    @Select({
            "select",
            "id, user_id, system, version, model, app_version, imei, create_time",
            "from use_log",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UseLogMapper.BaseResultMap")
    UseLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UseLog record);

    @Update({
            "update use_log",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "system = #{system,jdbcType=VARCHAR},",
            "version = #{version,jdbcType=VARCHAR},",
            "model = #{model,jdbcType=VARCHAR},",
            "app_version = #{appVersion,jdbcType=VARCHAR},",
            "imei = #{imei,jdbcType=VARCHAR},",
            "create_time = #{createTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UseLog record);

    /* the flowing are user defined ... */
    List<UseLogVo> selectByCondition(UserMessageConditionVo crashLogQueryConditionVo);
}