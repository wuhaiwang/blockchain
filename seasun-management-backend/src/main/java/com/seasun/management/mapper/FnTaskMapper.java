package com.seasun.management.mapper;

import com.seasun.management.model.FnTask;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface FnTaskMapper {
    @Delete({
            "delete from fn_task",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into fn_task (tag, file_name, type, ",
            "total, current, ",
            "status, process_message, ",
            "result_message, create_by, ",
            "create_time, update_time)",
            "values (#{tag,jdbcType=VARCHAR},#{fileName,jdbcType=VARCHAR}, #{type,jdbcType=VARCHAR}, ",
            "#{total,jdbcType=INTEGER}, #{current,jdbcType=INTEGER}, ",
            "#{status,jdbcType=VARCHAR}, #{processMessage,jdbcType=VARCHAR}, ",
            "#{resultMessage,jdbcType=VARCHAR}, #{createBy,jdbcType=VARCHAR}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FnTask record);

    int insertSelective(FnTask record);

    @Select({
            "select",
            "id, tag, file_name, type, total, current, status, process_message, result_message, create_by, ",
            "create_time, update_time",
            "from fn_task",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FnTaskMapper.BaseResultMap")
    FnTask selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FnTask record);

    @Update({
            "update fn_task",
            "set tag = #{tag,jdbcType=VARCHAR},",
            "file_name = #{fileName,jdbcType=VARCHAR},",
            "type = #{type,jdbcType=VARCHAR},",
            "total = #{total,jdbcType=INTEGER},",
            "current = #{current,jdbcType=INTEGER},",
            "status = #{status,jdbcType=VARCHAR},",
            "process_message = #{processMessage,jdbcType=VARCHAR},",
            "result_message = #{resultMessage,jdbcType=VARCHAR},",
            "create_by = #{createBy,jdbcType=VARCHAR},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FnTask record);

    /* the flowing are user defined ... */
    @Select("select * from fn_task where status = #{status} limit 1")
    FnTask selectOneByStatus(String status);

    @Select("select * from fn_task order by id desc limit #{limitNumber}")
    List<FnTask> selectAllOrderByIdDescAndLimitBy(int limitNumber);

    @Select("select * from fn_task where type = #{type} order by id desc")
    List<FnTask> selectByType(@Param("type") String type);

    @Select("select * from fn_task where status in ('init','processing')  limit 1")
    FnTask selectUnfinishedOne();

    @Update("update fn_task set status = 'discard' ,result_message = #{resultMessage} where status in ('init','processing')")
    int discardAllUnfinishedTask(String resultMessage);

    @Update("update fn_task set status = 'discard' ,result_message = #{resultMessage} where id = #{id}")
    int discardTask(@Param("id") Long id, @Param("resultMessage") String resultMessage);

    @Insert({
            "insert into fn_task (id,tag, type, ",
            "total, current, ",
            "status, process_message, ",
            "result_message, create_by, ",
            "create_time, update_time)",
            "values (#{tag,jdbcType=VARCHAR}, #{type,jdbcType=VARCHAR}, ",
            "#{total,jdbcType=INTEGER}, #{current,jdbcType=INTEGER}, ",
            "#{status,jdbcType=VARCHAR}, #{processMessage,jdbcType=VARCHAR}, ",
            "#{resultMessage,jdbcType=VARCHAR}, #{createBy,jdbcType=VARCHAR}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})"
    })
    int insertWithId(FnTask record);
}