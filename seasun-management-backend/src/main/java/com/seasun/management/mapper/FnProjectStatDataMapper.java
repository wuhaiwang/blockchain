package com.seasun.management.mapper;

import com.seasun.management.dto.BaseProjectIdAndFnLastCreateTime;
import com.seasun.management.dto.FnProjectStatDataDto;
import com.seasun.management.model.FnProjectStatData;
import com.seasun.management.model.FnShareData;
import com.seasun.management.vo.FnProjectInfoAppVo;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public interface FnProjectStatDataMapper {
    @Delete({
            "delete from fn_project_stat_data",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into fn_project_stat_data (fn_stat_id, project_id, ",
            "year, month, value, detail_flag,",
            "create_time, update_time)",
            "values (#{fnStatId,jdbcType=BIGINT}, #{projectId,jdbcType=BIGINT}, ",
            "#{year,jdbcType=INTEGER}, #{month,jdbcType=INTEGER}, #{value,jdbcType=REAL}, #{detailFlag,jdbcType=BIT},",
            "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FnProjectStatData record);

    int insertSelective(FnProjectStatData record);

    @Select({
            "select",
            "id, fn_stat_id, project_id, year, month, value, detail_flag, create_time, update_time",
            "from fn_project_stat_data",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FnProjectStatDataMapper.BaseResultMap")
    FnProjectStatData selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FnProjectStatData record);

    @Update({
            "update fn_project_stat_data",
            "set fn_stat_id = #{fnStatId,jdbcType=BIGINT},",
            "project_id = #{projectId,jdbcType=BIGINT},",
            "year = #{year,jdbcType=INTEGER},",
            "month = #{month,jdbcType=INTEGER},",
            "value = #{value,jdbcType=REAL},",
            "detail_flag = #{detailFlag,jdbcType=BIT}",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FnProjectStatData record);

    /* the flowing are user defined ... */
    int batchInsert(List<FnProjectStatData> fnProjectStatDataList);

    int batchUpdateByPks(List<FnProjectStatData> fnProjectStatDataList);

    int batchUpdateDetailFlagByPks(List<FnProjectStatData> fnProjectStatDataList);

    @Delete("delete from fn_project_stat_data where year = #{param1} and month = #{param2} and project_id = #{param3}")
    int deleteByYearAndMonthAndProjectId(@Param("year") int year, @Param("month") int month, @Param("projectId") Long projectId);

    @Delete("delete from fn_project_stat_data where year = #{year} and project_id = #{projectId}")
    int deleteByYearAndProjectId(@Param("year") int year, @Param("projectId") Long projectId);

    List<FnProjectStatData> selectByCondition(FnProjectStatData fnProjectStatData);

    @Select("select * from fn_project_stat_data where year=#{year} and month=#{month}")
    List<FnProjectStatData> selectByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @SelectProvider(type = FnProjectStatDataSqlBuilder.class, method = "buildSelectByYearAndMonthAndProjectId")
    List<FnProjectStatDataDto> selectLeftJoinFnStatByYearAndMonthAndProjectId(@Param("year") Integer year, @Param("month") Integer month, @Param("projectId") Long projectId);

    @Select("select * from fn_project_stat_data where value <> 0 and fn_stat_id <> 3700 and month<> 0 order by year desc, month desc limit 1")
    FnProjectStatData selectByNew();

    @Select({"(select * from fn_project_stat_data where project_id = #{projectId} and value <> 0 and fn_stat_id <> 3700 and month<> 0 and year >= 2017 order by year, month limit 1)",
            "union",
            "(select * from fn_project_stat_data where project_id = #{projectId} and value <> 0 and fn_stat_id <> 3700 and month<> 0 and year >= 2017 order by year desc, month desc limit 1)"
    })
    List<FnProjectStatData> selectFirstAndLastByProjectId(Long projectId);

    @Delete("delete from fn_project_stat_data where fn_stat_id = 3700")
    int deleteAllOutsourcingData();

    @Delete("delete from fn_project_stat_data where fn_stat_id = 3700 and year = #{0} and month = #{1}")
    int deleteOutsourcingDataByYearAndMonth(int year, int month);

    @SelectProvider(type = FnProjectStatDataSqlBuilder.class, method = "buildSelectSumProfitByYearAndMont")
    Float selectSumProfitByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);

    @Select({"select create_time from fn_project_stat_data where project_id=#{0} order by year desc,month desc limit 1"})
    Date selectLastInsertDateByProjectId(Long projectId);

    //获取项目最新两个月财务信息
    @Select({"select f.* from (select year,month from fn_project_stat_data where month!=0 and project_id = #{0} group by year,month ORDER BY year desc,month desc limit 2) p left join fn_project_stat_data f on p.year=f.year and p.month=f.month and f.project_id=#{0} "})
    List<FnProjectStatData> selectLastTwoMonthStatDataByProjectId(Long projectId);

    @Select({"select f.* from (select year,month from fn_project_stat_data where month!=0 and project_id = #{0} group by year,month ORDER BY year desc,month desc limit 2) p left join fn_project_stat_data f on p.year=f.year and p.month=f.month and f.project_id=#{0} "})
    List<FnProjectStatData> select(Long projectId);

    @Select({"select * from fn_project_stat_data where project_id=#{0} and month!=0 and ((year =#{1} and month >= #{2}) or (year > #{1}))"})
    List<FnProjectStatData> selectByProjectIdAndStartTime(Long projectId, Integer startYear, Integer startMonth);

    @Select({"select max(create_time) fnLastCreateTime from fn_project_stat_data "})
    Date selectLastCreateTime();

    @SelectProvider(type = FnProjectStatDataSqlBuilder.class, method = "buildSelectTwoYearDataByProjectIdAndYear")
    List<FnProjectStatData> selectTwoYearDataByProjectIdAndYear(Long id, Integer year);

    @Select({"select * from fn_project_stat_data where project_id=#{0}"})
    List<FnProjectStatData> selectByProjectId(Long projectId);

    class FnProjectStatDataSqlBuilder {

        public String buildSelectTwoYearDataByProjectIdAndYear(Long id, Integer year) {
            StringBuilder sb = new StringBuilder("select * from fn_project_stat_data where project_id =" + id + " and (year =" + year + " or year=" + (year - 1) + ") ");
            return sb.toString();
        }

        public String buildSelectByYearAndMonthAndProjectId(Map<String, Object> params) {
            Integer year = (Integer) params.get("year");
            Integer month = (Integer) params.get("month");
            Long projectId = (Long) params.get("projectId");
            return new SQL() {{
                SELECT("a.*");
                SELECT("b.name, b.parent_id");
                SELECT("c.name as parent_name");
                FROM("fn_project_stat_data a");
                LEFT_OUTER_JOIN("fn_stat b on a.fn_stat_id = b.id");
                LEFT_OUTER_JOIN("fn_stat c on b.parent_id = c.id");
                if (null != year && null != month) {
                    Calendar now = Calendar.getInstance();
                    now.set(year, month - 1, 1);
                    AND();
                    String conditions = "";
                    int year = now.get(Calendar.YEAR);
                    int month = now.get(Calendar.MONTH) + 1;
                    conditions += "(a.year = " + year + " and a.month = " + month + ")";
                    now.add(Calendar.MONTH, -2);
                    year = now.get(Calendar.YEAR);
                    month = now.get(Calendar.MONTH) + 1;
                    conditions += " or (a.year = " + year + " and a.month = " + month + ")";
                    now.add(Calendar.MONTH, 1);
                    year = now.get(Calendar.YEAR);
                    month = now.get(Calendar.MONTH) + 1;
                    conditions += " or (a.year = " + year + " and a.month = " + month + ")";
                    WHERE(conditions);
                }
                if (null != projectId) {
                    AND();
                    WHERE("a.project_id = " + projectId);
                }
            }}.toString();

        }

        public String buildSelectSumProfitByYearAndMont(Map<String, Object> params) {
            Integer year = (Integer) params.get("year");
            Integer month = (Integer) params.get("month");
            return new SQL() {{
                SELECT("sum(d.value)");
                FROM("fn_project_stat_data d");
                LEFT_OUTER_JOIN("fn_stat s on d.fn_stat_id = s.id");
                WHERE("s.name = '税前利润'");
                WHERE("d.year = " + year);
                if (null != month) {
                    WHERE("d.month = " + month);
                }
            }}.toString();
        }
    }
}


