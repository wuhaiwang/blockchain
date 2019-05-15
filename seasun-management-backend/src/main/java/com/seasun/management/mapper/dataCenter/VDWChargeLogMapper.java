package com.seasun.management.mapper.dataCenter;

import com.seasun.management.dto.dataCenter.VDWChargeLogDto;
import com.seasun.management.model.dataCenter.VDWChargeLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface VDWChargeLogMapper {
    @Insert({
            "insert into KBOSS20.V_DW_CHARGE_LOG (GAME_CODE, IID, ",
            "FILL_DATETIME, CARD_TYPE, ",
            "CARD_AMOUNT, FILL_TYPE, ",
            "GAMEUID, GATEWAY)",
            "values (#{gameCode,jdbcType=VARCHAR}, #{iid,jdbcType=DECIMAL}, ",
            "#{fillDatetime,jdbcType=TIMESTAMP}, #{cardType,jdbcType=DECIMAL}, ",
            "#{cardAmount,jdbcType=DECIMAL}, #{fillType,jdbcType=DECIMAL}, ",
            "#{gameuid,jdbcType=VARCHAR}, #{gateway,jdbcType=VARCHAR})"
    })
    int insert(VDWChargeLog record);

    int insertSelective(VDWChargeLog record);

    /* the flowing are user defined ... */


    /**
     * 返回所求时间区间的所有账号充值记录
     *
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @return 所有账号充值记录
     */
    @Select({"SELECT ",
            "T.FILL_DATETIME, ",
            "T.CARD_TYPE, ",
            "T.CARD_AMOUNT, ",
            "T.FILL_TYPE ",
            "FROM KBOSS20.V_DW_CHARGE_LOG T ",
            "WHERE T.FILL_DATETIME >= TO_DATE(#{0}, 'YYYY-MM-DD HH24:MI') ",
            "AND T.FILL_DATETIME < TO_DATE(#{1}, 'YYYY-MM-DD HH24:MI') ",
            "AND GAME_CODE = 'JX3' ORDER BY FILL_DATETIME"})
    List<VDWChargeLogDto> selectOnlineChargeDto(String startTime, String endTime);
}