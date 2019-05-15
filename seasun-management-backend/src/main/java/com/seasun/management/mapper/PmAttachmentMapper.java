package com.seasun.management.mapper;

import com.seasun.management.model.PmAttachment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface PmAttachmentMapper {
    @Delete({
        "delete from pm_attachment",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into pm_attachment (pm_finance_report_id, name, ",
        "url, size, create_time)",
        "values (#{pmFinanceReportId,jdbcType=BIGINT}, #{name,jdbcType=VARCHAR}, ",
        "#{url,jdbcType=VARCHAR}, #{size,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(PmAttachment record);

    int insertSelective(PmAttachment record);

    @Select({
        "select",
        "id, pm_finance_report_id, name, url, size, create_time",
        "from pm_attachment",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.PmAttachmentMapper.BaseResultMap")
    PmAttachment selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PmAttachment record);

    @Update({
        "update pm_attachment",
        "set pm_finance_report_id = #{pmFinanceReportId,jdbcType=BIGINT},",
          "name = #{name,jdbcType=VARCHAR},",
          "url = #{url,jdbcType=VARCHAR},",
          "size = #{size,jdbcType=VARCHAR},",
          "create_time = #{createTime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(PmAttachment record);

    /* the flowing are user defined ... */

    @Select({"select * from pm_attachment where pm_finance_report_id=#{0}"})
    List<PmAttachment> selectByPmFinanceReportId(Long reportId);

    List<PmAttachment> selectByPmFinanceReportIds(List<Long> reportIds);
}