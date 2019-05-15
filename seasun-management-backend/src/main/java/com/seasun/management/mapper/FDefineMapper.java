package com.seasun.management.mapper;

import com.seasun.management.model.FDefine;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface FDefineMapper {
    @Delete({
            "delete from f_define",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into f_define (name, description)",
            "values (#{name,jdbcType=VARCHAR}, #{description,jdbcType=VARCHAR})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FDefine record);

    int insertSelective(FDefine record);

    @Select({
            "select",
            "id, name, description",
            "from f_define",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FDefineMapper.BaseResultMap")
    FDefine selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FDefine record);

    @Update({
            "update f_define",
            "set name = #{name,jdbcType=VARCHAR},",
            "description = #{description,jdbcType=VARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FDefine record);

    /* the flowing are user defined ... */
    @Select("select * from f_define where name = #{flowName} limit 1")
    FDefine selectByName(String flowName);

}