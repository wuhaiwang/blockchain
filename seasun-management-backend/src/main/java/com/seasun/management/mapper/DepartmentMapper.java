package com.seasun.management.mapper;

import com.seasun.management.model.Department;
import com.seasun.management.model.Project;
import com.seasun.management.model.School;
import com.seasun.management.vo.DepartmentVo;
import com.seasun.management.vo.ProjectVo;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

public interface DepartmentMapper {

    @Delete({
            "delete from department",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into department (name, assistant_id, ",
            "city, description, ",
            "active_flag, create_time, ",
            "update_time)",
            "values (#{name,jdbcType=VARCHAR}, #{assistantId,jdbcType=BIGINT}, ",
            "#{city,jdbcType=VARCHAR}, #{description,jdbcType=VARCHAR}, ",
            "#{activeFlag,jdbcType=BIT}, #{createTime,jdbcType=TIMESTAMP}, ",
            "#{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(Department record);

    int insertSelective(Department record);

    @Select({
            "select",
            "id, name, assistant_id, city, description, active_flag, create_time, update_time",
            "from department",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.DepartmentMapper.BaseResultMap")
    Department selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Department record);

    @Update({
            "update department",
            "set name = #{name,jdbcType=VARCHAR},",
            "assistant_id = #{assistantId,jdbcType=BIGINT},",
            "city = #{city,jdbcType=VARCHAR},",
            "description = #{description,jdbcType=VARCHAR},",
            "active_flag = #{activeFlag,jdbcType=BIT},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(Department record);


    /* the flowing are user defined ... */
    int insertSelectiveWithId(Department record);

    @Select({"SELECT a.*, b.codes costCenterCodesStr, c.userIds managerIds",
            "FROM department a",
            "left join (SELECT department_id, GROUP_CONCAT(CODE) codes FROM cost_center GROUP BY department_id) b on a.id = b.department_id",
            "LEFT JOIN (SELECT department_id, GROUP_CONCAT(user_id) userIds FROM r_user_department_perm GROUP BY department_id) c ON a.id=c.department_id"})
    List<DepartmentVo> selectAllWithCostCodesStr();

    @Select({"SELECT a.*, b.codes costCenterCodesStr, c.userIds managerIds",
            "FROM department a",
            "LEFT JOIN (SELECT department_id, GROUP_CONCAT(CODE) codes FROM cost_center GROUP BY department_id) b ON a.id=b.department_id",
            "LEFT JOIN (SELECT department_id, GROUP_CONCAT(user_id) userIds FROM r_user_department_perm GROUP BY department_id) c ON a.id=c.department_id",
            "WHERE a.id=#{id}"})
    DepartmentVo selectWithCostCodesStrByPrimaryKey(Long id);

    @Update("update department set name=#{param2} where id=#{param1}")
    void updateNameById(long id, String name);

    @Update("update department set active_flag=0 where id=#{id}")
    int disableByPrimaryKey(Long id);

    @Select("SELECT * from department")
    @Results(
            {
                    @Result(property = "id", column = "id"),
                    @Result(property = "costs", column = "id",
                            many = @Many(select = "com.seasun.management.mapper.CostCenterMapper.selectByDepartment")),
            })
    List<Department> selectAllWithRelation();

    @SelectProvider(type = DepartmentSqlBuilder.class, method = "buildSelectByCondition")
    List<Department> selectByCondition(Department department);

    @Select({"update department set active_flag = 1 where id =#{0}"})
    void activeByPrimaryKey(Long departmentId);

    class DepartmentSqlBuilder {
        public String buildSelectByCondition(Department department) {
            return new SQL() {{
                SELECT("*");
                FROM("department");
                if (null != department.getId()) {
                    WHERE("id = #{id}");
                }
                if (null != department.getName()) {
                    WHERE("name = #{name}");
                }
            }}.toString();
        }
    }
}