package com.seasun.management.mapper;

import com.seasun.management.dto.UserDormitoryReportDto;
import com.seasun.management.dto.UserDormitoryReservationDto;
import com.seasun.management.model.UserDormitoryReservation;

import com.seasun.management.vo.UserDormitoryReservationVo;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface UserDormitoryReservationMapper {
    @Delete({
            "delete from user_dormitory_reservation",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into user_dormitory_reservation (name,id_code,user_id, bedroom_type,berth_type, expect_checkin_date,expect_checkout_date, status,login_id, create_date,room_no ,gender,live_no,mobile,type,project_id,company_id,certificate_type) ",
            "values (#{name,jdbcType=VARCHAR}, #{idCode,jdbcType=VARCHAR}, ",
            "#{userId,jdbcType=BIGINT}, #{bedroomType,jdbcType=VARCHAR}, ",
            "#{berthType,jdbcType=VARCHAR}, #{expectCheckinDate,jdbcType=TIMESTAMP}, ",
            "#{expectCheckoutDate,jdbcType=TIMESTAMP}, #{status,jdbcType=VARCHAR}, ",
            "#{loginId,jdbcType=VARCHAR}, #{createDate,jdbcType=DATE}, #{roomNo,jdbcType=VARCHAR}," ,
            "#{gender,jdbcType=INTEGER}, #{liveNo,jdbcType=VARCHAR}, #{mobile,jdbcType=VARCHAR}, #{type,jdbcType=INTEGER}, #{projectId,jdbcType=BIGINT}, #{companyId,jdbcType=BIGINT}, #{certificateType,jdbcType=VARCHAR})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Long.class)
    int insert(UserDormitoryReservation record);

    int insertSelective(UserDormitoryReservation record);

    @Select({
            "select",
            "id, name, id_code, user_id, bedroom_type, berth_type, expect_checkin_date, expect_checkout_date, ",
            "status, create_id, create_date",
            "from user_dormitory_reservation",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("com.seasun.management.mapper.UserDormitoryReservationMapper.BaseResultMap")
    UserDormitoryReservation selectByPrimaryKey(Long id);

    @Select("select * from user_dormitory_reservation where user_id = #{0}")
    List<UserDormitoryReservation> selectByUserId(Long userId);

    int updateByPrimaryKeySelective(UserDormitoryReservation record);

    @Update("update user_dormitory_reservation set user_id = #{1} where id_code = #{0}")
    int updateUserIdByCertNO(String CertNO,Long userId);

    @Update({
            "update user_dormitory_reservation",
            "set name = #{name,jdbcType=VARCHAR},",
            "id_code = #{idCode,jdbcType=VARCHAR},",
            "user_id = #{userId,jdbcType=BIGINT},",
            "bedroom_type = #{bedroomType,jdbcType=VARCHAR},",
            "berth_type = #{berthType,jdbcType=VARCHAR},",
            "expect_checkin_date = #{expectCheckinDate,jdbcType=TIMESTAMP},",
            "expect_checkout_date = #{expectCheckoutDate,jdbcType=TIMESTAMP},",
            "status = #{status,jdbcType=VARCHAR},",
            "create_id = #{createId,jdbcType=BIGINT},",
            "create_date = #{createDate,jdbcType=DATE}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(UserDormitoryReservation record);

    /* the flowing are user defined ... */

    @Select({"select * from user_dormitory_reservation"})
    List<UserDormitoryReservation> selectAll();

    void batchInsert(List<UserDormitoryReservation> userDormitoryReservations);

    @Delete({"delete from user_dormitory_reservation"})
    void deleteAll();

    List<UserDormitoryReservationDto> selectDtoByIdCodes(List<String> idCodes);

    @Select({
            "SELECT udr.id, ud.user_id FROM user_dormitory_reservation udr  JOIN user_detail ud ON udr.user_id IS NULL AND ud.certification_no = udr.id_code;"
    })
    List<UserDormitoryReservation> selectByUserIdIsNullJoinUserNotNull();

    /**
     * 代用这个方法必须先给集合判空
     * */
    int batchUpdateUserIdByPrimaryKey(@Param("userDormitoryReservationList") List<UserDormitoryReservation> userDormitoryReservationList);

    @Select({
            "SELECT live_no, id, DATE_FORMAT(checkin_date, '%Y-%m-%d %T') AS checkin_date, DATE_FORMAT(checkout_date, '%Y-%m-%d %T') AS checkout_date   FROM user_dormitory_reservation WHERE checkin_date  IS NULL OR checkout_date IS NULL;"
    })
    List<UserDormitoryReservationVo> selectByNonCheckInOrNonCheckOut();

    /**
     * 代用这个方法必须先给集合判空
     * */
    int batchUpdateCheckInAndCheckout (@Param("items") List<UserDormitoryReservationVo> items);

    List<UserDormitoryReportDto> selectUserDormitoryReport(@Param("startDate")String startDate,@Param("endDate")String endDate);

    @Delete({
            "delete from user_dormitory_reservation where live_no = #{liveNo,jdbcType=VARCHAR}"
    })
    int deleteByLiveNo(String liveNo);

    @Update("update user_dormitory_reservation d set d.user_id=(select u.id from user u,user_detail ud where u.id = ud.user_id and d.id_code = ud.certification_no order by u.id desc LIMIT 0 ,1) where d.user_id IS NULL")
    int updateUserIdBycertificationNo();
    @Select({
            "select d.id,tmp.companyId,tmp.projectId from user_dormitory_reservation d,(\n" +
                    "select u.id id,d.certification_no,s.id companyId,IFNULL((select pc.id from project pc where pc.id = p.parent_hr_id),p.id) projectId \n" +
                    "from user u left join user_detail d on u.id = d.user_id \n" +
                    "left join order_center o on o.id = u.order_center_id \n" +
                    "left join cost_center c on u.cost_center_id = c.id \n" +
                    "left join subcompany s on s.code = substr(c.code, 1, 4) \n" +
                    "left join user_special_disp sd on sd.user_id = u.id \n" +
                    "left join project p on p.id = o.project_id)tmp where d.id_code = tmp.certification_no and (d.company_id is null or d.project_id is null)"
    })
    List<UserDormitoryReservation> selectUserIsCompanyIdOrProjectIdNull();

    int updateUserCompanyIdOrProjectIdById(@Param("list") List<UserDormitoryReservation> list);
}