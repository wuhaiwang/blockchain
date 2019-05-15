package com.seasun.management.mapper;

import com.seasun.management.dto.FnSumShareConfigDto;
import com.seasun.management.dto.PlatIdAndProjectIdDto;
import com.seasun.management.model.FnSumShareConfig;
import com.seasun.management.vo.DateRangeVo;
import com.seasun.management.vo.FnSumShareConfigGroupVo;
import com.seasun.management.vo.FnSumShareConfigVo;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public interface FnSumShareConfigMapper {
    @Delete({
            "delete from fn_sum_share_config",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into fn_sum_share_config (plat_id, project_id, ",
            "year, month, share_pro, ",
            "lock_flag, fixed_number, ",
            "update_by, create_time, ",
            "update_time)",
            "values (#{platId,jdbcType=BIGINT}, #{projectId,jdbcType=BIGINT}, ",
            "#{year,jdbcType=INTEGER}, #{month,jdbcType=INTEGER}, #{sharePro,jdbcType=DECIMAL}, ",
            "#{lockFlag,jdbcType=BIT}, #{fixedNumber,jdbcType=DECIMAL}, ",
            "#{updateBy,jdbcType=BIGINT}, #{createTime,jdbcType=TIMESTAMP}, ",
            "#{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FnSumShareConfig record);

    int insertSelective(FnSumShareConfig record);

    @Select({
            "select",
            "id, plat_id, project_id, year, month, share_pro, lock_flag, fixed_number, update_by, ",
            "create_time, update_time",
            "from fn_sum_share_config",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FnSumShareConfigMapper.BaseResultMap")
    FnSumShareConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FnSumShareConfig record);

    @Update({
            "update fn_sum_share_config",
            "set plat_id = #{platId,jdbcType=BIGINT},",
            "project_id = #{projectId,jdbcType=BIGINT},",
            "year = #{year,jdbcType=INTEGER},",
            "month = #{month,jdbcType=INTEGER},",
            "share_pro = #{sharePro,jdbcType=DECIMAL},",
            "lock_flag = #{lockFlag,jdbcType=BIT},",
            "fixed_number = #{fixedNumber,jdbcType=DECIMAL},",
            "update_by = #{updateBy,jdbcType=BIGINT},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FnSumShareConfig record);

    /* the flowing are user defined ... */

    @Select({
            "(select year, month from fn_sum_share_config where (plat_id = #{platId} or plat_id in (select id from project where parent_share_id=#{platId} and active_flag=1)) order by year, month limit 1)",
            "union",
            "(select year, month from fn_sum_share_config where (plat_id = #{platId} or plat_id in (select id from project where parent_share_id=#{platId} and active_flag=1)) order by year desc, month desc limit 1)"
    })
    List<DateRangeVo> selectFirstAndLastDateByPlatId(@Param("platId") Long platId);

    @Select({
            "(select year, month from fn_sum_share_config where (project_id=#{projectId}) order by year, month limit 1)",
            "union",
            "(select year, month from fn_sum_share_config where (project_id=#{projectId}) order by year desc, month desc limit 1)"
    })
    List<DateRangeVo> selectFirstAndLastDateByProjectId(@Param("projectId") Long projectId);

    @Select("select * from fn_sum_share_config where plat_id = #{platId} and year = #{year} and month = #{month}")
    List<FnSumShareConfig> selectSumShareConfigByPlatIdAndYearAndMonth(@Param("platId") Long platId, @Param("year") int year, @Param("month") int month);

    @Select("select * from fn_sum_share_config where plat_id = #{platId} and project_Id=#{projectId} and year = #{year} and month = #{month}")
    FnSumShareConfig selectSumShareConfigByPlatIdAndProjectIdAndYearAndMonth(@Param("platId") Long platId, @Param("projectId") Long projectId, @Param("year") int year, @Param("month") int month);

    @Select("select * from fn_sum_share_config where (plat_id = #{platId} or plat_id in (select id from project where parent_hr_id=#{platId})) and year = #{year} and month = #{month}")
    List<FnSumShareConfig> selectAllSubSumShareConfigByPlatIdAndYearAndMonth(@Param("platId") Long platId, @Param("year") int year, @Param("month") int month);

    @Select("select * from fn_sum_share_config where year = #{year} and month = #{month}")
    List<FnSumShareConfig> selectSumShareConfigByYearAndMonth(@Param("year") int year, @Param("month") int month);


    @Select("select id as sumShareProId, project_id as ProjectId  from fn_sum_share_config where plat_id = #{platId} and year = #{year} and month = #{month}")
    List<FnSumShareConfigVo.ProjectSumPro> selectSumShareConfigIdsByPlatIdAndYearAndMonth(@Param("platId") Long platId, @Param("year") int year, @Param("month") int month);

    @Delete("delete from fn_sum_share_config where plat_id = #{platId} and year = #{year} and month = #{month}")
    int deleteByPlatIdAndYearAndMonth(@Param("platId") Long platId, @Param("year") int year, @Param("month") int month);

    int batchInsert(List<FnSumShareConfig> fnSumShareConfigs);

    @Select("SELECT lock_flag FROM fn_sum_share_config " +
            "WHERE year= #{year} " +
            "AND month= #{month} " +
            "AND plat_id=#{platId} " +
            "LIMIT 1;")
    Boolean getFlagLockStatus(@Param("platId") Long platId, @Param("year") Integer year, @Param("month") Integer month);

    @Select("SELECT lock_flag FROM fn_sum_share_config " +
            "WHERE id=#{id} " +
            "LIMIT 1;")
    Boolean getFlagLockStatusById(@Param("id") Long id);

    int batchUpdateLockStatus(@Param("plats") List<Long> plats, @Param("year") int year, @Param("month") int month, @Param("lockFlag") boolean lockFlag);

    @Update("update fn_sum_share_config set lock_flag = 1 where year = #{year} and month = #{month}")
    void lockByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Select("select * from fn_sum_share_config where project_id = #{projectId} group by plat_id, year ,month")
    List<FnSumShareConfigGroupVo> selectByProjectIdAndGroup(@Param("projectId") Long projectId);

    @SelectProvider(type = FnPlatShareConfigSqlBuilder.class, method = "buildSelectByYearAndMonthAndPlatId")
    List<FnSumShareConfigDto> selectByYearAndMonthAndPlatId(@Param("platId") Long platId, @Param("year") Integer year, @Param("month") Integer month);

    @SelectProvider(type = FnPlatShareConfigSqlBuilder.class, method = "buildSelectByYearAndMonthAndProjectId")
    List<FnSumShareConfigDto> selectByYearAndMonthAndProjectId(@Param("projectId") Long projectId, @Param("year") Integer year, @Param("month") Integer month);

    @Select({"select plat_id,project_id from fn_sum_share_config where share_pro is not null and share_pro !=0 and year=#{0} and month=#{1}"})
    List<PlatIdAndProjectIdDto> selectPlatIdAndProjectIdByYearAndMonth(Integer year, Integer month);

    class FnPlatShareConfigSqlBuilder {
        public String buildSelectByYearAndMonthAndPlatId(Map<String, Object> params) {
            Integer year = (Integer) params.get("year");
            Integer month = (Integer) params.get("month");

            int yearCurrent = 0, monthCurrent = 0, yearLastTwo = 0, monthLastTwo = 0, yearLast = 0, monthLast = 0;
            if (null != year && null != month) {
                Calendar now = Calendar.getInstance();
                now.set(year, month - 1, 1);
                yearCurrent = now.get(Calendar.YEAR);
                monthCurrent = now.get(Calendar.MONTH) + 1;
                now.add(Calendar.MONTH, -2);
                yearLastTwo = now.get(Calendar.YEAR);
                monthLastTwo = now.get(Calendar.MONTH) + 1;
                now.add(Calendar.MONTH, 1);
                yearLast = now.get(Calendar.YEAR);
                monthLast = now.get(Calendar.MONTH) + 1;

                params.put("yearCurrent", yearCurrent);
                params.put("monthCurrent", monthCurrent);
                params.put("yearLastTwo", yearLastTwo);
                params.put("monthLastTwo", monthLastTwo);
                params.put("yearLast", yearLast);
                params.put("monthLast", monthLast);
            }

            return new SQL() {{
                SELECT("a.*, b.name plat_name, c.name project_name");
                FROM("fn_sum_share_config as a");
                LEFT_OUTER_JOIN("project b on a.plat_id=b.id");
                LEFT_OUTER_JOIN("project c on a.project_id=c.id");
                WHERE("(a.year = #{yearCurrent} and a.month = #{monthCurrent}) or (a.year = #{yearLastTwo} and a.month = #{monthLastTwo}) or (a.year = #{yearLast} and a.month = #{monthLast})");
                AND();
                WHERE("(b.parent_hr_id=#{platId} OR a.plat_id=#{platId})");
            }}.toString();
        }

        public String buildSelectByYearAndMonthAndProjectId(Map<String, Object> params) {
            Integer year = (Integer) params.get("year");
            Integer month = (Integer) params.get("month");

            int yearCurrent = 0, monthCurrent = 0, yearLastTwo = 0, monthLastTwo = 0, yearLast = 0, monthLast = 0;
            if (null != year && null != month) {
                Calendar now = Calendar.getInstance();
                now.set(year, month - 1, 1);
                yearCurrent = now.get(Calendar.YEAR);
                monthCurrent = now.get(Calendar.MONTH) + 1;
                now.add(Calendar.MONTH, -2);
                yearLastTwo = now.get(Calendar.YEAR);
                monthLastTwo = now.get(Calendar.MONTH) + 1;
                now.add(Calendar.MONTH, 1);
                yearLast = now.get(Calendar.YEAR);
                monthLast = now.get(Calendar.MONTH) + 1;

                params.put("yearCurrent", yearCurrent);
                params.put("monthCurrent", monthCurrent);
                params.put("yearLastTwo", yearLastTwo);
                params.put("monthLastTwo", monthLastTwo);
                params.put("yearLast", yearLast);
                params.put("monthLast", monthLast);
            }

            return new SQL() {{
                SELECT("a.*, b.name plat_name, d.name project_name, b.parent_hr_id plat_parent_hr_id, c.name plat_parent_hr_name");
                FROM("fn_sum_share_config as a");
                LEFT_OUTER_JOIN("project b on a.plat_id=b.id");
                LEFT_OUTER_JOIN("project c on b.parent_hr_id=c.id");
                LEFT_OUTER_JOIN("project d on a.project_id=d.id");
                WHERE("(a.year = #{yearCurrent} and a.month = #{monthCurrent}) or (a.year = #{yearLastTwo} and a.month = #{monthLastTwo}) or (a.year = #{yearLast} and a.month = #{monthLast})");
                AND();
                WHERE("(a.project_id=#{projectId})");
            }}.toString();
        }
    }
}