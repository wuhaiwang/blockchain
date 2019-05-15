package com.seasun.management.mapper;

import com.seasun.management.model.UserMessage;
import com.seasun.management.vo.UserMessageCommuteDto;
import com.seasun.management.vo.UserMessageConditionVo;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

public interface UserMessageMapper {
    @Delete({
            "delete from user_message",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into user_message (sender, receiver, ",
            "type, location, ",
            "params, content, ",
            "version_id, read_flag, ",
            "create_time, read_time)",
            "values (#{sender,jdbcType=BIGINT}, #{receiver,jdbcType=BIGINT}, ",
            "#{type,jdbcType=VARCHAR}, #{location,jdbcType=VARCHAR}, ",
            "#{params,jdbcType=VARCHAR}, #{content,jdbcType=VARCHAR}, ",
            "#{versionId,jdbcType=BIGINT}, #{readFlag,jdbcType=BIT}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{readTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(UserMessage record);

    int insertSelective(UserMessage record);

    @Select({
            "select",
            "id, sender, receiver, type, location, params, content, version_id, read_flag, ",
            "create_time, read_time",
            "from user_message",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserMessageMapper.BaseResultMap")
    UserMessage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserMessage record);

    @Update({
            "update user_message",
            "set sender = #{sender,jdbcType=BIGINT},",
            "receiver = #{receiver,jdbcType=BIGINT},",
            "type = #{type,jdbcType=VARCHAR},",
            "location = #{location,jdbcType=VARCHAR},",
            "params = #{params,jdbcType=VARCHAR},",
            "content = #{content,jdbcType=VARCHAR},",
            "version_id = #{versionId,jdbcType=BIGINT},",
            "read_flag = #{readFlag,jdbcType=BIT},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "read_time = #{readTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserMessage record);

    /* the flowing are user defined ... */
    @Select({
            "select *",
            "from user_message",
            "where receiver = #{receiver}"
    })
    List<UserMessage> selectAllByReceiver(@Param("receiver") Long receiver);

    @Select({
            "select *",
            "from user_message",
            "where receiver = #{receiver} and read_flag = #{readFlag}"
    })
    List<UserMessage> selectAllByReceiverAndReadFlag(@Param("receiver") Long receiver, @Param("readFlag") Boolean readFlag);

    @Select({
            "select *",
            "from user_message",
            "where receiver = #{receiver} and version_id > #{versionId}"
    })
    List<UserMessage> selectAllByReceiverAndVersionId(@Param("receiver") Long receiver, @Param("versionId") Long versionId);

    @Update({
            "update user_message set read_flag = 1, read_time = now() where read_flag = 0 and receiver = #{receiver} and location = #{location}"
    })
    int updateReadFlagByReceiverAndLocation(@Param("receiver") Long receiver, @Param("location") String location);

    @Update({
            "update user_message set read_flag = 1, read_time = now() where read_flag = 0 and id = #{id}"
    })
    int updateReadFlagById(Long id);

    int batchInsertSelective(List<UserMessage> list);

    List<UserMessageCommuteDto> selectByCondition(UserMessageConditionVo vo);

    @Select({
            "select *",
            "from user_message",
            "where receiver = #{receiver} order by version_id desc limit 1"
    })
    UserMessage selectLatestRecordByReceiver(@Param("receiver") Long receiver);

    double selectCountByCondition(UserMessageConditionVo vo);

    @Select({"select count(1) from user_message where content=#{0}"})
    int checkDailyMessage(String content);

    void updateReadFlagByIds(List<Long> ids);

    @Update({"update user_message set read_flag=1 , read_time=now() where receiver =#{0} and read_flag=0"})
    void setUserAllMessageReadFlag(Long userId);

    @Select({"select count(1) from user_message where content like #{0} or content like #{1} or content like #{2}"})
    Integer selectPerfNotifyCountByYearAndMonth(String notifyGroupSubmitStr, String notifySubmitUserPerfStr, String notifyWriteUserPerfStr);
}