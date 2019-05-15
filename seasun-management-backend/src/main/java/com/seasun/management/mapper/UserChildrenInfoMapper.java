package com.seasun.management.mapper;

import com.seasun.management.dto.UserChildrenInfoDto;
import com.seasun.management.model.UserChildrenInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserChildrenInfoMapper {
    @Delete({
            "delete from user_children_info",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into user_children_info (user_id, name, ",
            "gender, birthday, school)",
            "values (#{userId,jdbcType=BIGINT}, #{name,jdbcType=VARCHAR}, ",
            "#{gender,jdbcType=BIT}, #{birthday,jdbcType=DATE}, #{school,jdbcType=VARCHAR})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(UserChildrenInfo record);

    int insertSelective(UserChildrenInfo record);

    @Select({
            "select",
            "id, user_id, name, gender, birthday, school",
            "from user_children_info",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserChildrenInfoMapper.BaseResultMap")
    UserChildrenInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserChildrenInfo record);

    @Update({
            "update user_children_info",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "name = #{name,jdbcType=VARCHAR},",
            "gender = #{gender,jdbcType=BIT},",
            "birthday = #{birthday,jdbcType=DATE},",
            "school = #{school,jdbcType=VARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserChildrenInfo record);

    /* the flowing are user defined ... */
    UserChildrenInfo selectByUserIdAndName(UserChildrenInfo record);

    int updateByUserIdAndNameSelective(UserChildrenInfo record);

    @Delete({
            "delete from user_children_info",
            "where user_id = #{userId,jdbcType=BIGINT}"
    })
    int deleteByUserId(Long userId);

    int batchInsert(List<UserChildrenInfo> userChildrenInfos);

    @Select({
            "select * from user_children_info where user_id = #{user_id};"
    })
    List<UserChildrenInfoDto> selectByUserId(@Param("user_id") Long userId);

}