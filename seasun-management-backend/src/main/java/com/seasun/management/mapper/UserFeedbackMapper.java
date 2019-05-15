package com.seasun.management.mapper;

import com.seasun.management.dto.UserFeedbackDto;
import com.seasun.management.model.UserFeedback;
import com.seasun.management.vo.UserFeedbackQueryConditionVo;
import com.seasun.management.vo.UserFeedbackVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserFeedbackMapper {
    @Delete({
            "delete from user_feedback",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into user_feedback (user_id, content, ",
            "channel, create_time)",
            "values (#{userId,jdbcType=BIGINT}, #{content,jdbcType=VARCHAR}, ",
            "#{channel,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(UserFeedback record);

    int insertSelective(UserFeedback record);

    @Select({
            "select",
            "id, user_id, content, channel, create_time",
            "from user_feedback",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserFeedbackMapper.BaseResultMap")
    UserFeedback selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserFeedback record);

    @Update({
            "update user_feedback",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "content = #{content,jdbcType=VARCHAR},",
            "channel = #{channel,jdbcType=VARCHAR},",
            "create_time = #{createTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserFeedback record);

    /* the flowing are user defined ... */

    @Select({
            "select f.*, concat(u.last_name,u.first_name) user_name",
            "from user_feedback f",
            "left join user u on u.id = f.user_id"
    })
    List<UserFeedbackVo> selectAllWithUserName();

    List<UserFeedbackDto> selectByCondition(UserFeedbackQueryConditionVo condition);

    double selectCountByCondition(UserFeedbackQueryConditionVo condition);
}