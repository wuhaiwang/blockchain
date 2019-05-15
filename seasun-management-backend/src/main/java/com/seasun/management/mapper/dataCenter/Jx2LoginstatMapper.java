package com.seasun.management.mapper.dataCenter;

import com.seasun.management.model.dataCenter.Jx2Loginstat;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface Jx2LoginstatMapper {
    @Insert({
            "insert into KBOSS_PAYSYS10.JX2_LOGINSTAT (STAT_DATE, LOGIN_TOTAL_ACC_NUM, ",
            "FIRST_LOGIN_ACC_NUM, PAY_PLAYER_NUM, ",
            "WEEK_PLAYER_NUM, MONTH_PLAYER_NUM, ",
            "POINT_PLAYER_NUM, ONLINE_PAY_PLAYER_MAX, ",
            "TYPE, MON_ACTIVE_ACC_NUM)",
            "values (#{statDate,jdbcType=TIMESTAMP}, #{loginTotalAccNum,jdbcType=DECIMAL}, ",
            "#{firstLoginAccNum,jdbcType=DECIMAL}, #{payPlayerNum,jdbcType=DECIMAL}, ",
            "#{weekPlayerNum,jdbcType=DECIMAL}, #{monthPlayerNum,jdbcType=DECIMAL}, ",
            "#{pointPlayerNum,jdbcType=DECIMAL}, #{onlinePayPlayerMax,jdbcType=DECIMAL}, ",
            "#{type,jdbcType=VARCHAR}, #{monActiveAccNum,jdbcType=DECIMAL})"
    })
    int insert(Jx2Loginstat record);

    int insertSelective(Jx2Loginstat record);

      /* the flowing are user defined ... */

    @Select("SELECT * FROM KBOSS_PAYSYS10.JX2_LOGINSTAT" +
            " WHERE STAT_DATE >= TO_DATE(#{0}, 'YYYY-MM-DD')" +
            "   AND STAT_DATE <= TO_DATE(#{1}, 'YYYY-MM-DD')" +
            "   AND TYPE = #{2}")
    List<Jx2Loginstat> selectAllWithDateAndType(String startTime, String endTime, String type);
}