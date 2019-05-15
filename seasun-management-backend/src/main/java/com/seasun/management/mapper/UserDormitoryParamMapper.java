package com.seasun.management.mapper;

import com.seasun.management.dto.UserDormitoryParamDto;
import com.seasun.management.model.UserDormitoryParam;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface UserDormitoryParamMapper {
    @Delete({
        "delete from user_dormitory_param",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into user_dormitory_param (year, month, ",
        "name, value)",
        "values (#{year,jdbcType=INTEGER}, #{month,jdbcType=INTEGER}, ",
        "#{key,jdbcType=VARCHAR}, #{value,jdbcType=INTEGER})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(UserDormitoryParam record);

    int insertSelective(UserDormitoryParam record);

    @Select({
        "select",
        "id, year, month, name, value",
        "from user_dormitory_param",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserDormitoryParamMapper.BaseResultMap")
    UserDormitoryParam selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserDormitoryParam record);

    @Update({
        "update user_dormitory_param",
        "set year = #{year,jdbcType=INTEGER},",
          "month = #{month,jdbcType=INTEGER},",
          "name = #{name,jdbcType=VARCHAR},",
          "value = #{value,jdbcType=INTEGER}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserDormitoryParam record);

    @Select({
            "select id, year, month, name, value from user_dormitory_param where year = #{0,jdbcType=INTEGER} and month = #{1,jdbcType=INTEGER}"
    })
    @ResultMap("com.seasun.management.mapper.UserDormitoryParamMapper.BaseResultMap")
    List<UserDormitoryParam> selectAll(Integer year,Integer month);

    @Select({
            "select id, year, month, name, value from user_dormitory_param where year = #{year,jdbcType=BIGINT} and month = #{month,jdbcType=BIGINT} and name = #{name,jdbcType=VARCHAR}"
    })
    @ResultMap("com.seasun.management.mapper.UserDormitoryParamMapper.BaseResultMap")
    UserDormitoryParam selectByKey(UserDormitoryParamDto userDormitoryParamDto);

    @Select({
            "select year,month,name,value from user_dormitory_param where name in ('A_ROOM','B_ROOM','C_ROOM','All_ROOM') ORDER BY year desc,month desc LIMIT 0,4"
    })
    @ResultMap("com.seasun.management.mapper.UserDormitoryParamMapper.BaseResultMap")
    List<UserDormitoryParam> selectUserDormitoryParam();
}