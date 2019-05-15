package com.seasun.management.mapper;

import com.seasun.management.model.AReprProjData;
import com.seasun.management.model.AReprProjSchedData;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

import java.util.List;

public interface AReprProjDataMapper {
    @Insert({
            "insert into a_repr_proj_data (project_id, project_name, ",
            "stat_id, year, month, ",
            "value, create_time, ",
            "update_time)",
            "values (#{projectId,jdbcType=VARCHAR}, #{projectName,jdbcType=VARCHAR}, ",
            "#{statId,jdbcType=VARCHAR}, #{year,jdbcType=INTEGER}, #{month,jdbcType=INTEGER}, ",
            "#{value,jdbcType=REAL}, #{createTime,jdbcType=TIMESTAMP}, ",
            "#{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(AReprProjData record);

    int insertSelective(AReprProjData record);

      /* the flowing are user defined ... */

    @Select("select * from a_repr_proj_data where stat_id = 20065")
    List<AReprProjData> selectAllOutsourcing();

    @Select("select * from a_repr_proj_data where stat_id = 20065 and year = #{0} and month = #{1}")
    List<AReprProjData> selectOutsourcingByYearAndMonth(int year, int month);
}