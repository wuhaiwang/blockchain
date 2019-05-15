package com.seasun.management.controller.app;

import com.seasun.management.controller.pm.ProjectPlanController;
import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.service.ProjectPlanService;
import com.seasun.management.service.dataCenter.DataCenterSampleService;
import com.seasun.management.vo.AppAnnualPlanVo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class AppProjectPlanController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProjectPlanController.class);

    @Autowired
    private ProjectPlanService projectPlanService;

    @Autowired
    private DataCenterSampleService dataCenterSampleService;

    @RequestMapping(value = "/apis/auth/app/pm-milestone", method = RequestMethod.GET)
    public ResponseEntity<?> getAllPMileStones(){
        logger.info("begin getAllPMileStones...");
        AppAnnualPlanVo result = projectPlanService.getAppAnnulPlan();
        logger.info("end getAllPMileStones...");
        return ResponseEntity.ok(new CommonAppResponse(0,result));
    }
    @RequestMapping(value = "/apis/auth/app/pm-quality", method = RequestMethod.GET)
    public ResponseEntity<?> getQualityUrl(@RequestParam Long projectId){
        logger.info("begin getQualityUrl...");
        Map<String,String> map = dataCenterSampleService.getQualityUrl(projectId);
        logger.info("end getQualityUrl...");
        return ResponseEntity.ok(new CommonAppResponse(Integer.parseInt(map.get("ret")),map.get("url")));
    }

}
