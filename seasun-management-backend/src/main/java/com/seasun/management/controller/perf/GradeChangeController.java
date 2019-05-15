package com.seasun.management.controller.perf;

import com.seasun.management.annotation.AccessLog;
import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.dto.UserGradeDetailDto;
import com.seasun.management.dto.UserGradeDto;
import com.seasun.management.service.UserGradeService;
import com.seasun.management.vo.UserGradeChangeVo;
import com.seasun.management.vo.UserGradeVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping(value = "/apis/auth/user-grade-change")
public class GradeChangeController {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceController.class);

    @Autowired
    private UserGradeService userGradeService;

    @AccessLog(tag = "pc-grade-access", message = "访问了调级界面")
    @RequestMapping(value = "/sub-grade-change", method = RequestMethod.GET)
    public ResponseEntity<?> selectUserGradeOfWorkGroup(@RequestParam Long workGroupId) {
        logger.info("begin selectUserGradeOfWorkGroup...workGroupId:{}", workGroupId);
        UserGradeVo userGradeVo = userGradeService.getSubGradeChange(workGroupId);
        logger.info("end selectUserGradeOfWorkGroup...");
        return ResponseEntity.ok(userGradeVo);
    }

    @RequestMapping(value = "/member-grade-change", method = RequestMethod.GET)
    public ResponseEntity<?> selectMemberGradeInfos(@RequestParam Long workGroupId) {
        logger.info("begin selectMemberGradeInfos...workGroupId:{}", workGroupId);
        List<UserGradeDto> userGradeInfoList = userGradeService.getMemberGradeChange(workGroupId);
        logger.info("end selectMemberGradeInfos...");
        return ResponseEntity.ok(userGradeInfoList);
    }

    @RequestMapping(value = "/grade-change-detail", method = RequestMethod.GET)
    public ResponseEntity<?> selectUserGradeDetailInfo(@RequestParam Long userId) {
        logger.info("begin selectUserGradeDetailInfo...workGroupId:{}", userId);
        UserGradeDetailDto userGradeDetailInfo = userGradeService.getGradeChangeDetail(userId);
        logger.info("end selectUserGradeDetailInfo...");
        return ResponseEntity.ok(userGradeDetailInfo);
    }

    @RequestMapping(value = "/user-grade-change", method = RequestMethod.POST)
    public ResponseEntity<?> updateUserGradeInfo(@RequestBody UserGradeChangeVo userGradeChangeVo) {
        logger.info("begin updateUserGradeInfo...userId:{}, year:{}, month:{}, grade:{}, evaluateType:{}",
                userGradeChangeVo.getUserId(), userGradeChangeVo.getYear(), userGradeChangeVo.getMonth(), userGradeChangeVo.getGrade(), userGradeChangeVo.getEvaluateType());
        Boolean blRet = userGradeService.changeUserGradeInfo(userGradeChangeVo.getUserId(), Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1, userGradeChangeVo.getGrade(), userGradeChangeVo.getEvaluateType());
        logger.info("end updateUserGradeInfo...");
        return ResponseEntity.ok(blRet);
    }
}
