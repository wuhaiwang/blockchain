package com.seasun.management.mapper;

import com.seasun.management.model.CfgSystemParam;
import com.seasun.management.util.MySystemParamUtils;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CfgSystemParamMapper {
    @Delete({
            "delete from cfg_system_param",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into cfg_system_param (name, value, ",
            "value_type, comment)",
            "values (#{name,jdbcType=VARCHAR}, #{value,jdbcType=VARCHAR}, ",
            "#{valueType,jdbcType=VARCHAR}, #{comment,jdbcType=VARCHAR})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(CfgSystemParam record);

    int insertSelective(CfgSystemParam record);

    @Select({
            "select",
            "id, name, value, value_type, comment",
            "from cfg_system_param",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.CfgSystemParamMapper.BaseResultMap")
    CfgSystemParam selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CfgSystemParam record);

    @Update({
            "update cfg_system_param",
            "set name = #{name,jdbcType=VARCHAR},",
            "value = #{value,jdbcType=VARCHAR},",
            "value_type = #{valueType,jdbcType=VARCHAR},",
            "comment = #{comment,jdbcType=VARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(CfgSystemParam record);

    /* the flowing are user defined ... */
    @Select("select * from cfg_system_param where name = #{name}")
    CfgSystemParam selectByName(String name);

    @Select({"select value from cfg_system_param where name ='" + MySystemParamUtils.Key.AnnualPartyStartFlag + "'"})
    boolean selectAnnualPartyStartFlag();

    @Select({"select * from cfg_system_param"})
    List<CfgSystemParam> selectAll();

    @Update({
            "update cfg_system_param set ",
            "value = #{value,jdbcType=VARCHAR},",
            "value_type = #{valueType,jdbcType=VARCHAR},",
            "comment = #{comment,jdbcType=VARCHAR}",
            "where name = #{name,jdbcType=VARCHAR}"
    })
    int updateByName(CfgSystemParam record);
}