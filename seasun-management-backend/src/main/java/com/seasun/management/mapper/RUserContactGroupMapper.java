package com.seasun.management.mapper;

import com.seasun.management.dto.ContactGroupUserDto;
import com.seasun.management.dto.WorkGroupUserDto;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.RUserContactGroup;
import com.seasun.management.model.UserContactGroup;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface RUserContactGroupMapper {
    @Delete({
        "delete from r_user_contact_group",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into r_user_contact_group (user_id, user_contact_group_id)",
        "values (#{userId,jdbcType=BIGINT}, #{userContactGroupId,jdbcType=BIGINT})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(RUserContactGroup record);

    int insertSelective(RUserContactGroup record);

    @Select({
        "select",
        "id, user_id, user_contact_group_id",
        "from r_user_contact_group",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.RUserContactGroupMapper.BaseResultMap")
    RUserContactGroup selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RUserContactGroup record);

    @Update({
        "update r_user_contact_group",
        "set user_id = #{userId,jdbcType=BIGINT},",
          "user_contact_group_id = #{userContactGroupId,jdbcType=BIGINT}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(RUserContactGroup record);


    // < the following are user defined >...

    List<ContactGroupUserDto> selectContactGroupUsersByUserContactGroupIds(List<Long> contactGroups);

    List<ContactGroupUserDto> selectContactGroupUsersByUserContactGroupId(Long contactGroupId);

    @Delete("delete from r_user_contact_group where user_contact_group_id=#{0}")
    int deleteByUserContactGroupId(Long id);

    int batchInsert(@Param("id") Long id,@Param("list") List<ContactGroupUserDto> users);

    @Select("select u.id,u.name from user_contact_group u where u.id in(select distinct user_contact_group_id from r_user_contact_group where user_id =#{0})")
    List<IdNameBaseObject> selectContactGroupsByUserId(Long userId);
}