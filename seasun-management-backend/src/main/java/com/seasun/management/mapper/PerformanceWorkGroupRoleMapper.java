package com.seasun.management.mapper;

import com.seasun.management.model.PerformanceWorkGroupRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface PerformanceWorkGroupRoleMapper {
    @Delete({
        "delete from performance_work_group_role",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into performance_work_group_role (name)",
        "values (#{name,jdbcType=VARCHAR})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(PerformanceWorkGroupRole record);

    int insertSelective(PerformanceWorkGroupRole record);

    @Select({
        "select",
        "id, name",
        "from performance_work_group_role",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.PerformanceWorkGroupRoleMapper.BaseResultMap")
    PerformanceWorkGroupRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PerformanceWorkGroupRole record);

    @Update({
        "update performance_work_group_role",
        "set name = #{name,jdbcType=VARCHAR}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(PerformanceWorkGroupRole record);
}