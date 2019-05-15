package com.seasun.management.mapper;

import com.seasun.management.dto.UserCertificationDto;
import com.seasun.management.model.UserCertification;
import com.seasun.management.model.UserChildrenInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserCertificationMapper {
    @Delete({
            "delete from user_certification",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into user_certification (user_id, certification, ",
            "begin_date, end_date, organization)",
            "values (#{userId,jdbcType=BIGINT}, #{certification,jdbcType=VARCHAR}, ",
            "#{beginDate,jdbcType=DATE}, #{endDate,jdbcType=DATE}, #{organization,jdbcType=VARCHAR})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(UserCertification record);

    int insertSelective(UserCertification record);

    @Select({
            "select",
            "id, user_id, certification, begin_date, end_date, organization",
            "from user_certification",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserCertificationMapper.BaseResultMap")
    UserCertification selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserCertification record);

    @Update({
            "update user_certification",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "certification = #{certification,jdbcType=VARCHAR},",
            "begin_date = #{beginDate,jdbcType=DATE},",
            "end_date = #{endDate,jdbcType=DATE},",
            "organization = #{organization,jdbcType=VARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserCertification record);

    /* the flowing are user defined ... */
    @Insert({
            "insert into user_certification (id, user_id, certification, ",
            "begin_date, end_date, organization)",
            "values (#{id,jdbcType=BIGINT}, #{userId,jdbcType=BIGINT}, #{certification,jdbcType=VARCHAR}, ",
            "#{beginDate,jdbcType=DATE}, #{endDate,jdbcType=DATE}, #{organization,jdbcType=VARCHAR})"
    })
    int insertWithId(UserCertification record);

    int batchInsert(List<UserCertification> userCertifications);

    @Delete({
            "delete from user_certification",
            "where user_id = #{userId,jdbcType=BIGINT}"
    })
    int deleteByUserId(Long userId);

    @Select({
            "select * from user_certification where user_id = #{user_id}"
    })
    List<UserCertificationDto> selectByUserId(@Param("user_id") Long userId);
}