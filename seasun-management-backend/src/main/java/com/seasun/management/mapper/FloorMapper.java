package com.seasun.management.mapper;

import com.seasun.management.dto.FloorDetailDto;
import com.seasun.management.dto.FloorDto;
import com.seasun.management.model.Floor;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface FloorMapper {
    @Delete({
            "delete from floor",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into floor (name, assistant_id)",
            "values (#{name,jdbcType=VARCHAR}, #{assistantId,jdbcType=BIGINT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(Floor record);

    int insertSelective(Floor record);

    @Select({
            "select",
            "id, name, assistant_id",
            "from floor",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FloorMapper.BaseResultMap")
    Floor selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Floor record);

    @Update({
            "update floor",
            "set name = #{name,jdbcType=VARCHAR},",
            "assistant_id = #{assistantId,jdbcType=BIGINT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(Floor record);

    /* the flowing are user defined ... */
    @Insert({
            "insert into floor (id, name, assistant_id)",
            "values (#{id,jdbcType=BIGINT}, #{name,jdbcType=VARCHAR}, #{assistantId,jdbcType=BIGINT})"
    })
    int insertWithId(Floor record);


    @Select({
            "SELECT floor.*, concat(asistant.last_name, asistant.first_name) as assistant_name, count(floor.id) as total from floor \n" +
                    "left join user asistant on floor.assistant_id = asistant.id\n" +
                    "join user member on member.floor_id = floor.id\n" +
                    "group by floor.id"
    })
    List<FloorDto> selectAll();

    @Select({
            "select concat(user.last_name, user.first_name) as full_name, user.id, user.login_id, user.photo from user where floor_id = #{floor_id}"
    })
    List<FloorDetailDto.Member> selectMemberById(@Param("floor_id") Long floorId);

    @Select({
            "select floor.*, concat(user.last_name, user.first_name) as assistant_name from floor left join user on user.id = floor.assistant_id where floor.id = #{floor_id};"
    })
    FloorDetailDto selectByFloorId (@Param("floor_id") Long floorId);
}