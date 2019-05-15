package com.seasun.management.mapper;

import com.seasun.management.dto.IdValueDto;
import com.seasun.management.dto.PmPlanDetailDto;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.PmPlanDetail;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface PmPlanDetailMapper {
    @Delete({
            "delete from pm_plan_detail",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into pm_plan_detail (pm_plan_id, stage_name, ",
            "start_time, end_time, ",
            "month_goal, description, ",
            "actual_status, delay_reason, ",
            "actual_progress, cancel_reason, ",
            "manager_id, create_time, ",
            "update_time, real_end_time, ",
            "new_flag)",
            "values (#{pmPlanId,jdbcType=BIGINT}, #{stageName,jdbcType=VARCHAR}, ",
            "#{startTime,jdbcType=TIMESTAMP}, #{endTime,jdbcType=TIMESTAMP}, ",
            "#{monthGoal,jdbcType=VARCHAR}, #{description,jdbcType=VARCHAR}, ",
            "#{actualStatus,jdbcType=VARCHAR}, #{delayReason,jdbcType=VARCHAR}, ",
            "#{actualProgress,jdbcType=VARCHAR}, #{cancelReason,jdbcType=VARCHAR}, ",
            "#{managerId,jdbcType=BIGINT}, #{createTime,jdbcType=TIMESTAMP}, ",
            "#{updateTime,jdbcType=TIMESTAMP}, #{realEndTime,jdbcType=TIMESTAMP}, ",
            "#{newFlag,jdbcType=TINYINT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(PmPlanDetail record);

    int insertSelective(PmPlanDetail record);

    @Select({
            "select",
            "id, pm_plan_id, stage_name, start_time, end_time, month_goal, description, actual_status, ",
            "delay_reason, actual_progress, cancel_reason, manager_id, create_time, update_time, ",
            "real_end_time, new_flag",
            "from pm_plan_detail",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.PmPlanDetailMapper.BaseResultMap")
    PmPlanDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PmPlanDetail record);

    @Update({
            "update pm_plan_detail",
            "set pm_plan_id = #{pmPlanId,jdbcType=BIGINT},",
            "stage_name = #{stageName,jdbcType=VARCHAR},",
            "start_time = #{startTime,jdbcType=TIMESTAMP},",
            "end_time = #{endTime,jdbcType=TIMESTAMP},",
            "month_goal = #{monthGoal,jdbcType=VARCHAR},",
            "description = #{description,jdbcType=VARCHAR},",
            "actual_status = #{actualStatus,jdbcType=VARCHAR},",
            "delay_reason = #{delayReason,jdbcType=VARCHAR},",
            "actual_progress = #{actualProgress,jdbcType=VARCHAR},",
            "cancel_reason = #{cancelReason,jdbcType=VARCHAR},",
            "manager_id = #{managerId,jdbcType=BIGINT},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP},",
            "real_end_time = #{realEndTime,jdbcType=TIMESTAMP},",
            "new_flag = #{newFlag,jdbcType=TINYINT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(PmPlanDetail record);

    /* the flowing are user defined ... */

    @Select({"select a.*, (select  GROUP_CONCAT(id,'-',last_name,first_name)  from user where  FIND_IN_SET(id,a.manager_id) ) managerName from pm_plan_detail a where a.pm_plan_id=#{0} order by create_time"})
    List<PmPlanDetailDto> selectPmPlanDetailDtoByPmPlanId(Long pmPlanId);

    @Select({"select * from pm_plan_detail  where pm_plan_id=#{0}"})
    List<PmPlanDetail> selectByPmPlanId(Long pmPlanId);

    void batchInsert(@Param("list") List<PmPlanDetail> result);

    @Select({"select a.*, (select  GROUP_CONCAT(last_name,first_name)  from user where  FIND_IN_SET(id,a.manager_id) ) managerName from pm_plan_detail a where a.pm_plan_id=(select id from pm_plan where status=2 and project_id=#{0} order by version desc limit 1)  order by start_time desc"})
    List<PmPlanDetailDto> selectPublishedDetailsByProjectId(Long project);

    @Select({"select a.project_id id, (select count(1) from pm_plan_detail where  FIND_IN_SET(pm_plan_id,a.pm_plan_id)) value from (select project_id,GROUP_CONCAT(id) pm_plan_id from pm_plan where status=2 GROUP BY project_id) a"})
    List<IdValueDto> selectPublishedProjectDeatilCount();

    @Select({"select b.id,p.name from (select a.project_id id, (select count(1) from pm_plan_detail where  FIND_IN_SET(pm_plan_id,a.pm_plan_id)) value from (select project_id,GROUP_CONCAT(id) pm_plan_id from pm_plan where status=2 GROUP BY project_id) a) b left join project p on p.id=b.id where b.value > 0"})
    List<IdNameBaseObject> selectPublishedProjects();

}