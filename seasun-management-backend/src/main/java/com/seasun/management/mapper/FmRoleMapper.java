package com.seasun.management.mapper;

import com.seasun.management.model.FmRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface FmRoleMapper {
    @Delete({
            "delete from fm_role",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into fm_role (name)",
            "values (#{name,jdbcType=VARCHAR})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FmRole record);

    int insertSelective(FmRole record);

    @Select({
            "select",
            "id, name",
            "from fm_role",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FmRoleMapper.BaseResultMap")
    FmRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FmRole record);

    @Update({
            "update fm_role",
            "set name = #{name,jdbcType=VARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FmRole record);
}