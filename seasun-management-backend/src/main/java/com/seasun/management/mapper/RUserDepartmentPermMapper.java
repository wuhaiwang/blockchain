package com.seasun.management.mapper;

import com.seasun.management.model.RUserDepartmentPerm;
import com.seasun.management.vo.RUserDepartmentPermVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface RUserDepartmentPermMapper {
    @Delete({
            "delete from r_user_department_perm",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into r_user_department_perm (user_id, department_id)",
            "values (#{userId,jdbcType=BIGINT}, #{departmentId,jdbcType=BIGINT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(RUserDepartmentPerm record);

    int insertSelective(RUserDepartmentPerm record);

    @Select({
            "select",
            "id, user_id, department_id",
            "from r_user_department_perm",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.RUserDepartmentPermMapper.BaseResultMap")
    RUserDepartmentPerm selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RUserDepartmentPerm record);

    @Update({
            "update r_user_department_perm",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "department_id = #{departmentId,jdbcType=BIGINT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(RUserDepartmentPerm record);

    /* the flowing are user defined ... */
    @Select({"select r.*, d.name departmentName from r_user_department_perm r",
            "left join department d on r.department_id = d.id",
            "where r.user_id = #{userId, jdbcType=BIGINT}"})
    List<RUserDepartmentPermVo> selectByUserId(Long userId);

    @Select({"select * from r_user_department_perm where department_id=#{departmentId}"})
    List<RUserDepartmentPerm> selectByDepartmentId(Long departmentId);

    void deleteByDepartmentIdAndUserIdsNotIn(Map<String, Object> params);

    @Delete("delete from r_user_department_perm where department_id=#{departmentId}")
    void deleteByDepartmentId(Long departmentId);

    int batchInsert(List<RUserDepartmentPerm> rUserDepartmentPermList);
}