package com.seasun.management.mapper;

import com.seasun.management.model.FProjectMaxMember;
import com.seasun.management.vo.FProjectMaxMemberVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface FProjectMaxMemberMapper {
    @Delete({
            "delete from f_project_max_member",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into f_project_max_member (project_id, current_member, ",
            "max_member, apply_max_member, ",
            "reason, approval_comment, ",
            "deploy_flag)",
            "values (#{projectId,jdbcType=BIGINT}, #{currentMember,jdbcType=INTEGER}, ",
            "#{maxMember,jdbcType=INTEGER}, #{applyMaxMember,jdbcType=INTEGER}, ",
            "#{reason,jdbcType=VARCHAR}, #{approvalComment,jdbcType=VARCHAR}, ",
            "#{deployFlag,jdbcType=BIT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(FProjectMaxMember record);

    int insertSelective(FProjectMaxMember record);

    @Select({
            "select",
            "id, project_id, current_member, max_member, apply_max_member, reason, approval_comment, ",
            "deploy_flag",
            "from f_project_max_member",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.FProjectMaxMemberMapper.BaseResultMap")
    FProjectMaxMember selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FProjectMaxMember record);

    @Update({
            "update f_project_max_member",
            "set project_id = #{projectId,jdbcType=BIGINT},",
            "current_member = #{currentMember,jdbcType=INTEGER},",
            "max_member = #{maxMember,jdbcType=INTEGER},",
            "apply_max_member = #{applyMaxMember,jdbcType=INTEGER},",
            "reason = #{reason,jdbcType=VARCHAR},",
            "approval_comment = #{approvalComment,jdbcType=VARCHAR},",
            "deploy_flag = #{deployFlag,jdbcType=BIT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(FProjectMaxMember record);

    /* the flowing are user defined ... */
    @Select({
            "select p.*, i.id as instance_id, i.start_time as create_time, i.process_result, pj.name as project_name, d.manager_id",
            "from f_project_max_member p",
            "left join f_instance i on p.id = i.business_key",
            "left join f_instance_detail d on d.instance_id = i.id and d.process_result = 0",
            "left join project pj on pj.id = p.project_id",
            "where p.project_id = #{projectId} and i.process_result <> 0",
            "order by i.start_time desc"
    })
    List<FProjectMaxMemberVo> selectAllByProjectIdAndComplete(Long projectId);

    @Select({
            "select p.*, i.id as instance_id, i.start_time as create_time, i.process_result, pj.name as project_name, d.manager_id",
            "from f_instance i",
            "left join f_instance_detail d on d.instance_id = i.id and d.process_result = 0",
            "left join f_project_max_member p on p.id = i.business_key",
            "left join project pj on pj.id = p.project_id",
            "where d.manager_id = #{managerId}",
            "order by i.start_time desc"
    })
    List<FProjectMaxMemberVo> selectAllByManagerIdAndProcessing(Long managerId);

    @Select({
            "select p.*, i.id as instance_id, i.start_time as create_time, i.process_result, pj.name as project_name, d.manager_id",
            "from f_instance i",
            "left join f_instance_detail d on d.instance_id = i.id and d.process_result = 0",
            "left join f_project_max_member p on p.id = i.business_key",
            "left join project pj on pj.id = p.project_id",
            "where d.process_result = 0",
            "order by i.start_time desc"
    })
    List<FProjectMaxMemberVo> selectAllByProcessing();

    @Select({
            "select p.*, i.id as instance_id, i.start_time as create_time, i.process_result, pj.name as project_name, d.manager_id",
            "from f_instance i",
            "left join f_instance_detail d on d.instance_id = i.id and d.process_result = 0",
            "left join f_project_max_member p on p.id = i.business_key",
            "left join project pj on pj.id = p.project_id",
            "where i.id = #{instanceId}",
            "limit 1"
    })
    FProjectMaxMemberVo selectByInstanceId(Long instanceId);

    @Select({
            "select p.*, i.id as instance_id, i.start_time as create_time, i.process_result, pj.name as project_name, d.manager_id",
            "from f_instance i",
            "left join f_instance_detail d on d.instance_id = i.id and d.process_result = 0",
            "left join f_project_max_member p on p.id = i.business_key",
            "left join project pj on pj.id = p.project_id",
            "where p.project_id = #{projectId} and i.process_result = 0",
            "limit 1"
    })
    FProjectMaxMemberVo selectByProjectIdAndProcessing(Long projectId);

    @Select({
            "select p.*, pj.name as project_name",
            "from f_project_max_member p",
            "left join project pj on pj.id = p.project_id",
            "where p.id = #{id}"
    })
    FProjectMaxMemberVo selectWithProjectNameByPrimaryKey(Long id);
}