package com.seasun.management.mapper;

import com.seasun.management.model.FnStat;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface FnStatMapper {
    @Delete({
            "delete from fn_stat",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into fn_stat (name, parent_id, ",
            "type, project_id)",
            "values (#{name,jdbcType=VARCHAR}, #{parentId,jdbcType=BIGINT}, ",
            "#{type,jdbcType=VARCHAR}, #{projectId,jdbcType=BIGINT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FnStat record);

    int insertSelective(FnStat record);

    @Select({
            "select",
            "id, name, parent_id, type, project_id",
            "from fn_stat",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FnStatMapper.BaseResultMap")
    FnStat selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FnStat record);

    @Update({
            "update fn_stat",
            "set name = #{name,jdbcType=VARCHAR},",
            "parent_id = #{parentId,jdbcType=BIGINT},",
            "type = #{type,jdbcType=VARCHAR},",
            "project_id = #{projectId,jdbcType=BIGINT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FnStat record);

    /* the flowing are user defined ... */

    @Select("select * from fn_stat where type = 'fixed'")
    List<FnStat> selectAllFixedStats();

    @Select("select * from fn_stat where type = 'project' and project_id = #{projectId}")
    List<FnStat> selectStatByProjectId(Long projectId);

    int batchInsert(List<FnStat> fnStats);

    @Select("select * from fn_stat where type = 'fixed' or project_id = #{projectId}")
    List<FnStat> selectStatByProjectIdOrFixed(Long projectId);
}