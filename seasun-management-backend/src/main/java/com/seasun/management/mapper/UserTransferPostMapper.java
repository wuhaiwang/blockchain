package com.seasun.management.mapper;

import com.seasun.management.model.UserTransferPost;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface UserTransferPostMapper {
    @Delete({
            "delete from user_transfer_post",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into user_transfer_post (user_id, transfer_type, ",
            "transfer_time, pre_department, ",
            "pre_project, pre_company, ",
            "pre_post, pre_cost_center_id, ",
            "pre_order_center_id, new_department, ",
            "new_project, new_company, ",
            "new_post, new_post_type, ",
            "new_cost_center_id, new_order_center_id)",
            "values (#{userId,jdbcType=BIGINT}, #{transferType,jdbcType=VARCHAR}, ",
            "#{transferTime,jdbcType=DATE}, #{preDepartment,jdbcType=VARCHAR}, ",
            "#{preProject,jdbcType=VARCHAR}, #{preCompany,jdbcType=VARCHAR}, ",
            "#{prePost,jdbcType=VARCHAR}, #{preCostCenterId,jdbcType=BIGINT}, ",
            "#{preOrderCenterId,jdbcType=BIGINT}, #{newDepartment,jdbcType=VARCHAR}, ",
            "#{newProject,jdbcType=VARCHAR}, #{newCompany,jdbcType=VARCHAR}, ",
            "#{newPost,jdbcType=VARCHAR}, #{newPostType,jdbcType=VARCHAR}, ",
            "#{newCostCenterId,jdbcType=BIGINT}, #{newOrderCenterId,jdbcType=BIGINT})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(UserTransferPost record);

    int insertSelective(UserTransferPost record);

    @Select({
            "select",
            "id, user_id, transfer_type, transfer_time, pre_department, pre_project, pre_company, ",
            "pre_post, pre_cost_center_id, pre_order_center_id, new_department, new_project, ",
            "new_company, new_post, new_post_type, new_cost_center_id, new_order_center_id",
            "from user_transfer_post",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserTransferPostMapper.BaseResultMap")
    UserTransferPost selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserTransferPost record);

    @Update({
            "update user_transfer_post",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "transfer_type = #{transferType,jdbcType=VARCHAR},",
            "transfer_time = #{transferTime,jdbcType=DATE},",
            "pre_department = #{preDepartment,jdbcType=VARCHAR},",
            "pre_project = #{preProject,jdbcType=VARCHAR},",
            "pre_company = #{preCompany,jdbcType=VARCHAR},",
            "pre_post = #{prePost,jdbcType=VARCHAR},",
            "pre_cost_center_id = #{preCostCenterId,jdbcType=BIGINT},",
            "pre_order_center_id = #{preOrderCenterId,jdbcType=BIGINT},",
            "new_department = #{newDepartment,jdbcType=VARCHAR},",
            "new_project = #{newProject,jdbcType=VARCHAR},",
            "new_company = #{newCompany,jdbcType=VARCHAR},",
            "new_post = #{newPost,jdbcType=VARCHAR},",
            "new_post_type = #{newPostType,jdbcType=VARCHAR},",
            "new_cost_center_id = #{newCostCenterId,jdbcType=BIGINT},",
            "new_order_center_id = #{newOrderCenterId,jdbcType=BIGINT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserTransferPost record);

    /* the flowing are user defined ... */
    int updateByUserIdAndTransferTime(UserTransferPost userTransferPost);

    @Select({
            "select u.*, p.city pre_work_place, np.city new_work_place, o.code pre_order_center_code",
            "from user_transfer_post u",
            "left join order_center o on o.id = u.pre_order_center_id",
            "left join project p on p.id = o.project_id",
            "left join order_center no on no.id = u.new_order_center_id",
            "left join project np on np.id = no.project_id",
            "order by u.transfer_time"
    })
    List<UserTransferPost> selectAll();
}