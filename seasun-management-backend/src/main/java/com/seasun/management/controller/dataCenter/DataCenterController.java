package com.seasun.management.controller.dataCenter;

import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.model.dataCenter.Jx2Loginstat;
import com.seasun.management.service.dataCenter.DataCenterSampleService;
import com.seasun.management.vo.dataCenter.BossDataVo;
import com.seasun.management.vo.dataCenter.RealTimeOnlineDataVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class DataCenterController {

    private static final Logger logger = LoggerFactory.getLogger(DataCenterController.class);

    @Autowired
    private DataCenterSampleService dataCenterSampleService;

    @RequestMapping(value = "/apis/auth/data-center/real-time-login-data", method = RequestMethod.POST)
    public ResponseEntity<?> getRealTimeLoginData() {
        logger.info("begin getRealTimeLoginData...");
        List<Jx2Loginstat> result = dataCenterSampleService.getAllLoginInfoByCond("2017-05-08", "2017-06-07", "day");
        logger.info("end getRealTimeLoginData...");
        return ResponseEntity.ok(result);
    }

//    @RequestMapping(value = "/apis/auth/app/stat-data")
//    public ResponseEntity<?> getRealTimeOnlineData() {
//        logger.info("begin getRealTimeOnlineData ...");
//        Date currentDate = new Date();
//        RealTimeOnlineDataVo result = dataCenterSampleService.getRealTimeDataCollection(currentDate, currentDate, currentDate,"JX3");
//        logger.info("end getRealTimeOnlineData ...");
//        return ResponseEntity.ok(new CommonAppResponse(0, result));
//    }
    @RequestMapping(value = "/apis/auth/app/stat-data", method = RequestMethod.GET)
    public ResponseEntity<?> getRealTimeOnlineData() {
        BossDataVo vo = dataCenterSampleService.getBossInterfaceData("JX3");
        return ResponseEntity.ok(new CommonAppResponse(0, vo));
    }
}
