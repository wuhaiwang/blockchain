package com.seasun.management.mapper;

import com.seasun.management.model.CfgGuiCategory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CfgGuiCategoryMapper {
    @Delete({
        "delete from cfg_gui_category",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into cfg_gui_category (title, subtitle, ",
        "type, channel, image, ",
        "location, param, remark)",
        "values (#{title,jdbcType=VARCHAR}, #{subtitle,jdbcType=VARCHAR}, ",
        "#{type,jdbcType=VARCHAR}, #{channel,jdbcType=VARCHAR}, #{image,jdbcType=VARCHAR}, ",
        "#{location,jdbcType=VARCHAR}, #{param,jdbcType=VARCHAR}, #{remark,jdbcType=VARCHAR})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(CfgGuiCategory record);

    int insertSelective(CfgGuiCategory record);

    @Select({
        "select",
        "id, title, subtitle, type, channel, image, location, param, remark",
        "from cfg_gui_category",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.CfgGuiCategoryMapper.BaseResultMap")
    CfgGuiCategory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CfgGuiCategory record);

    @Update({
        "update cfg_gui_category",
        "set title = #{title,jdbcType=VARCHAR},",
          "subtitle = #{subtitle,jdbcType=VARCHAR},",
          "type = #{type,jdbcType=VARCHAR},",
          "channel = #{channel,jdbcType=VARCHAR},",
          "image = #{image,jdbcType=VARCHAR},",
          "location = #{location,jdbcType=VARCHAR},",
          "param = #{param,jdbcType=VARCHAR},",
          "remark = #{remark,jdbcType=VARCHAR}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(CfgGuiCategory record);

    /* the flowing are user defined ... */
    @Select({"select * from cfg_gui_category"})
    List<CfgGuiCategory> selectAll();
}