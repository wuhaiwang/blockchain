package com.seasun.management.controller.finance;

import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.helper.ReportHelper;
import com.seasun.management.model.FnProjectSchedData;
import com.seasun.management.model.FnProjectStatData;
import com.seasun.management.service.FnProjectSchedDataService;
import com.seasun.management.service.FnProjectStatDataService;
import com.seasun.management.vo.FnProjectSchedDataVo;
import com.seasun.management.vo.FnProjectStatDataVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SettingController {

    private static final Logger logger = LoggerFactory.getLogger(SettingController.class);

    @Autowired
    private FnProjectSchedDataService fnProjectSchedDataService;

    @Autowired
    private FnProjectStatDataService fnProjectStatDataService;

    @RequestMapping(value = "/apis/auth/fn-project-sched-data", method = RequestMethod.GET)
    public ResponseEntity<?> getProjectSchedData(@RequestParam Integer year) {
        logger.info("begin getProjectSchedData...");
        FnProjectSchedData fnProjectSchedData = new FnProjectSchedData();
        fnProjectSchedData.setYear(year);
        List<FnProjectSchedDataVo> fnProjectSchedDataVos = fnProjectSchedDataService.getFnProjectSchedDataByCondition(fnProjectSchedData);
        logger.info("end getProjectSchedData...");
        return ResponseEntity.ok(fnProjectSchedDataVos);
    }

    @RequestMapping(value = "/apis/auth/fn-project-sched-data", method = RequestMethod.POST)
    public ResponseEntity<?> batchUpdateProjectSchedData(@RequestBody List<FnProjectSchedData> fnProjectSchedData) {
        logger.info("begin batchUpdateProjectSchedData...");
        fnProjectSchedDataService.batchUpdateProjectSchedData(fnProjectSchedData);
        logger.info("end batchUpdateProjectSchedData...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/fn-project-sched-data", method = RequestMethod.PUT)
    public ResponseEntity<?> updateProjectSchedData(@RequestBody FnProjectSchedData fnProjectSchedData) {
        logger.info("begin updateProjectSchedData...");
        fnProjectSchedDataService.updateProjectSchedData(fnProjectSchedData);
        logger.info("end updateProjectSchedData...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/fn-project-stat-data/outsourcing", method = RequestMethod.GET)
    public ResponseEntity<?> getProjectOutsourcingStatData(@RequestParam Integer year) {
        logger.info("begin getProjectStatData...");
        FnProjectStatData fnProjectStatData = new FnProjectStatData();
        fnProjectStatData.setYear(year);
        fnProjectStatData.setFnStatId(ReportHelper.OUTSOURCING_STAT_ID);
        List<FnProjectStatDataVo> fnProjectStatDataVos = fnProjectStatDataService.getFnProjectStatDataVosByCondition(fnProjectStatData);
        logger.info("end getProjectStatData...");
        return ResponseEntity.ok(fnProjectStatDataVos);
    }


    @RequestMapping(value = "/apis/auth/fn-project-stat-data/outsourcing", method = RequestMethod.POST)
    public ResponseEntity<?> batchUpdateProjectStatData(@RequestBody List<FnProjectStatData> fnProjectStatData) {
        logger.info("begin batch updateProject outsourcing StatData...");
        fnProjectStatDataService.batchUpdateProjectStatData(fnProjectStatData);
        logger.info("end batch updateProject outsourcing StatData...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }


}
