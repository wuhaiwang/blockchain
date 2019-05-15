package com.seasun.management.mapper;

import com.seasun.management.model.CfgPerfHideManagerComment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CfgPerfHideManagerCommentMapper {
    @Delete({
            "delete from cfg_perf_hide_manager_comment",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into cfg_perf_hide_manager_comment (perf_work_group_id, year, ",
            "month)",
            "values (#{perfWorkGroupId,jdbcType=BIGINT}, #{year,jdbcType=INTEGER}, ",
            "#{month,jdbcType=INTEGER})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(CfgPerfHideManagerComment record);

    int insertSelective(CfgPerfHideManagerComment record);

    @Select({
            "select",
            "id, perf_work_group_id, year, month",
            "from cfg_perf_hide_manager_comment",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.CfgPerfHideManagerCommentMapper.BaseResultMap")
    CfgPerfHideManagerComment selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CfgPerfHideManagerComment record);

    @Update({
            "update cfg_perf_hide_manager_comment",
            "set perf_work_group_id = #{perfWorkGroupId,jdbcType=BIGINT},",
            "year = #{year,jdbcType=INTEGER},",
            "month = #{month,jdbcType=INTEGER}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(CfgPerfHideManagerComment record);

    /* the flowing are user defined ... */
    @Select("select * from cfg_perf_hide_manager_comment")
    List<CfgPerfHideManagerComment> selectAll();
}