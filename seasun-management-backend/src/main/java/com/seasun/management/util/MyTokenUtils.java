package com.seasun.management.util;

import com.seasun.management.dto.AppInfoDto;
import com.seasun.management.exception.TokenInvalidException;
import com.seasun.management.helper.PermissionHelper;
import com.seasun.management.mapper.ProjectMapper;
import com.seasun.management.mapper.RUserProjectPermMapper;
import com.seasun.management.mapper.UserMapper;
import com.seasun.management.mapper.UserTokenMapper;
import com.seasun.management.model.RUserProjectPerm;
import com.seasun.management.model.User;
import com.seasun.management.model.UserToken;
import com.seasun.management.vo.ProjectMaxMemberVo;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class MyTokenUtils {

    public static final Long BOSS_ID = 1086L;
    public static final Long ADMIN_ID = 1101L;
    public static final String BOSS = "guoweiwei";


    public interface Channel {
        String pc = "pc";
        String mobile = "mobile";
        String dsp = "dsp";
    }

    public static String getToken(HttpServletRequest request) {
        String token = request.getHeader("Token");
        return token;
    }

    public static String getChannel() {
        return getCurrentRequest().getHeader("channel");
    }

    public static String getCurrentToken() {
        String token = getCurrentRequest().getHeader("Token");
        return token;
    }

    public static AppInfoDto getAppInfo() {
        AppInfoDto appInfo = new AppInfoDto();
        HttpServletRequest request = getCurrentRequest();
        appInfo.setSystem(request.getHeader("system"));
        appInfo.setVersion(request.getHeader("version"));
        appInfo.setModel(request.getHeader("model"));
        appInfo.setAppVersion(request.getHeader("appVersion"));
        appInfo.setImei(request.getHeader("imei"));
        appInfo.setCodePushLabel(request.getHeader("codePushLabel"));
        appInfo.setCodePushEnvironment(request.getHeader("codePushEnvironment"));
        return appInfo;
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public static Long getCurrentUserIdWithDefaultValue(Long defaultUserId) {
        Long userId = defaultUserId;
        try {
            userId = getCurrentUserId();
        } catch (Exception e) {
            return userId;
        }
        return userId;
    }


    public static boolean isAdmin() {
        RUserProjectPermMapper rUserProjectPermMapper = MyBeanUtils.getBean(RUserProjectPermMapper.class);
        RUserProjectPerm rUserProjectPerm = new RUserProjectPerm();
        rUserProjectPerm.setUserId(getCurrentUserId());
        rUserProjectPerm.setProjectRoleId(PermissionHelper.SystemRole.BACKEND_MANAGEMENT_ID);
        RUserProjectPerm adminPerm = rUserProjectPermMapper.selectSelective(rUserProjectPerm);
        return null != adminPerm;
    }

    public static boolean isAppAdmin(Long userId) {
        if (null == userId) {
            userId = getCurrentUserId();
        }
        RUserProjectPermMapper rUserProjectPermMapper = MyBeanUtils.getBean(RUserProjectPermMapper.class);
        RUserProjectPerm rUserProjectPerm = new RUserProjectPerm();
        rUserProjectPerm.setUserId(userId);
        rUserProjectPerm.setProjectRoleId(PermissionHelper.AppRole.APP_MANAGEMENT_ID);
        RUserProjectPerm adminPerm = rUserProjectPermMapper.selectSelective(rUserProjectPerm);
        return null != adminPerm;
    }

    public static boolean isBoss() {
        User user = getCurrentUser();
        return isBoss(user);
    }

    public static boolean isBoss(User user) {
        if (null == user) {
            user = getCurrentUser();
        }
        return BOSS.equals(user.getLoginId());
    }

    public static User getBoss() {
        UserMapper userMapper = MyBeanUtils.getBean(UserMapper.class);
        return userMapper.selectUserByLoginId(BOSS);
    }

    public static boolean isAppHr(Long userId) {
        if (null == userId) {
            userId = getCurrentUserId();
        }
        RUserProjectPermMapper rUserProjectPermMapper = MyBeanUtils.getBean(RUserProjectPermMapper.class);
        RUserProjectPerm rUserProjectPerm = new RUserProjectPerm();
        rUserProjectPerm.setUserId(userId);
        rUserProjectPerm.setProjectRoleId(PermissionHelper.AppRole.APP_HR_ID);
        RUserProjectPerm hrPerm = rUserProjectPermMapper.selectSelective(rUserProjectPerm);
        return null != hrPerm;
    }

    public static boolean isHrManager(User user) {
        if (null == user) {
            user = getCurrentUser();
        }

        String receiverStr = MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.MaxMemberFlowReceiver, MySystemParamUtils.DefaultValue.MaxMemberFlowReceiver);
        String[] receivers = receiverStr.split(",");
        for (int i = 0; i < receivers.length; i++) {
            if (receivers[i].equals(user.getLoginId())) {
                return true; // 是hr负责人则返回true
            }
        }

        return false;
    }


    public static List<String> getHrManagers() {
        String[] receivers = MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.MaxMemberFlowReceiver,
                MySystemParamUtils.DefaultValue.MaxMemberFlowReceiver).split(",");
        List<String> result = new ArrayList<>();
        for (int i = 0; i < receivers.length; i++) {
            result.add(receivers[i]);
        }
        return result;
    }

    public static boolean isProjectManager(Long userId) {
        if (null == userId) {
            userId = getCurrentUserId();
        }
        ProjectMapper projectMapper = MyBeanUtils.getBean(ProjectMapper.class);
        List<ProjectMaxMemberVo> projectMaxMemberVos = projectMapper.selectAllWithMaxMemberByManagerAndShowApp(userId);
        return null != projectMaxMemberVos && !projectMaxMemberVos.isEmpty();
    }

    public static String getCurrentUserNameWithDefaultValue(String defaultName) {
        String name = defaultName;
        try {
            name = getCurrentUserName();
        } catch (Exception e) {
            return name;
        }
        return name;
    }

    public static String getCurrentUserName() {
        User user = getCurrentUser();
        return user.getName();
    }

    public static HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static String getCurrentSessionId() {
        return getCurrentRequest().getSession().getId();
    }


    /**
     * 获取当前登录的用户.web和app任意一个匹配到即返回.
     *
     * @return
     */
    public static User getCurrentUser() {
        return getCurrentUser(getCurrentToken());
    }

    public static User getCurrentUser(String token) {
        int type = getChannel().equalsIgnoreCase(Channel.pc) ? 0 : 1;
        UserTokenMapper userTokenMapper = MyBeanUtils.getBean(UserTokenMapper.class);
        UserMapper userMapper = MyBeanUtils.getBean(UserMapper.class);
        UserToken userToken = userTokenMapper.selectByTokenAndType(token, type);
        if (null == userToken) {
            throw new TokenInvalidException("can not found token...");
        }
        User user = userMapper.selectByPrimaryKey(userToken.getUserId());
        return user;
    }

}
