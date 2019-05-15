package com.seasun.management.mapper;

import com.seasun.management.dto.RMenuPerformanceWorkGroupPermDto;
import com.seasun.management.dto.RMenuProjectRolePermDto;
import com.seasun.management.model.RMenuPerformanceWorkGroupPerm;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface RMenuPerformanceWorkGroupPermMapper {
    @Delete({
        "delete from r_menu_performance_work_group_perm",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into r_menu_performance_work_group_perm (performance_work_group_role_id, menu_id)",
        "values (#{performanceWorkGroupRoleId,jdbcType=BIGINT}, #{menuId,jdbcType=BIGINT})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(RMenuPerformanceWorkGroupPerm record);

    int insertSelective(RMenuPerformanceWorkGroupPerm record);

    @Select({
        "select",
        "id, performance_work_group_role_id, menu_id",
        "from r_menu_performance_work_group_perm",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.RMenuPerformanceWorkGroupPermMapper.BaseResultMap")
    RMenuPerformanceWorkGroupPerm selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RMenuPerformanceWorkGroupPerm record);

    @Update({
        "update r_menu_performance_work_group_perm",
        "set performance_work_group_role_id = #{performanceWorkGroupRoleId,jdbcType=BIGINT},",
          "menu_id = #{menuId,jdbcType=BIGINT}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(RMenuPerformanceWorkGroupPerm record);

    @Select({"SELECT r.*, menu.`key`, menu.type, menu.module FROM r_menu_performance_work_group_perm r LEFT JOIN menu ON menu.id = r.menu_id " ,
            "WHERE performance_work_group_role_id = #{roleId}"})
    List<RMenuPerformanceWorkGroupPermDto> selectAllRMenuPermByRoleId(Long roleId);


    List<RMenuProjectRolePermDto> selectAllPerformanceDataPerm(@Param("userId") Long userId, @Param("list")List<Long> roleIds);
}