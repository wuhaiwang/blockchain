package com.seasun.management.mapper;

import com.seasun.management.model.CfgPlatAttr;
import com.seasun.management.vo.CfgPlatAttrVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CfgPlatAttrMapper {
    @Delete({
            "delete from cfg_plat_attr",
            "where id = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
            "insert into cfg_plat_attr (plat_id, share_start_year, ",
            "share_start_month, share_detail_flag, ",
            "share_flag, share_week_write_flag)",
            "values (#{platId,jdbcType=BIGINT}, #{shareStartYear,jdbcType=INTEGER}, ",
            "#{shareStartMonth,jdbcType=INTEGER}, #{shareDetailFlag,jdbcType=BIT}, ",
            "#{shareFlag,jdbcType=BIT},#{shareWeekWriteFlag,jdbcType=BIT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Integer.class)
    int insert(CfgPlatAttr record);

    int insertSelective(CfgPlatAttr record);

    @Select({
            "select",
            "id, plat_id, share_start_year, share_start_month, share_detail_flag, share_flag, share_week_write_flag",
            "from cfg_plat_attr",
            "where id = #{id,jdbcType=INTEGER}"
    })
    @ResultMap("com.seasun.management.mapper.CfgPlatAttrMapper.BaseResultMap")
    CfgPlatAttr selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CfgPlatAttr record);

    @Update({
            "update cfg_plat_attr",
            "set plat_id = #{platId,jdbcType=BIGINT},",
            "share_start_year = #{shareStartYear,jdbcType=INTEGER},",
            "share_start_month = #{shareStartMonth,jdbcType=INTEGER},",
            "share_detail_flag = #{shareDetailFlag,jdbcType=BIT},",
            "share_flag = #{shareFlag,jdbcType=BIT},",
            "share_week_write_flag = #{shareWeekWriteFlag,jdbcType=BIT}",
            "where id = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(CfgPlatAttr record);

    /* the flowing are user defined ... */
    @Select("select * from cfg_plat_attr where plat_id = #{0}")
    CfgPlatAttr selectByPlatId(Long platId);

    @Select("select * from cfg_plat_attr where share_detail_flag =1")
    List<CfgPlatAttr> selectAllDetailPlat();

    @Select("select count(1) from cfg_plat_attr where share_detail_flag =1")
    Integer selectCountDetailPlat();

    @Select("select * from cfg_plat_attr where data_center_flag =1")
    List<CfgPlatAttr> selectAllRealTimeFlag();

    @Select("select a.*,p.name as platName from cfg_plat_attr a left join project p on a.plat_id = p.id;")
    List<CfgPlatAttrVo> selectAllWithPlatName();

    @Select("select a.*,p.name as platName from cfg_plat_attr a left join project p on a.plat_id = p.id where a.share_week_write_flag=1")
    List<CfgPlatAttrVo> selectAllWeekShareWithPlatName();

    @Select({"select c.* from cfg_plat_attr c left join order_center o on o.project_id=c.plat_id left join user u on u.order_center_id=o.id where u.id=#{0}"})
    List<CfgPlatAttr> selectByPlatSubUserId(Long userId);

    Integer selectDetailPlatCountByPlatIds(List<Long> platIds);
}