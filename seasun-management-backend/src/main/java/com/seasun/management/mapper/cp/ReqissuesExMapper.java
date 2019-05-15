package com.seasun.management.mapper.cp;

import com.seasun.management.model.cp.ReqissuesEx;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface ReqissuesExMapper {
    @Delete({
        "delete from cp.reqissuesex",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
        "insert into cp.reqissuesex (IssueID, CPID, ",
        "GameProject, ResType, ",
        "Description, Location, ",
        "Amount, SchemeReady, ",
        "StandardReady, PreReady, ",
        "InterfaceMan, TestStatus, ",
        "ArtLeader, ArtConfirm, ",
        "ArtConfirmTime, OrderNo)",
        "values (#{issueId,jdbcType=INTEGER}, #{cPId,jdbcType=INTEGER}, ",
        "#{gameProject,jdbcType=VARCHAR}, #{resType,jdbcType=VARCHAR}, ",
        "#{description,jdbcType=VARCHAR}, #{location,jdbcType=VARCHAR}, ",
        "#{amount,jdbcType=INTEGER}, #{schemeReady,jdbcType=BIT}, ",
        "#{standardReady,jdbcType=BIT}, #{preReady,jdbcType=BIT}, ",
        "#{interfaceMan,jdbcType=INTEGER}, #{testStatus,jdbcType=INTEGER}, ",
        "#{artLeader,jdbcType=INTEGER}, #{artConfirm,jdbcType=BIT}, ",
        "#{artConfirmTime,jdbcType=TIMESTAMP}, #{orderNo,jdbcType=VARCHAR})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Integer.class)
    int insert(ReqissuesEx record);

    int insertSelective(ReqissuesEx record);

    @Select({
        "select",
        "ID, IssueID, CPID, GameProject, ResType, Description, Location, Amount, SchemeReady, ",
        "StandardReady, PreReady, InterfaceMan, TestStatus, ArtLeader, ArtConfirm, ArtConfirmTime, ",
        "OrderNo",
        "from cp.reqissuesex",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    @ResultMap("com.seasun.management.mapper.cp.ReqissuesExMapper.BaseResultMap")
    ReqissuesEx selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ReqissuesEx record);

    @Update({
        "update cp.reqissuesex",
        "set IssueID = #{issueId,jdbcType=INTEGER},",
          "CPID = #{cPId,jdbcType=INTEGER},",
          "GameProject = #{gameProject,jdbcType=VARCHAR},",
          "ResType = #{resType,jdbcType=VARCHAR},",
          "Description = #{description,jdbcType=VARCHAR},",
          "Location = #{location,jdbcType=VARCHAR},",
          "Amount = #{amount,jdbcType=INTEGER},",
          "SchemeReady = #{schemeReady,jdbcType=BIT},",
          "StandardReady = #{standardReady,jdbcType=BIT},",
          "PreReady = #{preReady,jdbcType=BIT},",
          "InterfaceMan = #{interfaceMan,jdbcType=INTEGER},",
          "TestStatus = #{testStatus,jdbcType=INTEGER},",
          "ArtLeader = #{artLeader,jdbcType=INTEGER},",
          "ArtConfirm = #{artConfirm,jdbcType=BIT},",
          "ArtConfirmTime = #{artConfirmTime,jdbcType=TIMESTAMP},",
          "OrderNo = #{orderNo,jdbcType=VARCHAR}",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(ReqissuesEx record);
}