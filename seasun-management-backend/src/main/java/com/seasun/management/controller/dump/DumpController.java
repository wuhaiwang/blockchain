package com.seasun.management.controller.dump;

import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.dto.AppDumpDayDto;
import com.seasun.management.service.DumpService;
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
public class DumpController {

    private static final Logger logger = LoggerFactory.getLogger(DumpController.class);

    @Autowired
    private DumpService dumpService;

    @RequestMapping(value = "/apis/auth/app/dump-info", method = RequestMethod.GET)
    public ResponseEntity<?> getAppDumpInfo(@RequestParam(required = false) Long projectId) {
        logger.info("begin getAppDumpInfo...");
        List<AppDumpDayDto> contactsAppVo = dumpService.getAppDumpInfo(projectId);
        logger.info("end getAppDumpInfo...");
        return ResponseEntity.ok(new CommonAppResponse(0, contactsAppVo));
    }
}
