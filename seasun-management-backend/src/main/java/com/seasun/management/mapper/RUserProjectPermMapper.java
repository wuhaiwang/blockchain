package com.seasun.management.mapper;

import com.seasun.management.dto.RUserProjectPermDto;
import com.seasun.management.model.RUserDepartmentPerm;
import com.seasun.management.model.RUserProjectPerm;
import com.seasun.management.vo.RUserProjectPermVo;
import com.seasun.management.vo.UserProjectPermVo;
import com.seasun.management.vo.UserSelectVo;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

public interface RUserProjectPermMapper {
    @Delete({
            "delete from r_user_project_perm",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into r_user_project_perm (user_id, project_id, ",
            "project_role_id)",
            "values (#{userId,jdbcType=BIGINT}, #{projectId,jdbcType=BIGINT}, ",
            "#{projectRoleId,jdbcType=BIGINT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(RUserProjectPerm record);

    int insertSelective(RUserProjectPerm record);

    @Select({
            "select",
            "id, user_id, project_id, project_role_id",
            "from r_user_project_perm",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.RUserProjectPermMapper.BaseResultMap")
    RUserProjectPerm selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RUserProjectPerm record);

    @Update({
            "update r_user_project_perm",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "project_id = #{projectId,jdbcType=BIGINT},",
            "project_role_id = #{projectRoleId,jdbcType=BIGINT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(RUserProjectPerm record);

    /* the flowing are user defined ... */

    @Select("select r.*,p.name projectName from r_user_project_perm r left join project p on p.id=r.project_id where r.user_id = #{0} and r.project_role_id = #{1}")
    List<RUserProjectPermVo> selectByUserIdAndProjectRoleId(Long userId, Long projectRoleId);

    @Select("SELECT\n" +
            "\tr.*, p. NAME projectName,\n" +
            "\tconcat(u.last_name, u.first_name) username,\n" +
            "\tpr. NAME roleName\n" +
            "FROM\n" +
            "\tr_user_project_perm r\n" +
            "LEFT JOIN USER u ON u.id = r.user_id\n" +
            "LEFT JOIN project_role pr ON pr.id = r.project_role_id\n" +
            "LEFT JOIN project p ON p.id = r.project_id\n" +
            "left JOIN user_detail on \n" +
            "WHERE\n" +
            "\tr.project_role_id = #{projectRoleId}")
    List<RUserProjectPermVo> selectByProjectRoleId(Long projectRoleId);

    int deleteSelective(RUserProjectPerm record);

    @Select({"select a.*,p.name as projectName,1 as managerFlag",
            "from  (select * from r_user_project_perm where user_id = #{userId}) a",
            "left join project p on a.project_id = p.id where p.active_flag = 1 and p.share_flag =1 and p.service_line='平台'"})
    List<UserProjectPermVo> selectUserSharePlatPermByUserId(Long userId);

    RUserProjectPerm selectSelective(RUserProjectPerm rUserProjectPerm);

    @Select({
            "select r.*, p.name projectName, l.name roleName, l.system_flag",
            "from r_user_project_perm r",
            "left join project_role l on r.project_role_id = l.id",
            "left join project p on r.project_id = p.id",
            "where r.user_id=#{userId,jdbcType=BIGINT} AND l.active_flag= 1 order by project_role_id asc"
    })
    List<RUserProjectPermVo> selectByUserIdAndOrderByProjectRoleIdAsc(Long userId);

    /**
     * 查看 用户所拥有的 项目的所有身份　项目要是激活，并且有对应的 app_show_mode
     *
     * @param userId
     * @return
     */
    @Select({
            "select r.*, l.name role_name, p.status project_status, p.service_line,p.app_show_mode ",
            "from r_user_project_perm r",
            "left join project_role l on r.project_role_id = l.id",
            "left join project p on r.project_id = p.id",
            "where r.user_id = #{userId} and (r.project_id is null or (p.active_flag = 1 and p.app_show_mode is not null and p.app_show_mode <> 0)) and l.system_flag = 1"
    })
    List<RUserProjectPermDto> selectAppPermByUserId(Long userId);

    @Select({
            "select r.*",
            "from r_user_project_perm r",
            "left join project_role l on r.project_role_id = l.id",
            "where user_id = #{userId} and project_id = #{projectId} and l.system_flag = 0",
            "limit 1"
    })
    RUserProjectPerm selectByUserIdAndProjectId(@Param("userId") Long userId, @Param("projectId") Long projectId);

    @Select({
            "select r.*",
            "from r_user_project_perm r",
            "left join project_role l on r.project_role_id = l.id",
            "where r.project_role_id = #{projectRoleId} and r.project_id = #{projectId} and l.system_flag = 1",
            "limit 1"
    })
    RUserProjectPerm selectAllSystemProjectRoleByProjectIdAndProjectRoleId(@Param("projectId") Long projectId, @Param("projectRoleId") Long projectRoleId);


    @Select({
            "select r.* from r_user_project_perm r where r.project_role_id = #{projectRoleId} and r.project_id = #{projectId}"
    })
    List<RUserProjectPerm> selectByProjectIdAndProjectRoleId(@Param("projectId") Long projectId, @Param("projectRoleId") Long projectRoleId);

    @Select({"select * from r_user_project_perm where project_id=#{projectId} and project_role_id=1"})
    List<RUserProjectPerm> selectManagersByProjectId(Long projectId);

    int updateSelective(RUserProjectPerm rUserProjectPerm);

    void deleteByProjectIdAndUserIdsNotIn(Map<String, Object> params);

    @Delete({"delete from r_user_project_perm where project_id=#{projectId}"})
    int deleteByProjectId(Long projectId);

    @Delete({"delete from r_user_project_perm where project_id=#{0} and project_role_id =#{1}"})
    int deleteByProjectIdAndProjectRoleId(Long projectId, Long projectRoleId);

    int batchInsert(List<RUserProjectPerm> rUserProjectPermList);

    @Delete("delete from r_user_project_perm where user_id = #{0} and project_role_id = 10")
    int deleteUserPerformanceMenu(Long userId);

    @Select({
            "select r.user_id, r.project_id, r.project_role_id, p.service_line",
            "from r_user_project_perm r",
            "left join project p on r.project_id = p.id",
            "where project_role_id in (100, 101, 102, 103) and (r.project_id is null or (p.app_show_mode is not null and p.app_show_mode <> 0))",
            "group by r.user_id, r.project_role_id"
    })
    List<RUserProjectPermDto> selectAllByAppRole();

    @Select({"SELECT r.*,p.name projectName FROM `r_user_project_perm` r left join project p on p.id = r.project_id where ((r.project_id = (SELECT plat_id FROM `cfg_plat_attr` where data_center_flag=1)) or (r.project_id is null)) and (r.project_role_id = #{0} or r.project_role_id =#{1})"})
    List<UserProjectPermVo> selectAppDailyProjectMessage(Long appManagerRoleId, Long appProjectRoleId);

    @Select({"select * from r_user_project_perm where user_id=#{0}"})
    List<RUserProjectPerm> selectByUserId(Long currentUserId);

    @Select({"select u.id as user_id,r.id as id,u.photo userPhoto,concat(u.last_name,u.first_name) name ,u.login_id from user u left join  r_user_project_perm r on r.user_id=u.id where r.project_role_id = #{1} and r.project_id=#{0}"})
    List<UserSelectVo> selectUserSelectVoByProjectIdAndRoleId(Long projectId, Long plat_share_manager_id);
}