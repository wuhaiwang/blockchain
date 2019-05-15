package com.seasun.management.mapper;

import com.seasun.management.model.UserDynamicPassword;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface UserDynamicPasswordMapper {
    @Delete({
            "delete from user_dynamic_password",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into user_dynamic_password (login_id, dynamic_password, ",
            "update_time, retry_cnt)",
            "values (#{loginId,jdbcType=VARCHAR}, #{dynamicPassword,jdbcType=VARCHAR}, ",
            "#{updateTime,jdbcType=TIMESTAMP}, #{retryCnt,jdbcType=INTEGER})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(UserDynamicPassword record);

    int insertSelective(UserDynamicPassword record);

    @Select({
            "select",
            "id, login_id, dynamic_password, update_time, retry_cnt",
            "from user_dynamic_password",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserDynamicPasswordMapper.BaseResultMap")
    UserDynamicPassword selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserDynamicPassword record);

    @Update({
            "update user_dynamic_password",
            "set login_id = #{loginId,jdbcType=VARCHAR},",
            "dynamic_password = #{dynamicPassword,jdbcType=VARCHAR},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP},",
            "retry_cnt = #{retryCnt,jdbcType=INTEGER}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserDynamicPassword record);

    /* the flowing are user defined ... */
    @Select({
            "select",
            "id, login_id, dynamic_password, update_time, retry_cnt",
            "from user_dynamic_password",
            "where login_id = #{loginId}"
    })
    UserDynamicPassword selectByLoginId(String loginId);
}