package com.seasun.management.mapper.cp;

import com.seasun.management.model.cp.Authority;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface AuthorityMapper {
    @Delete({
        "delete from cp.authority",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
        "insert into cp.authority (Name, UserID)",
        "values (#{name,jdbcType=VARCHAR}, #{userId,jdbcType=INTEGER})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Integer.class)
    int insert(Authority record);

    int insertSelective(Authority record);

    @Select({
        "select",
        "ID, Name, UserID",
        "from cp.authority",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    @ResultMap("com.seasun.management.mapper.cp.AuthorityMapper.BaseResultMap")
    Authority selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Authority record);

    @Update({
        "update cp.authority",
        "set Name = #{name,jdbcType=VARCHAR},",
          "UserID = #{userId,jdbcType=INTEGER}",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(Authority record);
}