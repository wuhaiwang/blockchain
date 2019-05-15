package com.seasun.management.mapper;

import com.seasun.management.dto.FnShareDataDto;
import com.seasun.management.dto.IdValueDto;
import com.seasun.management.model.FnShareData;
import com.seasun.management.vo.DateRangeVo;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface FnShareDataMapper {
    @Delete({
            "delete from fn_share_data",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into fn_share_data (plat_id, project_id, ",
            "year, month, share_pro, ",
            "share_amount, share_number, ",
            "create_time, update_time)",
            "values (#{platId,jdbcType=BIGINT}, #{projectId,jdbcType=BIGINT}, ",
            "#{year,jdbcType=INTEGER}, #{month,jdbcType=INTEGER}, #{sharePro,jdbcType=REAL}, ",
            "#{shareAmount,jdbcType=REAL}, #{shareNumber,jdbcType=REAL}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FnShareData record);

    int insertSelective(FnShareData record);

    @Select({
            "select",
            "id, plat_id, project_id, year, month, share_pro, share_amount, share_number, ",
            "create_time, update_time",
            "from fn_share_data",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FnShareDataMapper.BaseResultMap")
    FnShareData selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FnShareData record);

    @Update({
            "update fn_share_data",
            "set plat_id = #{platId,jdbcType=BIGINT},",
            "project_id = #{projectId,jdbcType=BIGINT},",
            "year = #{year,jdbcType=INTEGER},",
            "month = #{month,jdbcType=INTEGER},",
            "share_pro = #{sharePro,jdbcType=REAL},",
            "share_amount = #{shareAmount,jdbcType=REAL},",
            "share_number = #{shareNumber,jdbcType=REAL},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FnShareData record);

    /* the flowing are user defined ... */

    int batchInsert(Collection<FnShareData> shareDataList);

    @Delete("delete from fn_share_data where year = #{param1} and month = #{param2}")
    int deleteByYearAndMonth(int year, int month);

    @Delete("delete from fn_share_data where year = #{param1} ")
    int deleteByYear(int year);

    @SelectProvider(type = FnShareDataSqlBuilder.class, method = "buildSelectByYearAndMonthAndProjectId")
    List<FnShareDataDto> selectByYearAndMonthAndProjectId(@Param("year") Integer year, @Param("month") Integer month,
                                                          @Param("projectId") Long projectId, @Param("platId") Long platId);

    @Select({
            "(select * from fn_share_data where project_id = #{projectId} and year >= 2017 order by year, month limit 1)",
            "union",
            "(select * from fn_share_data where project_id = #{projectId} and year >= 2017 order by year desc, month desc limit 1)"
    })
    List<FnShareData> selectFirstAndLastByProjectId(Long projectId);

    @Select({
            "(select * from fn_share_data where plat_id = #{platId} and year >= 2017 order by year, month limit 1)",
            "union",
            "(select * from fn_share_data where plat_id = #{platId} and year >= 2017 order by year desc, month desc limit 1)"
    })
    List<FnShareData> selectFirstAndLastByPlatId(Long platId);

    @Select("select year, month from fn_share_data where (plat_id=#{platId} or plat_id in (select id from project where parent_hr_id=#{platId} and active_flag=1)) order by year desc, month desc limit 1")
    DateRangeVo selectEndYearMonthOfPlat(@Param("platId") Long platId);

    @Select("select year, month from fn_share_data where project_id=#{projectId} order by year desc, month desc limit 1")
    DateRangeVo selectEndYearMonthOfProject(@Param("projectId") Long projectId);

    @Select("select ifnull(sum(share_amount), 0) from fn_share_data " +
            "where plat_id in (select id from project where parent_share_id = #{0} ) and year = #{1} and month =#{2} and project_id =1")
    Float countChildShareCostByPlatIdAndYearAndMonth(Long platId, int year, int month);

    @Select("select plat_id as id,share_amount as value from fn_share_data " +
            "where project_id = 1 and year = #{0} and month = #{1}")
    List<IdValueDto> countParentFromSumColumn(int year, int month);

    @Select("select count(*) from (select project_id from fn_share_data where year = #{year} and month = #{month} and project_id <> 1 group by project_id) a")
    int selectServeProjectCountByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);

    class FnShareDataSqlBuilder {
        public String buildSelectByYearAndMonthAndProjectId(Map<String, Object> params) {
            Integer year = (Integer) params.get("year");
            Integer month = (Integer) params.get("month");
            Long projectId = (Long) params.get("projectId");
            Long platId = (Long) params.get("platId");
            return new SQL() {{
                SELECT("a.*");
                SELECT("b.name as plat_name");
                SELECT("c.fixed_number");
                SELECT("d.name as project_name");
                FROM("fn_share_data a");
                LEFT_OUTER_JOIN("project b on a.plat_id = b.id");
                LEFT_OUTER_JOIN("fn_sum_share_config c on a.plat_id = c.plat_id and a.project_id = c.project_id and a.year = c.year and a.month = c.month");
                LEFT_OUTER_JOIN("project d on a.project_id = d.id");
                if (null != year && null != month) {
                    Calendar now = Calendar.getInstance();
                    now.set(year, month - 1, 1);
                    StringBuilder conditions = new StringBuilder();
                    int year = now.get(Calendar.YEAR);
                    int month = now.get(Calendar.MONTH) + 1;
                    conditions.append("(a.year = ").append(year).append(" and a.month = ").append(month).append(")");
                    now.add(Calendar.MONTH, -2);
                    year = now.get(Calendar.YEAR);
                    month = now.get(Calendar.MONTH) + 1;
                    conditions.append(" or (a.year = ").append(year).append(" and a.month = ").append(month).append(")");
                    now.add(Calendar.MONTH, 1);
                    year = now.get(Calendar.YEAR);
                    month = now.get(Calendar.MONTH) + 1;
                    conditions.append(" or (a.year = ").append(year).append(" and a.month = ").append(month).append(")");
                    AND();
                    WHERE(conditions.toString());
                }
                if (null != projectId) {
                    AND();
                    WHERE("a.project_id = " + projectId);
                    WHERE("isnull(b.parent_share_id)");
                }
                if (null != platId) {
                    AND();
                    WHERE("a.plat_id = " + platId);
                }
            }}.toString();

        }
    }
}