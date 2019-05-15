package com.seasun.management.mapper;

import com.seasun.management.model.CfgExchangeRate;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

public interface CfgExchangeRateMapper {
    @Delete({
        "delete from cfg_exchange_rate",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into cfg_exchange_rate (s_code, s_name, ",
        "rate, d_code, d_name)",
        "values (#{sCode,jdbcType=VARCHAR}, #{sName,jdbcType=VARCHAR}, ",
        "#{rate,jdbcType=DECIMAL}, #{dCode,jdbcType=VARCHAR}, #{dName,jdbcType=VARCHAR})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(CfgExchangeRate record);

    int insertSelective(CfgExchangeRate record);

    @Select({
        "select",
        "id, s_code, s_name, rate, d_code, d_name",
        "from cfg_exchange_rate",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.CfgExchangeRateMapper.BaseResultMap")
    CfgExchangeRate selectByPrimaryKey(Long id);

    @Select({
            "select",
            "id, s_code, s_name, rate, d_code, d_name",
            "from cfg_exchange_rate",
            "where s_code = #{sCode,jdbcType=VARCHAR}"
    })
    @ResultMap("com.seasun.management.mapper.CfgExchangeRateMapper.BaseResultMap")
    CfgExchangeRate selectBySourceCode(String sCode);

    int updateByPrimaryKeySelective(CfgExchangeRate record);

    @Update({
        "update cfg_exchange_rate",
        "set s_code = #{sCode,jdbcType=VARCHAR},",
          "s_name = #{sName,jdbcType=VARCHAR},",
          "rate = #{rate,jdbcType=DECIMAL},",
          "d_code = #{dCode,jdbcType=VARCHAR},",
          "d_name = #{dName,jdbcType=VARCHAR}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(CfgExchangeRate record);

    @Update({
            "update cfg_exchange_rate",
            "set rate = #{rate,jdbcType=DECIMAL}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateRateByPrimaryKey(@Param("id")Long id, @Param("rate")BigDecimal rate);

    /* the flowing are user defined ... */

    @Select({"select * from cfg_exchange_rate"})
    List<CfgExchangeRate> selectAll();
}