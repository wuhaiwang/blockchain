package com.seasun.management.mapper.dataCenter;

import com.seasun.management.dto.dataCenter.StatOnlineAccountDto;
import com.seasun.management.model.dataCenter.StatOnlineAccount;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface StatOnlineAccountMapper {
    @Insert({
        "insert into KBOSS20.STAT_ONLINE_ACCOUNT (CONFIG_GAME_ID, STAT_TIME, ",
        "GATEWAY_ID, ACCOUNT_COUNT)",
        "values (#{configGameId,jdbcType=DECIMAL}, #{statTime,jdbcType=TIMESTAMP}, ",
        "#{gatewayId,jdbcType=DECIMAL}, #{accountCount,jdbcType=DECIMAL})"
    })
    int insert(StatOnlineAccount record);

    int insertSelective(StatOnlineAccount record);

    /* the flowing are user defined ... */

    @Select({"SELECT sum(z.ACCOUNT_COUNT) account_sum ,z.STAT_TIME FROM" ,
            "(SELECT " ,
            "A.ACCOUNT_COUNT," ,
            "TO_CHAR(A.STAT_TIME, 'YYYY-MM-DD HH24:') ||" ,
            "SUBSTR(TO_CHAR(A.STAT_TIME, 'mi'), 1, 1) || '0' AS STAT_TIME",
            "FROM KBOSS20.STAT_ONLINE_ACCOUNT A" ,
            "WHERE A.STAT_TIME >= TO_DATE(#{0}, 'YYYY-MM-DD HH24:MI')" ,
            "AND A.STAT_TIME < TO_DATE(#{1}, 'YYYY-MM-DD HH24:MI')" ,
            "AND A.CONFIG_GAME_ID = #{2}" ,
            ")z" ,
            "GROUP BY z.STAT_TIME" ,
            "ORDER BY z.STAT_TIME"})
    List<StatOnlineAccountDto> selectStatAccountByConfGameId(String startTime, String endTime,long configGameId);
}