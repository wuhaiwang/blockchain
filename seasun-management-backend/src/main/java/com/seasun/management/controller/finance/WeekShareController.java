package com.seasun.management.controller.finance;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.constant.UserShareType;
import com.seasun.management.controller.response.AddRecordResponse;
import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.dto.FnPlatWeekShareConfigDto;
import com.seasun.management.dto.FnWeekCommitStatusDto;
import com.seasun.management.dto.PlatFavorProjectDto;
import com.seasun.management.dto.ShareCopyDto;
import com.seasun.management.model.FnPlatWeekShareConfig;
import com.seasun.management.model.FnSumWeekShareConfig;
import com.seasun.management.model.FnWeekShareWorkdayStatus;
import com.seasun.management.service.*;
import com.seasun.management.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class WeekShareController {

    private static final Logger logger = LoggerFactory.getLogger(WeekShareController.class);
    @Autowired
    private FnWeekShareConfigService fnWeekShareConfigService;

    @Autowired
    private UserService userService;

    // 按周填写分摊平台列表(根据用户身份返回)
    @RequestMapping(value = "/apis/auth/user-share-project-perm/week", method = RequestMethod.GET)
    public ResponseEntity<?> getUserShareWeekProjectPerms() {
        logger.info("begin getUserShareProjectPerms...");
        List<UserWeekProjectPermVo> userProjectPerms = userService.getUserShareWeekProjectPerms();
        logger.info("end getUserShareProjectPerms...");
        return ResponseEntity.ok(userProjectPerms);
    }

    // 查看-平台汇总或本人在某年某周的分摊填写情况
    @RequestMapping(value = "/apis/auth/fn-plat-share-config/week", method = RequestMethod.GET)
    public ResponseEntity<?> getUserWeekShareConfig(@RequestParam String userType, @RequestParam Long platId,
                                                    @RequestParam Integer year, @RequestParam Integer week, @RequestParam Integer endMonth, @RequestParam Integer lastWeek) {
        logger.info("begin getUserWeekShareConfig...");
        Object result;
        if (UserShareType.member.toString().equals(userType)) {
            result = fnWeekShareConfigService.getUserWeekShareConfigData(userType, platId, year, week, endMonth);
        } else {
            result = fnWeekShareConfigService.getPlatWeekShareConfigData(userType, platId, year, week, lastWeek, endMonth);
        }
        logger.info("end getUserWeekShareConfig...");
        return ResponseEntity.ok(result);
    }

    // 填写本人周分摊
    @RequestMapping(value = "/apis/auth/fn-plat-share-config/week", method = RequestMethod.POST)
    public ResponseEntity<?> addWeekShareConfig(@RequestBody FnPlatWeekShareConfigDto fnPlatShareConfigVo) {
        logger.info("begin addWeekShareConfig...");
        boolean flag = fnWeekShareConfigService.insertFnPlatWeekShareConfig(fnPlatShareConfigVo);
        logger.info("end addWeekShareConfig...");
        if (flag) {
            return ResponseEntity.ok(new AddRecordResponse(0, "success", fnPlatShareConfigVo.getId()));
        }
        return ResponseEntity.ok(new AddRecordResponse(-2, "the plat was locked", fnPlatShareConfigVo.getId()));
    }

    // 修改本人周分摊
    @RequestMapping(value = "/apis/auth/fn-plat-share-config/week", method = RequestMethod.PUT)
    public ResponseEntity<?> updateWeekShareConfig(@RequestBody FnPlatWeekShareConfig fnPlatShareConfigVo) {
        logger.info("begin updateWeekShareConfig...");
        JSONObject jsonObject = fnWeekShareConfigService.updateWeekShareConfig(fnPlatShareConfigVo);
        logger.info("end updateWeekShareConfig...");
        return ResponseEntity.ok(jsonObject);
    }

    // 拷贝 本人/平台 上周分摊校验
    @RequestMapping(value = "/apis/auth/fn-plat-week-share-config/clone-check", method = RequestMethod.GET)
    public ResponseEntity<?> cloneFromLastWeekCheck(Long platId, Integer year, Integer month, Integer endMonth, Integer week, Integer lastWeek, Integer endDay, String userType) {
        logger.info("begin cloneFromLastWeekCheck...");
        List<String> result = fnWeekShareConfigService.copyFromLastWeek(platId, year, month, week, endMonth, endDay, userType, lastWeek, false);
        logger.info("end cloneFromLastWeekCheck...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }

    // 拷贝 本人/平台 上周分摊(本周分摊数据删除，上周分摊数据复制到本月)
    @RequestMapping(value = "/apis/auth/fn-plat-week-share-config/clone", method = RequestMethod.POST)
    public ResponseEntity<?> cloneFromLastWeek(@RequestBody ShareCopyDto configDto) {
        logger.info("begin cloneFromLastWeek...");
        fnWeekShareConfigService.copyFromLastWeek(configDto.getPlatId(), configDto.getYear(), configDto.getMonth(), configDto.getWeek(), configDto.getEndMonth(), configDto.getEndDay(), configDto.getUserType(), configDto.getLastWeek(), true);
        logger.info("end cloneFromLastWeek...");
        return ResponseEntity.ok(new CommonAppResponse(0, "success"));
    }

    // 周汇总分摊-详情(平台-项目)
    @RequestMapping(value = "/apis/auth/fn-plat-share-config/project-detail/week", method = RequestMethod.GET)
    public ResponseEntity<?> getProjectDetailWeekShareConfig(@RequestParam Long platId, @RequestParam Long projectId, @RequestParam int year, @RequestParam int week, @RequestParam int endMonth) {
        logger.info("begin getProjectDetailWeekShareConfig...");
        List<SimpleSharePlatWeekVo> result = fnWeekShareConfigService.getPlatWeekShareDataDetail(platId, projectId, year, week, endMonth);
        logger.info("end getProjectDetailWeekShareConfig...");
        return ResponseEntity.ok(result);
    }

    // 根据周分摊数据生成月分摊数据
    @RequestMapping(value = "/apis/auth/fn-sum-share-config/week", method = RequestMethod.POST)
    public ResponseEntity<?> createPlatSumShareConfigByWeekShareConfig(@RequestBody FnPlatWeekShareConfigDto configDto) {
        logger.info("begin createPlatSumShareConfigByWeekShareConfig...");
        fnWeekShareConfigService.createPlatSumShareConfigByWeekShareConfig(configDto.getPlatId(), configDto.getYear(), configDto.getMonth());
        logger.info("end createPlatSumShareConfigByWeekShareConfig...");
        return ResponseEntity.ok(new CommonAppResponse(0, "success"));
    }

    // 平台周分摊录入—查看成员填写进度
    // todo: 暂时废弃
    @RequestMapping(value = "/apis/auth/fn-members-share-progress/week", method = RequestMethod.GET)
    public ResponseEntity<?> getPlatWeekShareMembersNoConfig(@RequestParam Long platId, @RequestParam Integer year, @RequestParam Integer week, @RequestParam Integer endMonth) {
        logger.info("begin getPlatWeekShareMembersNoConfig...");
        ShareMemberSumVo result = fnWeekShareConfigService.getShareMembersWeekConfigInfo(platId, year, week, endMonth);
        logger.info("end getPlatWeekShareMembersNoConfig...");
        return ResponseEntity.ok(result);
    }

    // 导入周分摊明细-上传文件
    @RequestMapping(value = "/apis/auth/share-data-import/week", method = RequestMethod.POST)
    public ResponseEntity<?> importPlatWeekShareData(MultipartFile file, @RequestParam long platId, @RequestParam Integer year, @RequestParam Integer week, @RequestParam Integer month, @RequestParam Integer endMonth, @RequestParam Integer endDay) {
        logger.info("begin importPlatWeekShareData...");
        String errLogUrl = fnWeekShareConfigService.importPlatWeekShareData(file, platId, year, week, month, endMonth, endDay);
        if (errLogUrl.length() > 0) {
            logger.info("importPlatWeekShareData finish,but encounter some error...");
            JSONObject result = new JSONObject();
            result.put("code", -1);
            result.put("errLogUrl", errLogUrl);
            return ResponseEntity.ok(result);
        }
        logger.info("end importPlatWeekShareData...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    // 导出周分摊明细
    @RequestMapping(value = "/apis/auth/share-data-export/week", method = RequestMethod.GET)
    public ResponseEntity<?> exportPlatWeekShareData(@RequestParam Long platId, @RequestParam Integer year, @RequestParam Integer month, @RequestParam Integer week) {
        logger.info("begin exportPlatWeekShareData...");
        String url = fnWeekShareConfigService.exportPlatWeekShareData(platId, year, month, week);
        logger.info("end exportPlatWeekShareData...");
        return ResponseEntity.ok(new CommonAppResponse(0, url));
    }

    // 提交/锁定个人周分摊信息 todo: 暂时没用到
    @RequestMapping(value = "/apis/auth/share-data-lock/user", method = RequestMethod.POST)
    public ResponseEntity<?> lockUserWeekShareData(@RequestBody FnPlatWeekShareConfigDto configDto) {
        logger.info("begin lockUserWeekShareData...");
        Boolean result = fnWeekShareConfigService.lockUserWeekShareData(configDto.getPlatId(), configDto.getYear(), configDto.getWeek(), configDto.getId(), configDto.getLockFlag());
        logger.info("end lockUserWeekShareData...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }

    // 锁定/解锁人力组周分摊信息 todo： 已处理 xionghaitao
    @RequestMapping(value = "/apis/auth/share-data-lock/group", method = RequestMethod.POST)
    public ResponseEntity<?> lockGroupWeekShareData(@RequestBody FnPlatWeekShareConfigDto configDto) {
        logger.info("begin lockGroupWeekShareData...");
        Boolean result = fnWeekShareConfigService.lockGroupWeekShareData(configDto.getPlatId(), configDto.getYear(), configDto.getWeek(), configDto.getId(), configDto.getLockFlag());
        logger.info("end lockGroupWeekShareData...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }

    // 按周分摊-查看成员填写进度-个人详情
    @RequestMapping(value = "/apis/auth/fn-plat-share-config-member/week", method = RequestMethod.GET)
    public ResponseEntity<?> getMemberWeekShareConfig(@RequestParam Long platId, @RequestParam Integer year,
                                                      @RequestParam Integer week, @RequestParam Long userId, @RequestParam Integer endMonth) {
        logger.info("begin getMemberWeekShareConfig...");
        List<WeekShareConfigVo> result = fnWeekShareConfigService.getMemberWeekShareConfig(platId, year, week, userId, endMonth);
        logger.info("end getMemberWeekShareConfig...");
        return ResponseEntity.ok(result);
    }

    // 按周分摊-添加个人周备注数据
    @RequestMapping(value = "/apis/auth/fn-sum-week-share-config", method = RequestMethod.POST)
    public ResponseEntity<?> addFnSumWeekShareConfig(@RequestBody FnSumWeekShareConfig fnSumWeekShareConfig) {
        logger.info("begin addFnSumWeekShareConfig...");
        FnSumWeekShareConfig result = fnWeekShareConfigService.addFnSumWeekShareConfig(fnSumWeekShareConfig);
        logger.info("end addFnSumWeekShareConfig...");
        return ResponseEntity.ok(new CommonResponse(0, "success", result));
    }

    // 按周分摊-修改个人周备注
    @RequestMapping(value = "/apis/auth/fn-sum-week-share-config", method = RequestMethod.PUT)
    public ResponseEntity<?> updateFnSumWeekShareConfig(@RequestBody FnSumWeekShareConfig fnSumWeekShareConfig) {
        logger.info("begin updateFnSumWeekShareConfig...");
        fnWeekShareConfigService.updateFnSumWeekShareConfigRemark(fnSumWeekShareConfig);
        logger.info("end updateFnSumWeekShareConfig...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    // 按周分摊-确认平台分摊
    @RequestMapping(value = "/apis/auth/fn-week-share-commit-log", method = RequestMethod.POST)
    public ResponseEntity<?> confirmPlatWeekShare(@RequestBody JSONObject jsonObject) {
        logger.info("begin confirmPlatWeekShare...");
        fnWeekShareConfigService.confirmPlatWeekShare(jsonObject.getLong("platId"), jsonObject.getInteger("year"), jsonObject.getInteger("week"));
        logger.info("end confirmPlatWeekShare...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    // 按周分摊-查看平台填写进度
    @RequestMapping(value = "/apis/auth/fn-week-share-commit-log", method = RequestMethod.GET)
    public ResponseEntity<?> getAllCommitLog(@RequestParam int year, @RequestParam int week) {
        logger.info("begin getAllCommitLog...");
        List<FnWeekCommitStatusDto> result = fnWeekShareConfigService.getCommitLogByWeek(year, week);
        logger.info("end getAllCommitLog...");
        return ResponseEntity.ok(new CommonResponse(0, "success", result));
    }

    // 按周分摊-导出空模板
    @RequestMapping(value = "/apis/auth/fn-plat-share-template", method = RequestMethod.GET)
    public ResponseEntity<?> getPlatShareTemplate(@RequestParam Long platId) {
        logger.info("begin getPlatShareTemplate...");
        String result = fnWeekShareConfigService.getPlatFavorTemplate(platId);
        logger.info("end getPlatShareTemplate...");
        return ResponseEntity.ok(new CommonResponse(0, "success", result));
    }

    // 按周分摊-平台周工作日配置列表
    @RequestMapping(value = "/apis/auth/fn-week-share-workday-status", method = RequestMethod.GET)
    public ResponseEntity<?> getWeekShareWorkdayList(@RequestParam Integer year) {
        logger.info("begin getWeekShareWorkdayList...");
        List<WorkdayStatusVo> result = fnWeekShareConfigService.getWeekShareWorkdayList(year);
        logger.info("end getWeekShareWorkdayList...");
        return ResponseEntity.ok(result);
    }

    // 按周分摊- 新增/开启 平台周工作日
    @RequestMapping(value = "/apis/auth/fn-week-share-workday-status", method = RequestMethod.POST)
    public ResponseEntity<?> addWeekShareWorkday(@RequestBody FnWeekShareWorkdayStatus item) {
        logger.info("begin addWeekShareWorkday...");
        fnWeekShareConfigService.addWeekShareWorkday(item);
        logger.info("end addWeekShareWorkday...");
        return ResponseEntity.ok(new AddRecordResponse(0, "success", item.getId()));
    }

    // 按周分摊- 修改/开启/关闭 平台周工作日
    @RequestMapping(value = "/apis/auth/fn-week-share-workday-status", method = RequestMethod.PUT)
    public ResponseEntity<?> updateWeekShareWorkday(@RequestBody FnWeekShareWorkdayStatus item) {
        logger.info("begin updateWeekShareWorkday...");
        fnWeekShareConfigService.updateWeekShareWorkday(item);
        logger.info("end updateWeekShareWorkday...");
        return ResponseEntity.ok(new CommonAppResponse(0, "success"));
    }

    // 按周分摊-获取平台常用项目详情
    @RequestMapping(value = "/apis/auth/fn-plat-favor-project", method = RequestMethod.GET)
    public ResponseEntity<?> getPlatFavorProjectInfo(Long platId, Integer year, Integer week) {
        logger.info("begin getPlatFavorProjectInfo...");
        PlatFavorProjectInfoVo result = fnWeekShareConfigService.getPlatFavorProjectInfo(platId, year, week);
        logger.info("end getPlatFavorProjectInfo...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }

    // 按周分摊-修改平台常用项目排序
    @RequestMapping(value = "/apis/auth/fn-plat-favor-project", method = RequestMethod.POST)
    public ResponseEntity<?> setPlatFavorProject(@RequestBody PlatFavorProjectDto requestParam) {
        logger.info("begin setPlatFavorProject...");
        fnWeekShareConfigService.setPlatFavorProject(requestParam.getPlatId(), requestParam.getProjectIds());
        logger.info("end setPlatFavorProject...");
        return ResponseEntity.ok(new CommonAppResponse(0, "success"));
    }
}
