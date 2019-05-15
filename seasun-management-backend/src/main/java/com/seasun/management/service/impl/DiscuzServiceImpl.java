package com.seasun.management.service.impl;

import com.seasun.management.discuz.client.Client;
import com.seasun.management.discuz.util.XMLHelper;
import com.seasun.management.mapper.UserDiscuzInfoMapper;
import com.seasun.management.model.User;
import com.seasun.management.model.UserDiscuzInfo;
import com.seasun.management.service.DiscuzService;
import com.seasun.management.util.MyCharacterUtils;
import com.seasun.management.util.MyEncryptorUtils;
import com.seasun.management.util.MyTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Service
public class DiscuzServiceImpl implements DiscuzService {

    private static final Logger logger = LoggerFactory.getLogger(DiscuzServiceImpl.class);

    private final String ERROR_CODE = "999";
    private final String SUCCESS_CODE = "0";
    private final String SUCCESS_MSG = "OK";

    @Autowired
    private Client client;

    @Autowired
    private UserDiscuzInfoMapper userDiscuzInfoMapper;

    @Override
    public Map<String,String> userLogin(String token) {
        User  currentUser = MyTokenUtils.getCurrentUser(token);
        String loginId = currentUser.getLoginId();
        Map<String,String> map =new HashMap<String,String>();
        String message = null;
        if(StringUtils.isEmpty(loginId)){
            map.put("code",ERROR_CODE);
            map.put("msg","登录账号异常"+loginId);
            return map;
        }
        UserDiscuzInfo userDiscuzInfo = userDiscuzInfoMapper.selectByLoginId(loginId);
        if(userDiscuzInfo == null){
            userDiscuzInfo = new UserDiscuzInfo();
            String email = currentUser.getEmail();
            if(!StringUtils.isEmpty(email)){
                try {
                    email = MyEncryptorUtils.decryptByAES(email);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String password = MyCharacterUtils.getRandomString2(10);
            userDiscuzInfo.setLoginId(loginId);
            userDiscuzInfo.setPassword(password);
            try {
                message = userRegister(loginId, password, email, currentUser.getName());
            } catch (Exception e) {
                message = e.getMessage();
                logger.error("注册失败",e);
            }
        }else{
            try {
                userDiscuzInfo.setPassword(MyEncryptorUtils.decryptByAES(userDiscuzInfo.getPassword()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(!StringUtils.isEmpty(message) && !SUCCESS_MSG.equalsIgnoreCase(message)){
            map.put("code",ERROR_CODE);
            map.put("msg",message);
            return map;
        }
        map = login(userDiscuzInfo.getLoginId(), userDiscuzInfo.getPassword());
        return  map;
    }

    private Map<String,String> login(String loginId, String password){
        Map<String,String> map =new HashMap<String,String>();
        map.put("code",ERROR_CODE);
        map.put("msg","登录论坛未知异常");
        String result = client.uc_user_login(loginId, password);
        LinkedList<String> rs = XMLHelper.uc_unserialize(result);
        if(rs.size()>0){
            int $uid = Integer.parseInt(rs.get(0));
            if($uid > 0) {
                map.put("code",SUCCESS_CODE);
                map.put("msg",client.uc_user_synlogin($uid));
            }else if($uid == -1) {
                map.put("code",ERROR_CODE);
                map.put("msg","用户不存在,或者被删除");
            } else if($uid == -2) {
                map.put("code",ERROR_CODE);
                map.put("msg","密码错");
            } else {
                map.put("code",ERROR_CODE);
                map.put("msg","未定义");
            }
        }
        return map;
    }

    /**
     * 论坛用户中心注册
     * @param loginId
     * @param password
     * @param email
     * @return
     */
    public String userRegister(String loginId, String password, String email, String realName) {
        String message = null;
        String returns = client.uc_user_register(loginId, password,email,"","",realName);
        int uid = Integer.parseInt(returns);
        if(uid <= 0) {
            if(uid == -1) {
                message = "用户名不合法";
            } else if(uid == -2) {
                message = "包含要允许注册的词语";
            } else if(uid == -3) {
                message = "用户名已经存在";
            } else if(uid == -4) {
                message = "Email 格式有误";
            } else if(uid == -5) {
                message = "Email 不允许注册";
            } else if(uid == -6) {
                message = "该 Email 已经被注册";
            } else {
                message = "未定义";
            }
        } else {
            message = createUser(loginId,password);
        }
        return message;
    }

    /**
     * 创建论坛本地用户
     * @param loginId
     * @param password
     * @return
     */
    private String createUser(String loginId, String password){
        UserDiscuzInfo record = new UserDiscuzInfo();
        record.setLoginId(loginId);
        String message = null;
        try {
            record.setPassword(MyEncryptorUtils.encryptByAES(password));
            record.setCreationTime(new Date());
            int count = userDiscuzInfoMapper.insert(record);
            if(count>0) {
                message = SUCCESS_MSG;
            }
        } catch (Exception e) {
            message = "创建用户失败";
            logger.error("创建用户失败",e);
        }
        return message;
    }
}
