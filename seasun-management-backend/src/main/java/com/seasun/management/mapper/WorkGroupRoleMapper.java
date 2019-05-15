package com.seasun.management.mapper;

import com.seasun.management.model.WorkGroupRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface WorkGroupRoleMapper {
    @Delete({
            "delete from work_group_role",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into work_group_role (name)",
            "values (#{name,jdbcType=VARCHAR})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(WorkGroupRole record);

    int insertSelective(WorkGroupRole record);

    @Select({
            "select",
            "id, name",
            "from work_group_role",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.WorkGroupRoleMapper.BaseResultMap")
    WorkGroupRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(WorkGroupRole record);

    @Update({
            "update work_group_role",
            "set name = #{name,jdbcType=VARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(WorkGroupRole record);
}