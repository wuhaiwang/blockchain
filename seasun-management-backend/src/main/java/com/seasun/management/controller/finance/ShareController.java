package com.seasun.management.controller.finance;


import com.alibaba.fastjson.JSONObject;
import com.seasun.management.controller.response.AddRecordResponse;
import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.helper.CfgHelper;
import com.seasun.management.mapper.FnSumShareConfigMapper;
import com.seasun.management.model.FnPlatShareMember;
import com.seasun.management.model.FnShareInfo;
import com.seasun.management.model.FnShareTemplate;
import com.seasun.management.model.FnSumShareConfig;
import com.seasun.management.service.FnPlatShareConfigService;
import com.seasun.management.service.FnPlatShareMemberService;
import com.seasun.management.service.FnSumShareConfigService;
import com.seasun.management.service.UserService;
import com.seasun.management.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class ShareController {

    private static final Logger logger = LoggerFactory.getLogger(ShareController.class);

    @Resource(name = "fnPlatShareConfigService")
    FnPlatShareConfigService fnPlatShareConfigService;

    @Resource(name = "oldShareFnPlatShareConfigService")
    FnPlatShareConfigService oldShareFnPlatShareConfigService;

    @Autowired
    FnSumShareConfigService fnSumShareConfigService;

    @Autowired
    FnSumShareConfigMapper fnSumShareConfigMapper;

    @Resource(name = "fnPlatShareMemberService")
    private FnPlatShareMemberService fnPlatShareMemberService;

    @Resource(name = "oldShareFnPlatShareMemberService")
    private FnPlatShareMemberService oldShareFnPlatShareMemberService;

    @Autowired
    private UserService userService;

    // 查看平台汇总或本人-分摊视图
    @RequestMapping(value = "/apis/auth/fn-plat-share-config", method = RequestMethod.GET)
    public ResponseEntity<?> getUserShareConfig(@RequestParam String userType, @RequestParam Long platId,
                                                @RequestParam int year, @RequestParam int month) {
        logger.info("begin getUserShareConfig...");
        FnPlatShareConfigLockVo result = null;
        if (CfgHelper.isUserOldShareConfig(platId)) {
            result = oldShareFnPlatShareConfigService.getUserShareConfigData(userType, platId, year, month);
        } else {
            result = fnPlatShareConfigService.getUserShareConfigData(userType, platId, year, month);
        }
        logger.info("end getUserShareConfig...");
        return ResponseEntity.ok(result);
    }

    // 分摊填写情况
    @RequestMapping(value = "/apis/auth/fn-share-info", method = RequestMethod.GET)
    public ResponseEntity<?> getLatestFnShareInfo() {
        logger.info("begin getLatestFnShareInfo...");
        FnShareInfo fnShareInfo = oldShareFnPlatShareConfigService.getLatestFnShareInfo();
        logger.info("end getLatestFnShareInfo...");
        return ResponseEntity.ok(fnShareInfo);
    }

    // 查找最新一条fn-share-info表记录
    @RequestMapping(value = "/apis/auth/fn-share-info", method = RequestMethod.POST)
    public ResponseEntity<?> addFnShareInfo(@RequestBody JSONObject js) {
        logger.info("begin addFnShareInfo...");
        oldShareFnPlatShareConfigService.startNewMonthShareConfig(js.getInteger("year"), js.getInteger("month"));
        logger.info("end addFnShareInfo...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    // 查看平台分摊填写进度
    @RequestMapping(value = "/apis/auth/fn-plat-sum-share", method = RequestMethod.GET)
    public ResponseEntity<?> getPlatSumConfigList(@RequestParam Integer year, @RequestParam Integer month) {
        logger.info("begin getPlatSumConfigList...");
        List<FnPlatSumProVo> result = oldShareFnPlatShareConfigService.getPlatSumConfigList(year, month);
        logger.info("end getPlatSumConfigList...");
        return ResponseEntity.ok(result);
    }

    // 锁定平台
    @RequestMapping(value = "/apis/auth/fn-plat-sum-share/plat-lock", method = RequestMethod.POST)
    public ResponseEntity<?> batchUpdateLockStatus(@RequestBody FnPlatLockList fnPlatLockList) {
        logger.info("begin batchUpdateLockStatus...");
        oldShareFnPlatShareConfigService.batchUpdateLockFlag(fnPlatLockList.getPlats(), fnPlatLockList.getYear(),
                fnPlatLockList.getMonth(), fnPlatLockList.getType());
        logger.info("end batchUpdateLockStatus...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    // 查看成员填写进度-个人详情
    @RequestMapping(value = "/apis/auth/fn-plat-share-config-member", method = RequestMethod.GET)
    public ResponseEntity<?> getMemberShareConfig(@RequestParam String userType, @RequestParam Long platId, @RequestParam int year,
                                                  @RequestParam int month, @RequestParam Long memberId) {
        logger.info("begin getMemberShareConfig...");
        List<FnPlatShareConfigVo> result = oldShareFnPlatShareConfigService.getMemberShareConfigData(userType, platId, year, month, memberId);
        logger.info("end getMemberShareConfig...");
        return ResponseEntity.ok(result);
    }

    // 月汇总分摊-详情(平台-项目)
    @RequestMapping(value = "/apis/auth/fn-plat-share-config/project-detail", method = RequestMethod.GET)
    public ResponseEntity<?> getProjectDetailShareConfig(@RequestParam Long platId, @RequestParam Long projectId, @RequestParam int year, @RequestParam int month) {
        logger.info("begin getProjectDetailShareConfig...");
        List<FnPlatShareConfigVo> result = null;
        if (CfgHelper.isUserOldShareConfig(platId)) {
            result = oldShareFnPlatShareConfigService.getDetailData(platId, projectId, year, month);
        } else {
            result = fnPlatShareConfigService.getDetailData(platId, projectId, year, month);
        }
        logger.info("end getProjectDetailShareConfig...");
        return ResponseEntity.ok(result);
    }

    // 修改本人分摊
    @RequestMapping(value = "/apis/auth/fn-plat-share-config", method = RequestMethod.PUT)
    public ResponseEntity<?> modify(@RequestBody FnPlatShareConfigVo fnPlatShareConfigVo) {
        logger.info("begin modifyShareConfig...");
        JSONObject jsonObject = oldShareFnPlatShareConfigService.update(fnPlatShareConfigVo);
        logger.info("end modifyShareConfig...");
        return ResponseEntity.ok(jsonObject);
    }

    // 填写本人分摊
    @RequestMapping(value = "/apis/auth/fn-plat-share-config", method = RequestMethod.POST)
    public ResponseEntity<?> addShareConfig(@RequestBody FnPlatShareConfigVo fnPlatShareConfigVo) {
        logger.info("begin addShareConfig...");
        boolean flag = oldShareFnPlatShareConfigService.insert(fnPlatShareConfigVo);
        logger.info("end addShareConfig...");
        if (flag) {
            return ResponseEntity.ok(new AddRecordResponse(0, "success", fnPlatShareConfigVo.getId()));
        }
        return ResponseEntity.ok(new AddRecordResponse(-2, "the plat was locked", fnPlatShareConfigVo.getId()));
    }

    // 财务上传每月的分摊模板，并生成分摊结果文件
    @RequestMapping(value = "/apis/auth/share-template", method = RequestMethod.POST)
    public ResponseEntity<?> importShareTemplate(MultipartFile file, @RequestParam int year, @RequestParam int month) {
        logger.info("begin importShareTemplate...");
        JSONObject result = oldShareFnPlatShareConfigService.importShareTemplate(file, year, month);
        logger.info("end importShareTemplate...");
        if (result != null) {
            result.put("code", 0);
        } else {
            logger.info("importShareTemplate error,because  excelData is error");
            result = new JSONObject();
            result.put("code", 708);
            result.put("message", "模板上传信息异常");
        }
        return ResponseEntity.ok(result);
    }

    // 上传月分摊明细
    @RequestMapping(value = "/apis/auth/share-data-import", method = RequestMethod.POST)
    public ResponseEntity<?> importPlatShareData(MultipartFile file, @RequestParam long platId, @RequestParam int year, @RequestParam int month) {
        logger.info("begin importPlatShareData...");
        String errLogUrl = null;
        if (CfgHelper.isUserOldShareConfig(platId)) {
            errLogUrl = oldShareFnPlatShareConfigService.importShareDetailExcel(file, platId, year, month);
        } else {
            errLogUrl = fnPlatShareConfigService.importShareDetailExcel(file, platId, year, month);
        }
        if (errLogUrl == null) {
            logger.info("importPlatShareData error,because excelData is  error ");
            return ResponseEntity.ok(new CommonResponse(708, "文件解析异常"));
        }
        if (errLogUrl.length() > 0) {
            logger.info("importPlatShareData finish,but encounter some error...");
            JSONObject result = new JSONObject();
            result.put("code", -1);
            result.put("errLogUrl", errLogUrl);
            return ResponseEntity.ok(result);
        }
        logger.info("end importPlatShareData...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    // 导出月分摊明细
    @RequestMapping(value = "/apis/auth/share-data-export", method = RequestMethod.GET)
    public ResponseEntity<?> exportPlatShareData(@RequestParam Long platId, @RequestParam Integer year, @RequestParam Integer month) throws Exception {
        logger.info("begin exportPlatMonthShareData...");
        String url = oldShareFnPlatShareConfigService.exportPlatMonthShareData(platId, year, month);
        logger.info("end exportPlatMonthShareData...");
        return ResponseEntity.ok(new CommonAppResponse(0, url));
    }

    // 获取历史月份的模板，备用，暂不使用
    @RequestMapping(value = "/apis/auth/share-template", method = RequestMethod.GET)
    public ResponseEntity<?> importShareTemplate(@RequestParam int year, @RequestParam int month) {
        logger.info("begin importShareTemplate...");
        FnShareTemplate shareTemplate = oldShareFnPlatShareConfigService.getShareTemplate(year, month);
        logger.info("end importShareTemplate...");
        JSONObject jsonObject = new JSONObject();
        if (null != shareTemplate) {
            jsonObject.put("code", 0);
            jsonObject.put("location", shareTemplate.getLocation());
        } else {
            jsonObject.put("code", 1);
            jsonObject.put("location", "");
        }
        return ResponseEntity.ok(jsonObject);
    }

    // 拷贝本人上月分摊(本月分摊数据删除，上月分摊数据复制到本月)
    @RequestMapping(value = "/apis/auth/fn-plat-share-config/data-clone", method = RequestMethod.POST)
    public ResponseEntity<?> cloneFromLastMonth(@RequestBody JSONObject jsonObject) {
        logger.info("begin cloneFromLastMonth...");
        int year = jsonObject.getIntValue("year");
        int month = jsonObject.getIntValue("month");
        Long platId = jsonObject.getLongValue("platId");
        oldShareFnPlatShareConfigService.copyFromLastMonth(platId, year, month);
        logger.info("end cloneFromLastMonth...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    // 复制“分摊比例(fn_plat_share_config)”到“最终比例(fn_sum_share_config)”列
    @RequestMapping(value = "/apis/auth/fn-sum-share-config", method = RequestMethod.POST)
    public ResponseEntity<?> saveSumShareConfigs(@RequestBody List<FnSumShareConfigVo> fnSumShareConfigVos) {
        logger.info("begin saveSumShareConfigs...");
        List<FnSumShareConfigVo.ProjectSumPro> result = fnSumShareConfigService.saveSumShareConfig(fnSumShareConfigVos);
        JSONObject js = new JSONObject();
        if (result == null) {
            js.put("data", "the plat was locked");
            js.put("code", -2);
        }
        js.put("data", result);
        js.put("code", 0);
        return ResponseEntity.ok(js);
    }

    // 修改汇总分摊最终比例
    @RequestMapping(value = "/apis/auth/fn-sum-share-config/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateSumShareConfigs(@PathVariable Long id, @RequestBody JSONObject jsonObject) {
        logger.info("begin updateSumShareConfigs...");
        FnSumShareConfig cond = new FnSumShareConfig();
        if (fnSumShareConfigMapper.getFlagLockStatusById(id)) {
            return ResponseEntity.ok(new CommonResponse(-2, "the plat was locked"));
        }
        cond.setId(id);
        cond.setSharePro(jsonObject.getBigDecimal("sharePro"));
        fnSumShareConfigMapper.updateByPrimaryKeySelective(cond);
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    // 本人分摊-使用小工具操作后提交
    @RequestMapping(value = "/apis/auth/fn-plat-share-config/batch", method = RequestMethod.POST)
    public ResponseEntity<?> batchSaveShareConfig(@RequestBody FnPlatShareBatchVo fnPlatShareBatchVo) {
        logger.info("begin batchSaveShareConfig...");
        oldShareFnPlatShareConfigService.saveBatchShareConfig(fnPlatShareBatchVo);
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    // 获取平台分摊成员
    @RequestMapping(value = "/apis/auth/fn-plat-share-members", method = RequestMethod.GET)
    public ResponseEntity<?> getPlatShareMembers(@RequestParam Long platId) {
        logger.info("begin getPlatShareMembers...");
//        List<FnPlatShareMemberVo> fnPlatShareMemberVos = null;
        FnPlatManageAndMemberVo fnPlatManageAndMemberVos = null;
        if (CfgHelper.isUserOldShareConfig(platId)) {
//            fnPlatShareMemberVos = oldShareFnPlatShareMemberService.getPlatShareMembers(platId);
            fnPlatManageAndMemberVos = oldShareFnPlatShareMemberService.getPlatManageAndMemberVo(platId);
        } else {

            fnPlatManageAndMemberVos = fnPlatShareMemberService.getPlatManageAndMemberVo(platId);
        }
        logger.info("end getPlatShareMembers...");
        return ResponseEntity.ok(fnPlatManageAndMemberVos);
    }

    // 平台月分摊录入—查看成员填写进度
    @RequestMapping(value = "/apis/auth/fn-members-share-progress", method = RequestMethod.GET)
    public ResponseEntity<?> getPlatShareMembersNoConfig(@RequestParam Long platId, @RequestParam Integer year, @RequestParam Integer month) {
        logger.info("begin getPlatShareMembers...");
        List<UserLoginEmailVo> users = null;
        if (CfgHelper.isUserOldShareConfig(platId)) {
            users = oldShareFnPlatShareMemberService.getShareMembersConfigInfo(platId, year, month);
        } else {
            users = fnPlatShareMemberService.getShareMembersConfigInfo(platId, year, month);
        }
        logger.info("end getPlatShareMembers...");
        return ResponseEntity.ok(users);
    }

    // 增加分摊平台成员
    @RequestMapping(value = "/apis/auth/fn-plat-share-member", method = RequestMethod.POST)
    public ResponseEntity<?> insertPlatShareMember(@RequestBody FnPlatShareMember fnPlatShareMember) {
        logger.info

                ("begin insertPlatShareMember...");
        JSONObject jsonObject = oldShareFnPlatShareMemberService.insert(fnPlatShareMember);
        logger.info("end insertPlatShareMember...");
        return ResponseEntity.ok(jsonObject);
    }

    // 修改分摊平台人员权重
    @RequestMapping(value = "/apis/auth/fn-plat-share-member", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePlatShareMember(@RequestBody FnPlatShareMember fnPlatShareMember) {
        logger.info("begin updatePlatShareMember...");
        oldShareFnPlatShareMemberService.update(fnPlatShareMember);
        logger.info("end updatePlatShareMember...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    // 删除分摊平台人员
    @RequestMapping(value = "/apis/auth/fn-plat-share-member", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePlatShareMember(@RequestParam Long id) {
        logger.info("begin deletePlatShareMember...");
        oldShareFnPlatShareMemberService.delete(id);
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    // 导出周分摊报表
    @RequestMapping(value = "/apis/auth/share-weekly-export", method = RequestMethod.GET)
    public ResponseEntity<?> exportWeekly(@RequestParam Integer previousYear,@RequestParam Integer previousWeek,@RequestParam Integer currentYear,@RequestParam Integer currentWeek) throws Exception {
        logger.info("begin exportWeekly...");
        String url = fnPlatShareConfigService.exportWeekly(previousYear,previousWeek,currentYear,currentWeek);
        logger.info("end exportWeekly...");
        return ResponseEntity.ok(new CommonAppResponse(0, url));
    }
}
