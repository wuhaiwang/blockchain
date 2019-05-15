package com.seasun.management.mapper;

import com.seasun.management.model.Flog;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface FlogMapper {
    @Delete({
            "delete from f_log",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into f_log (instance_id, instance_detail_id, ",
            "tag, content, create_time)",
            "values (#{instanceId,jdbcType=BIGINT}, #{instanceDetailId,jdbcType=BIGINT}, ",
            "#{tag,jdbcType=VARCHAR}, #{content,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(Flog record);

    int insertSelective(Flog record);

    @Select({
            "select",
            "id, instance_id, instance_detail_id, tag, content, create_time",
            "from f_log",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FlogMapper.BaseResultMap")
    Flog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Flog record);

    @Update({
            "update f_log",
            "set instance_id = #{instanceId,jdbcType=BIGINT},",
            "instance_detail_id = #{instanceDetailId,jdbcType=BIGINT},",
            "tag = #{tag,jdbcType=VARCHAR},",
            "content = #{content,jdbcType=VARCHAR},",
            "create_time = #{createTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(Flog record);
}