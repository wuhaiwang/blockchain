package com.seasun.management.mapper;

import com.seasun.management.model.OrderErrorLog;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface OrderErrorLogMapper {
    @Delete({
        "delete from order_error_log",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into order_error_log (user_id, client_full_info, ",
        "response, error_message, ",
        "create_time)",
        "values (#{userId,jdbcType=BIGINT}, #{clientFullInfo,jdbcType=VARCHAR}, ",
        "#{response,jdbcType=VARCHAR}, #{errorMessage,jdbcType=VARCHAR}, ",
        "#{createTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(OrderErrorLog record);

    int insertSelective(OrderErrorLog record);

    @Select({
        "select",
        "id, user_id, client_full_info, response, error_message, create_time",
        "from order_error_log",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.OrderErrorLogMapper.BaseResultMap")
    OrderErrorLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderErrorLog record);

    @Update({
        "update order_error_log",
        "set user_id = #{userId,jdbcType=BIGINT},",
          "client_full_info = #{clientFullInfo,jdbcType=VARCHAR},",
          "response = #{response,jdbcType=VARCHAR},",
          "error_message = #{errorMessage,jdbcType=VARCHAR},",
          "create_time = #{createTime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(OrderErrorLog record);
}