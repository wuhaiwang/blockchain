package com.seasun.management.mapper;

import com.seasun.management.model.FnWeekShareCommitLog;
import com.seasun.management.vo.FnPlatShareConfigLockVo;
import com.seasun.management.dto.FnWeekCommitStatusDto;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface FnWeekShareCommitLogMapper {
    @Delete({
            "delete from fn_week_share_commit_log",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into fn_week_share_commit_log (plat_id, year, ",
            "week, commit_time, ",
            "operator_id)",
            "values (#{platId,jdbcType=BIGINT}, #{year,jdbcType=INTEGER}, ",
            "#{week,jdbcType=INTEGER}, #{commitTime,jdbcType=TIMESTAMP}, ",
            "#{operatorId,jdbcType=BIGINT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FnWeekShareCommitLog record);

    int insertSelective(FnWeekShareCommitLog record);

    @Select({
            "select",
            "id, plat_id, year, week, commit_time, operator_id",
            "from fn_week_share_commit_log",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FnWeekShareCommitLogMapper.BaseResultMap")
    FnWeekShareCommitLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FnWeekShareCommitLog record);

    @Update({
            "update fn_week_share_commit_log",
            "set plat_id = #{platId,jdbcType=BIGINT},",
            "year = #{year,jdbcType=INTEGER},",
            "week = #{week,jdbcType=INTEGER},",
            "commit_time = #{commitTime,jdbcType=TIMESTAMP},",
            "operator_id = #{operatorId,jdbcType=BIGINT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FnWeekShareCommitLog record);

    /* the flowing are user defined ... */
    @Select("select * from fn_week_share_commit_log where year = #{0} and week = #{1} and plat_id = #{2}")
    List<FnWeekShareCommitLog> selectByYearAndWeekAndPlatId(int year, int week, Long platId);

    @Select("select a.id as platId, p.name as platName,if(c.plat_id>0,\"已确认\",\"未确认\") as status from  cfg_plat_attr a left join (select * from fn_week_share_commit_log f where f.year = #{0} and f.week = #{1}) c on a.plat_id = c.plat_id left join project p on p.id = a.plat_id  " +
            "   where a.share_week_write_flag =1 and p.active_flag =1;")
    List<FnWeekCommitStatusDto> getCommitStatusByYearAndWeek(int year, int week);

    @Select("select a.* from " +
            "(select year,week from fn_plat_week_share_config where year = #{0} and month = #{1} " +
            "and plat_id = #{2} group by year,week) a ,fn_week_share_commit_log c " +
            "  where a.year = c.year and a.week = c.week and c.plat_id = #{2} order by year,week")
    List<FnPlatShareConfigLockVo.WeekConfirmInfo> selectPlatWeekConfirmData(int year, int month, Long platId);
}