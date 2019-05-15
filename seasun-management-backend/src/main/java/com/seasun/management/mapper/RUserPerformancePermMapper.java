package com.seasun.management.mapper;

import com.seasun.management.model.RUserPerformancePerm;
import com.seasun.management.vo.PerformanceObserverVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.RUserPerformancePerm;
import com.seasun.management.model.RUserProjectPerm;
import com.seasun.management.model.UserIdMinVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface RUserPerformancePermMapper {
    @Delete({
        "delete from r_user_performance_perm",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into r_user_performance_perm (user_id, performance_work_group_id, ",
        "performance_work_group_role_id)",
        "values (#{userId,jdbcType=BIGINT}, #{performanceWorkGroupId,jdbcType=BIGINT}, ",
        "#{performanceWorkGroupRoleId,jdbcType=BIGINT})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(RUserPerformancePerm record);

    int insertSelective(RUserPerformancePerm record);

    @Select({
        "select",
        "id, user_id, performance_work_group_id, performance_work_group_role_id",
        "from r_user_performance_perm",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.RUserPerformancePermMapper.BaseResultMap")
    RUserPerformancePerm selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RUserPerformancePerm record);

    @Update({
        "update r_user_performance_perm",
        "set user_id = #{userId,jdbcType=BIGINT},",
          "performance_work_group_id = #{performanceWorkGroupId,jdbcType=BIGINT},",
          "performance_work_group_role_id = #{performanceWorkGroupRoleId,jdbcType=BIGINT}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(RUserPerformancePerm record);

    /* the flowing are user defined ... */
    @Select({
        "select rupp.performance_work_group_Id, pwg.name performance_work_group_name, concat(u.last_name,u.first_name) performance_work_group_manage_name",
        "from r_user_performance_perm rupp",
        "left join performance_work_group pwg on pwg.id = rupp.performance_work_group_id",
        "left join performance_work_group_role pwgr on pwgr.id = rupp.performance_work_group_role_id",
        "left join user u on u.id = pwg.performance_manager_id",
        "where rupp.user_id = #{userId} and pwgr.role = '观察者'"
    })
    List<PerformanceObserverVo> selectAllByUserIdAndObserver(Long userId);

    List<RUserPerformancePerm> selectSelectiveByRole(RUserPerformancePerm perm);

    @Select({"SELECT r.id,r.performance_work_group_role_id ,user.id userId,CONCAT(user.last_name,user.first_name) name FROM r_user_performance_perm r " ,
            "LEFT JOIN `user` ON user.id = r.user_id " ,
            "WHERE performance_work_group_id = #{workGroupId}"})
    List<UserIdMinVo> selectPerformancePermsByGroupId(Long workGroupId);

}