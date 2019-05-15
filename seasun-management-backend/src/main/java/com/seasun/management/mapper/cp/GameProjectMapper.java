package com.seasun.management.mapper.cp;

import com.seasun.management.model.cp.GameProject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface GameProjectMapper {
    @Delete({
        "delete from cp.gameproject",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
        "insert into cp.gameproject (Name, City, ",
        "Code, ProductLine, ",
        "Active, CostCenter, ",
        "ProjectOrder)",
        "values (#{name,jdbcType=VARCHAR}, #{city,jdbcType=VARCHAR}, ",
        "#{code,jdbcType=VARCHAR}, #{productLine,jdbcType=VARCHAR}, ",
        "#{active,jdbcType=TINYINT}, #{costCenter,jdbcType=VARCHAR}, ",
        "#{projectOrder,jdbcType=VARCHAR})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Integer.class)
    int insert(GameProject record);

    int insertSelective(GameProject record);

    @Select({
        "select",
        "ID, Name, City, Code, ProductLine, Active, CostCenter, ProjectOrder",
        "from cp.gameproject",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    @ResultMap("com.seasun.management.mapper.cp.GameProjectMapper.BaseResultMap")
    GameProject selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GameProject record);

    @Update({
        "update cp.gameproject",
        "set Name = #{name,jdbcType=VARCHAR},",
          "City = #{city,jdbcType=VARCHAR},",
          "Code = #{code,jdbcType=VARCHAR},",
          "ProductLine = #{productLine,jdbcType=VARCHAR},",
          "Active = #{active,jdbcType=TINYINT},",
          "CostCenter = #{costCenter,jdbcType=VARCHAR},",
          "ProjectOrder = #{projectOrder,jdbcType=VARCHAR}",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(GameProject record);
}