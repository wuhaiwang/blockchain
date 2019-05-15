package com.seasun.management.controller.org;

import com.seasun.management.service.UseLogService;
import com.seasun.management.vo.UseLogVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/apis/auth")
public class UseLogController {
    private static final Logger logger = LoggerFactory.getLogger(UseLogController.class);

    @Autowired
    UseLogService useLogService;

    @RequestMapping(value = "/use-log", method = RequestMethod.GET)
    public ResponseEntity<?> getUseLogList(@RequestParam(required = false) Long currentPage, @RequestParam(required = false) Long pageSize) {
        logger.info("begin getUseLogList...");
        List<UseLogVo> useLogList = useLogService.getUseLogList(currentPage, pageSize);
        logger.info("end getUseLogList...");
        return ResponseEntity.ok(useLogList);
    }
}
