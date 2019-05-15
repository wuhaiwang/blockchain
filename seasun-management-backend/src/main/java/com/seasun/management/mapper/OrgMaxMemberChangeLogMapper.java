package com.seasun.management.mapper;

import com.seasun.management.dto.OrgMaxMemberChangeLogDto;
import com.seasun.management.model.OrgMaxMemberChangeLog;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface OrgMaxMemberChangeLogMapper {
    @Delete({
        "delete from org_max_member_change_log",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into org_max_member_change_log (project_id, old_max_member, ",
        "new_max_member, reason, ",
        "attachment_url, operator_id, ",
        "create_time)",
        "values (#{projectId,jdbcType=BIGINT}, #{oldMaxMember,jdbcType=INTEGER}, ",
        "#{newMaxMember,jdbcType=INTEGER}, #{reason,jdbcType=VARCHAR}, ",
        "#{attachmentUrl,jdbcType=VARCHAR}, #{operatorId,jdbcType=BIGINT}, ",
        "#{createTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(OrgMaxMemberChangeLog record);

    int insertSelective(OrgMaxMemberChangeLog record);

    @Select({
        "select",
        "id, project_id, old_max_member, new_max_member, reason, attachment_url, operator_id, ",
        "create_time",
        "from org_max_member_change_log",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.OrgMaxMemberChangeLogMapper.BaseResultMap")
    OrgMaxMemberChangeLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrgMaxMemberChangeLog record);

    @Update({
        "update org_max_member_change_log",
        "set project_id = #{projectId,jdbcType=BIGINT},",
          "old_max_member = #{oldMaxMember,jdbcType=INTEGER},",
          "new_max_member = #{newMaxMember,jdbcType=INTEGER},",
          "reason = #{reason,jdbcType=VARCHAR},",
          "attachment_url = #{attachmentUrl,jdbcType=VARCHAR},",
          "operator_id = #{operatorId,jdbcType=BIGINT},",
          "create_time = #{createTime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(OrgMaxMemberChangeLog record);

    @Select({
            "SELECT " +
                    "o.*, CONCAT(u.last_name, u.first_name) AS operatorName, " +
                    "p. NAME as projectName, " +
                    "p.service_line as serviceLine " +
                    "FROM " +
                    "org_max_member_change_log o " +
                    "LEFT JOIN `user` u ON o.operator_id = u.id " +
                    "LEFT JOIN project p ON o.project_id = p.id " +
                    "WHERE " +
                    "project_id = #{0} " +
                    "order by create_time desc "
    })
    List<OrgMaxMemberChangeLogDto> getMaxMemberChangeLogByProjectId(Long projectId);

    @Select({
            "SELECT " +
                    "o.*, CONCAT(u.last_name, u.first_name) AS managerName, " +
                    "p. NAME as projectName, " +
                    "p.service_line as serviceLine " +
                    "FROM " +
                    "org_max_member_change_log o " +
                    "LEFT JOIN `user` u ON o.operator_id = u.id " +
                    "LEFT JOIN project p ON o.project_id = p.id "
    })
    List<OrgMaxMemberChangeLogDto> getMaxMemberChangeLogsByProjectId();
}