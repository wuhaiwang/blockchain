package com.seasun.management.mapper.cp;

import com.seasun.management.model.cp.AutoOrderTemplet;
import com.seasun.management.model.cp.AutoOrderTempletKey;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface AutoOrderTempletMapper {
    @Delete({
        "delete from cp.autoordertemplet",
        "where GameProject = #{gameProject,jdbcType=VARCHAR}",
          "and ArtType = #{artType,jdbcType=VARCHAR}",
          "and Title = #{title,jdbcType=VARCHAR}"
    })
    int deleteByPrimaryKey(AutoOrderTempletKey key);

    @Insert({
        "insert into cp.autoordertemplet (GameProject, ArtType, ",
        "Title, Client, WorkRequirements, ",
        "TheFormatWorks, AcceptanceCriteria, ",
        "Remark, Other)",
        "values (#{gameProject,jdbcType=VARCHAR}, #{artType,jdbcType=VARCHAR}, ",
        "#{title,jdbcType=VARCHAR}, #{client,jdbcType=VARCHAR}, #{workRequirements,jdbcType=VARCHAR}, ",
        "#{theFormatWorks,jdbcType=VARCHAR}, #{acceptanceCriteria,jdbcType=VARCHAR}, ",
        "#{remark,jdbcType=VARCHAR}, #{other,jdbcType=VARCHAR})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Integer.class)
    int insert(AutoOrderTemplet record);

    int insertSelective(AutoOrderTemplet record);

    @Select({
        "select",
        "GameProject, ArtType, Title, ID, Client, WorkRequirements, TheFormatWorks, AcceptanceCriteria, ",
        "Remark, Other",
        "from cp.autoordertemplet",
        "where GameProject = #{gameProject,jdbcType=VARCHAR}",
          "and ArtType = #{artType,jdbcType=VARCHAR}",
          "and Title = #{title,jdbcType=VARCHAR}"
    })
    @ResultMap("com.seasun.management.mapper.cp.AutoOrderTempletMapper.BaseResultMap")
    AutoOrderTemplet selectByPrimaryKey(AutoOrderTempletKey key);

    int updateByPrimaryKeySelective(AutoOrderTemplet record);

    @Update({
        "update cp.autoordertemplet",
        "set ID = #{id,jdbcType=INTEGER},",
          "Client = #{client,jdbcType=VARCHAR},",
          "WorkRequirements = #{workRequirements,jdbcType=VARCHAR},",
          "TheFormatWorks = #{theFormatWorks,jdbcType=VARCHAR},",
          "AcceptanceCriteria = #{acceptanceCriteria,jdbcType=VARCHAR},",
          "Remark = #{remark,jdbcType=VARCHAR},",
          "Other = #{other,jdbcType=VARCHAR}",
        "where GameProject = #{gameProject,jdbcType=VARCHAR}",
          "and ArtType = #{artType,jdbcType=VARCHAR}",
          "and Title = #{title,jdbcType=VARCHAR}"
    })
    int updateByPrimaryKey(AutoOrderTemplet record);
}