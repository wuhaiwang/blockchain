package com.seasun.management.mapper;

import com.seasun.management.model.FnProjectStatDataDetail;
import com.seasun.management.vo.FnProjectStatDataDetailVo;
import com.seasun.management.vo.FnProjectStatDataDetaildataVo;
import org.apache.ibatis.annotations.*;
import java.util.List;

public interface FnProjectStatDataDetailMapper<T> {
    @Delete({
            "delete from fn_project_stat_data_detail",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into fn_project_stat_data_detail (stat_id, project_id, ",
            "year, month, name, ",
            "value, reason, code, ",
            "line_number)",
            "values (#{statId,jdbcType=BIGINT}, #{projectId,jdbcType=BIGINT}, ",
            "#{year,jdbcType=INTEGER}, #{month,jdbcType=INTEGER}, #{name,jdbcType=VARCHAR}, ",
            "#{value,jdbcType=DECIMAL}, #{reason,jdbcType=VARCHAR}, #{code,jdbcType=BIGINT}, ",
            "#{lineNumber,jdbcType=INTEGER})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FnProjectStatDataDetail record);

    int insertSelective(FnProjectStatDataDetail record);

    @Select({
            "select",
            "id, stat_id, project_id, year, month, name, value, reason, code, line_number",
            "from fn_project_stat_data_detail",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FnProjectStatDataDetailMapper.BaseResultMap")
    FnProjectStatDataDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FnProjectStatDataDetail record);

    @Update({
            "update fn_project_stat_data_detail",
            "set stat_id = #{statId,jdbcType=BIGINT},",
            "project_id = #{projectId,jdbcType=BIGINT},",
            "year = #{year,jdbcType=INTEGER},",
            "month = #{month,jdbcType=INTEGER},",
            "name = #{name,jdbcType=VARCHAR},",
            "value = #{value,jdbcType=DECIMAL},",
            "reason = #{reason,jdbcType=VARCHAR},",
            "code = #{code,jdbcType=BIGINT},",
            "line_number = #{lineNumber,jdbcType=INTEGER}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FnProjectStatDataDetail record);

    /* the flowing are user defined ... */

    @Delete({
            "delete from fn_project_stat_data_detail",
            "where year = #{0} and month = #{1}"
    })
    int deleteByYearAndMonth(int year, int month);

    @Select({"select * from fn_project_stat_data_detail where year=#{year} and month=#{month}"})
    List<FnProjectStatDataDetail> selectByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);

    @Select({"select f.*,p.name as projectName from fn_project_stat_data_detail  f left join project p on f.project_id = p.id where year=#{year} and month=#{month} and stat_id = #{statId} order by f.value desc"})
    List<FnProjectStatDataDetailVo> selectByYearAndMonthAndStatId(@Param("year") Integer year, @Param("month") Integer month, @Param("statId") Long statId);

    @Select({"select f.*,p.name as projectName from fn_project_stat_data_detail  f left join project p on f.project_id = p.id where project_id= #{projectId} and year=#{year} and month=#{month} order by f.value desc"})
    List<FnProjectStatDataDetail> selectByProjectIdYearAndMonth(@Param("projectId") Long projectId, @Param("year") Integer year, @Param("month") Integer month);

    @Select({"select f.*,p.name as projectName from fn_project_stat_data_detail  f left join project p on f.project_id = p.id where project_id= #{projectId} and year=#{year} order by f.value desc"})
    List<FnProjectStatDataDetail> selectByProjectIdYear(@Param("projectId") Long projectId, @Param("year") Integer year);

    @Select({"select f.*,p.name as projectName from fn_project_stat_data_detail  f left join project p on f.project_id = p.id where project_id= #{projectId} and year=#{year} and month=#{month} and stat_id = #{statId} order by f.value desc"})
    List<FnProjectStatDataDetailVo> selectByProjectIdYearAndMonthAndStatId(@Param("projectId") Long projectId, @Param("year") Integer year, @Param("month") Integer month, @Param("statId") Long statId);

    List<FnProjectStatDataDetailVo> getDetailByProjectIdsAndYearAndMonthAndStatId(@Param("projectIds") List<Long> projectIds, @Param("year") int year, @Param("month") int month, @Param("statId") Long statId);

    void batchInsert(List<FnProjectStatDataDetail> records);

    List<FnProjectStatDataDetaildataVo> selectDetailByCondition(@Param("projectIds")List<Long> projectIds,@Param("statId") Long statId,@Param("year") Integer year,@Param("month") Integer month);

    @Select({"select distinct stat_id,month from fn_project_stat_data_detail where project_id=#{projectId} and year=#{year}"})
    List<FnProjectStatDataDetail> getStatDetailByProjectIdAndYear(@Param("projectId") Long projectId, @Param("year") Integer year);

}