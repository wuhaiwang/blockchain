package com.seasun.management.mapper;

import com.seasun.management.model.School;
import com.seasun.management.model.SchoolExample;

import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

@CacheConfig(cacheNames = "schools")
public interface SchoolMapper {

    long countByExample(SchoolExample example);

    List<School> selectByExample(SchoolExample example);

    int deleteByExample(SchoolExample example);

    int updateByExampleSelective(@Param("record") School record, @Param("example") SchoolExample example);

    int updateByExample(@Param("record") School record, @Param("example") SchoolExample example);

    @Delete({
            "delete from school",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into school (name)",
            "values (#{name,jdbcType=VARCHAR})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(School record);

    int insertSelective(School record);

    @Cacheable(key = "'id:'+#p0")
    @Select({
            "select",
            "id, name",
            "from school",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.SchoolMapper.BaseResultMap")
    School selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(School record);


    @Update({
            "update school",
            "set name = #{name,jdbcType=VARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(School record);

    /* the flowing are user defined ... */

    @Select("select * from school")
    List<School> selectAll();

    @CacheEvict(value = "schools", allEntries = true)
    @Select("select 1")
    School reloadCache();

    @Insert({
            "insert into school (id, name)",
            "values (#{id,jdbcType=BIGINT}, #{name,jdbcType=VARCHAR})"
    })
    int insertWithId(School record);

    @SelectProvider(type = SchoolSqlBuilder.class, method = "buildSelectByCondition")
    List<School> selectByCondition(School school);

    class SchoolSqlBuilder {
        public String buildSelectByCondition(School school) {

            return new SQL() {{
                SELECT("*");
                FROM("school");
                if (null != school.getName()) {
                    WHERE("id = #{id}");
                }

                if (null != school.getName()) {
//                    AND("name = #{name}");
                }
            }}.toString();

        }
    }
}