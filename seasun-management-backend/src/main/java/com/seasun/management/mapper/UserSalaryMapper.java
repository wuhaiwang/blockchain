package com.seasun.management.mapper;

import com.seasun.management.model.FnStat;
import com.seasun.management.model.School;
import com.seasun.management.model.UserSalary;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@CacheConfig(cacheNames = "userSalary")
public interface UserSalaryMapper {
    @Delete({
            "delete from user_salary",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into user_salary (user_id, salary, ",
            "last_salary_change_date, last_salary_change_amount, ",
            "create_time)",
            "values (#{userId,jdbcType=BIGINT}, #{salary,jdbcType=VARCHAR}, ",
            "#{lastSalaryChangeDate,jdbcType=VARCHAR}, #{lastSalaryChangeAmount,jdbcType=VARCHAR}, ",
            "#{createTime,jdbcType=DATE})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(UserSalary record);

    int insertSelective(UserSalary record);

    @Select({
            "select",
            "id, user_id, salary, last_salary_change_date, last_salary_change_amount, create_time",
            "from user_salary",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserSalaryMapper.BaseResultMap")
    UserSalary selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserSalary record);

    @Update({
            "update user_salary",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "salary = #{salary,jdbcType=VARCHAR},",
            "last_salary_change_date = #{lastSalaryChangeDate,jdbcType=VARCHAR},",
            "last_salary_change_amount = #{lastSalaryChangeAmount,jdbcType=VARCHAR},",
            "create_time = #{createTime,jdbcType=DATE}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserSalary record);

    /* the flowing are user defined ... */

    int batchInsert(List<UserSalary> userSalaries);

    @Delete("delete from user_salary")
    void deleteAll();

    @CacheEvict(value = "userSalary", allEntries = true)
    @Select("select 1")
    School reloadCache();

    @Cacheable
    @Select("select * from user_salary")
    List<UserSalary> selectAll();

}
