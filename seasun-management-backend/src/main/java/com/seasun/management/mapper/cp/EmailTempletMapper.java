package com.seasun.management.mapper.cp;

import com.seasun.management.model.cp.EmailTemplet;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface EmailTempletMapper {
    @Delete({
        "delete from cp.emailtemplet",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
        "insert into cp.emailtemplet (Subject, MainBody, ",
        "MailTo, MailCC, ",
        "AutoSend, SendFrequency)",
        "values (#{subject,jdbcType=VARCHAR}, #{mainBody,jdbcType=VARCHAR}, ",
        "#{mailTo,jdbcType=VARCHAR}, #{mailCC,jdbcType=VARCHAR}, ",
        "#{autoSend,jdbcType=TINYINT}, #{sendFrequency,jdbcType=INTEGER})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Integer.class)
    int insert(EmailTemplet record);

    int insertSelective(EmailTemplet record);

    @Select({
        "select",
        "ID, Subject, MainBody, MailTo, MailCC, AutoSend, SendFrequency",
        "from cp.emailtemplet",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    @ResultMap("com.seasun.management.mapper.cp.EmailTempletMapper.BaseResultMap")
    EmailTemplet selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(EmailTemplet record);

    @Update({
        "update cp.emailtemplet",
        "set Subject = #{subject,jdbcType=VARCHAR},",
          "MainBody = #{mainBody,jdbcType=VARCHAR},",
          "MailTo = #{mailTo,jdbcType=VARCHAR},",
          "MailCC = #{mailCC,jdbcType=VARCHAR},",
          "AutoSend = #{autoSend,jdbcType=TINYINT},",
          "SendFrequency = #{sendFrequency,jdbcType=INTEGER}",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(EmailTemplet record);
}