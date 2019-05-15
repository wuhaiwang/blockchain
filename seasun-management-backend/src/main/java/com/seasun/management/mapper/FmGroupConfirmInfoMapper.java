package com.seasun.management.mapper;

import com.seasun.management.dto.HistoryStatusDto;
import com.seasun.management.model.FmGroupConfirmInfo;
import com.seasun.management.vo.FmGroupConfirmInfoVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface FmGroupConfirmInfoMapper {
    @Delete({
            "delete from fm_group_confirm_info",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into fm_group_confirm_info (year, month, ",
            "performance_work_group_id, plat_id, ",
            "project_id, plat_confirmer_id, ",
            "project_confirmer_id, status)",
            "values (#{year,jdbcType=INTEGER}, #{month,jdbcType=INTEGER}, ",
            "#{performanceWorkGroupId,jdbcType=BIGINT}, #{platId,jdbcType=BIGINT}, ",
            "#{projectId,jdbcType=BIGINT}, #{platConfirmerId,jdbcType=BIGINT}, ",
            "#{projectConfirmerId,jdbcType=BIGINT}, #{status,jdbcType=VARCHAR})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FmGroupConfirmInfo record);

    int insertSelective(FmGroupConfirmInfo record);

    @Select({
            "select",
            "id, year, month, performance_work_group_id, plat_id, project_id, plat_confirmer_id, ",
            "project_confirmer_id, status",
            "from fm_group_confirm_info",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FmGroupConfirmInfoMapper.BaseResultMap")
    FmGroupConfirmInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FmGroupConfirmInfo record);

    @Update({
            "update fm_group_confirm_info",
            "set year = #{year,jdbcType=INTEGER},",
            "month = #{month,jdbcType=INTEGER},",
            "performance_work_group_id = #{performanceWorkGroupId,jdbcType=BIGINT},",
            "plat_id = #{platId,jdbcType=BIGINT},",
            "project_id = #{projectId,jdbcType=BIGINT},",
            "plat_confirmer_id = #{platConfirmerId,jdbcType=BIGINT},",
            "project_confirmer_id = #{projectConfirmerId,jdbcType=BIGINT},",
            "status = #{status,jdbcType=VARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FmGroupConfirmInfo record);

    /* the flowing are user defined ... */

    @Select({
            "select i.*, g.name performance_work_group_name, pl.name plat_name, p.name project_name,",
            "concat(ul.last_name,ul.first_name) plat_confirmer_name, concat(u.last_name,u.first_name) project_confirmer_name",
            "from fm_group_confirm_info i",
            "left join performance_work_group g on g.id = i.performance_work_group_id",
            "left join project pl on pl.id = i.plat_id",
            "left join project p on p.id = i.project_id",
            "left join user ul on ul.id = i.plat_confirmer_id",
            "left join user u on u.id = i.project_confirmer_id",
            "where i.year = #{year} and i.month = #{month}"
    })
    List<FmGroupConfirmInfoVo> selectAllByYearMonth(@Param("year") Integer year, @Param("month") Integer month);

    @Select({
            "select i.*, g.name performance_work_group_name, pl.name plat_name, p.name project_name,",
            "concat(ul.last_name,ul.first_name) plat_confirmer_name, concat(u.last_name,u.first_name) project_confirmer_name",
            "from fm_group_confirm_info i",
            "left join performance_work_group g on g.id = i.performance_work_group_id",
            "left join project pl on pl.id = i.plat_id",
            "left join project p on p.id = i.project_id",
            "left join user ul on ul.id = i.plat_confirmer_id",
            "left join user u on u.id = i.project_confirmer_id",
            "where i.performance_work_group_id = #{performanceWorkGroupId} and i.year = #{year} and i.month = #{month}"
    })
    List<FmGroupConfirmInfoVo> selectAllByPerformanceWorkGroupIdAndYearMonth(@Param("performanceWorkGroupId") Long performanceWorkGroupId, @Param("year") Integer year, @Param("month") Integer month);

    @Select({
            "select i.*, g.name performance_work_group_name, pl.name plat_name, p.name project_name,",
            "concat(ul.last_name,ul.first_name) plat_confirmer_name, concat(u.last_name,u.first_name) project_confirmer_name",
            "from fm_group_confirm_info i",
            "left join performance_work_group g on g.id = i.performance_work_group_id",
            "left join project pl on pl.id = i.plat_id",
            "left join project p on p.id = i.project_id",
            "left join user ul on ul.id = i.plat_confirmer_id",
            "left join user u on u.id = i.project_confirmer_id",
            "left join fm_user_role ur on ur.project_id = i.project_id and ur.plat_id = i.plat_id",
            "where ur.user_id = #{userId} and i.year = #{year} and i.month = #{month}",
            "order by i.project_id"
    })
    List<FmGroupConfirmInfoVo> selectAllByProjectConfirmerAndYearMonth(@Param("userId") Long userId, @Param("year") Integer year, @Param("month") Integer month);

    @Select({
            "select i.*, g.name performance_work_group_name, pl.name plat_name, p.name project_name,",
            " concat(ul.last_name,ul.first_name) plat_confirmer_name, concat(up.last_name,up.first_name) project_confirmer_name",
            " from fm_group_confirm_info i",
            " left join performance_work_group g on g.id = i.performance_work_group_id",
            " left join project pl on pl.id = i.plat_id",
            " left join project p on p.id = i.project_id",
            " left join user ul on ul.id = i.plat_confirmer_id",
            " left join user up on up.id = i.project_confirmer_id",
            " left join fm_user_role r on i.project_id = r.project_id ",
            " left join user u on u.id = r.user_id",
            " where r.user_id = #{0}  and i.year = #{1} and i.month = #{2} and r.role_id = 4",
            " order by i.project_id"
    })
    List<FmGroupConfirmInfoVo> selectFixSencondConfirmInfoByProjectConfirmerAndYearAndMonth(Long userId, Integer year, Integer month);

    @Select({
            "select i.*",
            "from fm_group_confirm_info i",
            "left join fm_user_role r on i.project_id = r.project_id ",
            "where r.user_id = #{0} and r.role_id = #{1}",
    })
    List<FmGroupConfirmInfo> selectAllByUserIdAndFmRoleId(Long userId, Long roleid);


    @Select({
            "select i.*, g.name performance_work_group_name, pl.name plat_name, p.name project_name,",
            "concat(ul.last_name,ul.first_name) plat_confirmer_name, concat(u.last_name,u.first_name) project_confirmer_name",
            "from fm_group_confirm_info i",
            "left join performance_work_group g on g.id = i.performance_work_group_id",
            "left join project pl on pl.id = i.plat_id",
            "left join project p on p.id = i.project_id",
            "left join user ul on ul.id = i.plat_confirmer_id",
            "left join user u on u.id = i.project_confirmer_id",
            "where i.id = #{id}",
            "limit 1"
    })
    FmGroupConfirmInfoVo selectById(@Param("id") Long id);

    int batchInsertByPlatConfirmerSubmit(List<FmGroupConfirmInfo> list);

    int batchUpdateConfirmerAndStatusByPks(List<FmGroupConfirmInfo> list);

    @Select("SELECT * FROM fm_group_confirm_info WHERE plat_id = #{projectId} order by year desc, month desc")
    List<FmGroupConfirmInfo> selectAllConfirmInfoByProjectId(@Param("projectId") long projectId);

    @Select("SELECT * FROM fm_group_confirm_info WHERE plat_id = #{platId} order by year desc, month desc")
    List<FmGroupConfirmInfo> selectAllConfirmInfoByPlatId(@Param("platId") long platId);

    @Delete("delete from  fm_group_confirm_info WHERE year =#{year} AND month =#{month}")
    void deleteByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Select("SELECT f.*,p.name projectName,CONCAT(u.last_name,u.first_name) projectConfirmerName FROM fm_group_confirm_info f LEFT JOIN project p on f.project_id=p.id LEFT JOIN user u on u.id=f.project_confirmer_id where year=#{year} and month=#{month}")
    List<FmGroupConfirmInfoVo> selectAllConfirmInfoByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Select({"select * from fm_group_confirm_info where year=#{1} and month = #{2} and plat_confirmer_id =#{0}"})
    List<FmGroupConfirmInfo> selectAllByPlatConfirmerIdAndYearMonth(Long performanceWorkGroupId, Integer year, Integer month);


}