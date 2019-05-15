package com.seasun.management.mapper.cp;

import com.seasun.management.model.cp.AutoOrderContent;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface AutoOrderContentMapper {
    @Delete({
        "delete from cp.autoordercontent",
        "where IssueID = #{issueId,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer issueId);

    @Insert({
        "insert into cp.autoordercontent (WorkName, WorkAmount, ",
        "WorkCost, FileNames, ",
        "WorkRequirements, TheFormatWorks, ",
        "EndDate, AcceptanceCriteria, ",
        "Remark, Other, OpTime, ",
        "MakeType, Content)",
        "values (#{workName,jdbcType=VARCHAR}, #{workAmount,jdbcType=VARCHAR}, ",
        "#{workCost,jdbcType=VARCHAR}, #{fileNames,jdbcType=VARCHAR}, ",
        "#{workRequirements,jdbcType=VARCHAR}, #{theFormatWorks,jdbcType=VARCHAR}, ",
        "#{endDate,jdbcType=VARCHAR}, #{acceptanceCriteria,jdbcType=VARCHAR}, ",
        "#{remark,jdbcType=VARCHAR}, #{other,jdbcType=VARCHAR}, #{opTime,jdbcType=TIMESTAMP}, ",
        "#{makeType,jdbcType=VARCHAR}, #{content,jdbcType=LONGVARCHAR})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="issueId", before=false, resultType=Integer.class)
    int insert(AutoOrderContent record);

    int insertSelective(AutoOrderContent record);

    @Select({
        "select",
        "IssueID, WorkName, WorkAmount, WorkCost, FileNames, WorkRequirements, TheFormatWorks, ",
        "EndDate, AcceptanceCriteria, Remark, Other, OpTime, MakeType, Content",
        "from cp.autoordercontent",
        "where IssueID = #{issueId,jdbcType=INTEGER}"
    })
    @ResultMap("com.seasun.management.mapper.cp.AutoOrderContentMapper.ResultMapWithBLOBs")
    AutoOrderContent selectByPrimaryKey(Integer issueId);

    int updateByPrimaryKeySelective(AutoOrderContent record);

    @Update({
        "update cp.autoordercontent",
        "set WorkName = #{workName,jdbcType=VARCHAR},",
          "WorkAmount = #{workAmount,jdbcType=VARCHAR},",
          "WorkCost = #{workCost,jdbcType=VARCHAR},",
          "FileNames = #{fileNames,jdbcType=VARCHAR},",
          "WorkRequirements = #{workRequirements,jdbcType=VARCHAR},",
          "TheFormatWorks = #{theFormatWorks,jdbcType=VARCHAR},",
          "EndDate = #{endDate,jdbcType=VARCHAR},",
          "AcceptanceCriteria = #{acceptanceCriteria,jdbcType=VARCHAR},",
          "Remark = #{remark,jdbcType=VARCHAR},",
          "Other = #{other,jdbcType=VARCHAR},",
          "OpTime = #{opTime,jdbcType=TIMESTAMP},",
          "MakeType = #{makeType,jdbcType=VARCHAR},",
          "Content = #{content,jdbcType=LONGVARCHAR}",
        "where IssueID = #{issueId,jdbcType=INTEGER}"
    })
    int updateByPrimaryKeyWithBLOBs(AutoOrderContent record);

    @Update({
        "update cp.autoordercontent",
        "set WorkName = #{workName,jdbcType=VARCHAR},",
          "WorkAmount = #{workAmount,jdbcType=VARCHAR},",
          "WorkCost = #{workCost,jdbcType=VARCHAR},",
          "FileNames = #{fileNames,jdbcType=VARCHAR},",
          "WorkRequirements = #{workRequirements,jdbcType=VARCHAR},",
          "TheFormatWorks = #{theFormatWorks,jdbcType=VARCHAR},",
          "EndDate = #{endDate,jdbcType=VARCHAR},",
          "AcceptanceCriteria = #{acceptanceCriteria,jdbcType=VARCHAR},",
          "Remark = #{remark,jdbcType=VARCHAR},",
          "Other = #{other,jdbcType=VARCHAR},",
          "OpTime = #{opTime,jdbcType=TIMESTAMP},",
          "MakeType = #{makeType,jdbcType=VARCHAR}",
        "where IssueID = #{issueId,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(AutoOrderContent record);
}