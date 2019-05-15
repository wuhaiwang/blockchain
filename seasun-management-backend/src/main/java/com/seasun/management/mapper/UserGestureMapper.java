package com.seasun.management.mapper;

import com.seasun.management.model.UserGesture;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface UserGestureMapper {
    @Delete({
            "delete from user_gesture",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into user_gesture (user_id, gesture, ",
            "create_time)",
            "values (#{userId,jdbcType=BIGINT}, #{gesture,jdbcType=VARCHAR}, ",
            "#{createTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(UserGesture record);

    int insertSelective(UserGesture record);

    @Select({
            "select",
            "id, user_id, gesture, create_time",
            "from user_gesture",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserGestureMapper.BaseResultMap")
    UserGesture selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserGesture record);

    @Update({
            "update user_gesture",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "gesture = #{gesture,jdbcType=VARCHAR},",
            "create_time = #{createTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserGesture record);

    /* the flowing are user defined ... */
    @Select("select * from user_gesture where user_id = #{userId}")
    UserGesture selectByUserId(Long userId);
}