package com.seasun.management.mapper;

import com.seasun.management.model.UserDetail;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserDetailMapper {
    @Delete({
            "delete from user_detail",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into user_detail (user_id, post, ",
            "gender, work_status, ",
            "Work_Age_Out_KS, birthday, ",
            "certificate_type, certification_no, ",
            "extra_phone, qq, ",
            "emg_contact_person, emg_contact_person_rel, ",
            "emg_contact_person_tel, marry_flag, ",
            "hobbies, fax, msn, ",
            "special_skill, graduate_from, ",
            "graduate_date, major, ",
            "job, join_type, personal_email, ",
            "post_code, post_address, ",
            "leave_type, leave_date, ",
            "top_education, top_degree, ",
            "nation_name, political_status, ",
            "household_type, english_level, ",
            "other_language_level, first_join_date, ",
            "subcompany_id, belong_to_center, ",
            "family_contact_person, family_contact_person_rel, ",
            "family_contact_person_plane, family_contact_person_tel, ",
            "Nationality, home_address, ",
            "current_address, native_place, ",
            "insurance_place, household_place, ",
            "encouragement, join_post_date, ",
            "intership_end_date, become_valid_date, ",
            "create_time, update_time)",
            "values (#{userId,jdbcType=BIGINT}, #{post,jdbcType=VARCHAR}, ",
            "#{gender,jdbcType=BIT}, #{workStatus,jdbcType=VARCHAR}, ",
            "#{workAgeOutKs,jdbcType=INTEGER}, #{birthday,jdbcType=DATE}, ",
            "#{certificateType,jdbcType=VARCHAR}, #{certificationNo,jdbcType=VARCHAR}, ",
            "#{extraPhone,jdbcType=VARCHAR}, #{qq,jdbcType=VARCHAR}, ",
            "#{emgContactPerson,jdbcType=VARCHAR}, #{emgContactPersonRel,jdbcType=VARCHAR}, ",
            "#{emgContactPersonTel,jdbcType=VARCHAR}, #{marryFlag,jdbcType=VARCHAR}, ",
            "#{hobbies,jdbcType=VARCHAR}, #{fax,jdbcType=VARCHAR}, #{msn,jdbcType=VARCHAR}, ",
            "#{specialSkill,jdbcType=VARCHAR}, #{graduateFrom,jdbcType=VARCHAR}, ",
            "#{graduateDate,jdbcType=DATE}, #{major,jdbcType=VARCHAR}, ",
            "#{job,jdbcType=VARCHAR}, #{joinType,jdbcType=VARCHAR}, #{personalEmail,jdbcType=VARCHAR}, ",
            "#{postCode,jdbcType=VARCHAR}, #{postAddress,jdbcType=VARCHAR}, ",
            "#{leaveType,jdbcType=VARCHAR}, #{leaveDate,jdbcType=DATE}, ",
            "#{topEducation,jdbcType=VARCHAR}, #{topDegree,jdbcType=VARCHAR}, ",
            "#{nationName,jdbcType=VARCHAR}, #{politicalStatus,jdbcType=VARCHAR}, ",
            "#{householdType,jdbcType=VARCHAR}, #{englishLevel,jdbcType=VARCHAR}, ",
            "#{otherLanguageLevel,jdbcType=VARCHAR}, #{firstJoinDate,jdbcType=DATE}, ",
            "#{subcompanyId,jdbcType=BIGINT}, #{belongToCenter,jdbcType=VARCHAR}, ",
            "#{familyContactPerson,jdbcType=VARCHAR}, #{familyContactPersonRel,jdbcType=VARCHAR}, ",
            "#{familyContactPersonPlane,jdbcType=VARCHAR}, #{familyContactPersonTel,jdbcType=VARCHAR}, ",
            "#{nationality,jdbcType=VARCHAR}, #{homeAddress,jdbcType=VARCHAR}, ",
            "#{currentAddress,jdbcType=VARCHAR}, #{nativePlace,jdbcType=VARCHAR}, ",
            "#{insurancePlace,jdbcType=VARCHAR}, #{householdPlace,jdbcType=VARCHAR}, ",
            "#{encouragement,jdbcType=VARCHAR}, #{joinPostDate,jdbcType=DATE}, ",
            "#{intershipEndDate,jdbcType=DATE}, #{becomeValidDate,jdbcType=DATE}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(UserDetail record);

    int insertSelective(UserDetail record);

    @Select({
            "select",
            "id, user_id, post, gender, work_status, Work_Age_Out_KS, birthday, certificate_type, ",
            "certification_no, extra_phone, qq, emg_contact_person, emg_contact_person_rel, ",
            "emg_contact_person_tel, marry_flag, hobbies, fax, msn, special_skill, graduate_from, ",
            "graduate_date, major, job, join_type, personal_email, post_code, post_address, ",
            "leave_type, leave_date, top_education, top_degree, nation_name, political_status, ",
            "household_type, english_level, other_language_level, first_join_date, subcompany_id, ",
            "belong_to_center, family_contact_person, family_contact_person_rel, family_contact_person_plane, ",
            "family_contact_person_tel, Nationality, home_address, current_address, native_place, ",
            "insurance_place, household_place, encouragement, join_post_date, intership_end_date, ",
            "become_valid_date, create_time, update_time",
            "from user_detail",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserDetailMapper.BaseResultMap")
    UserDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserDetail record);

    @Update({
            "update user_detail",
            "set user_id = #{userId,jdbcType=BIGINT},",
            "post = #{post,jdbcType=VARCHAR},",
            "gender = #{gender,jdbcType=BIT},",
            "work_status = #{workStatus,jdbcType=VARCHAR},",
            "Work_Age_Out_KS = #{workAgeOutKs,jdbcType=INTEGER},",
            "birthday = #{birthday,jdbcType=DATE},",
            "certificate_type = #{certificateType,jdbcType=VARCHAR},",
            "certification_no = #{certificationNo,jdbcType=VARCHAR},",
            "extra_phone = #{extraPhone,jdbcType=VARCHAR},",
            "qq = #{qq,jdbcType=VARCHAR},",
            "emg_contact_person = #{emgContactPerson,jdbcType=VARCHAR},",
            "emg_contact_person_rel = #{emgContactPersonRel,jdbcType=VARCHAR},",
            "emg_contact_person_tel = #{emgContactPersonTel,jdbcType=VARCHAR},",
            "marry_flag = #{marryFlag,jdbcType=VARCHAR},",
            "hobbies = #{hobbies,jdbcType=VARCHAR},",
            "fax = #{fax,jdbcType=VARCHAR},",
            "msn = #{msn,jdbcType=VARCHAR},",
            "special_skill = #{specialSkill,jdbcType=VARCHAR},",
            "graduate_from = #{graduateFrom,jdbcType=VARCHAR},",
            "graduate_date = #{graduateDate,jdbcType=DATE},",
            "major = #{major,jdbcType=VARCHAR},",
            "job = #{job,jdbcType=VARCHAR},",
            "join_type = #{joinType,jdbcType=VARCHAR},",
            "personal_email = #{personalEmail,jdbcType=VARCHAR},",
            "post_code = #{postCode,jdbcType=VARCHAR},",
            "post_address = #{postAddress,jdbcType=VARCHAR},",
            "leave_type = #{leaveType,jdbcType=VARCHAR},",
            "leave_date = #{leaveDate,jdbcType=DATE},",
            "top_education = #{topEducation,jdbcType=VARCHAR},",
            "top_degree = #{topDegree,jdbcType=VARCHAR},",
            "nation_name = #{nationName,jdbcType=VARCHAR},",
            "political_status = #{politicalStatus,jdbcType=VARCHAR},",
            "household_type = #{householdType,jdbcType=VARCHAR},",
            "english_level = #{englishLevel,jdbcType=VARCHAR},",
            "other_language_level = #{otherLanguageLevel,jdbcType=VARCHAR},",
            "first_join_date = #{firstJoinDate,jdbcType=DATE},",
            "subcompany_id = #{subcompanyId,jdbcType=BIGINT},",
            "belong_to_center = #{belongToCenter,jdbcType=VARCHAR},",
            "family_contact_person = #{familyContactPerson,jdbcType=VARCHAR},",
            "family_contact_person_rel = #{familyContactPersonRel,jdbcType=VARCHAR},",
            "family_contact_person_plane = #{familyContactPersonPlane,jdbcType=VARCHAR},",
            "family_contact_person_tel = #{familyContactPersonTel,jdbcType=VARCHAR},",
            "Nationality = #{nationality,jdbcType=VARCHAR},",
            "home_address = #{homeAddress,jdbcType=VARCHAR},",
            "current_address = #{currentAddress,jdbcType=VARCHAR},",
            "native_place = #{nativePlace,jdbcType=VARCHAR},",
            "insurance_place = #{insurancePlace,jdbcType=VARCHAR},",
            "household_place = #{householdPlace,jdbcType=VARCHAR},",
            "encouragement = #{encouragement,jdbcType=VARCHAR},",
            "join_post_date = #{joinPostDate,jdbcType=DATE},",
            "intership_end_date = #{intershipEndDate,jdbcType=DATE},",
            "become_valid_date = #{becomeValidDate,jdbcType=DATE},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserDetail record);

    /* the flowing are user defined ... */
    int updateByUserIdSelective(UserDetail userDetail);

    @Select("select * from user_detail where user_id = #{userId}")
    UserDetail selectByUserId(Long userId);

}