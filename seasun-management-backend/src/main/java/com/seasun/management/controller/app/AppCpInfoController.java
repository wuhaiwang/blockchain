package com.seasun.management.controller.app;

import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.service.CpInfoService;
import com.seasun.management.vo.BaseQueryResponseVo;
import com.seasun.management.vo.cp.ProjectBudgetInfo;
import com.seasun.management.vo.cp.ProjectOutcourceInfoVo;
import org.slf4j.Logger;
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
@RequestMapping(value = "/apis/auth/app/cp")
public class AppCpInfoController {

    private static final Logger logger = LoggerFactory.getLogger(AppCpInfoController.class);

    @Autowired
    private CpInfoService cpInfoService;

    @RequestMapping(value = "/outsourcers", method = RequestMethod.GET)
    public ResponseEntity getOutsourcers(Integer year, Integer currentPage, Integer pageSize, @RequestParam(required = false) List<Integer> grade, @RequestParam(required = false) String makingType, @RequestParam(required = false) String styleType, @RequestParam(required = false) String sortField) {
        logger.info("begin getOutsourcers...");
        BaseQueryResponseVo result = cpInfoService.getOutsourcers(year, currentPage, pageSize, grade, makingType, styleType, sortField);
        logger.info("end getOutsourcers...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }

    @RequestMapping(value = "/projects", method = RequestMethod.GET)
    public ResponseEntity getProjectInfos(Integer year, Integer currentPage, Integer pageSize, String sortField) {
        logger.info("begin getProjectInfos...");
        BaseQueryResponseVo result = cpInfoService.getProjectInfos(year, currentPage, pageSize, sortField);
        logger.info("end getProjectInfos...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }

    @RequestMapping(value = "/support", method = RequestMethod.GET)
    public ResponseEntity<?> getSupport() {
        Map<String, List<String>> map = cpInfoService.getSupport();
        return ResponseEntity.ok(new CommonAppResponse(0, map));
    }

    @RequestMapping(value = "/budget", method = RequestMethod.GET)
    public ResponseEntity<?> getSeasunBudgetInfo(Integer year) {
        logger.info("begin getSeasunBudgetInfo...");
        ProjectBudgetInfo seasunBudgetInfo = cpInfoService.getSeasunBudgetInfo(year);
        logger.info("end getSeasunBudgetInfo...");
        return ResponseEntity.ok(new CommonAppResponse(0, seasunBudgetInfo));
    }

    @RequestMapping(value = "/project", method = RequestMethod.GET)
    public ResponseEntity getProjectInfo(Long id, Integer year, Integer quarter, Integer month, Integer currentPage, Integer pageSize) {
        logger.info("begin getProjectInfo...");
        ProjectOutcourceInfoVo result = cpInfoService.getProjectInfo(id, year, quarter, month, currentPage, pageSize);
        logger.info("end getProjectInfo...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }
}

