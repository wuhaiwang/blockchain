package com.seasun.management.mapper.cp;

import com.seasun.management.model.cp.CPDetails;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface CPDetailsMapper {
    @Delete({
        "delete from cp.cpdetails",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
        "insert into cp.cpdetails (CPID, Type, ",
        "Txt, Digit, Remark, ",
        "blValue, TypeCircle)",
        "values (#{cPId,jdbcType=INTEGER}, #{type,jdbcType=INTEGER}, ",
        "#{txt,jdbcType=VARCHAR}, #{digit,jdbcType=DECIMAL}, #{remark,jdbcType=VARCHAR}, ",
        "#{blValue,jdbcType=BIT}, #{typeCircle,jdbcType=DECIMAL})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Integer.class)
    int insert(CPDetails record);

    int insertSelective(CPDetails record);

    @Select({
        "select",
        "ID, CPID, Type, Txt, Digit, Remark, blValue, TypeCircle",
        "from cp.cpdetails",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    @ResultMap("com.seasun.management.mapper.cp.CPDetailsMapper.BaseResultMap")
    CPDetails selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CPDetails record);

    @Update({
        "update cp.cpdetails",
        "set CPID = #{cPId,jdbcType=INTEGER},",
          "Type = #{type,jdbcType=INTEGER},",
          "Txt = #{txt,jdbcType=VARCHAR},",
          "Digit = #{digit,jdbcType=DECIMAL},",
          "Remark = #{remark,jdbcType=VARCHAR},",
          "blValue = #{blValue,jdbcType=BIT},",
          "TypeCircle = #{typeCircle,jdbcType=DECIMAL}",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(CPDetails record);
}