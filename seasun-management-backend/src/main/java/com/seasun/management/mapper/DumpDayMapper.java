package com.seasun.management.mapper;

import com.seasun.management.dto.AppDumpDayDto;
import com.seasun.management.model.DumpDay;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface DumpDayMapper {
    @Delete({
        "delete from dump_day",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
        "insert into dump_day (date, project, ",
        "version, versionex, ",
        "dump_count, dump_machine_count, ",
        "active_machine_count, session_count, ",
        "dump_type_count)",
        "values (#{date,jdbcType=TIMESTAMP}, #{project,jdbcType=VARCHAR}, ",
        "#{version,jdbcType=VARCHAR}, #{versionex,jdbcType=VARCHAR}, ",
        "#{dumpCount,jdbcType=INTEGER}, #{dumpMachineCount,jdbcType=INTEGER}, ",
        "#{activeMachineCount,jdbcType=INTEGER}, #{sessionCount,jdbcType=INTEGER}, ",
        "#{dumpTypeCount,jdbcType=INTEGER})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Integer.class)
    int insert(DumpDay record);

    int insertSelective(DumpDay record);

    @Select({
        "select",
        "id, date, project, version, versionex, dump_count, dump_machine_count, active_machine_count, ",
        "session_count, dump_type_count",
        "from dump_day",
        "where id = #{id,jdbcType=INTEGER}"
    })
    @ResultMap("com.seasun.management.mapper.DumpDayMapper.BaseResultMap")
    DumpDay selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DumpDay record);

    @Update({
        "update dump_day",
        "set date = #{date,jdbcType=TIMESTAMP},",
          "project = #{project,jdbcType=VARCHAR},",
          "version = #{version,jdbcType=VARCHAR},",
          "versionex = #{versionex,jdbcType=VARCHAR},",
          "dump_count = #{dumpCount,jdbcType=INTEGER},",
          "dump_machine_count = #{dumpMachineCount,jdbcType=INTEGER},",
          "active_machine_count = #{activeMachineCount,jdbcType=INTEGER},",
          "session_count = #{sessionCount,jdbcType=INTEGER},",
          "dump_type_count = #{dumpTypeCount,jdbcType=INTEGER}",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(DumpDay record);

    /* the flowing are user defined ... */

    @Select({"select * from (select dump_count,dump_machine_count,active_machine_count,DATE_FORMAT(date,'%c-%e') dateStr,date,CONCAT(convert(dump_count/active_machine_count*100,decimal(10,1)),'%') dumpProbability,CONCAT(convert(dump_machine_count/active_machine_count*100,decimal(10,1)),'%') dumpMachineProbability,versionex from dump_day where project=#{0} order by date desc limit #{1})d order by d.date"})
    List<AppDumpDayDto> selectByProjectAndLastday(String project, Integer i);
}