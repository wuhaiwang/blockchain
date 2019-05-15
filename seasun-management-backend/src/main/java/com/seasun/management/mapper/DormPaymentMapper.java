package com.seasun.management.mapper;

import com.seasun.management.model.DormPayment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface DormPaymentMapper {
    @Delete({
            "delete from dorm_payment",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into dorm_payment (user_id, id_code, year, ",
            "month, pay_amount, ",
            "real_pay_amount, pay_status, operator_id,create_time, update_time)",
            "values (#{userId,jdbcType=BIGINT}, #{idCode,jdbcType=VARCHAR}, #{year,jdbcType=INTEGER}, ",
            "#{month,jdbcType=INTEGER}, #{payAmount,jdbcType=DECIMAL}, ",
            "#{realPayAmount,jdbcType=DECIMAL}, #{payStatus,jdbcType=INTEGER}, #{operatorId,jdbcType=BIGINT}, #{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(DormPayment record);

    int insertSelective(DormPayment record);

    @Select({
            "select",
            "id, user_id, id_code, year, month, pay_amount, real_pay_amount, pay_status, operator_id, create_time, update_time",
            "from dorm_payment",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.DormPaymentMapper.BaseResultMap")
    DormPayment selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DormPayment record);

    @Update({
            "update dorm_payment",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "id_code = #{idCode,jdbcType=VARCHAR},",
            "year = #{year,jdbcType=INTEGER},",
            "month = #{month,jdbcType=INTEGER},",
            "pay_amount = #{payAmount,jdbcType=DECIMAL},",
            "real_pay_amount = #{realPayAmount,jdbcType=DECIMAL},",
            "pay_status = #{payStatus,jdbcType=INTEGER},",
            "operator_id = #{operatorId,jdbcType=BIGINT},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(DormPayment record);

    /* the flowing are user defined ... */
    @Select({"select * from dorm_payment where year=#{0} and month=#{1}"})
    List<DormPayment> selectByYearAndMonth(Integer year, Integer month);

    @Select({"select * from dorm_payment where id_code=#{0} and year=#{1} and month=#{2} limit 1"})
    DormPayment selectByIdCardAndYearMonth(String idCard, Integer year, Integer month);
}