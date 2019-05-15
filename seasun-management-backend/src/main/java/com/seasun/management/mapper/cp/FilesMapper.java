package com.seasun.management.mapper.cp;

import com.seasun.management.model.cp.Files;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface FilesMapper {
    @Delete({
        "delete from cp.files",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
        "insert into cp.files (IssueID, Name, ",
        "Days, PriceOfPerson, ",
        "SendDate, ETA, ",
        "VerifyDate, VerifyBy, ",
        "Status, Entry, ModifyTimes)",
        "values (#{issueId,jdbcType=INTEGER}, #{name,jdbcType=VARCHAR}, ",
        "#{days,jdbcType=DECIMAL}, #{priceOfPerson,jdbcType=DECIMAL}, ",
        "#{sendDate,jdbcType=TIMESTAMP}, #{eta,jdbcType=TIMESTAMP}, ",
        "#{verifyDate,jdbcType=TIMESTAMP}, #{verifyBy,jdbcType=INTEGER}, ",
        "#{status,jdbcType=INTEGER}, #{entry,jdbcType=BIT}, #{modifyTimes,jdbcType=INTEGER})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Integer.class)
    int insert(Files record);

    int insertSelective(Files record);

    @Select({
        "select",
        "ID, IssueID, Name, Days, PriceOfPerson, SendDate, ETA, VerifyDate, VerifyBy, ",
        "Status, Entry, ModifyTimes",
        "from cp.files",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    @ResultMap("com.seasun.management.mapper.cp.FilesMapper.BaseResultMap")
    Files selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Files record);

    @Update({
        "update cp.files",
        "set IssueID = #{issueId,jdbcType=INTEGER},",
          "Name = #{name,jdbcType=VARCHAR},",
          "Days = #{days,jdbcType=DECIMAL},",
          "PriceOfPerson = #{priceOfPerson,jdbcType=DECIMAL},",
          "SendDate = #{sendDate,jdbcType=TIMESTAMP},",
          "ETA = #{eta,jdbcType=TIMESTAMP},",
          "VerifyDate = #{verifyDate,jdbcType=TIMESTAMP},",
          "VerifyBy = #{verifyBy,jdbcType=INTEGER},",
          "Status = #{status,jdbcType=INTEGER},",
          "Entry = #{entry,jdbcType=BIT},",
          "ModifyTimes = #{modifyTimes,jdbcType=INTEGER}",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(Files record);
}