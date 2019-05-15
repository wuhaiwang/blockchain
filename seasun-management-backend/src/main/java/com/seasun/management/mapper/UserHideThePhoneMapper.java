package com.seasun.management.mapper;

import com.seasun.management.model.UserHideThePhone;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserHideThePhoneMapper {
    @Delete({
            "delete from user_hide_the_phone",
            "where user_id = #{userId,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long userId);

    @Insert({
            "insert into user_hide_the_phone (user_id)",
            "values (#{userId,jdbcType=BIGINT})"
    })
    int insert(UserHideThePhone record);

    int insertSelective(UserHideThePhone record);

    /* the flowing are user defined ... */
    @Select("select * from user_hide_the_phone")
    List<UserHideThePhone> selectAll();
}