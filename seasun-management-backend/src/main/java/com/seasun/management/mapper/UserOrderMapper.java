package com.seasun.management.mapper;

import com.seasun.management.dto.IdValueDto;
import com.seasun.management.model.UserOrder;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface UserOrderMapper {
    @Delete({
            "delete from user_order",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into user_order (user_id, product_id, ",
            "total_fee, local_trade_no, ",
            "prepay_id, pay_type, ",
            "client_full_info, status, ",
            "transaction_id, open_id, create_time, ",
            "callback_time, update_time, ",
            "update_comment, pay_time, ",
            "error_message)",
            "values (#{userId,jdbcType=BIGINT}, #{productId,jdbcType=BIGINT}, ",
            "#{totalFee,jdbcType=BIGINT}, #{localTradeNo,jdbcType=VARCHAR}, ",
            "#{prepayId,jdbcType=VARCHAR}, #{payType,jdbcType=VARCHAR}, ",
            "#{clientFullInfo,jdbcType=VARCHAR}, #{status,jdbcType=VARCHAR}, ",
            "#{transactionId,jdbcType=VARCHAR}, #{openId,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, ",
            "#{callbackTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP}, ",
            "#{updateComment,jdbcType=VARCHAR}, #{payTime,jdbcType=VARCHAR}, ",
            "#{errorMessage,jdbcType=VARCHAR})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(UserOrder record);

    int insertSelective(UserOrder record);

    @Select({
            "select",
            "id, user_id, product_id, total_fee, local_trade_no, prepay_id, pay_type, client_full_info, ",
            "status, transaction_id, open_id,create_time, callback_time, update_time, update_comment, ",
            "pay_time, error_message",
            "from user_order",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserOrderMapper.BaseResultMap")
    UserOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserOrder record);

    @Update({
            "update user_order",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "product_id = #{productId,jdbcType=BIGINT},",
            "total_fee = #{totalFee,jdbcType=BIGINT},",
            "local_trade_no = #{localTradeNo,jdbcType=VARCHAR},",
            "prepay_id = #{prepayId,jdbcType=VARCHAR},",
            "pay_type = #{payType,jdbcType=VARCHAR},",
            "client_full_info = #{clientFullInfo,jdbcType=VARCHAR},",
            "status = #{status,jdbcType=VARCHAR},",
            "transaction_id = #{transactionId,jdbcType=VARCHAR},",
            "open_id = #{openId,jdbcType=VARCHAR},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "callback_time = #{callbackTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP},",
            "update_comment = #{updateComment,jdbcType=VARCHAR},",
            "pay_time = #{payTime,jdbcType=TIMESTAMP},",
            "error_message = #{errorMessage,jdbcType=VARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserOrder record);


    /* the flowing are user defined ... */
    @Select({"select * from user_order where local_trade_no=#{0} limit 1"})
    UserOrder selectByLocalTradeNo(String localTradeNo);

    @Select({"select * from user_order where transaction_id=#{0} limit 1"})
    UserOrder selectByTransactionId(String transactionId);

    @Select({"select IFNULL(sum(total_fee),0) from user_order where product_id=#{0} and status ='" + UserOrder.Status.payment + "'"})
    Long selectTotalFeeByProductId(Long id);
}