package com.seasun.management.mapper.cp;

import com.seasun.management.model.cp.Reqissues;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface ReqissuesMapper {
    @Delete({
        "delete from cp.reqissues",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
        "insert into cp.reqissues (BriefDescription, FullDescription, ",
        "CreatedBy, ClosedBy, ",
        "AssignedTo, CreatedOn, ",
        "Type, Severity, ",
        "Priority, ResolutionOutcome, ",
        "Comment, Status, ",
        "Attachments, Project, ",
        "CloseDate, DuplicateIssueID, ",
        "LastAssignedTo, ActovotyRatio, ",
        "ResolvedBy, LastModifiedBy, ",
        "LastModifiedOn, ResolvedOn, ",
        "ExProps, ETA, ",
        "SubSystem, Approvals, ",
        "ParentID, Owner)",
        "values (#{briefDescription,jdbcType=VARCHAR}, #{fullDescription,jdbcType=VARCHAR}, ",
        "#{createdBy,jdbcType=INTEGER}, #{closedBy,jdbcType=INTEGER}, ",
        "#{assignedTo,jdbcType=VARCHAR}, #{createdOn,jdbcType=TIMESTAMP}, ",
        "#{type,jdbcType=INTEGER}, #{severity,jdbcType=INTEGER}, ",
        "#{priority,jdbcType=INTEGER}, #{resolutionOutcome,jdbcType=INTEGER}, ",
        "#{comment,jdbcType=VARCHAR}, #{status,jdbcType=INTEGER}, ",
        "#{attachments,jdbcType=VARCHAR}, #{project,jdbcType=INTEGER}, ",
        "#{closeDate,jdbcType=TIMESTAMP}, #{duplicateIssueId,jdbcType=VARCHAR}, ",
        "#{lastAssignedTo,jdbcType=VARCHAR}, #{actovotyRatio,jdbcType=REAL}, ",
        "#{resolvedBy,jdbcType=INTEGER}, #{lastModifiedBy,jdbcType=INTEGER}, ",
        "#{lastModifiedOn,jdbcType=TIMESTAMP}, #{resolvedOn,jdbcType=TIMESTAMP}, ",
        "#{exProps,jdbcType=VARCHAR}, #{eTA,jdbcType=TIMESTAMP}, ",
        "#{subSystem,jdbcType=INTEGER}, #{approvals,jdbcType=VARCHAR}, ",
        "#{parentId,jdbcType=INTEGER}, #{owner,jdbcType=INTEGER})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Integer.class)
    int insert(Reqissues record);

    int insertSelective(Reqissues record);

    @Select({
        "select",
        "ID, BriefDescription, FullDescription, CreatedBy, ClosedBy, AssignedTo, CreatedOn, ",
        "Type, Severity, Priority, ResolutionOutcome, Comment, Status, Attachments, Project, ",
        "CloseDate, DuplicateIssueID, LastAssignedTo, ActovotyRatio, ResolvedBy, LastModifiedBy, ",
        "LastModifiedOn, ResolvedOn, ExProps, ETA, SubSystem, Approvals, ParentID, Owner",
        "from cp.reqissues",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    @ResultMap("com.seasun.management.mapper.cp.ReqissuesMapper.BaseResultMap")
    Reqissues selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Reqissues record);

    @Update({
        "update cp.reqissues",
        "set BriefDescription = #{briefDescription,jdbcType=VARCHAR},",
          "FullDescription = #{fullDescription,jdbcType=VARCHAR},",
          "CreatedBy = #{createdBy,jdbcType=INTEGER},",
          "ClosedBy = #{closedBy,jdbcType=INTEGER},",
          "AssignedTo = #{assignedTo,jdbcType=VARCHAR},",
          "CreatedOn = #{createdOn,jdbcType=TIMESTAMP},",
          "Type = #{type,jdbcType=INTEGER},",
          "Severity = #{severity,jdbcType=INTEGER},",
          "Priority = #{priority,jdbcType=INTEGER},",
          "ResolutionOutcome = #{resolutionOutcome,jdbcType=INTEGER},",
          "Comment = #{comment,jdbcType=VARCHAR},",
          "Status = #{status,jdbcType=INTEGER},",
          "Attachments = #{attachments,jdbcType=VARCHAR},",
          "Project = #{project,jdbcType=INTEGER},",
          "CloseDate = #{closeDate,jdbcType=TIMESTAMP},",
          "DuplicateIssueID = #{duplicateIssueId,jdbcType=VARCHAR},",
          "LastAssignedTo = #{lastAssignedTo,jdbcType=VARCHAR},",
          "ActovotyRatio = #{actovotyRatio,jdbcType=REAL},",
          "ResolvedBy = #{resolvedBy,jdbcType=INTEGER},",
          "LastModifiedBy = #{lastModifiedBy,jdbcType=INTEGER},",
          "LastModifiedOn = #{lastModifiedOn,jdbcType=TIMESTAMP},",
          "ResolvedOn = #{resolvedOn,jdbcType=TIMESTAMP},",
          "ExProps = #{exProps,jdbcType=VARCHAR},",
          "ETA = #{eTA,jdbcType=TIMESTAMP},",
          "SubSystem = #{subSystem,jdbcType=INTEGER},",
          "Approvals = #{approvals,jdbcType=VARCHAR},",
          "ParentID = #{parentId,jdbcType=INTEGER},",
          "Owner = #{owner,jdbcType=INTEGER}",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(Reqissues record);
}