package com.seasun.management.mapper;

import com.seasun.management.dto.FnPlatShareMemberDto;
import com.seasun.management.model.FnPlatShareMember;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.vo.FnPlatShareMemberVo;
import com.seasun.management.vo.UserLoginEmailVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface FnPlatShareMemberMapper {
    @Delete({
            "delete from fn_plat_share_member",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into fn_plat_share_member (plat_id, user_id, ",
            "weight)",
            "values (#{platId,jdbcType=BIGINT}, #{userId,jdbcType=BIGINT}, ",
            "#{weight,jdbcType=REAL})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FnPlatShareMember record);

    int insertSelective(FnPlatShareMember record);

    @Select({
            "select",
            "id, plat_id, user_id, weight",
            "from fn_plat_share_member",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FnPlatShareMemberMapper.BaseResultMap")
    FnPlatShareMember selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FnPlatShareMember record);

    @Update({
            "update fn_plat_share_member",
            "set plat_id = #{platId,jdbcType=BIGINT},",
            "user_id = #{userId,jdbcType=BIGINT},",
            "weight = #{weight,jdbcType=REAL}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FnPlatShareMember record);

    /* the flowing are user defined ... */

    int batchInsert(List<FnPlatShareMember> fnPlatShareMemberList);

    @Select("select * from fn_plat_share_member where user_id = #{userId}")
    FnPlatShareMember selectByUserId(Long userId);

    @Select("select * from fn_plat_share_member where user_id = #{userId} and plat_id = #{platId} limit 1")
    FnPlatShareMember selectByPlatIdAndUserId(@Param("userId") Long userId, @Param("platId") Long platId);

    @Select("select m.*, p.name as plat_name from fn_plat_share_member m left join project p on m.plat_id = p.id where m.user_id = #{userId} limit 1")
    FnPlatShareMemberDto selectWithPlatNameByUserId(@Param("userId") Long userId);

    @Select("select * from fn_plat_share_member")
    List<FnPlatShareMember> selectAll();

    @Select("select a.*,concat(b.last_name,b.first_name) as userName,b.photo as userPhoto from fn_plat_share_member a left join user b on a.user_id = b.id where plat_id = #{platId}")
    List<FnPlatShareMemberVo> selectByPlatId(Long platId);

    @Select({"SELECT m.user_id id, concat(u.last_name, u.first_name) name, u.login_id loginId, u.email email FROM fn_plat_share_member m",
            "LEFT JOIN USER u ON m.user_id = u.id",
            "WHERE m.plat_id=#{0}",
            "AND m.user_id NOT IN (SELECT DISTINCT c.create_by FROM fn_plat_share_config c WHERE c.plat_id=m.plat_id AND c.year=#{1} AND c.month=#{2})"})
    List<UserLoginEmailVo> getNoConfigShareMemberInfo(Long plateId, Integer year, Integer month);

    @Select("select v.create_by id, concat(v.last_name, v.first_name) name,v.login_id loginId,v.total_pro totalPro from " +
            "(select * from (" +
            "SELECT sum(binary(share_pro)) total_pro,create_by FROM fn_plat_share_config " +
            "where plat_id = #{0} and year = #{1} and month = #{2}" +
            " group by create_by) n" +
            " left join user u on u.id = n.create_by) v order by totalPro desc")
    List<UserLoginEmailVo> getShareMemberConfigInfo(Long plateId, Integer year, Integer month);

    @Delete("delete from fn_plat_share_member where plat_Id=#{platId}")
    int deleteByPlatId(@Param("platId") long platId);
}