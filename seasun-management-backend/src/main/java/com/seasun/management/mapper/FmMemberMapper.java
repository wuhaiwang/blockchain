package com.seasun.management.mapper;

import com.seasun.management.dto.FmGroupMemberDto;
import com.seasun.management.dto.FmMemberDto;
import com.seasun.management.model.FmMember;
import com.seasun.management.model.FmPerfSubmitInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface FmMemberMapper {
    @Delete({
            "delete from fm_member",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into fm_member (user_id, plat_id, ",
            "project_id, fixed_flag)",
            "values (#{userId,jdbcType=BIGINT}, #{platId,jdbcType=BIGINT}, ",
            "#{projectId,jdbcType=BIGINT}, #{fixedFlag,jdbcType=BIT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FmMember record);

    int insertSelective(FmMember record);

    @Select({
            "select",
            "id, user_id, plat_id, project_id, fixed_flag",
            "from fm_member",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FmMemberMapper.BaseResultMap")
    FmMember selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FmMember record);

    @Update({
            "update fm_member",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "plat_id = #{platId,jdbcType=BIGINT},",
            "project_id = #{projectId,jdbcType=BIGINT},",
            "fixed_flag = #{fixedFlag,jdbcType=BIT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FmMember record);

    /* the flowing are user defined ... */

    @Select({
            "select m.*, p.name project_name, pl.name plat_name",
            "from fm_member m",
            "left join project p on p.id = m.project_id",
            "left join project pl on pl.id = m.plat_id"
    })
    List<FmMemberDto> selectAll();

    @Select({
            "select m.*, p.name project_name, pl.name plat_name",
            "from fm_member m",
            "left join project p on p.id = m.project_id",
            "left join project pl on pl.id = m.plat_id",
            "where m.project_id = #{projectId} and m.plat_id = #{platId}"
    })
    List<FmMemberDto> selectAllByProjectIdAndPlatId(@Param("projectId") Long projectId, @Param("platId") Long platId);

    @Select({"SELECT user_id id ,plat_id ,project_id, `user`.login_id,CONCAT(user.last_name,`user`.first_name) name ,user.photo photo_address ,f.fixed_flag permanent_flag FROM fm_member f LEFT JOIN `user`",
            "ON f.user_id =`user`.id WHERE plat_id = #{id}"})
    List<FmGroupMemberDto> selectFixMembersByPlatId(Long id);

    @Select({"SELECT user_id id ,plat_id ,project_id, `user`.login_id,CONCAT(user.last_name,`user`.first_name) name ,user.photo photo_address ,f.fixed_flag permanent_flag FROM fm_member f LEFT JOIN `user`",
            "ON f.user_id =`user`.id WHERE project_id = #{id}"})
    List<FmGroupMemberDto> selectFixMembersByProjectId(Long id);

    @Select({"SELECT * from fm_member WHERE plat_id= #{platId} AND project_id = #{projectId} AND user_id = #{userId}"})
    FmMember selectByProjectIdAndPlatId(@Param("platId") Long platId, @Param("projectId") Long projectId, @Param("userId") Long userId);

    int batchInsert(List<FmMember> fmMembers);

    @Delete("delete from fm_member where plat_id=#{platId}")
    void deleteAllFmMemberByPlatId(Long platId);

    @Select({"SELECT f.*,p.`name` plat_name ,px.`name` project_name FROM fm_member f ",
            "LEFT JOIN project p ON p.id =f.plat_id ",
            "LEFT JOIN project px ON px.id =f.project_id",
            "WHERE user_id =#{userId} limit 1"})
    FmMemberDto selectByUserId(Long userId);

    @Select("UPDATE fm_member SET fixed_flag= NOT fixed_flag WHERE user_id= #{userId}")
    void updateFixFlag(Long userId);

    @Select("SELECT * FROM fm_member WHERE plat_id = #{platId} AND project_id =#{projectId}")
    List<FmMember> selectFixMembersByPlatIdAndProjectId(@Param("platId") Long platId, @Param("projectId") Long projectId);

    @Select("SELECT * FROM fm_member WHERE fixed_flag = 1 AND plat_id =#{platId}")
    List<FmMember> selectAllPermanentFixedMember(Long platId);

    List<Long> selectPlatIdsByUserId(@Param("list") List<Long> userIds);


}