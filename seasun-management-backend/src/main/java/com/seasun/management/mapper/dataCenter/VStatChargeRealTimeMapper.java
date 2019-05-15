package com.seasun.management.mapper.dataCenter;

import com.seasun.management.dto.dataCenter.VStatChargeRealTimeDto;
import com.seasun.management.model.dataCenter.VStatChargeRealTime;
import com.seasun.management.vo.dataCenter.OnlineChargeSumVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface VStatChargeRealTimeMapper {
    @Insert({
        "insert into KBOSS20.V_STAT_CHARGE_REAL_TIME (CONFIG_GAME_ID, BEGIN_TIME, ",
        "END_TIME, ACCOUNT_NUM, ",
        "NUM_THAN_YESTERDAY, NUM_THAN_LAST_WEEK, ",
        "CHARGE_VALUE, VALUE_THAN_YESTERDAY, ",
        "VALUE_THAN_LAST_WEEK)",
        "values (#{configGameId,jdbcType=DECIMAL}, #{beginTime,jdbcType=TIMESTAMP}, ",
        "#{endTime,jdbcType=TIMESTAMP}, #{accountNum,jdbcType=DECIMAL}, ",
        "#{numThanYesterday,jdbcType=DECIMAL}, #{numThanLastWeek,jdbcType=DECIMAL}, ",
        "#{chargeValue,jdbcType=DECIMAL}, #{valueThanYesterday,jdbcType=DECIMAL}, ",
        "#{valueThanLastWeek,jdbcType=DECIMAL})"
    })
    int insert(VStatChargeRealTime record);

    int insertSelective(VStatChargeRealTime record);

    @Select({"SELECT TO_CHAR(begin_time,'YYYY-MM-DD HH24:MI') BEGIN_TIME,TO_CHAR(end_time,'YYYY-MM-DD HH24:MI') END_TIME,charge_value" ,
            "  FROM KBOSS20.V_STAT_CHARGE_REAL_TIME",
            " WHERE BEGIN_TIME >= TO_DATE(#{0}, 'YYYY-MM-DD HH24:MI')",
            "   AND BEGIN_TIME < TO_DATE(#{1}, 'YYYY-MM-DD HH24:MI')",
            "   AND CONFIG_GAME_ID = #{2} ORDER BY BEGIN_TIME "})
    List<VStatChargeRealTimeDto> selectOnlineChargeSumByConfGameId(String startTime , String endTime,long configGameId);
}