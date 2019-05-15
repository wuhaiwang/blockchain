package com.seasun.management.mapper;

import com.seasun.management.dto.OrderCenterWithParentHrIdDto;
import com.seasun.management.model.OrderCenter;
import com.seasun.management.vo.OrderCenterVo;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface OrderCenterMapper {
    @Delete({
            "delete from order_center",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into order_center (name, code, ",
            "project_id,city)",
            "values (#{name,jdbcType=VARCHAR}, #{code,jdbcType=VARCHAR}, ",
            "#{projectId,jdbcType=BIGINT}, #{city,jdbcType=VARCHAR})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int inser1t(OrderCenter record);

    int insertSelective(OrderCenter record);

    @Select({
            "select",
            "id, name, code, project_id, city",
            "from order_center",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.OrderCenterMapper.BaseResultMap")
    OrderCenter selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderCenter record);

    @Update({
            "update order_center",
            "set name = #{name,jdbcType=VARCHAR},",
            "code = #{code,jdbcType=VARCHAR},",
            "project_id = #{projectId,jdbcType=BIGINT},",
            "city = #{city,jdbcType=VARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(OrderCenter record);

    /* the flowing are user defined ... */

    @Select("select * from order_center where code = #{code}")
    OrderCenter selectByCode(String code);

    @Select("SELECT * FROM order_center where project_id= #{projectId} or project_id in (select id from project WHERE parent_hr_id = #{projectId})")
    List<OrderCenter> selectByProject(Long projectId);

    @Select("select a.*, b.parent_hr_id from order_center as a left join project as b on a.project_id=b.id where (a.project_id=#{projectId} or b.parent_hr_id=#{projectId})")
    List<OrderCenterWithParentHrIdDto> selectOrderCenterDtoByProjectId(@Param("projectId") Long projectId);

    List<OrderCenterWithParentHrIdDto> selectByProjectIdsOrParentHrIds(List<Long> projectIds);

    @Select("select * from order_center")
    List<OrderCenter> selectAll();

    int removeByProjectIdAndCodesNotIn(Map<String, Object> params);

    int updateProjectByCodesIn(Map<String, Object> params);

    @Update("update order_center set project_id=null where project_id=#{projectId}")
    int removeByProjectId(Long projectId);

    int batchInsert(List<OrderCenter> orderCenterList);

    List<OrderCenterVo> selectOrderCenterVoByUsedFlag(@Param("usedFlag") Boolean usedFlag);
}