package com.seasun.management.mapper;

import com.seasun.management.model.ProjectUsedName;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface ProjectUsedNameMapper {
    @Delete({
            "delete from project_used_name",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into project_used_name (name, project_id)",
            "values (#{name,jdbcType=VARCHAR}, #{projectId,jdbcType=BIGINT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(ProjectUsedName record);

    int insertSelective(ProjectUsedName record);

    @Select({
            "select",
            "id, name, project_id",
            "from project_used_name",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.ProjectUsedNameMapper.BaseResultMap")
    ProjectUsedName selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProjectUsedName record);

    @Update({
            "update project_used_name",
            "set name = #{name,jdbcType=VARCHAR},",
            "project_id = #{projectId,jdbcType=BIGINT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(ProjectUsedName record);

    /* the flowing are user defined ... */
    @Select("select * from project_used_name where project_id = #{project_id}")
    List<ProjectUsedName> selectByProject(Long projectId);

    @Select("select * from project_used_name")
    List<ProjectUsedName> selectAll();

    int deleteByProjectIdAndUsedNamesNotIn(Map<String, Object> params);

    int batchInsert(List<ProjectUsedName> projectUsedNameList);

    @Delete({"delete from project_used_name where project_id=#{0}"})
    void deleteByProjectId(Long projectId);

}