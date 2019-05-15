package com.seasun.management.controller.finance;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.model.FnProjectStatDataDetail;
import com.seasun.management.service.FnProjectStatDataDetailService;
import com.seasun.management.vo.FnProjectStatDataDetaildataVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/apis/auth")
public class FnProjectDetailController {

    private static final Logger logger = LoggerFactory.getLogger(FnProjectDetailController.class);

    @Autowired
    FnProjectStatDataDetailService fnProjectStatDataDetailService;


    @RequestMapping(value = "/fn-project-stat-data-details", method = RequestMethod.POST)
    public ResponseEntity<?> importFnProjectStatDataDetails(@RequestParam MultipartFile file, @RequestParam Integer year, @RequestParam Integer month) {
        logger.info("begin importFnProjectStatDataDetails...");
        String strErrorFileUrl = fnProjectStatDataDetailService.importFile(file, year, month);
        logger.info("end importFnProjectStatDataDetails...");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("url", strErrorFileUrl);
        return ResponseEntity.ok(jsonObject);
    }

    // 项目费用-子项目明细
    @RequestMapping(value = "/fn-project-stat-data-detail", method = RequestMethod.GET)
    public ResponseEntity<?> getDetailByCondition(@RequestParam Long projectId,@RequestParam Long statId, @RequestParam Integer year,@RequestParam Integer month) {
        logger.info("begin getFnProjectStatDetailDataVosByCondition...");
        List<FnProjectStatDataDetaildataVo> result = fnProjectStatDataDetailService.getDetailByCondition( projectId,   statId,   year,   month);
        logger.info("end getFnProjectStatDetailDataVosByCondition...");
        return ResponseEntity.ok(result);
    }

}