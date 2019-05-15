package com.seasun.management.mapper;

import com.seasun.management.model.AReprProjSchedData;
import com.seasun.management.model.AReprShareData;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface AReprShareDataMapper {
    @Delete({
            "delete from a_repr_share_data",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into a_repr_share_data (project_name, plat_name, ",
            "share_item_id, shared_item_id, ",
            "template_id, year, ",
            "month, share_pro, share_amount, ",
            "share_number, fixed_outnumber, ",
            "create_time, update_time)",
            "values (#{projectName,jdbcType=VARCHAR}, #{platName,jdbcType=VARCHAR}, ",
            "#{shareItemId,jdbcType=VARCHAR}, #{sharedItemId,jdbcType=VARCHAR}, ",
            "#{templateId,jdbcType=VARCHAR}, #{year,jdbcType=INTEGER}, ",
            "#{month,jdbcType=INTEGER}, #{sharePro,jdbcType=REAL}, #{shareAmount,jdbcType=REAL}, ",
            "#{shareNumber,jdbcType=REAL}, #{fixedOutnumber,jdbcType=REAL}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(AReprShareData record);

    int insertSelective(AReprShareData record);

    @Select({
            "select",
            "id, project_name, plat_name, share_item_id, shared_item_id, template_id, year, ",
            "month, share_pro, share_amount, share_number, fixed_outnumber, create_time, ",
            "update_time",
            "from a_repr_share_data",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.AReprShareDataMapper.BaseResultMap")
    AReprShareData selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AReprShareData record);

    @Update({
            "update a_repr_share_data",
            "set project_name = #{projectName,jdbcType=VARCHAR},",
            "plat_name = #{platName,jdbcType=VARCHAR},",
            "share_item_id = #{shareItemId,jdbcType=VARCHAR},",
            "shared_item_id = #{sharedItemId,jdbcType=VARCHAR},",
            "template_id = #{templateId,jdbcType=VARCHAR},",
            "year = #{year,jdbcType=INTEGER},",
            "month = #{month,jdbcType=INTEGER},",
            "share_pro = #{sharePro,jdbcType=REAL},",
            "share_amount = #{shareAmount,jdbcType=REAL},",
            "share_number = #{shareNumber,jdbcType=REAL},",
            "fixed_outnumber = #{fixedOutnumber,jdbcType=REAL},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(AReprShareData record);

      /* the flowing are user defined ... */

    @Select("select * from a_repr_share_data")
    List<AReprShareData> selectAll();

    @Select("select * from a_repr_share_data where year = #{0} and month = #{1}")
    List<AReprShareData> selectByYearAndMonth(int year, int month);
}