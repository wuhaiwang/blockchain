package com.seasun.management.mapper;

import com.seasun.management.dto.UserWorkExperienceDto;
import com.seasun.management.model.UserEduExperience;
import com.seasun.management.model.UserWorkExperience;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserWorkExperienceMapper {
    @Delete({
            "delete from user_work_experience",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into user_work_experience (user_id, begin_date, ",
            "end_date, city, company, ",
            "department, post, ",
            "responsibility, leave_reason)",
            "values (#{userId,jdbcType=BIGINT}, #{beginDate,jdbcType=DATE}, ",
            "#{endDate,jdbcType=DATE}, #{city,jdbcType=VARCHAR}, #{company,jdbcType=VARCHAR}, ",
            "#{department,jdbcType=VARCHAR}, #{post,jdbcType=VARCHAR}, ",
            "#{responsibility,jdbcType=VARCHAR}, #{leaveReason,jdbcType=VARCHAR})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(UserWorkExperience record);

    int insertSelective(UserWorkExperience record);

    @Select({
            "select",
            "id, user_id, begin_date, end_date, city, company, department, post, responsibility, ",
            "leave_reason",
            "from user_work_experience",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserWorkExperienceMapper.BaseResultMap")
    UserWorkExperience selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserWorkExperience record);

    @Update({
            "update user_work_experience",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "begin_date = #{beginDate,jdbcType=DATE},",
            "end_date = #{endDate,jdbcType=DATE},",
            "city = #{city,jdbcType=VARCHAR},",
            "company = #{company,jdbcType=VARCHAR},",
            "department = #{department,jdbcType=VARCHAR},",
            "post = #{post,jdbcType=VARCHAR},",
            "responsibility = #{responsibility,jdbcType=VARCHAR},",
            "leave_reason = #{leaveReason,jdbcType=VARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserWorkExperience record);


    /* the flowing are user defined ... */
    @Insert({
            "insert into user_work_experience (id, user_id, begin_date, ",
            "end_date, city, company, ",
            "department, post, ",
            "responsibility, leave_reason)",
            "values (#{id,jdbcType=BIGINT}, #{userId,jdbcType=BIGINT}, #{beginDate,jdbcType=DATE}, ",
            "#{endDate,jdbcType=DATE}, #{city,jdbcType=VARCHAR}, #{company,jdbcType=VARCHAR}, ",
            "#{department,jdbcType=VARCHAR}, #{post,jdbcType=VARCHAR}, ",
            "#{responsibility,jdbcType=VARCHAR}, #{leaveReason,jdbcType=VARCHAR})"
    })
    //@SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insertWithId(UserWorkExperience record);

    @Delete({
            "delete from user_work_experience",
            "where user_id = #{userId,jdbcType=BIGINT}"
    })
    int deleteByUserId(Long userId);

    int batchInsert(List<UserWorkExperience> userWorkExperiences);

    @Select({"select uw.* from user u left join order_center o on u.order_center_id=o.id left join project p on p.id =o.project_id left join work_group w on u.work_group_id=w.id left join user_work_experience uw on uw.user_id=u.id where u.active_flag=1 and u.virtual_flag=0 and p.city=#{0} and uw.id is not null"})
    List<UserWorkExperience> selectByCity(String city);

    @Select({
            "select * from user_work_experience where user_id = #{user_id}"
    })
    List<UserWorkExperienceDto> selectByUserId(@Param("user_id") Long userId);
}