package com.seasun.management.mapper;

import com.seasun.management.dto.FixRoleInfoDto;
import com.seasun.management.dto.FmUserRoleDto;
import com.seasun.management.model.FmUserRole;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.UserPerformance;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface FmUserRoleMapper {
    @Delete({
            "delete from fm_user_role",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into fm_user_role (user_id, role_id, ",
            "project_id, plat_id)",
            "values (#{userId,jdbcType=BIGINT}, #{roleId,jdbcType=BIGINT}, ",
            "#{projectId,jdbcType=BIGINT}, #{platId,jdbcType=BIGINT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FmUserRole record);

    int insertSelective(FmUserRole record);

    @Select({
            "select",
            "id, user_id, role_id, project_id, plat_id",
            "from fm_user_role",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FmUserRoleMapper.BaseResultMap")
    FmUserRole selectByPrimaryKey(Long id);

    int updateByPlatAndProject(FmUserRole record);

    @Update({
            "update fm_user_role",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "role_id = #{roleId,jdbcType=BIGINT},",
            "project_id = #{projectId,jdbcType=BIGINT},",
            "plat_id = #{platId,jdbcType=BIGINT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FmUserRole record);

    /* the flowing are user defined ... */

    @Select({"SELECT u.id ,u.plat_id,u.project_id,u.role_id,u.user_id ,r.`name` role_name ,p.name ,p.id p_id FROM fm_user_role u LEFT JOIN fm_role r ON r.id = u.role_id",
            "LEFT JOIN (SELECT id,name,service_line FROM project) p ON CASE WHEN p.service_line ='平台' THEN ",
            "u.plat_id= p.id  ELSE u.project_id = p.id END",
            "WHERE user_id = #{userId} AND role_id<>2"})
    List<FixRoleInfoDto> getUserIdentityInfo(Long userId);

    @Select({"SELECT u.id ,u.plat_id,u.project_id,u.role_id,u.user_id ,r.`name` role_name ,p.name ,p.id p_id FROM fm_user_role u LEFT JOIN fm_role r ON r.id = u.role_id",
            "LEFT JOIN (SELECT id,name,service_line FROM project) p ON CASE WHEN p.service_line ='平台' THEN ",
            "u.plat_id= p.id  ELSE u.project_id = p.id END",
            "WHERE role_id<>2 GROUP BY name"})
    List<FixRoleInfoDto> getAdminUserIdentityInfo();

    @Select({"SELECT f.*,CONCAT(`user`.last_name,`user`.first_name) user_name FROM fm_user_role f",
            "LEFT JOIN user ON f.user_id=`user`.id",
            "WHERE f.plat_id = #{platId} AND role_id = 2"})
    List<FixRoleInfoDto> selectAllUserRoleByPlatId(Long platId);

    @Select({"SELECT f.*,CONCAT(`user`.last_name,`user`.first_name) user_name FROM fm_user_role f",
            "LEFT JOIN user ON f.user_id=`user`.id",
            "WHERE f.project_id = #{projectId} AND role_id = 2"})
    List<FixRoleInfoDto> selectAllUserRoleByProjectId(Long projectId);

    @Select({
            "select ur.*, concat(u.last_name,u.first_name) user_name",
            "from fm_user_role ur",
            "left join user u on u.id = ur.user_id",
            "where ur.role_id = #{roleId}"
    })
    List<FmUserRoleDto> selectAllByRoleId(Long roleId);

    @Select({"select * from fm_user_role where role_id = #{0} and plat_id = #{1}"
    })
    List<FmUserRole> selectAllRecordsByRoleIdAndPlatId(Long roleId, Long platId);

    @Select({"SELECT * FROM fm_user_role WHERE user_id = #{userId} AND role_id <> 2"})
    List<FmUserRoleDto> selectManagerByUserId(Long userId);

    @Select({
            "select ur.*, concat(u.last_name,u.first_name) user_name",
            "from fm_user_role ur",
            "left join user u on u.id = ur.user_id",
            "where ur.role_id = #{roleId} and ur.plat_id = #{platId}"
    })
    List<FmUserRoleDto> selectAllByRoleIdAndPlatId(@Param("roleId") Long roleId, @Param("platId") Long platId);

    @Select({
            "SELECT f.user_id FROM fm_member fm",
            "LEFT JOIN fm_user_role f ON f.project_id = fm.project_id AND f.plat_id = fm.plat_id ",
            " WHERE fm.user_id =#{0} AND f.role_id =2"
    })
    FmUserRoleDto selectUserFMConfirmerId(Long userId);

    // 项目管理员
    List<FmUserRoleDto> selectManagerByMemberIds(@Param("list") List<UserPerformance> userIds, @Param("roleId") Long roleId);

    @Select({
            "select ur.*, concat(u.last_name,u.first_name) user_name, pl.name plat_name, p.name project_name",
            "from fm_user_role ur",
            "left join user u on u.id = ur.user_id",
            "left join project pl on pl.id = ur.plat_id",
            "left join project p on p.id = ur.project_id",
            "where ur.user_id = #{userId} and ur.role_id = #{roleId}"
    })
    List<FmUserRoleDto> selectAllByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Delete({"DELETE FROM fm_user_role WHERE role_id = 2 AND plat_id = #{platId}"})
    void deleteConfimerByPlatId(Long platId);

    void deleteSelective(FmUserRole role);

    @Select({"SELECT plat_id FROM fm_user_role WHERE project_id = #{projectId} UNION",
            "SELECT plat_id FROM fm_member WHERE project_id = #{projectId}"})
    List<Long> selectAllFixGroupPlatIdByProjectId(@Param("projectId") Long projectId);

    //查看项目负责人，包括老k
    @Select({"SELECT user_id FROM r_user_work_group_perm WHERE work_group_id in",
            "(SELECT id FROM work_group WHERE parent=267)",
            "UNION  ",
            "SELECT id FROM `user` WHERE login_id='guoweiwei'"})
    List<Long> selectAllProjectManagers();

    FmUserRole selectSelective(FmUserRole role);

    @Select("SELECT * FROM fm_user_role WHERE plat_id =#{platId} AND project_id = #{projectId}")
    FmUserRole selectUserRoleByPlatAndProject(@Param("platId") Long platId, @Param("projectId") Long projectId);

    @Select({"select * from fm_user_role where role_id=#{0}"})
    List<FmUserRole> selectByRoleId(Long roleId);

    @Select({"select p.id, p.name from project p left join fm_user_role f  on p.id=f.project_id where f.role_id=4 and f.user_id=#{0} and p.active_flag=1"})
    List<IdNameBaseObject> selectUserFixSecondProjectsByUserId(Long userId);
}