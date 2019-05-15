package com.seasun.management.mapper.cp;

import com.seasun.management.model.cp.Attachments;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface AttachmentsMapper {
    @Delete({
        "delete from cp.attachments",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
        "insert into cp.attachments (ParentID, Type, ",
        "FileName, TrueName)",
        "values (#{parentId,jdbcType=INTEGER}, #{type,jdbcType=INTEGER}, ",
        "#{fileName,jdbcType=VARCHAR}, #{trueName,jdbcType=VARCHAR})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Integer.class)
    int insert(Attachments record);

    int insertSelective(Attachments record);

    @Select({
        "select",
        "ID, ParentID, Type, FileName, TrueName",
        "from cp.attachments",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    @ResultMap("com.seasun.management.mapper.cp.AttachmentsMapper.BaseResultMap")
    Attachments selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Attachments record);

    @Update({
        "update cp.attachments",
        "set ParentID = #{parentId,jdbcType=INTEGER},",
          "Type = #{type,jdbcType=INTEGER},",
          "FileName = #{fileName,jdbcType=VARCHAR},",
          "TrueName = #{trueName,jdbcType=VARCHAR}",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(Attachments record);
}