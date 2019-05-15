package com.seasun.management.mapper;

import com.seasun.management.model.UserFavorite;
import com.seasun.management.model.UserWorkExperience;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserFavoriteMapper {
    @Delete({
            "delete from user_favorite",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into user_favorite (user_id, menu_id, ",
            "seq)",
            "values (#{userId,jdbcType=BIGINT}, #{menuId,jdbcType=BIGINT}, ",
            "#{seq,jdbcType=INTEGER})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(UserFavorite record);

    int insertSelective(UserFavorite record);

    @Select({
            "select",
            "id, user_id, menu_id, seq",
            "from user_favorite",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserFavoriteMapper.BaseResultMap")
    UserFavorite selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserFavorite record);

    @Update({
            "update user_favorite",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "menu_id = #{menuId,jdbcType=BIGINT},",
            "seq = #{seq,jdbcType=INTEGER}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserFavorite record);

    /* the flowing are user defined ... */
    @Insert({
            "insert into user_favorite (id, user_id, menu_id, ",
            "seq)",
            "values (#{id,jdbcType=BIGINT}, #{userId,jdbcType=BIGINT}, #{menuId,jdbcType=BIGINT}, ",
            "#{seq,jdbcType=INTEGER})"
    })
    int insertWithId(UserFavorite record);

    int batchInsert(List<UserFavorite> userFavorites);

    @Delete({
            "delete from user_favorite",
            "where user_id = #{userId,jdbcType=BIGINT}"
    })
    int deleteByUserId(Long userId);

    @Select("select * from user_favorite where user_id = #{param1} and menu_id = #{param2}")
    UserFavorite selectByUserIdAndMenuId(Long userId, Long menuId);

}