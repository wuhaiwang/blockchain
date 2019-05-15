package com.seasun.management.mapper.cp;

import com.seasun.management.model.cp.ProjectLeader;
import com.seasun.management.model.cp.ProjectLeaderKey;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface ProjectLeaderMapper {
    @Delete({
        "delete from cp.projectleader",
        "where GameProject = #{gameProject,jdbcType=VARCHAR}",
          "and UserID = #{userId,jdbcType=INTEGER}",
          "and ConnectionType = #{connectionType,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(ProjectLeaderKey key);

    @Insert({
        "insert into cp.projectleader (GameProject, UserID, ",
        "ConnectionType)",
        "values (#{gameProject,jdbcType=VARCHAR}, #{userId,jdbcType=INTEGER}, ",
        "#{connectionType,jdbcType=INTEGER})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Integer.class)
    int insert(ProjectLeader record);

    int insertSelective(ProjectLeader record);

    @Select({
        "select",
        "GameProject, UserID, ConnectionType, ID",
        "from cp.projectleader",
        "where GameProject = #{gameProject,jdbcType=VARCHAR}",
          "and UserID = #{userId,jdbcType=INTEGER}",
          "and ConnectionType = #{connectionType,jdbcType=INTEGER}"
    })
    @ResultMap("com.seasun.management.mapper.cp.ProjectLeaderMapper.BaseResultMap")
    ProjectLeader selectByPrimaryKey(ProjectLeaderKey key);

    int updateByPrimaryKeySelective(ProjectLeader record);

    @Update({
        "update cp.projectleader",
        "set ID = #{id,jdbcType=INTEGER}",
        "where GameProject = #{gameProject,jdbcType=VARCHAR}",
          "and UserID = #{userId,jdbcType=INTEGER}",
          "and ConnectionType = #{connectionType,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(ProjectLeader record);
}