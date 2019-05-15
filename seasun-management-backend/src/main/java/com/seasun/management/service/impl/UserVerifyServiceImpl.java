package com.seasun.management.service.impl;

import java.net.URLEncoder;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import com.seasun.management.koa.SSOKeyHelper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kingsoft.sso.api.SSORequest;
import com.seasun.management.exception.ParamException;
import com.seasun.management.exception.PasswordVerifyException;
import com.seasun.management.koa.CTokenTask;
import com.seasun.management.mapper.UserDynamicPasswordMapper;
import com.seasun.management.mapper.UserMapper;
import com.seasun.management.mapper.UserTokenMapper;
import com.seasun.management.model.User;
import com.seasun.management.model.UserDynamicPassword;
import com.seasun.management.model.UserToken;
import com.seasun.management.service.UserVerifyService;
import com.seasun.management.util.MyEncryptorUtils;
import com.seasun.management.util.MyEnvUtils;
import com.seasun.management.util.MyHttpClientUtils;
import com.seasun.management.util.MySystemParamUtils;
import com.seasun.management.util.MyTokenUtils;

@Service
public class UserVerifyServiceImpl implements UserVerifyService {

    private static final Logger logger = LoggerFactory.getLogger(UserVerifyServiceImpl.class);

    @Autowired
    private UserTokenMapper userTokenMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserDynamicPasswordMapper userDynamicPasswordMapper;

    private static final String DSP_VERIFY_URL = "https://spiapi.seasungame.com:9001/checkloginforyw";

    private static final String DSP_SEND_MESSAGE_URL = "https://spiapi.seasungame.com:8082/api/sendmessage";

    private static final String APP_ORIGIN_KEY = "P2JDioFIl13PPIZ9";

    @Override
    public boolean verifyToken(String token, int type) {
        if (null == token) {
            return false;
        }

        // 若不是生产环境（local,qa,pp），且本地系统参数配置为测试模式, 则走db的token验证
        if (!MyEnvUtils.isProdEnv()
                && MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.TestModeFlag, true)) {
            UserToken dbUserToken = userTokenMapper.selectByTokenAndType(token, type);
            return dbUserToken != null && dbUserToken.getId() != null;
        }

        String tid = null;
        if (UserToken.Type.web == type) {
            tid = SSOKeyHelper.getSSoKey().getWebTid();
        } else if (UserToken.Type.app == type) {
            tid = SSOKeyHelper.getSSoKey().getAppTid();
        }
        JSONObject json = SSORequest.userTokenCheck(token, tid);
        return json.getBooleanValue("success");
    }

    @Override
    public String verifyPassword(String userName, String password, int type) {

        // 密码处理
        if (UserToken.Type.web == type) {
            password = new String(Base64.getDecoder().decode(password));
        } else if (UserToken.Type.app == type) {
            try {
                password = MyEncryptorUtils.decryptByAES(password, APP_ORIGIN_KEY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 检查账号
        User user = userMapper.selectActiveUserByLoginId(userName);
        if (null == user) {
            logger.error("can not found active user in IT-system , contact admin to sync user ...");
            throw new PasswordVerifyException("该账号不存在...");
        } else if (user.getVirtualFlag()) {
            throw new PasswordVerifyException("虚拟账号不能登录系统...");
        }


        // 若不是生产环境（local,qa,pp），且本地系统参数配置为测试模式, 则使用虚拟密码登录
        if (!MyEnvUtils.isProdEnv()
                && MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.TestModeFlag, true)) {
            String dbPassword = MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.TestModePw, "");
            try {
                if (password.equals(MyEncryptorUtils.decryptByAES(dbPassword))) {
                    String token = RandomStringUtils.randomAlphanumeric(12);
                    return updateUserToken(user, token, type);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String token = verifyFromKoa(userName, password, type);
        if (StringUtils.isEmpty(token)) {
            logger.info("koa verify failed ...");
            throw new PasswordVerifyException("验证失败");
        }

        logger.info("login success");
        return updateUserToken(user, token, type);
    }

    @Override
    public String verifyPasswordByCurrentUser(String password) {
        User user = MyTokenUtils.getCurrentUser();

        int type = MyTokenUtils.getChannel().equalsIgnoreCase(MyTokenUtils.Channel.pc) ? 0 : 1;
        if (UserToken.Type.web == type) {
            password = new String(Base64.getDecoder().decode(password));
        } else if (UserToken.Type.app == type) {
            try {
                password = MyEncryptorUtils.decryptByAES(password, APP_ORIGIN_KEY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String token = verifyFromKoa(user.getLoginId(), password, type);
        if (StringUtils.isEmpty(token)) {
            logger.info("koa verify failed ...");
            throw new PasswordVerifyException("验证失败");
        } else {
            updateUserToken(user, token, type);
        }
        logger.info("login success");
        return token;

    }

    private Boolean verifyPassword(String userName, String password) {
        boolean isDspVerifySuccess = false;

        // 获取测试密码开关
        Boolean performanceTestModeFlag = MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.TestModeFlag, MySystemParamUtils.DefaultValue.TestModeFlag);

        // 1.本地不验证密码
        if (MyEnvUtils.isLocalEnv()) {
            isDspVerifySuccess = true;
        }
        // 2.测试密码开关打开
        else if (performanceTestModeFlag) {
            String performanceTestModePw = MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.TestModePw, MySystemParamUtils.DefaultValue.TestModePw);
            // 判断是否匹配了测试密码
            try {
                String realPassword = MyEncryptorUtils.decryptByAES(performanceTestModePw);
                isDspVerifySuccess = password.equals(realPassword);
            } catch (Exception e) {
                logger.error("decrypt code failed...");
                e.printStackTrace();
            }
            // 若不匹配测试密码，再次走dsp验证。
            if (!isDspVerifySuccess) {
                isDspVerifySuccess = verifyFromDsp(userName, password);
            }
        }
        // 其他情况，均需要走dsp验证。
        else {
            isDspVerifySuccess = verifyFromDsp(userName, password);
        }

        return isDspVerifySuccess;
    }

    @Override
    public String addTestUser(String userName) {
        User user = userMapper.selectUserByName(userName);
        if (null == user) {
            User newUser = new User();
            Long newUserId = 1L;
            User maxUser = userMapper.selectMaxTestUser();
            if (null != maxUser) {
                newUserId = maxUser.getId() + 1;
            }

            newUser.setId(newUserId);
            newUser.setLastName(userName);
            newUser.setFirstName("");
            newUser.setActiveFlag(true);
            newUser.setVirtualFlag(false);
            newUser.setOrderCenterId(1L);
            newUser.setCostCenterId(1L);
            newUser.setCreateTime(new Date());
            userMapper.insertWithId(newUser);
            user = newUser;
        }
        String newToken = updateUserToken(user, UserToken.Type.web);
        return newToken;
    }


    /**
     * 登录成功后,刷新token
     *
     * @param user
     * @return
     */
    private String updateUserToken(User user, int type) {
        String token = RandomStringUtils.randomAlphanumeric(12);
        return updateUserToken(user, token, type);
    }

    private String updateUserToken(User user, String token, int type) {
        UserToken userToken = userTokenMapper.selectByUserId(user.getId(), type);
        if (null == userToken) {
            logger.info("new user token,will create ...");
            UserToken newUserToken = new UserToken();
            newUserToken.setUserId(user.getId());
            newUserToken.setUserName(user.getLastName() + user.getFirstName());
            newUserToken.setToken(token);
            newUserToken.setLatestLoginTime(new Date());
            newUserToken.setType(type);
            userTokenMapper.insertSelective(newUserToken);
        } else {
            logger.info("user token exist,will update token...");
            userToken.setType(type);
            userToken.setToken(token);
            userToken.setLatestLoginTime(new Date());
            userTokenMapper.updateByPrimaryKeySelective(userToken);
        }
        return token;
    }

    /**
     * 注销登录后清空token
     *
     * @param type
     */
    @Override
    public void clearUserToken(int type) {
        User user = MyTokenUtils.getCurrentUser();
        ;
        if (null != user) {
            UserToken userToken = userTokenMapper.selectByUserId(user.getId(), type);
            if (null != userToken) {
                userToken.setToken(null);
                userTokenMapper.updateByPrimaryKey(userToken);
            }
        }
    }

    /**
     * 从dsp验证用户的有效性
     *
     * @param name
     * @param password
     * @return
     */
    private boolean verifyFromDsp(String name, String password) {

        try {
            StringBuilder body = new StringBuilder();
            body.append("loginid=").append(URLEncoder.encode(name, "utf-8")).append("&").append("pwd=").append(URLEncoder.encode(password, "utf-8"));
            long startTime = System.currentTimeMillis();
            String result = MyHttpClientUtils.doPost(DSP_VERIFY_URL, body.toString(), "application/x-www-form-urlencoded");
            long endTime = System.currentTimeMillis();
            logger.info("dsp verify cost : {} ms", endTime - startTime);
            if (result != null) {
                JSONObject resultJson = JSON.parseObject(result);
                if (resultJson.getBoolean("success")) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("exception,dsp verify failed...");
            return false;
        }

        return false;
    }

    private String verifyFromKoa(String userName, String password, int type) {

        // 海外的账号有“_”前缀，统一去除
        int hwIndex = userName.indexOf("_");
        if (hwIndex > 0) {
            userName = userName.substring(hwIndex + 1);
        }

        JSONObject json = null;
        if (UserToken.Type.web == type) {
            json = SSORequest.userTokenLogin(userName, password, SSOKeyHelper.getSSoKey().getWebPubKey(), SSOKeyHelper.getSSoKey().getWebTid());
        } else if (UserToken.Type.app == type) {
            json = SSORequest.userTokenLogin(userName, password, SSOKeyHelper.getSSoKey().getAppPubKey(), SSOKeyHelper.getSSoKey().getAppTid());
        }
        if (json != null) {
            return json.getString("uid");
        }
        return null;
    }

    /**
     * 获取动态密码
     *
     * @param userName
     * @return
     */
    @Override
    public boolean getDynamicPassword(String userName) {
        User user = userMapper.selectActiveUserByLoginId(userName);
        if (null == user) {
            logger.info("user not found ...");
            throw new ParamException("用户不存在");
        }

        String dynamicPassword = RandomStringUtils.randomNumeric(6);
        boolean isNew = false;
        UserDynamicPassword userDynamicPassword = userDynamicPasswordMapper.selectByLoginId(userName);
        if (null == userDynamicPassword) {
            isNew = true;
            userDynamicPassword = new UserDynamicPassword();
        }
        userDynamicPassword.setDynamicPassword(MyEncryptorUtils.encryptByMD5(dynamicPassword));
        userDynamicPassword.setUpdateTime(new Date());
        userDynamicPassword.setRetryCnt(0);
        if (isNew) {
            userDynamicPassword.setLoginId(userName);
            userDynamicPasswordMapper.insert(userDynamicPassword);
        } else {
            userDynamicPasswordMapper.updateByPrimaryKey(userDynamicPassword);
        }

        return sendMail(userName, "【西山居管理系统】动态密码", "【西山居管理系统】动态密码： " + dynamicPassword + " （10分钟有效）");
    }

    /**
     * 验证动态密码
     *
     * @param userName
     * @param password
     * @return
     */
    @Override
    public String verifyDynamicPassword(String userName, String password) {
        password = MyEncryptorUtils.encryptByMD5(password);

        UserDynamicPassword userDynamicPassword = userDynamicPasswordMapper.selectByLoginId(userName);
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, -10);
        if (null == userDynamicPassword || nowTime.getTime().getTime() > userDynamicPassword.getUpdateTime().getTime()) {
            logger.info("dynamic password is invalid or time out... ");
            throw new PasswordVerifyException("动态密码已超时");
        }

        if (userDynamicPassword.getRetryCnt() > 3) {
            logger.info("dynamic password retry more than 3 times... ");
            throw new PasswordVerifyException("动态密码尝试超过3次，请重新获取动态密码");
        }

        if (!userDynamicPassword.getDynamicPassword().equals(password)) {
            logger.info("dynamic password verify failed ...");

            userDynamicPassword.setRetryCnt(userDynamicPassword.getRetryCnt() + 1);
            userDynamicPasswordMapper.updateByPrimaryKey(userDynamicPassword);

            throw new PasswordVerifyException("动态密码验证失败");
        } else {
            userDynamicPassword.setUpdateTime(nowTime.getTime());
            userDynamicPasswordMapper.updateByPrimaryKey(userDynamicPassword);
        }

        logger.info("dynamic password verify success ... ");

        User user = userMapper.selectActiveUserByLoginId(userName);

        return updateUserToken(user, UserToken.Type.app);
    }

    /**
     * 使用DSP的邮件服务  todo: IT系统没有自己的邮件服务，暂时先用这dsp的接口
     *
     * @param userName
     * @param subject
     * @param message
     * @return
     */
    private boolean sendMail(String userName, String subject, String message) {

        try {
            StringBuilder body = new StringBuilder();
            body.append("receive=").append(URLEncoder.encode(userName, "utf-8"))
                    .append("&").append("title=").append(URLEncoder.encode(subject, "utf-8"))
                    .append("&").append("message=").append(URLEncoder.encode(message, "utf-8"))
                    .append("&").append("sendtype=").append(URLEncoder.encode("email", "utf-8"));
            long startTime = System.currentTimeMillis();
            String result = MyHttpClientUtils.doPost(DSP_SEND_MESSAGE_URL, body.toString(), "application/x-www-form-urlencoded");
            long endTime = System.currentTimeMillis();
            logger.info("dsp send message cost : {} ms", endTime - startTime);
            if (result != null) {
                if (result.contains("send success")) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("exception,dsp send message failed...");
            return false;
        }

        return false;
    }
}

