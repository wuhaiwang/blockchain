package com.seasun.management.mapper;

import com.seasun.management.model.PmMilestone;
import com.seasun.management.vo.PMilestoneDto;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

public interface PmMilestoneMapper {
    @Delete({
            "delete from pm_milestone",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into pm_milestone (milestone_day, project_estimate_day, ",
            "end_time, status, project_id, ",
            "channel, risk, publish_flag, ",
            "create_time, update_time)",
            "values (#{milestoneDay,jdbcType=DATE}, #{projectEstimateDay,jdbcType=DATE}, ",
            "#{endTime,jdbcType=DATE}, #{status,jdbcType=BIT}, #{projectId,jdbcType=BIGINT}, ",
            "#{channel,jdbcType=VARCHAR}, #{risk,jdbcType=VARCHAR}, #{publishFlag,jdbcType=BIT}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(PmMilestone record);

    int insertSelective(PmMilestone record);

    @Select({
            "select",
            "id, milestone_day, project_estimate_day, end_time, status, project_id, channel, ",
            "risk, publish_flag, create_time, update_time",
            "from pm_milestone",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.PmMilestoneMapper.BaseResultMap")
    PmMilestone selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PmMilestone record);

    @Update({
            "update pm_milestone",
            "set milestone_day = #{milestoneDay,jdbcType=DATE},",
            "project_estimate_day = #{projectEstimateDay,jdbcType=DATE},",
            "end_time = #{endTime,jdbcType=DATE},",
            "status = #{status,jdbcType=BIT},",
            "project_id = #{projectId,jdbcType=BIGINT},",
            "channel = #{channel,jdbcType=VARCHAR},",
            "risk = #{risk,jdbcType=VARCHAR},",
            "publish_flag = #{publishFlag,jdbcType=BIT},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(PmMilestone record);


    // Defined by user ...

    @Select({"SELECT m.*, p.`name` project_name , p.contact_name manager_name ,p.type FROM pm_milestone  m LEFT JOIN ",
            "project p ON m.project_id = p.id WHERE m.milestone_day >= #{startDate} ORDER BY m.milestone_day"})
    List<PMilestoneDto> getPmMilestoneByTimeRange(Date startDate);

    @Select({" SELECT m.*, p.`name` project_name , p.contact_name manager_name ,p.type ,m.end_time ,m.status FROM pm_milestone m LEFT JOIN    ",
            "             project p ON m.project_id = p.id ORDER BY m.milestone_day  "})
    List<PMilestoneDto> getAllPmMilestone();

    @Update("UPDATE pm_milestone SET publish_flag = TRUE WHERE milestone_day >= #{startDate} AND milestone_day <= #{endDate} ")
    void updateRecordStatus(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Update({"update pm_milestone set project_estimate_day=#{date},update_time=now() where project_id=#{id}"})
    void updateProjectEstimateDayByProjectIdAndDate(@Param("id") Long projectId, @Param("date") Date projectEstimateDay);

    @Select({"select * from pm_milestone where project_id=#{0}"})
    PmMilestone selectByProjectId(Long projectId);

    @Select({"select update_time from pm_milestone order by update_time desc limit 1"})
    Date selectLastUpdateTime();
}