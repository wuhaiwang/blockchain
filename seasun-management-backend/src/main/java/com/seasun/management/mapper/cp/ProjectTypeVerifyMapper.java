package com.seasun.management.mapper.cp;

import com.seasun.management.model.cp.ProjectTypeVerify;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface ProjectTypeVerifyMapper {
    @Delete({
        "delete from cp.projecttypeverify",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
        "insert into cp.projecttypeverify (GameProject, ArtType, ",
        "VerifyBy)",
        "values (#{gameProject,jdbcType=VARCHAR}, #{artType,jdbcType=VARCHAR}, ",
        "#{verifyBy,jdbcType=INTEGER})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Integer.class)
    int insert(ProjectTypeVerify record);

    int insertSelective(ProjectTypeVerify record);

    @Select({
        "select",
        "ID, GameProject, ArtType, VerifyBy",
        "from cp.projecttypeverify",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    @ResultMap("com.seasun.management.mapper.cp.ProjectTypeVerifyMapper.BaseResultMap")
    ProjectTypeVerify selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProjectTypeVerify record);

    @Update({
        "update cp.projecttypeverify",
        "set GameProject = #{gameProject,jdbcType=VARCHAR},",
          "ArtType = #{artType,jdbcType=VARCHAR},",
          "VerifyBy = #{verifyBy,jdbcType=INTEGER}",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(ProjectTypeVerify record);
}