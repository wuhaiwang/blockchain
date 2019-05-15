package com.seasun.management.mapper;

import com.seasun.management.model.FnShareTemplate;
import com.seasun.management.vo.DateRangeVo;
import org.apache.ibatis.annotations.*;

public interface FnShareTemplateMapper {
    @Delete({
            "delete from fn_share_template",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into fn_share_template (year, month, ",
            "location, create_time)",
            "values (#{year,jdbcType=INTEGER}, #{month,jdbcType=INTEGER}, ",
            "#{location,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FnShareTemplate record);

    int insertSelective(FnShareTemplate record);

    @Select({
            "select",
            "id, year, month, location, create_time",
            "from fn_share_template",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FnShareTemplateMapper.BaseResultMap")
    FnShareTemplate selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FnShareTemplate record);

    @Update({
            "update fn_share_template",
            "set year = #{year,jdbcType=INTEGER},",
            "month = #{month,jdbcType=INTEGER},",
            "location = #{location,jdbcType=VARCHAR},",
            "create_time = #{createTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FnShareTemplate record);

    @Select("select * from fn_share_template where year = #{year} and month = #{month} limit 1")
    FnShareTemplate selectTemplateByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Select("select * from fn_share_template order by year desc, month desc limit 1")
    FnShareTemplate selectTemplateLast();

    @Select({"select year, month from fn_share_template order by year desc, month desc limit 1"})
    DateRangeVo selectEndYearMonth();
}