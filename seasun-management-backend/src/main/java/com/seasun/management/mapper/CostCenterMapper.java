package com.seasun.management.mapper;

import com.seasun.management.model.CostCenter;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface CostCenterMapper {
    @Delete({
            "delete from cost_center",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into cost_center (name, code, ",
            "department_id)",
            "values (#{name,jdbcType=VARCHAR}, #{code,jdbcType=VARCHAR}, ",
            "#{departmentId,jdbcType=BIGINT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(CostCenter record);

    int insertSelective(CostCenter record);

    @Select({
            "select",
            "id, name, code, department_id",
            "from cost_center",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.CostCenterMapper.BaseResultMap")
    CostCenter selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CostCenter record);

    @Update({
            "update cost_center",
            "set name = #{name,jdbcType=VARCHAR},",
            "code = #{code,jdbcType=VARCHAR},",
            "department_id = #{departmentId,jdbcType=BIGINT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(CostCenter record);

    /* the flowing are user defined ... */
    @Select("select * from cost_center where code = #{code}")
    CostCenter selectByCode(String code);

    @Select("select * from cost_center where department_id = #{departmentId}")
    List<CostCenter> selectByDepartment(Long departmentId);

    @Select("select * from cost_center")
    List<CostCenter> selectAll();

    int removeByDepartmentIdAndCodesNotIn(Map<String, Object> params);

    int updateDepartmentByCodesIn(Map<String, Object> params);

    @Update({"update cost_center set department_id=null where department_id=#{departmentId}"})
    int removeByDepartmentId(Long departmentId);

    int batchInsert(List<CostCenter> costCenterList);
}