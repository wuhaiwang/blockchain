package com.seasun.management.mapper;

import com.seasun.management.model.CpProjectRelation;
import com.seasun.management.vo.cp.CPProjectInfoVo;
import com.seasun.management.vo.cp.CpProjectRelationVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CpProjectRelationMapper {
    @Delete({
            "delete from cp_project_relation",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into cp_project_relation (it_project_id, cp_project_id)",
            "values (#{itProjectId,jdbcType=BIGINT}, #{cpProjectId,jdbcType=BIGINT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(CpProjectRelation record);

    int insertSelective(CpProjectRelation record);

    @Select({
            "select",
            "id, it_project_id, cp_project_id",
            "from cp_project_relation",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.CpProjectRelationMapper.BaseResultMap")
    CpProjectRelation selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CpProjectRelation record);

    @Update({
            "update cp_project_relation",
            "set it_project_id = #{itProjectId,jdbcType=BIGINT},",
            "cp_project_id = #{cpProjectId,jdbcType=BIGINT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(CpProjectRelation record);

    /* the flowing are user defined ... */

    @Select({"select a.*,b.* from (select p.id id,p.name,p.logo,GROUP_CONCAT(g.name) cPProjectName,GROUP_CONCAT(cp.cp_project_id) cPProjectId,p.contact_name contactName from cp_project_relation cp left join project p on cp.it_project_id=p.id left join cp.gameProject g on g.id=cp.cp_project_id where p.active_flag=1 and g.Active=1  GROUP BY cp.it_project_id) a left join (select os.CreatedOn createTime,ox.GameProject from cp.ordissues os left join cp.ordissuesex ox on os.ID=ox.IssueID where DATE_FORMAT(os.CreatedOn,'%Y')=#{0} and os.Status not in (15,21) ) b on FIND_IN_SET(b.GameProject ,a.cPProjectName) where b.createTime is not null  group by a.id limit  #{1},#{2} "})
    List<CPProjectInfoVo> selectAllActiveItProject(Integer year, Integer beginNum, Integer pageSize);

    @Select({"select  a.* from (select p.id id,p.name,p.logo,GROUP_CONCAT(g.name) cPProjectName,GROUP_CONCAT(cpp.cp_project_id) cPProjectId,p.contact_name contactName from cp_project_relation cpp LEFT JOIN project p on  p.id=cpp.it_project_id LEFT JOIN cp.gameProject g on g.id=cpp.cp_project_id where p.active_flag = 1 AND g.Active = 1 GROUP BY p.id) a left join cp_budget cb on FIND_IN_SET(cb.cp_project_id,a.cPProjectId) where cb.budget_year=#{0} LIMIT #{1},#{2}"})
    List<CPProjectInfoVo> selectAllAmountProject(Integer year, Integer beginNum, Integer pageSize);

    @Select({"select count(1) from (select a.*,b.* from (select p.id id,p.name,p.logo,GROUP_CONCAT(g.name) cPProjectName,GROUP_CONCAT(cp.cp_project_id) cPProjectId,p.contact_name contactName from cp_project_relation cp left join project p on cp.it_project_id=p.id left join cp.gameProject g on g.id=cp.cp_project_id where p.active_flag=1 and g.Active=1  GROUP BY cp.it_project_id) a left join (select os.CreatedOn createTime,ox.GameProject from cp.ordissues os left join cp.ordissuesex ox on os.ID=ox.IssueID where DATE_FORMAT(os.CreatedOn,'%Y')=#{0} and os.Status not in (15,21) ) b on FIND_IN_SET(b.GameProject ,a.cPProjectName) where b.createTime is not null  group by a.id ) a "})
    Integer selectAllActiveItAppProjectCountByCond(Integer year);

    @Delete({
            "delete from cp_project_relation",
            "where cp_project_id = #{cpProjectId,jdbcType=BIGINT}"
    })
    int deleteByCpProjectId(Long cpProjectId);

    List<CpProjectRelationVo> selectProjects(@Param("currentPage") Integer currentPage, @Param("pageSize") Integer pageSize, @Param("year") Integer year, @Param("keyWord") String keyWord);

    int selectProjectsCount(@Param("year") Integer year, @Param("keyWord") String keyWord);

    @Select({"select c.cp_project_id from cp_project_relation c left join cp.gameproject g on g.id=c.cp_project_id where g.Active=1 and it_project_id=#{0}"})
    List<Integer> selectActiveCPProjectIdsByITProjectId(Long id);

    @Select({"select a.* from cp_project_relation a left join project p on p.id=a.it_project_id left join cp.gameproject g on g.id=a.cp_project_id where g.Active=1 and p.active_flag=1"})
    List<CpProjectRelation> selectAllActive();

    @Select({"select r.cp_project_id from cp_project_relation r,(select r.it_project_id from cp.gameproject g,cp_project_relation r where g.Active = 1 and g.ID = r.cp_project_id and g.name = #{0}) t\n" +
            "where r.it_project_id = t.it_project_id\n"})
    List<Integer> selectItProjectIdsByCPProjectName(String name);
}