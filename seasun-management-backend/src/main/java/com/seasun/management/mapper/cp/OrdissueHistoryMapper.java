package com.seasun.management.mapper.cp;

import com.seasun.management.model.cp.OrdissueHistory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface OrdissueHistoryMapper {
    @Delete({
        "delete from cp.ordissuehistory",
        "where ID = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into cp.ordissuehistory (IssueID, UserAction, ",
        "OnDate, ByWho, ",
        "Comment, Info)",
        "values (#{issueId,jdbcType=INTEGER}, #{userAction,jdbcType=INTEGER}, ",
        "#{onDate,jdbcType=TIMESTAMP}, #{byWho,jdbcType=INTEGER}, ",
        "#{comment,jdbcType=VARCHAR}, #{info,jdbcType=VARCHAR})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(OrdissueHistory record);

    int insertSelective(OrdissueHistory record);

    @Select({
        "select",
        "ID, IssueID, UserAction, OnDate, ByWho, Comment, Info",
        "from cp.ordissuehistory",
        "where ID = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.cp.OrdissueHistoryMapper.BaseResultMap")
    OrdissueHistory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrdissueHistory record);

    @Update({
        "update cp.ordissuehistory",
        "set IssueID = #{issueId,jdbcType=INTEGER},",
          "UserAction = #{userAction,jdbcType=INTEGER},",
          "OnDate = #{onDate,jdbcType=TIMESTAMP},",
          "ByWho = #{byWho,jdbcType=INTEGER},",
          "Comment = #{comment,jdbcType=VARCHAR},",
          "Info = #{info,jdbcType=VARCHAR}",
        "where ID = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(OrdissueHistory record);
}