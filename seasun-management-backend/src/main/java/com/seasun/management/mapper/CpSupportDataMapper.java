package com.seasun.management.mapper;

import com.seasun.management.model.CpSupportData;
import com.seasun.management.vo.cp.CpSupportVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface CpSupportDataMapper {
    @Delete({
        "delete from cp_support_data",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into cp_support_data (type, value, ",
        "seq)",
        "values (#{type,jdbcType=VARCHAR}, #{value,jdbcType=VARCHAR}, ",
        "#{seq,jdbcType=INTEGER})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(CpSupportData record);

    int insertSelective(CpSupportData record);

    @Select({
        "select",
        "id, type, value, seq",
        "from cp_support_data",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.CpSupportDataMapper.BaseResultMap")
    CpSupportData selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CpSupportData record);

    @Update({
        "update cp_support_data",
        "set type = #{type,jdbcType=VARCHAR},",
          "value = #{value,jdbcType=VARCHAR},",
          "seq = #{seq,jdbcType=INTEGER}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(CpSupportData record);

    @Select({"select type,value from cp_support_data where type in ('1','2') order by type,seq asc"})
    List<CpSupportVo> getSupport();
}