package com.seasun.management.controller.perf;

import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.controller.response.FileResponse;
import com.seasun.management.dto.YearMonthDto;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.service.PerformanceDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/apis/auth")
public class PerformanceDataAccessController {

    @Autowired
    private PerformanceDataService performanceDataService;

    private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);

    @RequestMapping(value = "/performance-data-access/user-identity", method = RequestMethod.GET)
    public ResponseEntity<?> getUserIdentity(@RequestParam(required = false) Long userId) {
        logger.info("begin exportPerformanceData...");
        List<IdNameBaseObject> permissionList = performanceDataService.getPerformanceDataPermission(userId);
        logger.info("end exportPerformanceData...");
        return ResponseEntity.ok(permissionList);
    }

    @RequestMapping(value = "/performance-data-access/performance-date", method = RequestMethod.GET)
    public ResponseEntity<?> getPerformanceDate(Long performanceWorkGroupId) {
        logger.info("begin getPerformanceDate...");
        List<YearMonthDto> yearMonthDtoList = performanceDataService.getPerformanceDate(performanceWorkGroupId);
        logger.info("end getPerformanceDate...");
        return ResponseEntity.ok(yearMonthDtoList);
    }

    @RequestMapping(value = "/performance-data-access/performance-import-check", method = RequestMethod.POST)
    public ResponseEntity<?> checkoutPerformanceFormat(MultipartFile file, Long performanceWorkGroupId) {
        logger.info("begin checkoutPerformanceFormat...");
        List<String> errorList = performanceDataService.checkImportUserPerformanceData(file, performanceWorkGroupId);
        logger.info("end checkoutPerformanceFormat...");
        return ResponseEntity.ok(errorList);
    }

    @RequestMapping(value = "/performance-data-access/performance-import", method = RequestMethod.POST)
    public ResponseEntity<?> importPerformanceData(MultipartFile file, Long performanceWorkGroupId, Boolean insertFlag) {
        logger.info("begin importPerformanceData...");
        performanceDataService.importUserPerformance(file, performanceWorkGroupId, insertFlag);
        logger.info("end importPerformanceData...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    //已合并至PerformanceController.downloadSubPerformance()方法
   /* @RequestMapping(value = "/performance-data-access/performance-export", method = RequestMethod.GET)
    public ResponseEntity<?> exportPerformanceData(@RequestParam Integer year, @RequestParam Integer month, @RequestParam Long performanceWorkGroupId) {
        logger.info("begin exportPerformanceData...");
        String str = performanceDataService.exportPerformanceData(performanceWorkGroupId, year, month);
        logger.info("end exportPerformanceData...");
        return ResponseEntity.ok(new FileResponse(0, str));
    }*/

    @RequestMapping(value = "/performance-config-manager", method = RequestMethod.POST)
    public ResponseEntity<?> changeDataManager(@RequestParam Long userId, @RequestParam Long workGroupPerformanceId, @RequestParam String type) {
        logger.info("begin exportPerformanceData...");
        boolean result = performanceDataService.changeDataManager(userId, workGroupPerformanceId, type);
        logger.info("end exportPerformanceData...");
        if (result) {
            return ResponseEntity.ok(new CommonResponse(0, "success"));
        } else {
            return ResponseEntity.ok(new CommonResponse(1, "不能添加重复的专员"));
        }
    }

    @RequestMapping(value = "/performance-config-manager", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteDataManager(@RequestParam Long id) {
        logger.info("begin exportPerformanceData...");
        performanceDataService.deleteDataManager(id);
        logger.info("end exportPerformanceData...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }
}
