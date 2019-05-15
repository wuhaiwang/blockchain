package com.seasun.management.mapper;

import com.seasun.management.dto.PmPlanDto;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.PmPlan;
import com.seasun.management.vo.PmFinanceDetailVo;
import com.seasun.management.vo.StatusVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

public interface PmPlanMapper {
    @Delete({
            "delete from pm_plan",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into pm_plan (project_id, year, ",
            "version, status,project_estimate_day, ",
            "create_time, update_time)",
            "values (#{projectId,jdbcType=BIGINT}, #{year,jdbcType=INTEGER}, ",
            "#{version,jdbcType=INTEGER}, #{status,jdbcType=INTEGER},#{projectEstimateDay,jdbcType=TIMESTAMP},  ",
            "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(PmPlan record);

    int insertSelective(PmPlan record);

    @Select({
            "select",
            "id, project_id, year, version, status, project_estimate_day, create_time, update_time",
            "from pm_plan",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.PmPlanMapper.BaseResultMap")
    PmPlan selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PmPlan record);

    @Update({
            "update pm_plan",
            "set project_id = #{projectId,jdbcType=BIGINT},",
            "year = #{year,jdbcType=INTEGER},",
            "version = #{version,jdbcType=INTEGER},",
            "status = #{status,jdbcType=INTEGER},",
            "project_estimate_day = #{projectEstimateDay,jdbcType=INTEGER},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(PmPlan record);

    /* the flowing are user defined ... */

    @Select({"select p.id project_id,p.name projectName,p.status projectStatus,pm.id,pm.status,pm.project_estimate_day,pm.create_time,pm.update_time from (select id,name,status from project where  active_flag=1 and service_line !='平台' and service_line !='汇总' and service_line !='分摊项' and app_show_mode > 0) p left join (select pn.* from pm_plan pn left join (select project_id, max(version) version from pm_plan group by project_id ) pp on  pn.project_id=pp.project_id  where pn.version>pp.version-2) pm on pm.project_id=p.id"})
    List<PmPlanDto> selectProjectsLastTwoPmplan();

    List<StatusVo> selectProjectsStatusByProjectAssistant(List<Long> manageProjectIds);

    @Select({"select * from pm_plan where status!=#{1} and project_id=#{0} order by version desc limit 1"})
    PmPlan selectBranchByProjectIdAndStatus(Long projectId, Integer status);

    @Select({"select * from pm_plan where project_id=#{0} order by version desc limit 1"})
    PmPlan selectLastVersionByProjectId(Long projectId);

    @Update("update pm_plan set status=1, update_time=#{1} where project_id=#{0} and status=0 and version=(select a.version from(select version from pm_plan where project_id=#{0} order by version desc limit 1) a)")
    int submitByProjectId(Long projectId, Date updateTime);

    @Update({"update pm_plan set status = 2,update_time=#{1} where id=#{0}"})
    int publishById(Long id, Date updateTime);

    List<PmFinanceDetailVo> selectPmFinanceDetail(PmFinanceDetailVo pmFinanceDetailVo);
}