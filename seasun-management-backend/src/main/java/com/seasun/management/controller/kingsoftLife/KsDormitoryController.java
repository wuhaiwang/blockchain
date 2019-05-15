package com.seasun.management.controller.kingsoftLife;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.dto.UserDormitoryParamDto;
import com.seasun.management.exception.KsException;
import com.seasun.management.exception.ParamException;
import com.seasun.management.model.DormPayment;
import com.seasun.management.service.kingsoftLife.KsDormitoryService;
import com.seasun.management.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(value = "/apis/auth/ks-life")
public class KsDormitoryController {
    private final Integer SUCCESS_CODE = 1200;
    private static final Logger logger = LoggerFactory.getLogger(KsDormitoryController.class);
    @Autowired
    private KsDormitoryService ksDormitoryService;

    @RequestMapping(value = "/dormitory", method = RequestMethod.GET)
    public ResponseEntity<?> getDormitory(KsDormitoryVo ksDormitoryVo) {
        return ksDormitoryService.getDormitory(ksDormitoryVo);
    }

    @RequestMapping(value = "/dormitory-reserve", method = RequestMethod.POST)
    public ResponseEntity<?> addDormitoryReserve(@RequestBody KsDormitoryReserveVo ksDormitoryReserveVo) {
        try {
            if (ksDormitoryReserveVo != null) {
                String json = JSON.toJSONString(ksDormitoryReserveVo);
                logger.info("begin addDormitoryReserve" + json);
            }
            return ksDormitoryService.addDormitoryReserve(ksDormitoryReserveVo);
        } catch (Exception e) {
            logger.error("error addDormitoryReserve", e);
        }
        return ResponseEntity.ok(new CommonResponse(999, "系统异常"));
    }

    @RequestMapping(value = "/dormitory-reserve-info", method = RequestMethod.GET)
    public ResponseEntity<?> getDormitoryReserveInfo(KsDormitoryReserveVo ksDormitoryReserveVo) {
        return ksDormitoryService.getDormitoryReserveInfo(ksDormitoryReserveVo);
    }

    @RequestMapping(value = "/dormitory-change-info", method = RequestMethod.GET)
    public ResponseEntity<?> getDormitoryChangeInfo(Integer pageSize, Integer pageNo, String name) {
        logger.error("begin getDormitoryChangeInfo...");
        KsDormitoryChangeRepVo result = ksDormitoryService.getDormitoryChangeInfo(pageNo, pageSize, name);
        logger.error("end getDormitoryChangeInfo...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }

    @RequestMapping(value = "/dormitory-reserve-import", method = RequestMethod.POST)
    public ResponseEntity<?> getDormitotyReserveImport(MultipartFile file) throws Exception {
        logger.info("begin important dormitoryReserve file");
        CommonResponse commonResponse = ksDormitoryService.importDormitoryReserve(file);
        logger.info("end important dormitoryReserve file");
        if (commonResponse == null) {
            logger.error("please check up database of upload file");
            return ResponseEntity.ok(new CommonResponse(-1, "表格数据不正确,请重新检查表格数据"));
        }
        return ResponseEntity.ok(commonResponse);
    }

    @RequestMapping(value = "/dormitory-reserve-finance-info", method = RequestMethod.GET)
    public ResponseEntity<?> getDormitoryReserveFinanceInfo(Integer year, Integer month, Integer currentPage, Integer pageSize) throws Exception {
        logger.info("begin getDormitoryReserveFinanceInfo");
        BaseQueryResponseVo result = ksDormitoryService.getDormitoryReserveFinanceInfo(year, month, currentPage, pageSize);
        logger.info("end getDormitoryReserveFinanceInfo");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }

    @RequestMapping(value = "/dorm-payment", method = RequestMethod.POST)
    public ResponseEntity<?> confirmUserDormPayInfo(@RequestBody DormPayment userDormPayInfo) throws Exception {
        logger.info("begin confirmUserDormPayInfo");
        ksDormitoryService.confirmUserDormPayInfo(userDormPayInfo);
        logger.info("end confirmUserDormPayInfo");
        return ResponseEntity.ok(new CommonAppResponse(0, "success"));
    }

    @RequestMapping(value = "/dormitory-reserve-cancel", method = RequestMethod.POST)
    public ResponseEntity<?> deleteDormitoryReserve(@RequestBody JSONObject jsonObject) {
        Long id = jsonObject.getLong("id");
        String liveNo = jsonObject.getString("liveNo");
        logger.info("begin deleteDormitoryReserve" + id);
        if (id != null && StringUtils.isNotEmpty(liveNo)) {
            return ksDormitoryService.deleteDormitoryReserve(id, liveNo);
        } else {
            return ResponseEntity.ok(new CommonResponse(999, "fail:ID 或者 订单号为空"));
        }
    }

    @RequestMapping(value = "/dormitory-user", method = RequestMethod.GET)
    public ResponseEntity<?> getDormitoryUserInfo(@RequestParam String loginId) {
        UserMiniVo vo = null;
        try {
            vo = ksDormitoryService.getDormitoryUserInfo(loginId);
        } catch (Exception e) {
            logger.error("begin getDormitoryUserInfo", e);
        }
        return ResponseEntity.ok(new CommonAppResponse(0, vo));
    }

    @RequestMapping(value = "/dormitory-reserve-info/export-excel", method = RequestMethod.POST)
    public ResponseEntity<?> exportDormitoryReverseInfo(KsDormitoryReserveVo ksDormitoryReserveVo, @RequestBody LinkedHashSet<String> fields) {
        logger.info("begin exportDormitoryReverseInfo ...");
        try {
            return ksDormitoryService.exportDormitoryReserveInfo(ksDormitoryReserveVo, fields);
        } catch (IOException e) {
            logger.error("查询宿舍导出excel 异常 -> {}", e.getMessage());
            return ResponseEntity.ok(new CommonAppResponse(999, "系统异常"));
        } finally {
            logger.info("end exportDormitoryReverseInfo ...");
        }
    }

    @RequestMapping(value = "/dormitory-reserve-info/live-nos", method = RequestMethod.POST)
    public ResponseEntity<?> gettDormitoryReverseInfoByLiveNos(@RequestBody Map<String, List<String>> liveNoMap, @RequestParam Integer pageSize) {
        List<String> liveNos = liveNoMap.get("liveNos");
        try {
            BaseQueryResponseVo baseQueryResponseVo = ksDormitoryService.getDormitoryReserveInfoByLiveNos(liveNos, pageSize);
            return ResponseEntity.ok(new CommonAppResponse(0, baseQueryResponseVo));
        } catch (ParamException e1) {
            return ResponseEntity.ok(new CommonAppResponse(999, e1.getMessage()));
        } catch (KsException e2) {
            return ResponseEntity.ok(new CommonAppResponse(e2.getCode(), e2.getMessage()));
        } catch (Exception e) {
            logger.error("查询宿舍导出excel 异常 -> {}", e.getMessage());
            return ResponseEntity.ok(new CommonAppResponse(999, "系统异常"));
        }
    }

    @RequestMapping(value = "/dormitory-parameter", method = RequestMethod.GET)
    public ResponseEntity<?> getUserDormitoryParam(Integer year, Integer month) {
        List<UserDormitoryParamDto> list = null;
        if (year != null && month != null) {
            list = ksDormitoryService.getUserDormitoryParam(year, month);
        }
        if (list == null || list.size() < 1) {
            UserDormitoryParamDto dto = null;
            list = new ArrayList<UserDormitoryParamDto>();
            dto = new UserDormitoryParamDto();
            dto.setYear(year);
            dto.setMonth(month);
            dto.setName("All_ROOM");
            list.add(dto);

            dto = new UserDormitoryParamDto();
            dto.setYear(year);
            dto.setMonth(month);
            dto.setName("A_ROOM");
            list.add(dto);

            dto = new UserDormitoryParamDto();
            dto.setYear(year);
            dto.setMonth(month);
            dto.setName("B_ROOM");
            list.add(dto);

            dto = new UserDormitoryParamDto();
            dto.setYear(year);
            dto.setMonth(month);
            dto.setName("C_ROOM");
            list.add(dto);
        }
        return ResponseEntity.ok(new CommonAppResponse(0, list));
    }

    @RequestMapping(value = "/dormitory-parameter", method = RequestMethod.POST)
    public ResponseEntity<?> saveOrUpdateUserDormitoryParam(@RequestBody JSONObject jsonObject) {
        JSONArray items = jsonObject.getJSONArray("items");
        List<UserDormitoryParamDto> list = new ArrayList<UserDormitoryParamDto>();
        if (items != null) {
            Iterator it = items.iterator();
            UserDormitoryParamDto vo = null;
            while (it.hasNext()) {
                JSONObject obj = (JSONObject) it.next();
                vo = new UserDormitoryParamDto();
                vo.setId(obj.getLong("id"));
                vo.setYear(obj.getInteger("year"));
                vo.setMonth(obj.getInteger("month"));
                vo.setName(obj.getString("name"));
                vo.setValue(obj.getInteger("value"));
                list.add(vo);
            }
        }
        boolean falg = ksDormitoryService.saveOrUpdateUserDormitoryParam(list);
        if (falg) {
            return ResponseEntity.ok(new CommonAppResponse(0, "success"));
        } else {
            return ResponseEntity.ok(new CommonResponse(999, "当前数据中年月存在相同记录"));
        }
    }

    @RequestMapping(value = "/dormitory-parameter-delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUserDormitoryParam(@RequestParam Long id) {
        ksDormitoryService.deleteUserDormitoryParam(id);
        return ResponseEntity.ok(new CommonAppResponse(0, "success"));
    }

    @RequestMapping(value = "/dormitory-report-export", method = RequestMethod.GET)
    public ResponseEntity<?> userDormitoryReport(@RequestParam Integer year, @RequestParam Integer month) {
        if (year != null && month != null) {
            String url = ksDormitoryService.userDormitoryReport(year, month);
            return ResponseEntity.ok(new CommonResponse(0, url));
        } else {
            return ResponseEntity.ok(new CommonResponse(999, "请选择年月"));
        }
    }

    @RequestMapping(value = "/dormitory-check-status", method = RequestMethod.GET)
    public ResponseEntity<?> checkDormitoryStatus(KsDormitoryReserveVo ksDormitoryReserveVo) {
        KsLifeCommonVo ksLifeCommonVo = ksDormitoryService.checkDormitoryStatus(ksDormitoryReserveVo);
        if (ksLifeCommonVo != null) {
            if (SUCCESS_CODE.compareTo(ksLifeCommonVo.getCode()) == 0) {
                return ResponseEntity.ok(new CommonAppResponse(0, ksLifeCommonVo.getMsg()));
            } else if (SUCCESS_CODE.compareTo(ksLifeCommonVo.getCode()) != 0) {
                return ResponseEntity.ok(new CommonAppResponse(ksLifeCommonVo.getCode(), ksLifeCommonVo.getMsg()));
            }
        }
        return ResponseEntity.ok(new CommonAppResponse(999, "系统异常"));
    }
}
