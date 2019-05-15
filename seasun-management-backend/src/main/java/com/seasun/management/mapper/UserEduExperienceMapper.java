package com.seasun.management.mapper;

import com.seasun.management.dto.UserEduExperienceDto;
import com.seasun.management.model.UserBankCard;
import com.seasun.management.model.UserEduExperience;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserEduExperienceMapper {
    @Delete({
            "delete from user_edu_experience",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into user_edu_experience (user_id, begin_date, ",
            "end_date, education_type, ",
            "certification, area, ",
            "school, college, ",
            "major)",
            "values (#{userId,jdbcType=BIGINT}, #{beginDate,jdbcType=DATE}, ",
            "#{endDate,jdbcType=DATE}, #{educationType,jdbcType=VARCHAR}, ",
            "#{certification,jdbcType=VARCHAR}, #{area,jdbcType=VARCHAR}, ",
            "#{school,jdbcType=VARCHAR}, #{college,jdbcType=VARCHAR}, ",
            "#{major,jdbcType=VARCHAR})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(UserEduExperience record);

    int insertSelective(UserEduExperience record);

    @Select({
            "select",
            "id, user_id, begin_date, end_date, education_type, certification, area, school, ",
            "college, major",
            "from user_edu_experience",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserEduExperienceMapper.BaseResultMap")
    UserEduExperience selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserEduExperience record);

    @Update({
            "update user_edu_experience",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "begin_date = #{beginDate,jdbcType=DATE},",
            "end_date = #{endDate,jdbcType=DATE},",
            "education_type = #{educationType,jdbcType=VARCHAR},",
            "certification = #{certification,jdbcType=VARCHAR},",
            "area = #{area,jdbcType=VARCHAR},",
            "school = #{school,jdbcType=VARCHAR},",
            "college = #{college,jdbcType=VARCHAR},",
            "major = #{major,jdbcType=VARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserEduExperience record);

    /* the flowing are user defined ... */
    @Insert({
            "insert into user_edu_experience (id, user_id, begin_date, ",
            "end_date, education_type, ",
            "certification, area, ",
            "school, college, ",
            "major)",
            "values (#{id,jdbcType=BIGINT}, #{userId,jdbcType=BIGINT}, #{beginDate,jdbcType=DATE}, ",
            "#{endDate,jdbcType=DATE}, #{educationType,jdbcType=VARCHAR}, ",
            "#{certification,jdbcType=VARCHAR}, #{area,jdbcType=VARCHAR}, ",
            "#{school,jdbcType=VARCHAR}, #{college,jdbcType=VARCHAR}, ",
            "#{major,jdbcType=VARCHAR})"
    })
    int insertWithId(UserEduExperience record);

    @Delete({
            "delete from user_edu_experience",
            "where user_id = #{userId,jdbcType=BIGINT}"
    })
    int deleteByUserId(Long userId);

    int batchInsert(List<UserEduExperience> userEduExperiences);

    @Select({
            "select * from user_edu_experience where user_id = #{userId}"
    })
    List<UserEduExperienceDto> selectByUserId(@Param("userId") Long userId);
}