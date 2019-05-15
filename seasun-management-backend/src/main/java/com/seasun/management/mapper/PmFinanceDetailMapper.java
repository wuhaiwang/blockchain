package com.seasun.management.mapper;

import com.seasun.management.model.PmFinanceDetail;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface PmFinanceDetailMapper {
    @Delete({
        "delete from pm_finance_detail",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into pm_finance_detail (year, month, ",
        "project_id, remark)",
        "values (#{year,jdbcType=INTEGER}, #{month,jdbcType=INTEGER}, ",
        "#{projectId,jdbcType=BIGINT}, #{remark,jdbcType=VARCHAR})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(PmFinanceDetail record);

    int insertSelective(PmFinanceDetail record);

    @Select({
        "select",
        "id, year, month, project_id, remark",
        "from pm_finance_detail",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.PmFinanceDetailMapper.BaseResultMap")
    PmFinanceDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PmFinanceDetail record);

    @Update({
        "update pm_finance_detail",
        "set year = #{year,jdbcType=INTEGER},",
          "month = #{month,jdbcType=INTEGER},",
          "project_id = #{projectId,jdbcType=BIGINT},",
          "remark = #{remark,jdbcType=VARCHAR}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(PmFinanceDetail record);
}