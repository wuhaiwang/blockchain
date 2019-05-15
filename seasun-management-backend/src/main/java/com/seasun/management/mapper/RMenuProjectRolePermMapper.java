package com.seasun.management.mapper;

import com.seasun.management.dto.RMenuProjectRolePermDto;
import com.seasun.management.dto.RMenuProjectRolePermUsersDto;
import com.seasun.management.model.RMenuProjectRolePerm;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface RMenuProjectRolePermMapper {
    @Delete({
            "delete from r_menu_project_role_perm",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into r_menu_project_role_perm (menu_id, project_role_id)",
            "values (#{menuId,jdbcType=BIGINT}, #{projectRoleId,jdbcType=BIGINT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(RMenuProjectRolePerm record);


    int insertBatch (@Param("project_role_id") Long projectRoleId ,@Param("menu_ids") List<Long> menuIds);


    @Delete({
            "delete from r_menu_project_role_perm where project_role_id = #{project_role_id} and menu_id = #{menu_id};"
    })
    int deleteByMenuIdAndProjectRoleId (@Param("project_role_id") Long projectRoleId,@Param("menu_id") Long menuId);

    int insertSelective(RMenuProjectRolePerm record);

    @Select({
            "select",
            "id, menu_id, project_role_id",
            "from r_menu_project_role_perm",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.RMenuProjectRolePermMapper.BaseResultMap")
    RMenuProjectRolePerm selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RMenuProjectRolePerm record);

    @Update({
            "update r_menu_project_role_perm",
            "set menu_id = #{menuId,jdbcType=BIGINT},",
            "project_role_id = #{projectRoleId,jdbcType=BIGINT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(RMenuProjectRolePerm record);

    /* the flowing are user defined ... */
    @Select({
            "select r.project_role_id from r_menu_project_role_perm r",
            "left join menu m on m.id = r.menu_id",
            "where m.key = #{key} and m.type = #{type}"
    })
    List<Long> selectAllRoleIdByMenuKey(@Param("key") String key, @Param("type") String type);

    /**
     *  查看 对应 userId 的 menu 菜单
     * @param type pc 或 mobile
     * @param userId userId
     * @return 对应的 menu 集合
     */
    @Select({
            "select r.*, m.key, m.type, m.module from r_menu_project_role_perm r",
            "left join menu m on m.id = r.menu_id",
            "where m.type = #{type} and r.project_role_id IN ",
            "(select project_role_id from r_user_project_perm where user_id = #{userId}" ,
            "AND project_role_id NOT IN (SELECT id FROM project_role WHERE active_flag = 0))"
    })
    List<RMenuProjectRolePermDto> selectAllByUserId(@Param("type") String type, @Param("userId") Long userId);

    @Select({
            "select * from r_menu_project_role_perm rp left join menu m on rp.menu_id = m.id where rp.project_role_id = #{project_role_id} ;"
    })
    List<RMenuProjectRolePerm> selectMenusByProjectRoleId(@Param("project_role_id") Long projectRoleId);

    @Select({
            " select rp.id , rp.project_id, project.name as project_name, user.id as user_id, user.login_id, concat(user.last_name,user.first_name) as user_name, user.photo " ,
            "from r_user_project_perm rp left join project on project.id = rp.project_id  ",
            "join user on user.id = rp.user_id and project_role_id = #{project_role_id} ;"
    })
    List<RMenuProjectRolePermUsersDto> selectMenusUsersByProjectRoleId(@Param("project_role_id") Long projectRoleId);

}