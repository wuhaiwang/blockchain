package com.seasun.management.mapper;

import com.seasun.management.model.ProjectRole;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ProjectRoleMapper {
    @Delete({
            "delete from project_role",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into project_role (name, active_flag, ",
            "system_flag)",
            "values (#{name,jdbcType=VARCHAR}, #{activeFlag,jdbcType=BIT}, ",
            "#{systemFlag,jdbcType=BIT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(ProjectRole record);

    int insertSelective(ProjectRole record);

    @Select({
            "select",
            "id, name, active_flag, system_flag",
            "from project_role",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.ProjectRoleMapper.BaseResultMap")
    ProjectRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProjectRole record);

    @Update({
            "update project_role",
            "set name = #{name,jdbcType=VARCHAR},",
            "active_flag = #{activeFlag,jdbcType=BIT},",
            "system_flag = #{systemFlag,jdbcType=BIT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(ProjectRole record);

    /* the flowing are user defined ... */
    @Insert({
            "insert into project_role (id, name, active_flag)",
            "values (#{id,jdbcType=BIGINT}, #{name,jdbcType=VARCHAR}, #{activeFlag,jdbcType=BIT})"
    })
    int insertWithId(ProjectRole record);

    @Select("select * from project_role where active_flag=1")
    List<ProjectRole> selectAllProjectRoles();

    @Select({"SELECT r.user_id FROM",
            "(SELECT id FROM project_role WHERE name='后台管理员')",
            "pr LEFT JOIN r_user_project_perm r on pr.id=r.project_role_id"
    })
    List<Long> selectAllAdministor();

}