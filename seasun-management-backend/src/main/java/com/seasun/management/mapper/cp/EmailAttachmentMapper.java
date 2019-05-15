package com.seasun.management.mapper.cp;

import com.seasun.management.model.cp.EmailAttachment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface EmailAttachmentMapper {
    @Delete({
        "delete from cp.emailattachment",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
        "insert into cp.emailattachment (EmailID, Name, ",
        "FileData)",
        "values (#{emailId,jdbcType=INTEGER}, #{name,jdbcType=VARCHAR}, ",
        "#{fileData,jdbcType=LONGVARCHAR})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Integer.class)
    int insert(EmailAttachment record);

    int insertSelective(EmailAttachment record);

    @Select({
        "select",
        "ID, EmailID, Name, FileData",
        "from cp.emailattachment",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    @ResultMap("com.seasun.management.mapper.cp.EmailAttachmentMapper.ResultMapWithBLOBs")
    EmailAttachment selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(EmailAttachment record);

    @Update({
        "update cp.emailattachment",
        "set EmailID = #{emailId,jdbcType=INTEGER},",
          "Name = #{name,jdbcType=VARCHAR},",
          "FileData = #{fileData,jdbcType=LONGVARCHAR}",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKeyWithBLOBs(EmailAttachment record);

    @Update({
        "update cp.emailattachment",
        "set EmailID = #{emailId,jdbcType=INTEGER},",
          "Name = #{name,jdbcType=VARCHAR}",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(EmailAttachment record);
}