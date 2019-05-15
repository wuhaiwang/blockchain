package com.seasun.management.mapper;

import com.seasun.management.model.UserContactGroup;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface UserContactGroupMapper {
    @Delete({
        "delete from user_contact_group",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into user_contact_group (name, user_id)",
        "values (#{name,jdbcType=VARCHAR}, #{userId,jdbcType=BIGINT})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(UserContactGroup record);

    int insertSelective(UserContactGroup record);

    @Select({
        "select",
        "id, name, user_id",
        "from user_contact_group",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserContactGroupMapper.BaseResultMap")
    UserContactGroup selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserContactGroup record);

    @Update({
        "update user_contact_group",
        "set name = #{name,jdbcType=VARCHAR},",
          "user_id = #{userId,jdbcType=BIGINT}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserContactGroup record);

    /* the flowing are user defined ... */

    @Select("select * from user_contact_group where user_id =#{0} order by id")
    List<UserContactGroup> selectByUserId(Long userId);

    @Select({"select * from user_contact_group where id in (select distinct user_contact_group_id from r_user_contact_group where user_id =#{0} )"})
    List<UserContactGroup> selectInContactGroupsByUserId(Long userId);
}