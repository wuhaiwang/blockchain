package com.seasun.management.mapper.dataCenter;

import com.seasun.management.dto.dataCenter.VFactJx3StatDDto;
import com.seasun.management.model.dataCenter.VFactJx3StatD;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface VFactJx3StatDMapper {
    @Insert({
            "insert into KBOSS20.V_FACT_JX3_STAT_D (STAT_DATE, ACTV_ACCOUNT_NUM, ",
            "NEW_ACCOUNT_NUM, WEEK_ACTV_ACCOUNT_NUM, ",
            "MONTH_ACTV_ACCOUNT_NUM, ALL_LOST_ACCOUNT_NUM, ",
            "CARD_MONEY_VALUE, CONSUME_VALUE, ",
            "CONSUME_TIME_VALUE, CONSUME_ITEM_VALUE, ",
            "CONSUME_TIME_RATE, ARPU, ",
            "CHARGE_ACCOUNT_NUM, NEW_CHARGE_ACCNT_NUM, ",
            "DAY_FILL_RACE, PCU, ",
            "INDEP_IP_NUM, ALL_LOST_RATE, ",
            "LOST_RATE, LOST_ACCOUNT_NUM, ",
            "BACK_ACCOUNT_NUM)",
            "values (#{statDate,jdbcType=TIMESTAMP}, #{actvAccountNum,jdbcType=DECIMAL}, ",
            "#{newAccountNum,jdbcType=DECIMAL}, #{weekActvAccountNum,jdbcType=DECIMAL}, ",
            "#{monthActvAccountNum,jdbcType=DECIMAL}, #{allLostAccountNum,jdbcType=DECIMAL}, ",
            "#{cardMoneyValue,jdbcType=DECIMAL}, #{consumeValue,jdbcType=DECIMAL}, ",
            "#{consumeTimeValue,jdbcType=DECIMAL}, #{consumeItemValue,jdbcType=DECIMAL}, ",
            "#{consumeTimeRate,jdbcType=DECIMAL}, #{arpu,jdbcType=DECIMAL}, ",
            "#{chargeAccountNum,jdbcType=DECIMAL}, #{newChargeAccntNum,jdbcType=DECIMAL}, ",
            "#{dayFillRace,jdbcType=DECIMAL}, #{pcu,jdbcType=DECIMAL}, ",
            "#{indepIpNum,jdbcType=DECIMAL}, #{allLostRate,jdbcType=DECIMAL}, ",
            "#{lostRate,jdbcType=DECIMAL}, #{lostAccountNum,jdbcType=DECIMAL}, ",
            "#{backAccountNum,jdbcType=DECIMAL})"
    })
    int insert(VFactJx3StatD record);

    int insertSelective(VFactJx3StatD record);

    /* the flowing are user defined ... */

    @Select("select * from KBOSS20.V_FACT_JX3_STAT_D WHERE STAT_DATE >= TO_DATE( #{0}, 'YYYY-MM-DD')" +
            "   AND STAT_DATE <= TO_DATE(#{1}, 'YYYY-MM-DD')")
    List<VFactJx3StatD> selectAllWithDate(String startTime, String endTime);

    @Select({"SELECT STAT_DATE，ACTV_ACCOUNT_NUM ,NEW_ACCOUNT_NUM,CARD_MONEY_VALUE,CONSUME_VALUE,CONSUME_ITEM_VALUE,CHARGE_ACCOUNT_NUM,NEW_CHARGE_ACCNT_NUM",
            "FROM KBOSS20.V_FACT_JX3_STAT_D",
            "WHERE STAT_DATE >= TO_DATE(#{0}, 'YYYY-MM-DD')",
            "AND STAT_DATE < TO_DATE(#{1}, 'YYYY-MM-DD')　ORDER BY STAT_DATE"})
    List<VFactJx3StatDDto> selectJX3TimeRangeStatD(String startTime, String endTime);
}