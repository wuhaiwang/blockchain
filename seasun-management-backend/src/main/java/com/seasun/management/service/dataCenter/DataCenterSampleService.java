package com.seasun.management.service.dataCenter;

import com.seasun.management.model.dataCenter.Jx2Loginstat;
import com.seasun.management.vo.dataCenter.BossDataVo;
import com.seasun.management.vo.dataCenter.RealTimeOnlineDataVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DataCenterSampleService {

    List<Jx2Loginstat> getAllLoginInfoByCond(String startTime, String endTime, String type);

    RealTimeOnlineDataVo getRealTimeDataCollection(Date onlineDate, Date chargeSumDate,
                                                   Date dailyDate, String project);

    /**
     * 老板数据接口
     * @param code 项目编码
     * @return
     */
    public BossDataVo getBossInterfaceData(String code);

    /**
     * 项目质量
     * @param projectId
     * @return
     */
    Map<String,String> getQualityUrl(Long projectId);
}
