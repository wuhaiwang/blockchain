package com.seasun.management.service;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.model.User;
import com.seasun.management.vo.UserCountCheckVo;

import java.util.List;

public interface TestService {

    void cleanUserSalaryChange(int year, int quarter);

    String testSqlServer();

    void cleanUserPerformance(int year, int month);

    void batchCreateUserPerformance(Integer year, Integer month, Integer quarter);

    /**
     * 同步固化人数
     *
     * @param params eg:{year:2017,month:4}
     */
    void syncFixedOutnumber(JSONObject params);

    /**
     * 同步在研累计成本
     *
     * @param params eg:{year:2017,month:4}
     */
    void syncSchedData(JSONObject params);

    /**
     * 同步外包数据
     *
     * @param params eg:{year:2017,month:4}
     */
    void syncOutsourcingData(JSONObject params);

    /**
     * 检查某个地区，hr和workGroup的人员是否一致
     *
     * @param city
     */
    UserCountCheckVo checkHrUserCount(String city, Long workGroupId);


    /**
     * 获取所有西米员工的ID
     * @return
     */
    List<Long> getXimiUsers();
}
