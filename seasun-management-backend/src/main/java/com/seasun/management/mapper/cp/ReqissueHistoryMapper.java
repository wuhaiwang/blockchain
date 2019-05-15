package com.seasun.management.mapper.cp;

import com.seasun.management.model.cp.ReqissueHistory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface ReqissueHistoryMapper {
    @Delete({
        "delete from cp.reqissuehistory",
        "where ID = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into cp.reqissuehistory (IssueID, UserAction, ",
        "OnDate, ByWho, ",
        "Comment)",
        "values (#{issueId,jdbcType=INTEGER}, #{userAction,jdbcType=INTEGER}, ",
        "#{onDate,jdbcType=TIMESTAMP}, #{byWho,jdbcType=INTEGER}, ",
        "#{comment,jdbcType=LONGVARCHAR})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(ReqissueHistory record);

    int insertSelective(ReqissueHistory record);

    @Select({
        "select",
        "ID, IssueID, UserAction, OnDate, ByWho, Comment",
        "from cp.reqissuehistory",
        "where ID = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.cp.ReqissueHistoryMapper.ResultMapWithBLOBs")
    ReqissueHistory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ReqissueHistory record);

    @Update({
        "update cp.reqissuehistory",
        "set IssueID = #{issueId,jdbcType=INTEGER},",
          "UserAction = #{userAction,jdbcType=INTEGER},",
          "OnDate = #{onDate,jdbcType=TIMESTAMP},",
          "ByWho = #{byWho,jdbcType=INTEGER},",
          "Comment = #{comment,jdbcType=LONGVARCHAR}",
        "where ID = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKeyWithBLOBs(ReqissueHistory record);

    @Update({
        "update cp.reqissuehistory",
        "set IssueID = #{issueId,jdbcType=INTEGER},",
          "UserAction = #{userAction,jdbcType=INTEGER},",
          "OnDate = #{onDate,jdbcType=TIMESTAMP},",
          "ByWho = #{byWho,jdbcType=INTEGER}",
        "where ID = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(ReqissueHistory record);
}