package com.seasun.management.mapper;

import com.seasun.management.dto.*;
import com.seasun.management.model.UserPsychologicalConsultation;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserPsychologicalConsultationMapper {
    @Delete({
            "delete from user_psychological_consultation",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into user_psychological_consultation (user_id, year, ",
            "month, employee_no, ",
            "password)",
            "values (#{userId,jdbcType=BIGINT}, #{year,jdbcType=INTEGER}, ",
            "#{month,jdbcType=INTEGER}, #{employeeNo,jdbcType=BIGINT}, ",
            "#{password,jdbcType=INTEGER})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(UserPsychologicalConsultation record);

    int insertSelective(UserPsychologicalConsultation record);

    @Select({
            "select",
            "id, user_id, year, month, employee_no, password",
            "from user_psychological_consultation",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserPsychologicalConsultationMapper.BaseResultMap")
    UserPsychologicalConsultation selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserPsychologicalConsultation record);

    @Update({
            "update user_psychological_consultation",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "year = #{year,jdbcType=INTEGER},",
            "month = #{month,jdbcType=INTEGER},",
            "employee_no = #{employeeNo,jdbcType=BIGINT},",
            "password = #{password,jdbcType=INTEGER}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserPsychologicalConsultation record);

    /* the flowing are user defined ... */
    int batchInsert(@Param("list") List<UserPsychologicalConsultation> inserts);

    @Select({"select password  from user_psychological_consultation where user_id=#{0} and year = #{1} and month = #{2}"})
    Integer selectPasswordByConditon(Long userId, Integer year, Integer month);

    @Select("select year,month,employee_no,password from user_psychological_consultation where year=#{0} and month=#{1}")
    List<UserPsychologicalConsultation> selectExportConditonByYearAndMonth(Integer year, Integer month);

    @Select({"select p.*,u.login_id,concat(u.last_name,u.first_name) userName  from user_psychological_consultation p left join user u on u.id =p.user_id where p.year=#{0} and p.month=#{1} and (concat(u.last_name,u.first_name) like  concat('%',#{4},'%') or u.login_id like concat('%',#{4},'%')) order by employee_no limit #{2},#{3}"})
    List<UserPsychologicalConsultationDto> selectPageBySearchCondition( Integer year, Integer month,  Integer beginNumber, Integer pageSize, String searchKey);

    @Select({"SELECT year,month,count(1) totalCount FROM user_psychological_consultation GROUP BY year,month ORDER BY year desc,month desc LIMIT 1"})
    BaseTotalDto selectLastCreateYearAndMonthAndCount();

    @Select({"select year,month ,count(1) totalCount from user_psychological_consultation p left join user u on p.user_id=u.id where concat(u.last_name,u.first_name) like concat('%',#{0},'%') or u.login_id like concat('%',#{0},'%') group by year,month order by year desc,month desc limit 1"})
    BaseTotalDto selectLastCreateYearAndMonthAndCountBySearchKey(String searchKey);
}