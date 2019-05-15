package com.seasun.management.mapper;

import com.seasun.management.model.PmQualityProjectRelation;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface PmQualityProjectRelationMapper {
    @Delete({
        "delete from pm_quality_project_relation",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into pm_quality_project_relation (it_project_id, quality_project_id)",
        "values (#{itProjectId,jdbcType=BIGINT}, #{qualityProjectId,jdbcType=BIGINT})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(PmQualityProjectRelation record);

    int insertSelective(PmQualityProjectRelation record);

    @Select({
        "select",
        "id, it_project_id, quality_project_id",
        "from pm_quality_project_relation",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.PmQualityProjectRelationMapper.BaseResultMap")
    PmQualityProjectRelation selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PmQualityProjectRelation record);

    @Update({
        "update pm_quality_project_relation",
        "set it_project_id = #{itProjectId,jdbcType=BIGINT},",
          "quality_project_id = #{qualityProjectId,jdbcType=BIGINT}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(PmQualityProjectRelation record);

    @Select({
            "select",
            "id, it_project_id, quality_project_id",
            "from pm_quality_project_relation",
            "where it_project_id = #{itProjectId,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.PmQualityProjectRelationMapper.BaseResultMap")
    PmQualityProjectRelation selectByItProjectId(Long itProjectId);

    @Select({
            "select it_project_id from pm_quality_project_relation where it_project_id = #{itProjectId,jdbcType=BIGINT}"
    })
    List<Long> selectProjectIds(Long itProjectId);
}