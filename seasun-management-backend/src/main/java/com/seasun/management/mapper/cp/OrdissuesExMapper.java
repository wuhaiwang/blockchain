package com.seasun.management.mapper.cp;

import com.seasun.management.dto.cp.CPOrderDto;
import com.seasun.management.dto.cp.OrdissuesExDto;
import com.seasun.management.model.cp.OrdissuesEx;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface OrdissuesExMapper {
    @Delete({
        "delete from cp.ordissuesex",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
        "insert into cp.ordissuesex (IssueID, CPID, ",
        "GameProject, ResType, ",
        "Amount, Attitude, ",
        "Feedback, OnTime, ",
        "Quality, OrderStatus, ",
        "OrderNo, Description, ",
        "EvaluationTime, ReimbursementNo, ",
        "OrderID, Sign, AutoStatus, ",
        "CPOrderNo, EndEvaluation, ",
        "Currencies, PayTimes, ",
        "VerifyTimes, PrePayMoney, ",
        "MidPayMoney, LastPayMoney, ",
        "RealPrePayMoney, RealMidPayMoney, ",
        "RealLastPayMoney, RealPayMoney, ",
        "Remark, ModifyRealPayMoney, ",
        "Verify, Appraise)",
        "values (#{issueId,jdbcType=INTEGER}, #{cPId,jdbcType=INTEGER}, ",
        "#{gameProject,jdbcType=VARCHAR}, #{resType,jdbcType=VARCHAR}, ",
        "#{amount,jdbcType=INTEGER}, #{attitude,jdbcType=DECIMAL}, ",
        "#{feedback,jdbcType=DECIMAL}, #{onTime,jdbcType=DECIMAL}, ",
        "#{quality,jdbcType=DECIMAL}, #{orderStatus,jdbcType=INTEGER}, ",
        "#{orderNo,jdbcType=VARCHAR}, #{description,jdbcType=VARCHAR}, ",
        "#{evaluationTime,jdbcType=TIMESTAMP}, #{reimbursementNo,jdbcType=VARCHAR}, ",
        "#{orderID,jdbcType=INTEGER}, #{sign,jdbcType=INTEGER}, #{autoStatus,jdbcType=INTEGER}, ",
        "#{cPOrderNo,jdbcType=VARCHAR}, #{endEvaluation,jdbcType=TINYINT}, ",
        "#{currencies,jdbcType=VARCHAR}, #{payTimes,jdbcType=INTEGER}, ",
        "#{verifyTimes,jdbcType=INTEGER}, #{prePayMoney,jdbcType=DECIMAL}, ",
        "#{midPayMoney,jdbcType=DECIMAL}, #{lastPayMoney,jdbcType=DECIMAL}, ",
        "#{realPrePayMoney,jdbcType=DECIMAL}, #{realMidPayMoney,jdbcType=DECIMAL}, ",
        "#{realLastPayMoney,jdbcType=DECIMAL}, #{realPayMoney,jdbcType=DECIMAL}, ",
        "#{remark,jdbcType=VARCHAR}, #{modifyRealPayMoney,jdbcType=TINYINT}, ",
        "#{verify,jdbcType=TINYINT}, #{appraise,jdbcType=LONGVARCHAR})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Integer.class)
    int insert(OrdissuesEx record);

    int insertSelective(OrdissuesEx record);

    @Select({
        "select",
        "ID, IssueID, CPID, GameProject, ResType, Amount, Attitude, Feedback, OnTime, ",
        "Quality, OrderStatus, OrderNo, Description, EvaluationTime, ReimbursementNo, ",
        "OrderID, Sign, AutoStatus, CPOrderNo, EndEvaluation, Currencies, PayTimes, VerifyTimes, ",
        "PrePayMoney, MidPayMoney, LastPayMoney, RealPrePayMoney, RealMidPayMoney, RealLastPayMoney, ",
        "RealPayMoney, Remark, ModifyRealPayMoney, Verify, Appraise",
        "from cp.ordissuesex",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    @ResultMap("com.seasun.management.mapper.cp.OrdissuesExMapper.ResultMapWithBLOBs")
    OrdissuesEx selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrdissuesEx record);

    @Update({
        "update cp.ordissuesex",
        "set IssueID = #{issueId,jdbcType=INTEGER},",
          "CPID = #{cPId,jdbcType=INTEGER},",
          "GameProject = #{gameProject,jdbcType=VARCHAR},",
          "ResType = #{resType,jdbcType=VARCHAR},",
          "Amount = #{amount,jdbcType=INTEGER},",
          "Attitude = #{attitude,jdbcType=DECIMAL},",
          "Feedback = #{feedback,jdbcType=DECIMAL},",
          "OnTime = #{onTime,jdbcType=DECIMAL},",
          "Quality = #{quality,jdbcType=DECIMAL},",
          "OrderStatus = #{orderStatus,jdbcType=INTEGER},",
          "OrderNo = #{orderNo,jdbcType=VARCHAR},",
          "Description = #{description,jdbcType=VARCHAR},",
          "EvaluationTime = #{evaluationTime,jdbcType=TIMESTAMP},",
          "ReimbursementNo = #{reimbursementNo,jdbcType=VARCHAR},",
          "OrderID = #{orderID,jdbcType=INTEGER},",
          "Sign = #{sign,jdbcType=INTEGER},",
          "AutoStatus = #{autoStatus,jdbcType=INTEGER},",
          "CPOrderNo = #{cPOrderNo,jdbcType=VARCHAR},",
          "EndEvaluation = #{endEvaluation,jdbcType=TINYINT},",
          "Currencies = #{currencies,jdbcType=VARCHAR},",
          "PayTimes = #{payTimes,jdbcType=INTEGER},",
          "VerifyTimes = #{verifyTimes,jdbcType=INTEGER},",
          "PrePayMoney = #{prePayMoney,jdbcType=DECIMAL},",
          "MidPayMoney = #{midPayMoney,jdbcType=DECIMAL},",
          "LastPayMoney = #{lastPayMoney,jdbcType=DECIMAL},",
          "RealPrePayMoney = #{realPrePayMoney,jdbcType=DECIMAL},",
          "RealMidPayMoney = #{realMidPayMoney,jdbcType=DECIMAL},",
          "RealLastPayMoney = #{realLastPayMoney,jdbcType=DECIMAL},",
          "RealPayMoney = #{realPayMoney,jdbcType=DECIMAL},",
          "Remark = #{remark,jdbcType=VARCHAR},",
          "ModifyRealPayMoney = #{modifyRealPayMoney,jdbcType=TINYINT},",
          "Verify = #{verify,jdbcType=TINYINT},",
          "Appraise = #{appraise,jdbcType=LONGVARCHAR}",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKeyWithBLOBs(OrdissuesEx record);

    @Update({
        "update cp.ordissuesex",
        "set IssueID = #{issueId,jdbcType=INTEGER},",
          "CPID = #{cPId,jdbcType=INTEGER},",
          "GameProject = #{gameProject,jdbcType=VARCHAR},",
          "ResType = #{resType,jdbcType=VARCHAR},",
          "Amount = #{amount,jdbcType=INTEGER},",
          "Attitude = #{attitude,jdbcType=DECIMAL},",
          "Feedback = #{feedback,jdbcType=DECIMAL},",
          "OnTime = #{onTime,jdbcType=DECIMAL},",
          "Quality = #{quality,jdbcType=DECIMAL},",
          "OrderStatus = #{orderStatus,jdbcType=INTEGER},",
          "OrderNo = #{orderNo,jdbcType=VARCHAR},",
          "Description = #{description,jdbcType=VARCHAR},",
          "EvaluationTime = #{evaluationTime,jdbcType=TIMESTAMP},",
          "ReimbursementNo = #{reimbursementNo,jdbcType=VARCHAR},",
          "OrderID = #{orderID,jdbcType=INTEGER},",
          "Sign = #{sign,jdbcType=INTEGER},",
          "AutoStatus = #{autoStatus,jdbcType=INTEGER},",
          "CPOrderNo = #{cPOrderNo,jdbcType=VARCHAR},",
          "EndEvaluation = #{endEvaluation,jdbcType=TINYINT},",
          "Currencies = #{currencies,jdbcType=VARCHAR},",
          "PayTimes = #{payTimes,jdbcType=INTEGER},",
          "VerifyTimes = #{verifyTimes,jdbcType=INTEGER},",
          "PrePayMoney = #{prePayMoney,jdbcType=DECIMAL},",
          "MidPayMoney = #{midPayMoney,jdbcType=DECIMAL},",
          "LastPayMoney = #{lastPayMoney,jdbcType=DECIMAL},",
          "RealPrePayMoney = #{realPrePayMoney,jdbcType=DECIMAL},",
          "RealMidPayMoney = #{realMidPayMoney,jdbcType=DECIMAL},",
          "RealLastPayMoney = #{realLastPayMoney,jdbcType=DECIMAL},",
          "RealPayMoney = #{realPayMoney,jdbcType=DECIMAL},",
          "Remark = #{remark,jdbcType=VARCHAR},",
          "ModifyRealPayMoney = #{modifyRealPayMoney,jdbcType=TINYINT},",
          "Verify = #{verify,jdbcType=TINYINT}",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(OrdissuesEx record);

    /* the flowing are user defined ... */
    // 订单排除了 新单 测试 已取消 验收不过 4种状态

    List<OrdissuesExDto> selectByGameProjectNamesAndCreatedYear(@Param("names") List<String> gameProjectNames, @Param("year") Integer year);

    @Select({"select os.`Status` status,ox.*,gp.ID cPProjectId from cp.ordissuesex ox left join cp.ordissues os on os.id=ox.IssueID left join cp.gameproject gp on ox.GameProject=gp.Name where os.Status not in (15,21) and gp.Active=1 and DATE_FORMAT(os.CreatedOn,'%Y') =#{0}"})
    List<OrdissuesExDto> selectActiveGameProjectOrdissuesByYear(Integer year);

    List<CPOrderDto> selectByGameProjectIdsAndCreatedYear(@Param("ids")List<Integer> cPProjectIds, @Param("year") Integer year);

    List<OrdissuesExDto> selectByCPIdsAndCreatedYear(@Param("ids") List<Integer> cPIds, @Param("year") Integer year);
}