package com.seasun.management.mapper;

import com.seasun.management.model.UserToken;
import org.apache.ibatis.annotations.*;

public interface UserTokenMapper {
    @Delete({
            "delete from user_token",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into user_token (user_id, user_name, ",
            "token, latest_login_time, ",
            "type)",
            "values (#{userId,jdbcType=BIGINT}, #{userName,jdbcType=VARCHAR}, ",
            "#{token,jdbcType=VARCHAR}, #{latestLoginTime,jdbcType=TIMESTAMP}, ",
            "#{type,jdbcType=INTEGER})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(UserToken record);

    int insertSelective(UserToken record);

    @Select({
            "select",
            "id, user_id, user_name, token, latest_login_time, type",
            "from user_token",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserTokenMapper.BaseResultMap")
    UserToken selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserToken record);

    @Update({
            "update user_token",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "user_name = #{userName,jdbcType=VARCHAR},",
            "token = #{token,jdbcType=VARCHAR},",
            "latest_login_time = #{latestLoginTime,jdbcType=TIMESTAMP},",
            "type = #{type,jdbcType=INTEGER}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserToken record);

    /* the flowing are user defined ... */

    @Select("select * from user_token where token = #{token} and type = #{type} limit 1")
    UserToken selectByTokenAndType(@Param("token") String token, @Param("type") int type);

    @Select("select * from user_token where user_id = #{userId} and type = #{type}")
    UserToken selectByUserId(@Param("userId") Long userId, @Param("type") int type);

    @Delete({
            "delete from user_token",
            "where user_id = #{id,jdbcType=BIGINT}"
    })
    int deleteByUserId(Long id);

    @Select("select * from user_token where token = #{token} limit 1")
    UserToken selectByToken(@Param("token") String token);

}