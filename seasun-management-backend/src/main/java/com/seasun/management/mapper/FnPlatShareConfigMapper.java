package com.seasun.management.mapper;

import com.seasun.management.dto.FnPlatShareConfigUserDTO;
import com.seasun.management.dto.FnPlatWeekShareReportDto;
import com.seasun.management.dto.FnPlatWeekShareUserCountDto;
import com.seasun.management.model.FnPlatShareConfig;
import com.seasun.management.vo.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface FnPlatShareConfigMapper {
    @Delete({
            "delete from fn_plat_share_config",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into fn_plat_share_config (plat_id, project_id, ",
            "year, month, share_pro, share_amount,",
            "fixed_number, create_by, ",
            "remark, create_time, ",
            "update_time, weight)",
            "values (#{platId,jdbcType=BIGINT}, #{projectId,jdbcType=BIGINT}, ",
            "#{year,jdbcType=INTEGER}, #{month,jdbcType=INTEGER}, #{sharePro,jdbcType=DECIMAL}, #{shareAmount,jdbcType=DECIMAL},",
            "#{fixedNumber,jdbcType=DECIMAL}, #{createBy,jdbcType=BIGINT}, ",
            "#{remark,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, ",
            "#{updateTime,jdbcType=TIMESTAMP}, #{weight,jdbcType=DECIMAL})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FnPlatShareConfig record);

    int insertSelective(FnPlatShareConfig record);

    @Select({
            "select",
            "id, plat_id, project_id, year, month, share_pro, share_amount, fixed_number, create_by, remark, ",
            "create_time, update_time, weight",
            "from fn_plat_share_config",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FnPlatShareConfigMapper.BaseResultMap")
    FnPlatShareConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FnPlatShareConfig record);

    @Update({
            "update fn_plat_share_config",
            "set plat_id = #{platId,jdbcType=BIGINT},",
            "project_id = #{projectId,jdbcType=BIGINT},",
            "year = #{year,jdbcType=INTEGER},",
            "month = #{month,jdbcType=INTEGER},",
            "share_pro = #{sharePro,jdbcType=DECIMAL},",
            "share_amount = #{shareAmount,jdbcType=DECIMAL},",
            "fixed_number = #{fixedNumber,jdbcType=DECIMAL},",
            "create_by = #{createBy,jdbcType=BIGINT},",
            "remark = #{remark,jdbcType=VARCHAR},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP},",
            "weight = #{weight,jdbcType=DECIMAL}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FnPlatShareConfig record);

    /* the flowing are user defined ... */

    int batchInsert(List<FnPlatShareConfig> platShareConfigs);

    @Select("select a.*,b.name as projectName,b.usedNamesStr as projectUsedNames,b.city from fn_plat_share_config a " +
            "left join v_project_name_info b on  b.id = a.project_id " +
            "where plat_id = #{platId} and  year = #{year} and month = #{month} and create_by = #{userId} order by a.project_id")
    List<FnPlatShareConfigVo> selectMemberDataByCond(@Param("platId") Long platId, @Param("year") int year,
                                                     @Param("month") int month, @Param("userId") Long userId);

    @Select("select v.*,s.share_pro as sumSharePro,s.id as sumShareProId from (select a.*,b.name as projectName ,b.usedNamesStr as projectUsedNames,b.city from v_sum_plat_share_config a \n" +
            "            left join v_project_name_info b on a.project_id = b.id " +
            "            where plat_id = #{platId} and  year = #{year} and month = #{month} " +
            "            order by a.project_id ) v left join fn_sum_share_config s on v.project_id = s.project_id and " +
            "            v.plat_id = s.plat_id and v.year = s.year and v.month = s.month where s.share_pro !=0 order by s.share_pro desc;"
    )
    List<FnPlatShareConfigVo> selectSumDataByCondWithoutZeroFnSumSharePro(@Param("platId") Long platId, @Param("year") int year,
                                                                          @Param("month") int month);


    @Select("select v.*,s.share_pro as sumSharePro,s.id as sumShareProId from (select a.*,b.name as projectName ,b.usedNamesStr as projectUsedNames,b.city from v_sum_plat_share_config a \n" +
            "            left join v_project_name_info b on a.project_id = b.id " +
            "            where plat_id = #{platId} and  year = #{year} and month = #{month} " +
            "            order by a.project_id ) v left join fn_sum_share_config s on v.project_id = s.project_id and " +
            "            v.plat_id = s.plat_id and v.year = s.year and v.month = s.month order by s.share_pro desc;"
    )
    List<FnPlatShareConfigVo> selectSumDataByCond(@Param("platId") Long platId, @Param("year") int year,
                                                  @Param("month") int month);

    @Select("select a.*,b.name as projectName ,b.usedNamesStr as projectUsedNames,b.city from v_sum_plat_share_config a " +
            "left join v_project_name_info b on a.project_id = b.id " +
            "where a.year = #{year} and a.month = #{month} " +
            "order by a.project_id")
    List<FnPlatShareConfigVo> selectSumDataByDate(@Param("year") int year, @Param("month") int month);

    @Select("select a.*,concat(b.last_name,b.first_name) as createUserName from v_real_share_config a " +
            "left join user b on a.create_by = b.id  where plat_id = #{platId} and project_id = #{projectId} and year = #{year} and month = #{month} " +
            "order by a.project_id")
    List<FnPlatShareConfigVo> selectDetailDataByCond(@Param("platId") Long platId, @Param("projectId") Long projectId,
                                                     @Param("year") int year, @Param("month") int month);

    @Select({
            "select * from fn_plat_share_config where plat_id = #{platId} and project_id = #{projectId} and create_by = #{createBy} and year = #{year} and month = #{month} limit 1"
    })
    FnPlatShareConfig selectByCond(FnPlatShareConfig record);

    @Select({"select fn.*,u.employee_no,concat(u.last_name,u.first_name) userName,u.work_group_id workGroupId,CASE d.gender WHEN 0 THEN '男' ELSE '女' END gender , d.work_status,date_format(u.in_date,'%Y/%c/%e') inDate,w.`name` workGroupName,d.post from fn_plat_share_config fn left join user u on u.id=fn.create_by left join user_detail d on d.user_id=u.id left join work_group w on w.id=u.work_group_id  where  fn.plat_id=#{0} and fn.year=#{1} and fn.month=#{2} "})
    List<FnPlatShareConfigUserDTO> selectConfigUserDTOByPlatIdAndYearAndMonth(Long platId, Integer year, Integer month);

    @Select({
            "select c.*,1 as weight,concat(u.last_name,u.first_name) as createUserName from fn_plat_share_config c left join user u on c.create_by = u.id  where plat_id = #{platId} and project_id = #{projectId} and year = #{year} and month = #{month} "
    })
    List<FnPlatShareConfigVo> selectByPlatIdAndProjectIdAndYearAndMonthWithUserInfo(@Param("platId") Long platId, @Param("projectId") Long projectId, @Param("year") int year, @Param("month") int month);

    @Select({
            "select a.*,1 as weight,p.city,p.name as projectName,p.usedNamesStr as projectUsedNames,s.share_pro as sumSharePro, s.id as sumShareProId from (select sum(share_pro) sharePro , sum(fixed_number) fixedNumber, c.plat_id, c.year,c.month,c.project_id from fn_plat_share_config c where c.plat_id = #{platId} and c.year = #{year} and c.month = #{month} group by c.project_id) a " +
                    "left join  v_project_name_info p on a.project_id = p.id left join fn_sum_share_config s on a.plat_id = s.plat_id " +
                    "and a.project_id = s.project_id and a.year = s.year and a.month= s.month order by a.sharePro desc"
    })
    List<FnPlatShareConfigVo> selectByPlatIdAndYearAndMonthWithProjectAndSumShareInfo(@Param("platId") Long platId, @Param("year") int year, @Param("month") int month);

    @Select({
            "select a.*,1 as weight,p.city,p.name as projectName,p.usedNamesStr as projectUsedNames,s.share_pro as sumSharePro, s.id as sumShareProId from (select sum(share_pro) sharePro , sum(fixed_number) fixedNumber, c.plat_id, c.year,c.month,c.project_id from fn_plat_share_config c where c.plat_id = #{platId} and c.year = #{year} and c.month = #{month} group by c.project_id) a " +
                    "left join  v_project_name_info p on a.project_id = p.id left join fn_sum_share_config s on a.plat_id = s.plat_id " +
                    "and a.project_id = s.project_id and a.year = s.year and a.month= s.month where s.share_pro !=0 order by a.sharePro desc"
    })
    List<FnPlatShareConfigVo> selectByPlatIdAndYearAndMonthWithProjectAndSumShareInfoWithoutZeroFnSumSharePro(@Param("platId") Long platId, @Param("year") int year, @Param("month") int month);


    @Select("select * from fn_plat_share_config f ,project p where p.active_flag =1 and p.share_flag = 1 and f.plat_id = #{platId} and f.year = #{year} " +
            "and f.month = #{month} and f.create_by = #{createBy} and p.id = f.project_id")
    List<FnPlatShareConfig> selectByPlatIdAndYearAndMonthAndCreateBy(@Param("platId") Long platId, @Param("year") int year, @Param("month") int month, @Param("createBy") Long createBy);


    @Delete({
            "delete from fn_plat_share_config",
            "where create_by = #{createBy}",
            "and plat_id = #{platId}",
            "and year = #{year}",
            "and month = #{month}"
    })
    int deleteForDateCopy(@Param("createBy") Long createBy, @Param("platId") Long platId, @Param("year") int year, @Param("month") int month);

    @Delete({
            "delete from fn_plat_share_config",
            "where create_by = #{createBy}",
            "and year = #{year}",
            "and month = #{month}"
    })
    int deleteByCreateBy(@Param("createBy") Long createBy, @Param("year") int year, @Param("month") int month);

    @Select("select ifnull(sum(share_pro), 0) from fn_plat_share_config where create_by = #{createBy} and year = #{year} and month = #{month} and id <> #{id}")
    Float selectOtherSumShareProByRecord(FnPlatShareConfig record);


    @Select({"SELECT y.plat_id,y.plat_name,y.sum_share_pro as sharePro,y.lock_flag,CONCAT(user.last_name,user.first_name) manager " +
            "FROM " +
            "( SELECT x.plat_id,x.plat_name,x.sum_share_pro,x.lock_flag,r.user_id FROM " +
            "(SELECT v.id plat_id,v.name plat_name, fn.lock_flag, fn.sum sum_share_pro " +
            " from (select vp.* from cfg_plat_attr cfg left join v_project_name_info vp on cfg.plat_id = vp. id) v LEFT JOIN " +
            "  (SELECT plat_id,SUM(share_pro)sum ,lock_flag " +
            "   from fn_sum_share_config WHERE year= #{year} AND month= #{month} GROUP BY plat_id)" +
            "fn ON v.id= fn.plat_id) x" +
            "  LEFT JOIN r_user_project_perm r " +
            "ON x.plat_id = r.project_id AND r.project_role_id = 1) y" +
            "  LEFT JOIN user ON y.user_id = user.id GROUP BY y.plat_id"})
    List<FnPlatSumProVo> getPlatSumConfigList(@Param("year") int year, @Param("month") int month);

    int batchUpdateLockStatus(@Param("plats") List<Long> plats, @Param("year") int year, @Param("month") int month, @Param("lockFlag") boolean lockFlag);

    @Delete("delete from fn_plat_share_config")
    int deleteAll();

    @Delete("delete from fn_plat_share_config where year = #{0} and month = #{1}")
    int deleteByYearAndMonth(int year, int month);

    @Delete("delete from fn_plat_share_config where plat_id=#{platId} and year=#{year} and month=#{month}")
    int deleteByPlatIdAndYearAndMonth(@Param("platId") long platId, @Param("year") int year, @Param("month") int month);

    @Select({"SELECT s.`id`, s.plat_id, p.`name` plat_name, s.create_by user_id, CONCAT(u.`last_name`, u.`first_name`) user_name, s.share_pro weight",
            "FROM fn_plat_share_config s",
            "LEFT JOIN USER u ON s.`create_by`=u.`id`",
            "LEFT JOIN project p ON s.`plat_id`=p.`id`",
            "WHERE s.project_id=#{projectId} AND s.year=#{year} AND s.month=#{month}"})
    List<FnPlatShareMemberVo> selectMemberByProjectIdAndYearAndMonth(@Param("projectId") long projectId, @Param("year") int year, @Param("month") int month);

    @Select({"select create_by as id ,sum(c.share_pro) totalPro,u.login_id, concat(u.last_name, u.first_name) as name ,c.plat_id from fn_plat_share_config c left join user u on c.create_by = u.id",
            "WHERE c.plat_id=#{platId} and c.year=#{year} AND c.month=#{month}  group by c.create_by order by totalPro asc"})
    List<UserLoginEmailVo> selectMemberByPlatIdAndYearAndMonth(@Param("platId") long platId, @Param("year") int year, @Param("month") int month);

    @Select({"SELECT #{platId} plat_Id, s.year, s.month, s.project_Id, s.create_by, sum(share_pro) share_pro, CONCAT(u.`last_name`, u.`first_name`) userName, p.`name` projectName FROM fn_plat_share_config s",
            "LEFT JOIN project p ON s.`project_id`=p.`id`",
            "LEFT JOIN USER u ON s.`create_by`=u.`id`",
            "WHERE (s.plat_id=#{platId} or s.plat_id in (select id from project where parent_share_id=#{platId} and active_flag=1))",
            "and s.year=#{year} AND s.month=#{month}",
            "group by s.project_id, s.create_by"})
    List<FnPlatShareConfigOfUserVo> selectFnPlatShareConfigByUserByPlatIdAndYearAndMonth(@Param("platId") long platId, @Param("year") int year, @Param("month") int month);

    @Select("SELECT " +
            "            c.plat_id as platId, " +
            "            w. NAME workGroupName, " +
            "            p. NAME AS platName," +
            "            concat(u.last_name, u.first_name) AS userName, " +
            "            c.share_pro as sharePro, " +
            "            p2. NAME AS parentHrProjectName " +
            "            FROM " +
            "            fn_plat_share_config c " +
            "            LEFT JOIN project p ON c.plat_id = p.id " +
            "            LEFT JOIN USER u ON c.create_by = u.id " +
            "            LEFT JOIN work_group w ON u.work_group_id = w.id " +
            "            LEFT JOIN project p2 ON p.parent_hr_id = p2.id " +
            "            WHERE " +
            "            c.project_id = #{projectId} " +
            "            AND c. YEAR = #{year} " +
            "            AND c. MONTH = #{month} " +
            "            AND c.share_pro > 0 " +
            "            ORDER BY " +
            "            parentHrProjectName DESC, " +
            "            platName DESC, " +
            "            workGroupName DESC, " +
            "            share_pro DESC ")
    List<FnProjectShareDetailVo> selectProjectShareDetail(@Param("projectId") long projectId, @Param("year") int year, @Param("month") int month);

    @Select({"select count(1) from (select count(1) from fn_plat_share_config where plat_id=#{0} and year=#{1} and month=#{2} group by create_by) a"})
    int countAllUserByProjectIdWithoutTrainee(Long platId, int year, int month);

    List<FnPlatShareConfigUserDTO> selectConfigUserDTOByPlatIdsAndYearAndMonth(@Param("platIds") List<Long> platIds, @Param("year") Integer year, @Param("month") Integer month);
    List<FnPlatWeekShareReportDto> selectWeekly(FnPlatWeekShareReportDto fnPlatWeekShareReportDto);
    List<FnPlatWeekShareUserCountDto> selectUserNo(@Param("platId") Long platId);
}