package com.seasun.management.controller.cp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seasun.management.constant.ErrorCode;
import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.dto.cp.ExchangeRateDto;
import com.seasun.management.model.CfgExchangeRate;
import com.seasun.management.service.CpInfoService;
import com.seasun.management.service.ProjectService;
import com.seasun.management.vo.BaseQueryResponseVo;
import com.seasun.management.vo.ProjectVo;
import com.seasun.management.vo.cp.CpBudgetAppendInfoVo;
import com.seasun.management.vo.cp.ProjectBudgetVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping(value = "/apis/auth/cp")
public class CpInfoController {

    private static final Logger logger = LoggerFactory.getLogger(CpInfoController.class);

    @Autowired
    private CpInfoService cpInfoService;
    @Autowired
    private ProjectService projectService;

    /**
     * IT系统于外包系统项目关系列表
     * @param currentPage
     * @param pageSize
     * @param year
     * @param keyWord
     * @return
     */
    @RequestMapping(value="/projects",method=RequestMethod.GET)
    public ResponseEntity<?> projects(@RequestParam(required = false) Integer currentPage, @RequestParam(required = false) Integer pageSize, @RequestParam Integer year, @RequestParam(required = false) String keyWord){
            BaseQueryResponseVo vo = cpInfoService.projects(currentPage, pageSize, year, keyWord);
        return ResponseEntity.ok(new CommonAppResponse(0, vo));
    }

    /**
     * 建立IT系统与外包系统项目关系
     * @return
     */
    @RequestMapping(value="/relation",method=RequestMethod.POST)
    public ResponseEntity<?> createProjectRelation(@RequestBody JSONObject jsonObject){
        Long cpProjectId = jsonObject.getLong("cpProjectId");
        Long itProjectId = jsonObject.getLong("itProjectId");
        int count = cpInfoService.createProjectRelation(cpProjectId,itProjectId);
        if(count>0){
            return ResponseEntity.ok(new CommonResponse(0,"success"));
        }else{
            return ResponseEntity.ok(new CommonResponse(ErrorCode.CP_OPERATION_ERROR,"fail"));
        }
    }

    /**
     * 设置项目预算金额接口
     * @param jsonObject
     * @return
     */
    @RequestMapping(value="/budget",method=RequestMethod.POST)
    public ResponseEntity<?> createProjectBudget(@RequestBody JSONObject jsonObject){
        Long cpProjectId = jsonObject.getLong("cpProjectId");
        BigDecimal amount = jsonObject.getBigDecimal("amount");
        Integer year = jsonObject.getInteger("year");
        int count = cpInfoService.createProjectBudget(cpProjectId,amount,year);
        if(count>0){
            return ResponseEntity.ok(new CommonResponse(0,"success"));
        }else{
            return ResponseEntity.ok(new CommonResponse(ErrorCode.CP_OPERATION_ERROR,"fail"));
        }
    }

    /**
     * 追加项目预算金额接口
     * @param jsonObject
     * @return
     */
    @RequestMapping(value="/append",method=RequestMethod.POST)
    public ResponseEntity<?> appendBudget(@RequestBody JSONObject jsonObject) {
        if (jsonObject != null) {
            Long cpBudgetId = jsonObject.getLong("cpBudgetId");
            BigDecimal totalAmount = jsonObject.getBigDecimal("totalAmount");
            JSONArray items = jsonObject.getJSONArray("items");
            if(cpBudgetId != null) {
                if (items != null) {
                    List<CpBudgetAppendInfoVo> list = new ArrayList<CpBudgetAppendInfoVo>();
                    Iterator it = items.iterator();
                    CpBudgetAppendInfoVo vo = null;
                    while (it.hasNext()) {
                        JSONObject obj = (JSONObject) it.next();
                        vo = new CpBudgetAppendInfoVo();
                        vo.setCpBudgetId(obj.getLong("cpBudgetId"));
                        vo.setAmount(obj.getBigDecimal("amount"));
                        vo.setReason(obj.getString("reason"));
                        vo.setCreateTime(obj.getDate("createTime"));
                        vo.setType("1");
                        list.add(vo);
                    }
                    cpInfoService.appendBudget(cpBudgetId, list);//更新追加明细
                }
                cpInfoService.updateCpBudgetAmount(cpBudgetId, totalAmount);//更新预算金额
            }
        }
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    /**
     * 查询预算金额与追加明细记录
     * @param cpProjectId
     * @param year
     * @return
     */
    @RequestMapping(value="/amounts",method=RequestMethod.GET)
    public ResponseEntity<?> getBudgetAmounts(@RequestParam Long cpProjectId, Integer year){
        ProjectBudgetVo vo = cpInfoService.getBudgetAmounts(cpProjectId,year);
        return ResponseEntity.ok(new CommonAppResponse(0, vo));
    }

    /**
     * 查询IT系统项目下拉列表
     * @param name
     * @return
     */
    @RequestMapping(value="/itProjects",method=RequestMethod.GET)
    public ResponseEntity<?> getItProjects(@RequestParam(required = false) String name){
        List<ProjectVo> list = projectService.selectItProject(name);
        return ResponseEntity.ok(new CommonAppResponse(0, list));
    }

    @RequestMapping(value="/exchange-rate",method=RequestMethod.GET)
    public ResponseEntity<?> getExchangeRates(){
        return ResponseEntity.ok(new CommonResponse(cpInfoService.getExchangeRates()));
    }
    @RequestMapping(value="/exchange-rate",method=RequestMethod.POST)
    public ResponseEntity<?> addExchangeRate(@RequestBody JSONObject jsonObject){
        String currencyCode = jsonObject.getString(ExchangeRateDto.CURRENCY_CODE);
        String currency = jsonObject.getString(ExchangeRateDto.CURRENCY);
        BigDecimal rate = jsonObject.getBigDecimal(ExchangeRateDto.RATE);
        if (StringUtils.isEmpty(currencyCode) || StringUtils.isEmpty(currency)) {
            return ResponseEntity.ok(new CommonResponse(ErrorCode.PARAM_ERROR,"参数不足"));
        } else if (rate == null || rate.doubleValue() <= 0) {
            return ResponseEntity.ok(new CommonResponse(ErrorCode.PARAM_ERROR,"汇率值不合法"));
        }
        Long resultId= cpInfoService.addExchangeRate(new ExchangeRateDto(currencyCode,currency,rate));
        if (resultId == 0) {
            return ResponseEntity.ok(new CommonResponse(ErrorCode.PARAM_ERROR,"货币类型已存在"));
        } else {
            JSONObject o = new JSONObject();
            o.put("id", resultId);
            return ResponseEntity.ok(new CommonResponse(o));
        }
    }
    @RequestMapping(value="/exchange-rate/{id}",method=RequestMethod.PUT)
    public ResponseEntity<?> updateExchangeRate(@PathVariable Long id, @RequestBody JSONObject jsonObject){
        BigDecimal rate = jsonObject.getBigDecimal(ExchangeRateDto.RATE);
        if (rate == null || rate.doubleValue() <= 0) {
            return ResponseEntity.ok(new CommonResponse(ErrorCode.PARAM_ERROR,"汇率值不合法"));
        }
        int result = cpInfoService.updateExchangeRate(id, jsonObject.getBigDecimal(ExchangeRateDto.RATE));
        if (result == 1) {
            return ResponseEntity.ok(new CommonResponse());
        } else {
            return ResponseEntity.ok(new CommonResponse(ErrorCode.PARAM_ERROR,"该货币类型不存在"));
        }
    }
    @RequestMapping(value="/exchange-rate/{id}",method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteExchangeRates(@PathVariable Long id){
        int result = cpInfoService.deleteExchangeRate(id);
        if (result == 1) {
            return ResponseEntity.ok(new CommonResponse());
        } else {
            return ResponseEntity.ok(new CommonResponse(ErrorCode.PARAM_ERROR,"该货币类型不存在"));
        }
    }
}