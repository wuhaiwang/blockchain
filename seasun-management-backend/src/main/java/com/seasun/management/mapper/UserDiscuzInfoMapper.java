package com.seasun.management.mapper;

import com.seasun.management.model.UserDiscuzInfo;
import org.apache.ibatis.annotations.*;

public interface UserDiscuzInfoMapper {
    @Delete({
        "delete from user_discuz_info",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into user_discuz_info (login_id, ",
        "password, creation_time)",
        "values (#{loginId,jdbcType=VARCHAR}, ",
        "#{password,jdbcType=VARCHAR}, #{creationTime,jdbcType=DATE})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(UserDiscuzInfo record);

    int insertSelective(UserDiscuzInfo record);

    @Select({
        "select",
        "id, login_id, email, password, creation_time",
        "from user_discuz_info",
        "where id = #{id,jdbcType=BIGINT}"
    })
    UserDiscuzInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserDiscuzInfo record);

    @Update({
        "update user_discuz_info",
        "set login_id = #{loginId,jdbcType=VARCHAR},",
          "email = #{email,jdbcType=VARCHAR},",
          "password = #{password,jdbcType=VARCHAR},",
          "creation_time = #{creationTime,jdbcType=DATE}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserDiscuzInfo record);

    @Select({
            "select",
            "id, login_id, password",
            "from user_discuz_info",
            "where login_id = #{loginId,jdbcType=VARCHAR}"
    })
    UserDiscuzInfo selectByLoginId(String loginId);
}