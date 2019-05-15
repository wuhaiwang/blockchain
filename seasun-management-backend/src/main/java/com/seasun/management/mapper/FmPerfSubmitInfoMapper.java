package com.seasun.management.mapper;

import com.seasun.management.model.FmPerfSubmitInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface FmPerfSubmitInfoMapper {
    @Delete({
            "delete from fm_perf_submit_info",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into fm_perf_submit_info (plat_id, perf_submit_flag, ",
            "year, month, create_time, ",
            "update_time)",
            "values (#{platId,jdbcType=BIGINT}, #{perfSubmitFlag,jdbcType=BIT}, ",
            "#{year,jdbcType=INTEGER}, #{month,jdbcType=INTEGER}, #{createTime,jdbcType=TIMESTAMP}, ",
            "#{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(FmPerfSubmitInfo record);

    int insertSelective(FmPerfSubmitInfo record);

    @Select({
            "select",
            "id, plat_id, perf_submit_flag, year, month, create_time, update_time",
            "from fm_perf_submit_info",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FmPerfSubmitInfoMapper.BaseResultMap")
    FmPerfSubmitInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FmPerfSubmitInfo record);

    @Update({
            "update fm_perf_submit_info",
            "set plat_id = #{platId,jdbcType=BIGINT},",
            "perf_submit_flag = #{perfSubmitFlag,jdbcType=BIT},",
            "year = #{year,jdbcType=INTEGER},",
            "month = #{month,jdbcType=INTEGER},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FmPerfSubmitInfo record);


    // these are user definition
    @Select("select perf_submit_flag from fm_perf_submit_info where year =#{year} and month=#{month} and plat_id =#{platId}")
    Boolean getPerfSubmitFlag(@Param("platId") Long platId, @Param("year") int year, @Param("month") int month);

    @Update("update fm_perf_submit_info set perf_submit_flag = not perf_submit_flag " +
            "where year =#{year} and month =#{month} and plat_id =#{platId} and  perf_submit_flag=#{nowStatus}")
    int updatePerfSubmitFlag(@Param("platId") Long platId, @Param("year") int year, @Param("month") int month, @Param("nowStatus") boolean nowStatus);

    @Select("select distinct * from fm_perf_submit_info where plat_id =#{platId} and year =#{year} and month =#{month}")
    FmPerfSubmitInfo selectByPlatIdAndTime(@Param("platId") Long platId, @Param("year") int year, @Param("month") int month);

    List<FmPerfSubmitInfo> selectByPlatIds(@Param("platIds") List<Long> platIds, @Param("year") int year, @Param("month") int month);

}