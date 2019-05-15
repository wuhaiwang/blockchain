package com.seasun.management.mapper;

import com.seasun.management.dto.FnPlatShareConfigUserDTO;
import com.seasun.management.dto.SimUserShareDto;
import com.seasun.management.dto.SimpleSharePlatWeekDto;
import com.seasun.management.model.FnPlatWeekShareConfig;
import com.seasun.management.vo.SimpleSharePlatWeekVo;
import com.seasun.management.vo.WeekShareConfigVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface FnPlatWeekShareConfigMapper {
    @Delete({
            "delete from fn_plat_week_share_config",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into fn_plat_week_share_config (plat_id, project_id, work_group_id, ",
            "create_by, year, month, ",
            "week, share_pro, ",
            "share_amount, remark, ",
            "lock_flag, create_time, ",
            "update_time)",
            "values (#{platId,jdbcType=BIGINT}, #{projectId,jdbcType=BIGINT}, #{workGroupId,jdbcType=BIGINT}, ",
            "#{createBy,jdbcType=BIGINT}, #{year,jdbcType=INTEGER}, #{month,jdbcType=INTEGER}, ",
            "#{week,jdbcType=INTEGER}, #{sharePro,jdbcType=DECIMAL}, ",
            "#{shareAmount,jdbcType=DECIMAL}, #{remark,jdbcType=VARCHAR}, ",
            "#{lockFlag,jdbcType=BIT}, #{createTime,jdbcType=TIMESTAMP}, ",
            "#{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FnPlatWeekShareConfig record);

    int insertSelective(FnPlatWeekShareConfig record);

    @Select({
            "select",
            "id, plat_id, project_id, create_by, year, month, week, share_pro, share_amount, ",
            "remark, lock_flag, create_time, update_time",
            "from fn_plat_week_share_config",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FnPlatWeekShareConfigMapper.BaseResultMap")
    FnPlatWeekShareConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FnPlatWeekShareConfig record);

    @Update({
            "update fn_plat_week_share_config",
            "set plat_id = #{platId,jdbcType=BIGINT},",
            "project_id = #{projectId,jdbcType=BIGINT},",
            "create_by = #{createBy,jdbcType=BIGINT},",
            "year = #{year,jdbcType=INTEGER},",
            "month = #{month,jdbcType=INTEGER},",
            "week = #{week,jdbcType=INTEGER},",
            "share_pro = #{sharePro,jdbcType=DECIMAL},",
            "share_amount = #{shareAmount,jdbcType=DECIMAL},",
            "remark = #{remark,jdbcType=VARCHAR},",
            "lock_flag = #{lockFlag,jdbcType=BIT},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FnPlatWeekShareConfig record);

    /* the flowing are user defined ... */

    @Select({"select a.*,v.city,v.name projectName,v.usedNamesStr projectUsedNames from (SELECT f.remark,sum(f.share_pro) share_pro,sum(f.share_amount) shareAmount, f.id, f.plat_id platId, f.project_id projectId, f.year, f.month, f.week, f.create_by createBy,f.lock_flag lockFlag FROM `fn_plat_week_share_config` f where f.create_by =#{0} and f.week=#{1} and f.year=#{2} GROUP BY project_id) a left join v_project_name_info v on a.projectId=v.id "})
    List<WeekShareConfigVo> selectUserDataByUserIdAndWeekAndYear(Long userId, Integer week, Integer year);

    @Select({"select a.*,v.city,v.name  projectName,v.usedNamesStr as projectUsedNames from (select year,month,project_id projectId,plat_id platId,week,sum(share_amount) shareAmount FROM `fn_plat_week_share_config` where plat_id=#{0} and week=#{1} and year=#{2} GROUP BY project_id) a left join v_project_name_info v on v.id=a.projectId"})
    List<WeekShareConfigVo> selectPlatDataByPlatIdAndWeekAndYear(Long platId, Integer week, Integer year);

    @Select({"select * from fn_plat_week_share_config f  where f.create_by =#{0} and f.week=#{1} and f.year=#{2} order by f.month"})
    List<FnPlatWeekShareConfig> selectByUserIdAndWeekAndYear(Long userId, Integer week, Integer year);

    List<FnPlatWeekShareConfig> selectByCond(FnPlatWeekShareConfig record);

    void batchInsert(List<? extends FnPlatWeekShareConfig> data);


    void batchUpdate(List<FnPlatWeekShareConfig> weekShareConfigVos);

    @Select({"select * from  fn_plat_week_share_config f where f.create_by=#{0} and f.week=(select max(week) from fn_plat_week_share_config where create_by=#{0} and year=#{1}) and f.year={1} "})
    List<FnPlatWeekShareConfig> selectLastWeekDataByUserIdAndYear(Long useId, Integer year);

    @Delete({"delete from fn_plat_week_share_config where create_by=#{0} and week=#{2} and year=#{1}"})
    void delectByUserIdAndYearAndWeek(Long useId, Integer year, Integer week);

    void delectByUserIdsAndYearAndWeekAndPlatId(@Param("userIds") List<Long> useIds, @Param("year") Integer year, @Param("week") Integer week, @Param("platId") Long platId);

    @Select({"SELECT concat(u.last_name,u.first_name) createUserName, a.* from (select f.remark,sum(f.share_pro) share_pro,sum(f.share_amount) shareAmount, f.id, f.plat_id platId, f.project_id projectId, f.year, f.month, f.week, f.create_by createBy,f.lock_flag lockFlag FROM `fn_plat_week_share_config` f where f.`plat_id`=#{0} and f.project_id=#{1} and f.`year`=#{2} and f.week=#{3} GROUP BY create_by) a left join user u on u.id=a.createBy "})
    List<SimpleSharePlatWeekVo> selectSimpleSharePlatWeekVoByCond(Long platId, Long projectId, Integer year, Integer week);

    @Select({"select * from fn_plat_week_share_config where plat_id=#{0} and year=#{1} and month=#{2}"})
    List<FnPlatWeekShareConfig> selectMonthShareConfigByPlatIdAndMonthAndWeek(Long platId, Integer year, Integer month);

    @Select({"select fn.*,u.employee_no,concat(u.last_name,u.first_name) userName,u.work_group_id workGroupId,CASE d.gender WHEN 0 THEN '男' ELSE '女' END gender , d.work_status,date_format(u.in_date,'%Y/%c/%e') inDate,w.`name` workGroupName,d.post from fn_plat_week_share_config fn left join user u on u.id=fn.create_by left join user_detail d on d.user_id=u.id left join work_group w on w.id=u.work_group_id  where fn.plat_id=#{0} and fn.year=#{1} and fn.week=#{2} "})
    List<FnPlatShareConfigUserDTO> selectConfigUserDTOByPlatIdAndYearAndMonth(Long platId, Integer year, Integer week);

    List<FnPlatShareConfigUserDTO> selectConfigUserDTOByPlatIdsAndYearAndMonth(@Param("platIds") List<Long> platIds, @Param("year") Integer year, @Param("week") Integer week);

    @Select({"select fn.shareAmount,fn.lockFlag, u.id,concat(u.last_name,u.first_name) name,u.login_id loginId from (select create_by,sum(share_amount) shareAmount,lock_flag lockFlag from fn_plat_week_share_config where plat_id=#{0} and week=#{2} and year=#{1} group by create_by) fn left join user u on u.id=fn.create_by "})
    List<SimUserShareDto> selectSimUserShareVoByPlatIdAndYearAndWeek(Long platId, Integer year, Integer week);

    List<SimUserShareDto> selectSimUserShareVoByPlatIdAndYearAndWeekAndUserIds(@Param("platId") Long platId, @Param("year") Integer year, @Param("week") Integer week, @Param("userIds") List<Long> useIds);

    List<WeekShareConfigVo> selectPlatDataByPlatIdAndWeekAndYearAndUserIds(@Param("platId") Long platId, @Param("week") Integer week, @Param("year") Integer year, @Param("userIds") List<Long> useIds);

    List<SimpleSharePlatWeekVo> selectSimpleSharePlatWeekVoByCondAndUserIds(@Param("platId") Long platId, @Param("projectId") Long proejctId, @Param("year") Integer year, @Param("week") Integer week, @Param("userIds") List<Long> useIds);

    void updateLockFlagByPlatIdAndYearAndWeekAndUserIds(@Param("lockFlag") Boolean lockFlag,@Param("platId") Long platId,@Param("year") Integer year, @Param("week") Integer week,@Param("userIds") List<Long> userIds);

    void updateLockFlagByPlatIdAndYearAndWeekAndWorkGroupIds(@Param("lockFlag") Boolean lockFlag,@Param("platId") Long platId,@Param("year") Integer year, @Param("week") Integer week,@Param("groupIds") List<Long> groupIds);

    List<FnPlatShareConfigUserDTO> selectConfigUserDTOByPlatIdAndYearAndMonthAndUserIds(@Param("platId") Long platId, @Param("year") Integer year, @Param("week") Integer week, @Param("userIds") List<Long> useIds);

    @Select({"select distinct create_by from fn_plat_week_share_config where plat_id=#{0} and year=#{1} and week=#{2} and lock_flag=1"})
    List<Long> selectLockUserIdsByPlatIdAndYearAndWeek(Long platId, Integer year, Integer week);

    @Select({"select max(a.shareAmount) from (select sum(share_amount) shareAmount from fn_plat_week_share_config where plat_id=#{0} and year=#{1} and week=#{2} group by create_by) a"})
    Float selectMaxWorkDayByPlatIdAndYearAndWeek(Long platId, Integer year, Integer week);

    @Delete({"delete from fn_plat_week_share_config where create_by=#{0} and year=#{1} and week=#{2} and project_id=#{3}"})
    Integer delectByUserIdAndYearAndWeekAndProjectId(Long createBy, Integer year, Integer week, Long projectId);

    @Select({"select max(week) from fn_plat_week_share_config where year=#{0}"})
    Integer selectLastWeekByYear(Integer year);

    @Select({"select create_by from fn_plat_week_share_config where plat_id=#{0} and year=#{1} and week=#{2} group by create_by"})
    List<Long> selectWritedUserIdsByPlatIdAndYearAndWeek(Long platId, Integer year, Integer week);

    @Select({"select f.remark,sum(f.share_amount) shareAmount, f.project_id projectId, f.create_by createBy,f.lock_flag lockFlag,f.id FROM `fn_plat_week_share_config` f where f.`plat_id`=#{0}  and f.`year`=#{1} and f.week=#{2} GROUP BY create_by,project_id"})
    List<SimpleSharePlatWeekDto> selectSimpleSharePlatWeekDtoByPlatIdAndYearAndWeek(Long platId, Integer year, Integer week);

    List<SimpleSharePlatWeekDto> selectSimpleSharePlatWeekDtoByPlatIdAndYearAndWeekAndUserIds(@Param("platId") Long platId, @Param("year") Integer year, @Param("week") Integer week, @Param("userIds") List<Long> userIds);
}