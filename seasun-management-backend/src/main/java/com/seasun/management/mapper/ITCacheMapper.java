package com.seasun.management.mapper;

import com.seasun.management.model.ITCache;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ITCacheMapper {
    @Delete({
            "delete from it_cache",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into it_cache (name, hash_key, ",
            "entry_key, type, ",
            "remark)",
            "values (#{name,jdbcType=VARCHAR}, #{hashKey,jdbcType=VARCHAR}, ",
            "#{entryKey,jdbcType=VARCHAR}, #{type,jdbcType=VARCHAR}, ",
            "#{remark,jdbcType=VARCHAR})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(ITCache record);

    int insertSelective(ITCache record);

    @Select({
            "select",
            "id, name, hash_key, entry_key, type, remark",
            "from it_cache",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.ITCacheMapper.BaseResultMap")
    ITCache selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ITCache record);

    @Update({
            "update it_cache",
            "set name = #{name,jdbcType=VARCHAR},",
            "hash_key = #{hashKey,jdbcType=VARCHAR},",
            "entry_key = #{entryKey,jdbcType=VARCHAR},",
            "type = #{type,jdbcType=VARCHAR},",
            "remark = #{remark,jdbcType=VARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(ITCache record);

    @Select({"select id,name,hash_key,entry_key,type,remark from it_cache"})
    List<ITCache> selectAllCaches();
}