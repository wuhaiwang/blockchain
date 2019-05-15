package com.seasun.management.mapper;

import com.seasun.management.model.PerformanceObserver;
import com.seasun.management.vo.PerformanceObserverVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface PerformanceObserverMapper {
    @Delete({
            "delete from performance_observer",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into performance_observer (perf_manager_id, observer_id)",
            "values (#{perfManagerId,jdbcType=BIGINT}, #{observerId,jdbcType=BIGINT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(PerformanceObserver record);

    int insertSelective(PerformanceObserver record);

    @Select({
            "select",
            "id, perf_manager_id, observer_id",
            "from performance_observer",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.PerformanceObserverMapper.BaseResultMap")
    PerformanceObserver selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PerformanceObserver record);

    @Update({
            "update performance_observer",
            "set perf_manager_id = #{perfManagerId,jdbcType=BIGINT},",
            "observer_id = #{observerId,jdbcType=BIGINT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(PerformanceObserver record);

    /* the flowing are user defined ... */

    @Select({
            "select o.perf_manager_id user_id, concat(mu.last_name,mu.first_name) user_name",
            "from performance_observer o",
            "left join user mu on mu.id = o.perf_manager_id",
            "where o.observer_id = #{observerId}"
    })
    List<PerformanceObserverVo> selectAllByObserverId(Long observerId);

    @Select("select * from performance_observer where perf_manager_id = #{managerId} and observer_id = #{observerId} limit 1")
    PerformanceObserver selectByPerfManagerIdAndObserverId(@Param("managerId") Long managerId, @Param("observerId") Long observerId);
}