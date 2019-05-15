package com.seasun.management.mapper;

import com.seasun.management.dto.UserNdaDto;
import com.seasun.management.model.UserFavorite;
import com.seasun.management.model.UserNda;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserNdaMapper {
    @Delete({
            "delete from user_nda",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into user_nda (user_id, nda_id, ",
            "operate_time, begin_date, ",
            "end_date, upload_file, ",
            "active_flag)",
            "values (#{userId,jdbcType=BIGINT}, #{ndaId,jdbcType=BIGINT}, ",
            "#{operateTime,jdbcType=TIMESTAMP}, #{beginDate,jdbcType=DATE}, ",
            "#{endDate,jdbcType=DATE}, #{uploadFile,jdbcType=VARCHAR}, ",
            "#{activeFlag,jdbcType=BIT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(UserNda record);

    int insertSelective(UserNda record);

    @Select({
            "select",
            "id, user_id, nda_id, operate_time, begin_date, end_date, upload_file, active_flag",
            "from user_nda",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserNdaMapper.BaseResultMap")
    UserNda selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserNda record);

    @Update({
            "update user_nda",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "nda_id = #{ndaId,jdbcType=BIGINT},",
            "operate_time = #{operateTime,jdbcType=TIMESTAMP},",
            "begin_date = #{beginDate,jdbcType=DATE},",
            "end_date = #{endDate,jdbcType=DATE},",
            "upload_file = #{uploadFile,jdbcType=VARCHAR},",
            "active_flag = #{activeFlag,jdbcType=BIT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserNda record);

    /* the flowing are user defined ... */
    UserNda selectByCondition(UserNda userNda);

    @Insert({
            "insert into user_nda (id, user_id, nda_id, ",
            "operate_time, begin_date, ",
            "end_date, upload_file, ",
            "active_flag)",
            "values (#{id,jdbcType=BIGINT}, #{userId,jdbcType=BIGINT}, #{ndaId,jdbcType=BIGINT}, ",
            "#{operateTime,jdbcType=TIMESTAMP}, #{beginDate,jdbcType=DATE}, ",
            "#{endDate,jdbcType=DATE}, #{uploadFile,jdbcType=VARCHAR}, ",
            "#{activeFlag,jdbcType=BIT})"
    })
    int insertWithId(UserNda record);

    @Delete({
            "delete from user_nda",
            "where user_id = #{userId,jdbcType=BIGINT}"
    })
    int deleteByUserId(Long userId);

    int batchInsert(List<UserNda> userNdas);

    @Select({
            "select",
            "nda.id, user_id, nda_id, operate_time, begin_date, end_date, upload_file, active_flag, nda.name",
            "from user_nda left join nda on nda.id = user_nda.nda_id",
            "where user_nda.user_id = #{id,jdbcType=BIGINT}"
    })
    List<UserNdaDto> selectByUserId(@Param("id") Long id);


}