package com.seasun.management.mapper;

import com.seasun.management.model.RUserWorkGroupPerm;
import com.seasun.management.vo.RUserWorkGroupPermVo;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface RUserWorkGroupPermMapper {

    @Delete({
            "delete from r_user_work_group_perm",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into r_user_work_group_perm (user_id, work_group_id, ",
            "work_group_role_id)",
            "values (#{userId,jdbcType=BIGINT}, #{workGroupId,jdbcType=BIGINT}, ",
            "#{workGroupRoleId,jdbcType=BIGINT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(RUserWorkGroupPerm record);

    int insertSelective(RUserWorkGroupPerm record);

    @Select({
            "select",
            "id, user_id, work_group_id, work_group_role_id",
            "from r_user_work_group_perm",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.RUserWorkGroupPermMapper.BaseResultMap")
    RUserWorkGroupPerm selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RUserWorkGroupPerm record);

    @Update({
            "update r_user_work_group_perm",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "work_group_id = #{workGroupId,jdbcType=BIGINT},",
            "work_group_role_id = #{workGroupRoleId,jdbcType=BIGINT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(RUserWorkGroupPerm record);

    /* the flowing are user defined ... */
    @Select({
            "select",
            "*",
            "from r_user_work_group_perm",
            "where work_group_id = #{workGroupId,jdbcType=BIGINT}"
    })
    List<RUserWorkGroupPerm> selectByWorkGroupId(Long workGroupId);

    @Select({
            "select",
            "*",
            "from r_user_work_group_perm",
            "where user_id = #{userId} and work_group_id = #{workGroupId} and work_group_role_id = #{workGroupRoleId}",
            "limit 1"
    })
    RUserWorkGroupPerm selectByUserIdAndWorkGroupId(@Param("userId") Long userId, @Param("workGroupId") Long workGroupId, @Param("workGroupRoleId") Long workGroupRoleId);

    @Delete({
            "delete from r_user_work_group_perm",
            "where user_id = #{userId} and work_group_id = #{workGroupId} and work_group_role_id = #{workGroupRoleId}"
    })
    int deleteByUserIdAndWorkGroupIdAndWorkGroupRoleId(@Param("userId") Long userId, @Param("workGroupId") Long workGroupId, @Param("workGroupRoleId") Long workGroupRoleId);


    int deleteSelective(RUserWorkGroupPerm rUserWorkGroupPerm);

    @Select("select * from r_user_work_group_perm where user_id = #{userId} and work_group_role_id = #{workGroupRoleId}")
    List<RUserWorkGroupPerm> selectByUserIdAndWorkGroupRoleId(@Param("userId") Long userId, @Param("workGroupRoleId") Long workGroupRoleId);

    @Select("select r.*,u.login_id FROM r_user_work_group_perm r left join user u on r.user_id = u.id where work_group_role_id != 3")
    List<RUserWorkGroupPermVo> selectAllPerformanceSystemPerm();

    @Select("select r.*,u.login_id FROM r_user_work_group_perm r left join user u on r.user_id = u.id where work_group_role_id = #{roleId}")
    List<RUserWorkGroupPermVo> selectAllSystemPerm(Long roleId);


    @Select({
            "select",
            "*",
            "from r_user_work_group_perm",
            "where work_group_id = #{0} and work_group_role_id = #{1} limit 1"
    })
    RUserWorkGroupPerm selectByUserIdAndWorkGroupIdAndRoleId(Long workGroupId, Long roleId);


    @Delete("delete from r_user_work_group_perm where user_id = #{0} and work_group_role_id = 1")
    int deleteUserPerformancePerm(Long userId);
}