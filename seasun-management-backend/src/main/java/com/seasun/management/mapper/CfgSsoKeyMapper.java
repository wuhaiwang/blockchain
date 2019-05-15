package com.seasun.management.mapper;

import com.seasun.management.model.CfgSsoKey;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface CfgSsoKeyMapper {
    @Delete({
        "delete from cfg_sso_key",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
        "insert into cfg_sso_key (app_tid, web_tid, ",
        "app_pub_key, web_pub_key, ",
        "update_time)",
        "values (#{appTid,jdbcType=VARCHAR}, #{webTid,jdbcType=VARCHAR}, ",
        "#{appPubKey,jdbcType=VARCHAR}, #{webPubKey,jdbcType=VARCHAR}, ",
        "#{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Integer.class)
    int insert(CfgSsoKey record);

    int insertSelective(CfgSsoKey record);

    @Select({
        "select",
        "id, app_tid, web_tid, app_pub_key, web_pub_key, update_time",
        "from cfg_sso_key",
        "where id = #{id,jdbcType=INTEGER}"
    })
    @ResultMap("com.seasun.management.mapper.CfgSsoKeyMapper.BaseResultMap")
    CfgSsoKey selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CfgSsoKey record);

    @Update({
        "update cfg_sso_key",
        "set app_tid = #{appTid,jdbcType=VARCHAR},",
          "web_tid = #{webTid,jdbcType=VARCHAR},",
          "app_pub_key = #{appPubKey,jdbcType=VARCHAR},",
          "web_pub_key = #{webPubKey,jdbcType=VARCHAR},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(CfgSsoKey record);

    @Delete("delete from cfg_sso_key")
    int deleteAll();

    @Select("select * from cfg_sso_key limit 1")
    CfgSsoKey selectOne();
}