package com.seasun.management.controller.admin;

import com.seasun.management.constant.ErrorCode;
import com.seasun.management.constant.ErrorMessage;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.controller.response.FileResponse;
import com.seasun.management.model.UserMessage;
import com.seasun.management.service.PsychologicalConsultantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/apis/auth")
public class DataExportController {

    private static final Logger logger = LoggerFactory.getLogger(DataExportController.class);

    @Autowired
    private PsychologicalConsultantService psychologicalConsultantService;

    @RequestMapping(value = "/user-psychological-consultations", method = RequestMethod.GET)
    public ResponseEntity<?> exportPsychologicalConsultantPassword(@RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month) {
        String result = psychologicalConsultantService.exportPsychologicalConsultantPassword(year, month);
        if (result != null) {
            return ResponseEntity.ok(new FileResponse(0,result));
        }
        return ResponseEntity.ok(new CommonResponse(ErrorCode.PARAM_ERROR, ErrorMessage.PARAM_ERROR_MESSAGE)) ;
    }

}
