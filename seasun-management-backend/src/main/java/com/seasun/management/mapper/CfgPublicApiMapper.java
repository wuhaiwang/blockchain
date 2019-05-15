package com.seasun.management.mapper;

import com.seasun.management.model.CfgPublicApi;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface CfgPublicApiMapper {
    @Delete({
            "delete from cfg_public_api",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into cfg_public_api (url, on_flag)",
            "values (#{url,jdbcType=VARCHAR}, #{onFlag,jdbcType=BIT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(CfgPublicApi record);

    int insertSelective(CfgPublicApi record);

    @Select({
            "select",
            "id, url, on_flag",
            "from cfg_public_api",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.CfgPublicApiMapper.BaseResultMap")
    CfgPublicApi selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CfgPublicApi record);

    @Update({
            "update cfg_public_api",
            "set url = #{url,jdbcType=VARCHAR},",
            "on_flag = #{onFlag,jdbcType=BIT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(CfgPublicApi record);

    /* the flowing are user defined ... */
    @Select("select * from cfg_public_api where url = #{url}")
    CfgPublicApi selectByUrl(String url);
}