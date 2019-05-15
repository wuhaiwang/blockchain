package com.seasun.management.controller.perf;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.service.PerformanceFMService;
import com.seasun.management.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/apis/auth/performance-fix-member")
public class PerformanceFMController {

    @Autowired
    private PerformanceFMService performanceFMService;

    private static final Logger logger = LoggerFactory.getLogger(PerformanceFMController.class);

    @RequestMapping(value = "/user-identity", method = RequestMethod.GET)
    public ResponseEntity<?> getUserIdentityInfo(@RequestParam(required = false) Long userId) {
        logger.info("begin getUserIdentityInfo...");
        List<PerformanceFMStageVo> result = performanceFMService.getUserIdentityInfo(userId);
        logger.info("end getUserIdentityInfo...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/members", method = RequestMethod.GET)
    public ResponseEntity<?> getFixMemberGroups(@RequestParam String type, @RequestParam Long id) {
        logger.info("begin getFixMemberGroups...");
        PerformanceFMGroupsVo result = performanceFMService.getFixMemberGroups(type, id);
        logger.info("end getFixMemberGroups...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/check-excel", method = RequestMethod.POST)
    public ResponseEntity<?> verifyFixMemberData(@RequestParam("file") MultipartFile file, @RequestParam Long platId) {
        logger.info("begin importFixMemberData...");
        ImportFixMemberDataResultVo resultVo = performanceFMService.verifyFixMemberData(file, platId);
        logger.info("end importFixMemberData...");
        return ResponseEntity.ok(resultVo);
    }

    @RequestMapping(value = "/data-import-confirm", method = RequestMethod.POST)
    public ResponseEntity<?> confirmPermanentFixedMember(@RequestBody PerformanceFMConfirmVo vo) {
        logger.info("begin confirmPermanentFixedMember...");
        CommonResponse response;
        performanceFMService.confirmPermanentFixedMember(vo.getRemovedMembers(), vo.getChangedMembers(), vo.getPlatId(), vo.getFileName());
        response = new CommonResponse(0, "success");
        logger.info("end confirmPermanentFixedMember...");
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/member/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> permanentFixMember(@PathVariable Long id) {
        logger.info("begin permanentFixMember...");
        performanceFMService.permanentFixMember(id);
        logger.info("end permanentFixMember...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }


    @RequestMapping(value = "/member", method = RequestMethod.POST)
    public ResponseEntity<?> addFixMember(@RequestBody FmGroupManagerInfoVo vo) {
        logger.info("begin addFixMember...");
        CommonResponse response = performanceFMService.addFixMember(vo.getPlatId(), vo.getProjectId(), vo.getUserId());
        logger.info("end addFixMember...");
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/member", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteFixMember(@RequestParam Long platId, @RequestParam Long projectId, @RequestParam Long memberId) {
        logger.info("begin deleteFixMember...");
        performanceFMService.deleteFixMember(platId, projectId, memberId);
        logger.info("end deleteFixMember...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/project-confirmer", method = RequestMethod.PUT)
    public ResponseEntity<?> changeProjectConfirmer(@RequestBody FmGroupManagerInfoVo vo) {
        logger.info("begin changeProjectConfirmer...");
        boolean flag = performanceFMService.changeProjectConfirmer(vo.getPlatId(), vo.getProjectId(), vo.getUserId());
        CommonResponse commonResponse;
        if (flag) {
            commonResponse = new CommonResponse(0, "success");
        } else {
            commonResponse = new CommonResponse(1, "选择的审核员不符合规则，固化审核员不能是项目负责人和老K");
        }
        logger.info("end changeProjectConfirmer...");
        return ResponseEntity.ok(commonResponse);
    }

    @RequestMapping(value = "/group", method = RequestMethod.POST)
    public ResponseEntity<?> createFixGroup(@RequestBody FmGroupManagerInfoVo vo) {
        logger.info("begin createFixGroup...");
        boolean flag = performanceFMService.createFixGroup(vo.getProjectId(), vo.getPlatId(), vo.getUserId());
        CommonResponse commonResponse;
        if (flag) {
            commonResponse = new CommonResponse(0, "success");
        } else {
            commonResponse = new CommonResponse(1, "选择的审核员不符合规则，固化审核员不能是项目负责人和老K");
        }
        logger.info("end createFixGroup...");
        return ResponseEntity.ok(commonResponse);
    }

    @RequestMapping(value = "/group", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteFixGroup(@RequestParam Long platId, @RequestParam Long projectId) {
        logger.info("begin deleteFixGroup...");
        CommonResponse response;
        String message = performanceFMService.deleteFixGroup(platId, projectId);
        if (message.equals("success")) {
            response = new CommonResponse(0, "success");
        } else {
            response = new CommonResponse(1, message);
        }
        logger.info("end deleteFixGroup...");
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/plats", method = RequestMethod.GET)
    public ResponseEntity<?> getAllPlats(@RequestParam Long projectId) {
        logger.info("begin getAllPlats...");
        List<PerformanceFixPlatVo> allPlatVos = performanceFMService.getAllPlats(projectId);
        logger.info("end getAllPlats...");
        return ResponseEntity.ok(allPlatVos);
    }

    @RequestMapping(value = "/plat-perf-submit", method = RequestMethod.PUT)
    public ResponseEntity<?> platPerfSubmitStatusModify(@RequestBody JSONObject jsonObject) {
        logger.info("begin platPerfSubmitStatusModify ...");
        Long platId = jsonObject.getLong("platId");
        Boolean nowStatus = jsonObject.getBoolean("nowStatus");
        Integer year = jsonObject.getInteger("year");
        Integer month = jsonObject.getInteger("month");
        int result = performanceFMService.modifyPerfSubmit(platId, nowStatus, year, month);
        logger.info("end platPerfSubmitStatusModify ...");
        if (result > 0) {
            return ResponseEntity.ok(new CommonResponse(0, "success"));
        } else {
            return ResponseEntity.ok(new CommonResponse(-1, "failure"));
        }
    }

}
