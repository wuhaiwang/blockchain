package com.seasun.management.controller.org;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.constant.ErrorCode;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.koa.CTokenTask;
import com.seasun.management.model.User;
import com.seasun.management.model.UserToken;
import com.seasun.management.service.OperateLogService;
import com.seasun.management.service.UseLogService;
import com.seasun.management.service.UserGestureService;
import com.seasun.management.service.UserVerifyService;
import com.seasun.management.util.MyTokenUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserVerifyService userVerifyService;

    @Autowired
    OperateLogService operateLogService;

    @Autowired
    UseLogService useLogService;

    @Autowired
    UserGestureService userGestureService;


    @RequestMapping(value = "/pub/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody JSONObject jsonObject) {
        logger.info("begin login...");
        String userName = jsonObject.getString("userName");
        String password = jsonObject.getString("password");

        String token = userVerifyService.verifyPassword(userName, password, UserToken.Type.web);
        if (token != null) {
            JSONObject result = new JSONObject();
            result.put("code", 0);
            result.put("token", token);
            User user = MyTokenUtils.getCurrentUser(token);
            operateLogService.add("web-login", user.getLoginId() + "登录了系统", user.getId());
            return ResponseEntity.ok(result);
        }
        logger.info("end login...");
        return ResponseEntity.ok(new CommonResponse(ErrorCode.USER_INVALID_ERROR, "无效用户"));
    }

    @RequestMapping(value = "/pub/login-app", method = RequestMethod.POST)
    public ResponseEntity<?> loginApp(@RequestBody JSONObject jsonObject) {
        logger.info("begin loginApp...");
        String userName = jsonObject.getString("userName");
        String password = jsonObject.getString("password");

        String token = userVerifyService.verifyPassword(userName, password, UserToken.Type.app);
        if (token != null) {
            User user = MyTokenUtils.getCurrentUser(token);
            operateLogService.add("app-login", user.getLoginId() + "登录了app", user.getId());

            boolean useGesture = userGestureService.useGesture(user.getId());
            JSONObject result = new JSONObject();
            result.put("code", 0);
            result.put("token", token);
            result.put("useGesture", useGesture);
            return ResponseEntity.ok(result);
        }
        logger.info("end loginApp...");
        return ResponseEntity.ok(new CommonResponse(ErrorCode.USER_INVALID_ERROR, "无效用户"));
    }

    @RequestMapping(value = "/pub/token", method = RequestMethod.POST)
    public ResponseEntity<?> checkToken(@RequestBody JSONObject jsonObject) {
        logger.info("begin checkToken...");
        String token = MyTokenUtils.getCurrentToken();
        boolean success = userVerifyService.verifyToken(token, UserToken.Type.app);
        if (success) {
            User user = MyTokenUtils.getCurrentUser();
            if (user.getActiveFlag()) {
                boolean useGesture = userGestureService.useGesture(user.getId());
                if (useGesture) {
                    //operateLogService.add("app-checkToken", user.getLoginId() + "自动登录了app", user.getId(), "app");
                }
                JSONObject responseObject = new JSONObject();
                responseObject.put("code", 0);
                responseObject.put("message", "success");
                responseObject.put("useGesture", useGesture);
                return ResponseEntity.ok(responseObject);
            } else {
                return ResponseEntity.ok(new CommonResponse(ErrorCode.USER_INVALID_ERROR, "无效用户"));
            }
        }
        logger.info("end checkToken...");
        return ResponseEntity.ok(new CommonResponse(ErrorCode.USER_INVALID_ERROR, "登录过期"));
    }

    @RequestMapping(value = "/pub/check-gesture", method = RequestMethod.POST)
    public ResponseEntity<?> checkGesture(@RequestBody JSONObject jsonObject) {
        logger.info("begin checkGesture...");
        String gesture = jsonObject.getString("gesture");
        boolean isValid = userGestureService.verifyGesture(gesture);
        if (isValid) {
            User user = MyTokenUtils.getCurrentUser();
            if (user.getActiveFlag()) {
                operateLogService.add("app-checkGesture", user.getLoginId() + "通过手势登录了app", user.getId());
                return ResponseEntity.ok(new CommonResponse(0, "success"));
            }
        }
        logger.info("end checkGesture...");
        return ResponseEntity.ok(new CommonResponse(ErrorCode.USER_INVALID_ERROR, "手势验证失败"));
    }

    @RequestMapping(value = "/pub/app-url")
    public void getAppUrl(HttpServletResponse response, HttpServletRequest request) {
        logger.info("begin getAppUrl...");
        String iosUrl = "http://tako.im/a2x7";
        String androidUrl = "https://tako.im/ni75";
        try {
            if ((request.getHeader("User-Agent").contains("Android"))) {
                response.sendRedirect(androidUrl);
            } else {
                response.sendRedirect(iosUrl);
            }
        } catch (IOException e) {
            logger.error("IO异常：{0}", e.getMessage());
            e.printStackTrace();
        }
    }

    //获取移动用户操作系统
    public static String getMobileOS(String userAgent) {
        if (userAgent.contains("Android")) {
            return "Android";
        } else {
            return "iOS";
        }
    }

    @RequestMapping(value = "/apis/auth/app/verify-password", method = RequestMethod.POST)
    public ResponseEntity<?> verifyPassword(@RequestBody JSONObject jsonObject) {
        logger.info("begin verifyPassword...");
        String password = jsonObject.getString("password");
        String koaUid = userVerifyService.verifyPasswordByCurrentUser(password);
        if (!StringUtils.isEmpty(koaUid)) {
            logger.info("end verifyPassword，success ...");
            JSONObject result = new JSONObject();
            result.put("code", 0);
            result.put("token", koaUid);
            result.put("message", "success");
            return ResponseEntity.ok(result);
        }
        logger.info("end verifyPassword，failed ...");
        return ResponseEntity.ok(new CommonResponse(ErrorCode.USER_INVALID_ERROR, "验证失败"));
    }

    @RequestMapping(value = "/apis/auth/app/verify-gesture", method = RequestMethod.POST)
    public ResponseEntity<?> verifyGesture(@RequestBody JSONObject jsonObject) {
        logger.info("begin verifyGesture...");
        String password = jsonObject.getString("password");
        boolean isValid = userGestureService.verifyGesture(password);
        if (isValid) {
            logger.info("end verifyGesture，success ...");
            return ResponseEntity.ok(new CommonResponse(0, "success"));
        }
        logger.info("end verifyGesture，failed ...");
        return ResponseEntity.ok(new CommonResponse(ErrorCode.USER_INVALID_ERROR, "验证失败"));
    }

    @RequestMapping(value = "/pub/login-app-dynamic-password", method = RequestMethod.POST)
    public ResponseEntity<?> loginAppByDynamicPassword(@RequestBody JSONObject jsonObject) {
        logger.info("begin loginApp...");
        String userName = jsonObject.getString("userName");
        String password = jsonObject.getString("password");

        String token = userVerifyService.verifyDynamicPassword(userName, password);
        if (token != null) {
            JSONObject result = new JSONObject();
            result.put("code", 0);
            result.put("token", token);
            return ResponseEntity.ok(result);
        }
        logger.info("end loginApp...");
        return ResponseEntity.ok(new CommonResponse(ErrorCode.USER_INVALID_ERROR, "无效用户"));
    }

    @RequestMapping(value = "/pub/dynamic-password", method = RequestMethod.POST)
    public ResponseEntity<?> getDynamicPassword(@RequestBody JSONObject jsonObject) {
        logger.info("begin getDynamicPassword...");
        String userName = jsonObject.getString("userName");
        boolean sendSuccess = userVerifyService.getDynamicPassword(userName);
        logger.info("end getDynamicPassword...");
        if (sendSuccess) {
            return ResponseEntity.ok(new CommonResponse(0, "动态密码获取成功"));
        } else {
            return ResponseEntity.ok(new CommonResponse(-1, "动态密码获取失败"));
        }
    }

    @RequestMapping(value = "/pub/logout-app", method = RequestMethod.POST)
    public ResponseEntity<?> logoutApp() {
        logger.info("begin logoutApp...");
        userVerifyService.clearUserToken(UserToken.Type.app);
        logger.info("end logoutApp...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/pub/login-test", method = RequestMethod.GET)
    public ResponseEntity<?> login(HttpServletResponse response, @RequestParam String userName) {
        logger.info("begin login-test...");
        String token = userVerifyService.addTestUser(userName);
        if (token != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 0);
            jsonObject.put("token", token);
            return ResponseEntity.ok(jsonObject);
        }
        logger.info("end login-test...");
        return ResponseEntity.ok(new CommonResponse(ErrorCode.USER_INVALID_ERROR, "无效用户"));
    }

    @RequestMapping(value = "/")
    public ModelAndView index() {
        return new ModelAndView("/index.html");
    }

}
