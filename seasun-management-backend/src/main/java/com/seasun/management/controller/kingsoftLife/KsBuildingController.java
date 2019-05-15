package com.seasun.management.controller.kingsoftLife;


import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.service.kingsoftLife.KsLocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "apis/auth/app/ks-life/")
public class KsBuildingController {

    @Autowired
    private KsLocationService ksLocationService;

    private static final Logger logger = LoggerFactory.getLogger(KsBuildingController.class);

    @RequestMapping(value = "buildings", method = RequestMethod.GET)
    public ResponseEntity<?> getAllImages() {
        logger.info("begin get All Images ...");
        CommonResponse result = ksLocationService.getAllBuildingImags();
        logger.info("end get All Images ...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "location/search", method = RequestMethod.GET)
    public ResponseEntity<?> searchBuilding(@RequestParam(required = false) String buildingCode, @RequestParam(required = false) String floor,
                                            @RequestParam(required = false) String keyword, @RequestParam(required = false) String pageNo,
                                            @RequestParam(required = false) String pageSize, @RequestParam(required = false) String loginId) {
        logger.info("begin search Building ...");
        CommonResponse result = ksLocationService.searchBuildingByCondition(buildingCode, floor, keyword, pageNo, pageSize, loginId);
        logger.info("begin search Building ...");
        return ResponseEntity.ok(result);
    }


    @RequestMapping(value = "location/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getPositionDetail(@PathVariable Long id, @RequestParam(required = false) String loginId) {
        logger.info("begin getPositionDetail ...");
        CommonResponse result = ksLocationService.getPositionDetail(id, loginId);
        logger.info("end getPositionDetail ...");
        return ResponseEntity.ok(result);
    }
}
