package com.seasun.management.mapper;

import com.seasun.management.model.FnPlatFavorProject;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.Project;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface FnPlatFavorProjectMapper {
    @Delete({
            "delete from fn_plat_favor_project",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into fn_plat_favor_project (plat_id, favor_project_id, ",
            "sort)",
            "values (#{platId,jdbcType=BIGINT}, #{favorProjectId,jdbcType=BIGINT}, ",
            "#{sort,jdbcType=INTEGER})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FnPlatFavorProject record);

    int insertSelective(FnPlatFavorProject record);

    @Select({
            "select",
            "id, plat_id, favor_project_id, sort",
            "from fn_plat_favor_project",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FnPlatFavorProjectMapper.BaseResultMap")
    FnPlatFavorProject selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FnPlatFavorProject record);

    @Update({
            "update fn_plat_favor_project",
            "set plat_id = #{platId,jdbcType=BIGINT},",
            "favor_project_id = #{favorProjectId,jdbcType=BIGINT},",
            "sort = #{sort,jdbcType=INTEGER}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FnPlatFavorProject record);


     /* the flowing are user defined ... */

    @Select("select favor_project_id from fn_plat_favor_project where plat_id = #{0} order by sort ")
    List<Long> selectFavorProjectIdsByPlatId(Long platId);

    @Select("select p.* from fn_plat_favor_project f left join project p on f.favor_project_id = p.id where f.plat_id = #{0} order by sort ")
    List<Project> selectFavorProjects(Long platId);

    @Select("select p.id,p.name from fn_plat_favor_project f left join project p on f.favor_project_id = p.id where f.plat_id = #{0} order by sort ")
    List<IdNameBaseObject> selectFavorProjectIdAndNamesByPlatId(Long platId);

    @Select({"select a.* from (select p.id,name from fn_plat_favor_project f left join project p on f.favor_project_id = p.id where f.plat_id =#{0} order by f.sort ) a union select id,name from project where id in (select project_id from fn_plat_week_share_config where plat_id=#{0} and year=#{1} and week=#{2})"})
    List<IdNameBaseObject> selectFavorIdNameByPlatIdAndYearAndWeek(Long platId, Integer year, Integer week);

    @Delete({"delete from fn_plat_favor_project where plat_id=#{0}"})
    void deleteByPlatId(Long platId);

    Integer batchInsert(List<FnPlatFavorProject> inserts);
}