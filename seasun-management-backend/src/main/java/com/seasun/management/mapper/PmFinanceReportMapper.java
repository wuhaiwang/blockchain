package com.seasun.management.mapper;

import com.seasun.management.model.PmFinanceReport;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

public interface PmFinanceReportMapper {
    @Delete({
            "delete from pm_finance_report",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into pm_finance_report (project_id, year, ",
            "month, income_float_reason, ",
            "cost_float_reason, profit_float_reason, ",
            "status, create_time, ",
            "update_time)",
            "values (#{projectId,jdbcType=BIGINT}, #{year,jdbcType=INTEGER}, ",
            "#{month,jdbcType=INTEGER}, #{incomeFloatReason,jdbcType=VARCHAR}, ",
            "#{costFloatReason,jdbcType=VARCHAR}, #{profitFloatReason,jdbcType=VARCHAR}, ",
            "#{status,jdbcType=INTEGER}, #{createTime,jdbcType=TIMESTAMP}, ",
            "#{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(PmFinanceReport record);

    int insertSelective(PmFinanceReport record);

    @Select({
            "select",
            "id, project_id, year, month, income_float_reason, cost_float_reason, profit_float_reason, ",
            "status, create_time, update_time",
            "from pm_finance_report",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.PmFinanceReportMapper.BaseResultMap")
    PmFinanceReport selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PmFinanceReport record);

    @Update({
            "update pm_finance_report",
            "set project_id = #{projectId,jdbcType=BIGINT},",
            "year = #{year,jdbcType=INTEGER},",
            "month = #{month,jdbcType=INTEGER},",
            "income_float_reason = #{incomeFloatReason,jdbcType=VARCHAR},",
            "cost_float_reason = #{costFloatReason,jdbcType=VARCHAR},",
            "profit_float_reason = #{profitFloatReason,jdbcType=VARCHAR},",
            "status = #{status,jdbcType=INTEGER},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(PmFinanceReport record);

    /* the flowing are user defined ... */
    @Select({"select * from pm_finance_report where project_id =#{0} order by year,month"})
    List<PmFinanceReport> selectByProjectId(Long projectId);

    List<PmFinanceReport> selectByCond(PmFinanceReport record);

    @Update({"update pm_finance_report set status=1 ,update_time=#{1} where project_id=#{0} and status=0"})
    int submitByProjectId(Long projectId, Date updateTime);

    @Update({"update pm_finance_report set status=2 ,update_time=#{1} where status!=2 and project_id=#{0} "})
    void publishByProjectId(Long projectId, Date updateTime);

    @Select({"select * from pm_finance_report where project_id=#{0} order by year desc,month desc limit 1"})
    PmFinanceReport selectLastByProjectId(Long projectId);
}