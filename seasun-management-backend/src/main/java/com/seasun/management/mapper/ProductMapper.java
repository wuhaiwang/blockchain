package com.seasun.management.mapper;

import com.seasun.management.dto.AppProductDto;
import com.seasun.management.model.Product;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ProductMapper {
    @Delete({
            "delete from product",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into product (seq, name, ",
            "logo, desc, actors)",
            "values (#{seq,jdbcType=INTEGER}, #{name,jdbcType=VARCHAR}, ",
            "#{logo,jdbcType=VARCHAR}, #{desc,jdbcType=VARCHAR}, #{actors,jdbcType=VARCHAR})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(Product record);

    int insertSelective(Product record);

    @Select({
            "select",
            "id, seq, name, logo, desc, actors",
            "from product",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.ProductMapper.BaseResultMap")
    Product selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Product record);

    @Update({
            "update product",
            "set seq = #{seq,jdbcType=INTEGER},",
            "name = #{name,jdbcType=VARCHAR},",
            "logo = #{logo,jdbcType=VARCHAR},",
            "desc = #{desc,jdbcType=VARCHAR},",
            "actors = #{actors,jdbcType=VARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(Product record);

    /* the flowing are user defined ... */
    @Select({"select * from product order by seq"})
    List<AppProductDto> selectAll();
}