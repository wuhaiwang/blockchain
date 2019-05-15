package com.seasun.management.mapper.cp;

import com.seasun.management.model.cp.OrdissueChangeLogs;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface OrdissueChangeLogsMapper {
    @Delete({
        "delete from cp.ordissuechangelogs",
        "where ID = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into cp.ordissuechangelogs (HistoryID, ChangeFieldName, ",
        "PreValue, PostValue, ",
        "Status)",
        "values (#{historyId,jdbcType=BIGINT}, #{changeFieldName,jdbcType=VARCHAR}, ",
        "#{preValue,jdbcType=VARCHAR}, #{postValue,jdbcType=VARCHAR}, ",
        "#{status,jdbcType=INTEGER})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(OrdissueChangeLogs record);

    int insertSelective(OrdissueChangeLogs record);

    @Select({
        "select",
        "ID, HistoryID, ChangeFieldName, PreValue, PostValue, Status",
        "from cp.ordissuechangelogs",
        "where ID = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.cp.OrdissueChangeLogsMapper.BaseResultMap")
    OrdissueChangeLogs selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrdissueChangeLogs record);

    @Update({
        "update cp.ordissuechangelogs",
        "set HistoryID = #{historyId,jdbcType=BIGINT},",
          "ChangeFieldName = #{changeFieldName,jdbcType=VARCHAR},",
          "PreValue = #{preValue,jdbcType=VARCHAR},",
          "PostValue = #{postValue,jdbcType=VARCHAR},",
          "Status = #{status,jdbcType=INTEGER}",
        "where ID = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(OrdissueChangeLogs record);
}