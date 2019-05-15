package com.seasun.management.mapper;

import com.seasun.management.dto.MenuDto;
import com.seasun.management.model.Menu;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface MenuMapper {
    @Delete({
            "delete from menu",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into menu (key, type, ",
            "remark)",
            "values (#{key,jdbcType=VARCHAR}, #{type,jdbcType=VARCHAR}, ",
            "#{remark,jdbcType=VARCHAR})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(Menu record);

    @Insert({
            "insert into menu (`key`,`type`,`module`,`remark`) values(#{key,jdbcType=VARCHAR}, #{type,jdbcType=VARCHAR}, #{module,jdbcType=VARCHAR}, #{remark,jdbcType=VARCHAR});"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insertOne (MenuDto record);

    int insertSelective(Menu record);

    @Select({
            "select",
            "id, key, type, remark",
            "from menu",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.MenuMapper.BaseResultMap")
    Menu selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Menu record);

    @Update({
            "update menu",
            "set key = #{key,jdbcType=VARCHAR},",
            "type = #{type,jdbcType=VARCHAR},",
            "remark = #{remark,jdbcType=VARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(Menu record);

    @Select({
            "select * from menu;"
    })
    List<MenuDto> selectAll ();

    @Select({
            "select * from menu where `key` = #{key} limit 1;"
    })
    Menu selectByKey(String key);


    @Select({
            "SELECT\n" +
                    "\tcount(*)\n" +
                    "FROM\n" +
                    "\tr_user_project_perm r\n" +
                    "\tJOIN project_role pr  ON pr.id = r.project_role_id\n" +
                    "\tJOIN r_menu_project_role_perm rp on rp.project_role_id = pr.id\n" +
                    "\tJOIN menu on menu.id = rp.menu_id\n" +
                    "\twhere r.user_id= #{userId} and menu.key = #{menuKey}"
    })
    int countUserMenuPermByMenuKey(@Param("menuKey") String menuKey, @Param("userId") Long userId);

}