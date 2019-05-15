package com.seasun.management.mapper;

import com.seasun.management.dto.HistoryStatusDto;
import com.seasun.management.dto.YearMonthDto;
import com.seasun.management.model.PerfWorkGroupStatus;
import com.seasun.management.model.UserPerformance;
import com.seasun.management.vo.SubPerformanceAppVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface PerfWorkGroupStatusMapper {
    @Delete({
            "delete from perf_work_group_status",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into perf_work_group_status (perf_work_group_id, status, ",
            "year, month, create_time, ",
            "update_time, operate_id)",
            "values (#{perfWorkGroupId,jdbcType=BIGINT}, #{status,jdbcType=VARCHAR}, ",
            "#{year,jdbcType=INTEGER}, #{month,jdbcType=INTEGER}, #{createTime,jdbcType=TIMESTAMP}, ",
            "#{updateTime,jdbcType=TIMESTAMP}, #{operateId,jdbcType=BIGINT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(PerfWorkGroupStatus record);

    int insertSelective(PerfWorkGroupStatus record);

    @Select({
            "select",
            "id, perf_work_group_id, status, year, month, create_time, update_time, operate_id",
            "from perf_work_group_status",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.PerfWorkGroupStatusMapper.BaseResultMap")
    PerfWorkGroupStatus selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PerfWorkGroupStatus record);

    @Update({
            "update perf_work_group_status",
            "set perf_work_group_id = #{perfWorkGroupId,jdbcType=BIGINT},",
            "status = #{status,jdbcType=VARCHAR},",
            "year = #{year,jdbcType=INTEGER},",
            "month = #{month,jdbcType=INTEGER},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP},",
            "operate_id = #{operateId,jdbcType=BIGINT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(PerfWorkGroupStatus record);

    /* the flowing are user defined ... */

    int batchInsert(@Param("list") List<PerfWorkGroupStatus> inserts);

    @Update({
            "update perf_work_group_status",
            "set status = '" + UserPerformance.Status.complete + "'",
            "where year = #{0} and month = #{1}"
    })
    void updateStatusToCompleteByYearAndMonth(Integer year, Integer month);

    @Select({"select * from perf_work_group_status where year=#{0} and month=#{1}"})
    List<PerfWorkGroupStatus> selectByYearAndMonth(Integer year, Integer month);

    @Select({"select year,month from perf_work_group_status where status= '" + UserPerformance.Status.complete + "'"})
    List<YearMonthDto> selectAllWithYearMonthByComplete();

    @Select({"select p.year, p.month, p.status from perf_work_group_status p left join performance_work_group g on g.id = p.perf_work_group_id where g.performance_manager_id =#{0}  order by p.year, p.month"})
    List<HistoryStatusDto> selectHistoryStatusByManagerId(Long managerId);

    @Select({
            "select p.year, p.month, p.status from perf_work_group_status p",
            "where p.perf_work_group_id = #{0}",
            "order by p.year, p.month"
    })
    List<HistoryStatusDto> selectHistoryStatusByWorkGroupId(Long observerWorkGroupId);

    @Select({
            "select year, month from perf_work_group_status",
            "where status = '" + UserPerformance.Status.complete + "'",
            "order by year desc, month desc",
            "limit 1"
    })
    YearMonthDto selectLastCompleteYearMonth();

    @Delete({"delete from perf_work_group_status where year=#{0} and month =#{1}"})
    void deleteByYearAndMonth(Integer year, Integer month);

    @Select({"select count(1) from perf_work_group_status ps left join performance_work_group p on ps.perf_work_group_id=p.id where ps.year=#{1} and ps.month =#{2} and p.performance_manager_id=#{0} and ((ps.`status`='" + SubPerformanceAppVo.HistoryInfo.Status.complete + "') or (ps.`status`='" + SubPerformanceAppVo.HistoryInfo.Status.submitted + "') )"})
    int selectCountByManagerAndLockedOrComplete(Long userId, Integer year, Integer month);

    @Select({"select count(1) from perf_work_group_status ps where ps.year=#{1} and ps.month =#{2} and ps.perf_work_group_id=#{0} and((ps.`status`='已完成') or (ps.`status`='已提交') )"})
    int selectCountByWorkGroupAndLockedOrComplete(Long workGroupId, Integer year, Integer month);
}
