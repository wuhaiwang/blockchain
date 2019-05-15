package com.seasun.management.controller.pm;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.constant.ErrorCode;
import com.seasun.management.constant.ErrorMessage;
import com.seasun.management.controller.response.AddRecordResponse;
import com.seasun.management.controller.response.AttachmentResponse;
import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.dto.BaseProjectIdDto;
import com.seasun.management.dto.PmPlanDetailDto;
import com.seasun.management.model.PmAttachment;
import com.seasun.management.model.PmFinanceReport;
import com.seasun.management.model.PmPlanDetail;
import com.seasun.management.service.PmFinanceReportService;
import com.seasun.management.service.PmPlanService;
import com.seasun.management.vo.PmFinanceDetailVo;
import com.seasun.management.vo.PmPlanInfoVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/apis/auth")
public class ProjectManageController {

    @Autowired
    private PmPlanService pmPlanService;

    @Autowired
    private PmFinanceReportService pmFinanceReportService;

    private static final Logger logger = LoggerFactory.getLogger(ProjectManageController.class);

    @RequestMapping(value = "/pm-report", method = RequestMethod.GET)
    public PmPlanInfoVo getPmPlanInfo(@RequestParam(required = false) Long projectId) {
        logger.info("begin getPmPlanInfo...");
        PmPlanInfoVo result = pmPlanService.getPmPlanInfo(projectId);
        logger.info("end getPmPlanInfo...");
        return result;
    }

    @RequestMapping(value = "/pm-finance-report", method = RequestMethod.POST)
    public CommonResponse insertPmPlan(@RequestBody PmFinanceReport pmFinanceReport) {
        logger.info("begin insertPmPlan...");
        Long result = pmFinanceReportService.insertSelective(pmFinanceReport);
        logger.info("end insertPmPlan...");
        if (null == result) {
            return new CommonResponse(ErrorCode.PARAM_ERROR, "插入失败");
        }
        return new AddRecordResponse(result);
    }

    @RequestMapping(value = "/pm-finance-report/{id}", method = RequestMethod.PUT)
    public CommonResponse updatePmPlan(@PathVariable Long id, @RequestBody PmFinanceReport pmFinanceReport) {
        logger.info("begin updatePmPlan...");
        int result = pmFinanceReportService.updateSelective(id, pmFinanceReport);
        logger.info("end updatePmPlan...");
        if (result > 0) {
            return new CommonResponse();
        }
        return new CommonResponse(ErrorCode.PARAM_ERROR, "更新数据失败");
    }

    @RequestMapping(value = "/pm-confirm-projectEstimateDay/{projectId}", method = RequestMethod.PUT)
    public CommonAppResponse confirmProjectEstimateDay(@PathVariable Long projectId, @RequestBody JSONObject jsonObject) {
        logger.info("begin confirmProjectEstimateDay...");
        Long pmPlanId = jsonObject.getLong("pmPlanId");
        Long projectEstimateDay = jsonObject.getLong("projectEstimateDay");
        Long result = pmPlanService.confirmProjectEstimateDay(projectId, pmPlanId, projectEstimateDay);
        logger.info("end confirmProjectEstimateDay...");
        if (result != null) {
            return new CommonAppResponse(0, result);
        }
        return new CommonAppResponse(ErrorCode.PARAM_ERROR, "确认项目预估上线时间失败。。。");
    }

    @RequestMapping(value = "/pm-attachment", method = RequestMethod.POST)
    public AttachmentResponse insertPmAttachment(Long projectId, Integer year, Integer month, MultipartFile file, String size) {
        logger.info("begin insertPmAttachment...");
        PmAttachment result = pmFinanceReportService.insertAttachment(projectId, year, month, file, size);
        logger.info("end insertPmAttachment...");
        if (result.getId() != null) {
            return new AttachmentResponse(0, result.getUrl(), result.getPmFinanceReportId(), result.getId());
        }
        return new AttachmentResponse(ErrorCode.PARAM_ERROR, "上传附件失败");
    }

    @RequestMapping(value = "/pm-attachment/{id}", method = RequestMethod.DELETE)
    public CommonResponse deletePmAttachment(@PathVariable Long id) {
        logger.info("begin deletePmAttachment...");
        int result = pmFinanceReportService.deletePmAttachmentByPmAttachmentId(id);
        logger.info("end deletePmAttachment...");
        if (result > 0) {
            return new CommonResponse();
        }
        return new CommonResponse(ErrorCode.PARAM_ERROR, "参数异常");
    }

    @RequestMapping(value = "/pm-plan-detail", method = RequestMethod.POST)
    public CommonAppResponse insertPmPlanDetail(@RequestBody PmPlanDetailDto pmPlanDetailDto) {
        logger.info("begin insertPmPlanDetail...");
        PmPlanDetailDto result = pmPlanService.insertPmPlanDetail(pmPlanDetailDto);
        logger.info("end insertPmPlanDetail...");
        if (result.getId() != null) {
            JSONObject jsonObject =new JSONObject();
            jsonObject.put("id",result.getId());
            jsonObject.put("pmPlanId",result.getPmPlanId());
            return new CommonAppResponse(0, jsonObject);
        }
        return new CommonAppResponse(ErrorCode.PARAM_ERROR, ErrorMessage.PARAM_ERROR_MESSAGE);
    }

    @RequestMapping(value = "/pm-plan-detail/{id}", method = RequestMethod.PUT)
    public CommonResponse updatePmPlanDetail(@PathVariable Long id, @RequestBody PmPlanDetail pmPlanDetail) {
        logger.info("begin updatePmPlanDetail...");
        int result = pmPlanService.updatePmPlanDetail(id, pmPlanDetail);
        logger.info("end updatePmPlanDetail...");
        if (result > 0) {
            return new CommonResponse();
        }
        return new CommonResponse(ErrorCode.PARAM_ERROR, "参数异常");
    }


    @RequestMapping(value = "/pm-plan-detail/{id}", method = RequestMethod.DELETE)
    public CommonResponse deletePmPlanDetail(@PathVariable Long id) {
        logger.info("begin updatePmPlanDetail...");
        int result = pmPlanService.deletePmPlanDetailByPmPlanDetailId(id);
        logger.info("end updatePmPlanDetail...");
        if (result > 0) {
            return new CommonResponse();
        }
        return new CommonResponse(ErrorCode.PARAM_ERROR, "参数异常");
    }

    @RequestMapping(value = "/pm-plan-submit", method = RequestMethod.PUT)
    public CommonResponse publishPmPlan(@RequestBody BaseProjectIdDto baseProjectIdDto) {
        logger.info("begin publishPmPlan...");
        int result = pmPlanService.publishPmPlan(baseProjectIdDto.getProjectId());
        logger.info("end publishPmPlan...");
        if (result > 0) {
            return new CommonResponse();
        }

        return new CommonResponse(ErrorCode.PM_SUBMIIT_ERROR, "没有填写项目计划,无法提交");
    }


    @RequestMapping(value = "/pm-finance-detail", method = RequestMethod.GET)
    public ResponseEntity<?> getPmFinanceDetail(@RequestParam Integer year, @RequestParam Integer month) {
        logger.info("begin getPmFinanceDetail...");
        List<PmFinanceDetailVo> result = pmPlanService.getPmFinanceDetail(year,month);
        logger.info("end getPmFinanceDetail...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }

    @RequestMapping(value="/pm-finance-remark",method=RequestMethod.POST)
    public ResponseEntity<?> createFinanceRemark(@RequestBody PmFinanceDetailVo pmFinanceDetailVo){
        Long id = pmPlanService.createFinanceRemark(pmFinanceDetailVo);
        return ResponseEntity.ok(new CommonResponse(0,String.valueOf(id)));
    }
}
