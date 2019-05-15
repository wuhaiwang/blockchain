package com.seasun.management.mapper.cp;

import com.seasun.management.model.cp.ReqissueChangeLogs;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface ReqissueChangeLogsMapper {
    @Delete({
        "delete from cp.reqissuechangelogs",
        "where ID = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into cp.reqissuechangelogs (HistoryID, ChangeFieldName, ",
        "PreValue, PostValue, ",
        "Status)",
        "values (#{historyId,jdbcType=BIGINT}, #{changeFieldName,jdbcType=VARCHAR}, ",
        "#{preValue,jdbcType=VARCHAR}, #{postValue,jdbcType=VARCHAR}, ",
        "#{status,jdbcType=INTEGER})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(ReqissueChangeLogs record);

    int insertSelective(ReqissueChangeLogs record);

    @Select({
        "select",
        "ID, HistoryID, ChangeFieldName, PreValue, PostValue, Status",
        "from cp.reqissuechangelogs",
        "where ID = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.cp.ReqissueChangeLogsMapper.BaseResultMap")
    ReqissueChangeLogs selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ReqissueChangeLogs record);

    @Update({
        "update cp.reqissuechangelogs",
        "set HistoryID = #{historyId,jdbcType=BIGINT},",
          "ChangeFieldName = #{changeFieldName,jdbcType=VARCHAR},",
          "PreValue = #{preValue,jdbcType=VARCHAR},",
          "PostValue = #{postValue,jdbcType=VARCHAR},",
          "Status = #{status,jdbcType=INTEGER}",
        "where ID = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(ReqissueChangeLogs record);
}