package com.seasun.management.mapper;

import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.Subcompany;
import com.seasun.management.vo.SubCompanyProjectVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface SubcompanyMapper {
    @Delete({
            "delete from subcompany",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into subcompany (name, code, ",
            "city)",
            "values (#{name,jdbcType=VARCHAR}, #{code,jdbcType=VARCHAR}, ",
            "#{city,jdbcType=VARCHAR})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(Subcompany record);

    int insertSelective(Subcompany record);

    @Select({
            "select",
            "id, name, code, city",
            "from subcompany",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.SubcompanyMapper.BaseResultMap")
    Subcompany selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Subcompany record);

    @Update({
            "update subcompany",
            "set name = #{name,jdbcType=VARCHAR},",
            "code = #{code,jdbcType=VARCHAR},",
            "city = #{city,jdbcType=VARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(Subcompany record);

    /* the flowing are user defined ... */
    @Insert({
            "insert into subcompany (id, name, code, ",
            "city)",
            "values (#{id,jdbcType=BIGINT}, #{name,jdbcType=VARCHAR}, #{code,jdbcType=VARCHAR}, ",
            "#{city,jdbcType=VARCHAR})"
    })
    int insertWithId(Subcompany record);

    @Select({"select * from subcompany"})
    List<Subcompany> selectAll();

    @Select({"select id,name from subcompany"})
    List<IdNameBaseObject> selectSimpleAll();

    @Select({"SELECT a.* FROM\n" +
            "(SELECT DISTINCT p.name platName,p.id platId,s.id companyId,s.name companyName\n" +
            "FROM subcompany s\n" +
            "LEFT JOIN order_center o on SUBSTR(o.code,1,4)=s.code\n" +
            "LEFT JOIN project p on o.project_id=p.id\n" +
            "WHERE s.NAME =#{0} \n" +
            "ORDER BY p.name) a\n" +
            "where FIND_IN_SET(#{1},a.platName)"})
    List<SubCompanyProjectVO> selectProjectIdByCompanyName(String companyName, String platName);

    @Select({"select DISTINCT s.id companyId,s.name companyName,p.id platId, p.name platName from subcompany s \n" +
            "LEFT JOIN cost_center c ON substr(c. CODE, 1, 4) = s. CODE\n" +
            "LEFT JOIN USER u ON u.cost_center_id = c.id\n" +
            "LEFT JOIN order_center o ON u.order_center_id = o.id\n" +
            "LEFT JOIN project p ON p.id = o.project_id\n" +
            "where p.active_flag = 1"})
    List<SubCompanyProjectVO> selectCompanyInfo();


    @Select({"select id, city as name from subcompany"})
    List<IdNameBaseObject> selectAllBaseInfo();
}