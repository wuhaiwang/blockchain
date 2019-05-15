package com.seasun.management.controller.interior;

import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.service.CpInfoService;
import com.seasun.management.service.UserService;
import com.seasun.management.service.WechatCropService;
import com.seasun.management.vo.RequestUserVo;
import com.seasun.management.vo.UserGroupManagerVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/apis/interior/pub")
/**
 * 内部子系统接口控制器
 * */
public class PubInteriorController {

    @Autowired
    private UserService userService;

    @Autowired
    WechatCropService wechatCropService;

    @Autowired
    private CpInfoService cpInfoService;

    private Logger logger = LoggerFactory.getLogger(PubInteriorController.class);


    @RequestMapping(value = "/group/managers/users")
    ResponseEntity<?> userGroupManagerDtoList (@RequestParam("userIds")List<Long> userIds, @RequestParam("isNeedFindParent") Boolean isNeedFindParent, HttpServletRequest request) {
        logger.debug("内部系统调用接口, querystring => {}, uri => {}", request.getQueryString(), request.getRequestURI());
        return ResponseEntity.ok(new CommonAppResponse(0, userService.findUserGroupManagers(userIds, isNeedFindParent)));
    }


    @RequestMapping(value = "/wechat/concat/users", method = RequestMethod.PUT)
    ResponseEntity<?> updateWechatConcatUsers (@RequestParam("type")String type, @RequestBody List<RequestUserVo> requestUserVoList, HttpServletRequest request) throws Exception{
        logger.debug("内部系统调用接口, querystring => {}, uri => {}", request.getQueryString(), request.getRequestURI());
        return ResponseEntity.ok(new CommonAppResponse(0, wechatCropService.updateUsers(requestUserVoList, type)));
    }

    @RequestMapping(value = "/cp/project", method = RequestMethod.GET)
    ResponseEntity<?> getProjectInfo (@RequestParam("projectName")String projectName,String date) throws Exception{
        logger.info(date+"========getProjectInfo==========="+projectName);
        Map<String,BigDecimal> surplusAmountMap =  cpInfoService.getProjectInfo(projectName, date);
        logger.info(date+"=========getProjectInfo=========="+surplusAmountMap);
        return ResponseEntity.ok(new CommonAppResponse(0, surplusAmountMap));
    }
}
