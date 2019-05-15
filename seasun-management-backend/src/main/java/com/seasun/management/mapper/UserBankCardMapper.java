package com.seasun.management.mapper;

import com.seasun.management.model.UserBankCard;
import com.seasun.management.model.UserCertification;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserBankCardMapper {
    @Delete({
            "delete from user_bank_card",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into user_bank_card (user_id, card_bin, ",
            "change_type, active_flag, ",
            "create_time, update_time)",
            "values (#{userId,jdbcType=BIGINT}, #{cardBin,jdbcType=VARCHAR}, ",
            "#{changeType,jdbcType=INTEGER}, #{activeFlag,jdbcType=BIT}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(UserBankCard record);

    int insertSelective(UserBankCard record);

    @Select({
            "select",
            "id, user_id, card_bin, change_type, active_flag, create_time, update_time",
            "from user_bank_card",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserBankCardMapper.BaseResultMap")
    UserBankCard selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserBankCard record);

    @Update({
            "update user_bank_card",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "card_bin = #{cardBin,jdbcType=VARCHAR},",
            "change_type = #{changeType,jdbcType=INTEGER},",
            "active_flag = #{activeFlag,jdbcType=BIT},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserBankCard record);


    /* the flowing are user defined ... */
    @Insert({
            "insert into user_bank_card (id, user_id, card_bin, ",
            "change_type, active_flag, ",
            "create_time, update_time)",
            "values (#{id,jdbcType=BIGINT}, #{userId,jdbcType=BIGINT}, #{cardBin,jdbcType=VARCHAR}, ",
            "#{changeType,jdbcType=INTEGER}, #{activeFlag,jdbcType=BIT}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})"
    })
    int insertWithId(UserBankCard record);

    int batchInsert(List<UserBankCard> userBankCards);

    @Delete({
            "delete from user_bank_card",
            "where user_id = #{userId,jdbcType=BIGINT}"
    })
    int deleteByUserId(Long userId);

    int setActiveFalseByUserId(Long userId);
}