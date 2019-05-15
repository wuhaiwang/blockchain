package com.seasun.management.service.permission;

import com.seasun.management.constant.MenuConstant;
import com.seasun.management.dto.MenuPermDto;
import com.seasun.management.dto.RConstantPermDto;
import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.CfgPlatAttrMapper;
import com.seasun.management.mapper.UserDetailMapper;
import com.seasun.management.mapper.UserMapper;
import com.seasun.management.model.CfgPlatAttr;
import com.seasun.management.model.User;
import com.seasun.management.model.UserDetail;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.CfgPlatAttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConstantPermCollector extends PermissionCollector {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CfgPlatAttrMapper cfgPlatAttrMapper;

    @Autowired
    private UserDetailMapper userDetailMapper;

    @Override
    public List<? extends MenuPermDto> getMenuPermsByUserId(Long userId) {

        // 补充个人绩效的菜单权限
        List<RConstantPermDto> rMenuPerfPermDtos = new ArrayList<>();
        if (isUserNeedSelfPerformance(userId)) {
            RConstantPermDto dto = new RConstantPermDto();
            dto.setModule("perf");
            dto.setKey(MenuConstant.web_self_performance_menu);
            rMenuPerfPermDtos.add(dto);
        }

        // 补充月/周分摊录入的菜单权限
        UserDetail userDetail = userDetailMapper.selectByUserId(userId);
        if (userDetail != null) {
            if (("正式".equals(userDetail.getWorkStatus()) || "试用".equals(userDetail.getWorkStatus()))) {
                rMenuPerfPermDtos.addAll(userHasPlatShareWritePower(userId));
            }
        }

        return rMenuPerfPermDtos;
    }


    /**
     * 检查用户是否参与绩效
     *
     * @return
     */
    private boolean isUserNeedSelfPerformance(Long userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (MyTokenUtils.isBoss(user)) {
            return false;
        }

        // 没有配置绩效组的用户不显示我的绩效
        if (null == user.getPerfWorkGroupId()) {
            return false;
        }

        return true;
    }


    // 补充员工 月分摊、周分摊模块进入权限
    private List<RConstantPermDto> userHasPlatShareWritePower(Long userId) {
        List<RConstantPermDto> result = new ArrayList<>();
        List<CfgPlatAttr> cfgPlatAttrs = cfgPlatAttrMapper.selectByPlatSubUserId(userId);
        boolean platShareFlag = false;
        boolean platShareWeekFlag = false;
        for (CfgPlatAttr cfgPlatAttr : cfgPlatAttrs) {
            if (cfgPlatAttr.getShareDetailFlag()) {
                platShareFlag = true;
            }
            // todo :新加字段，需要做判空操作，如果表中此字段都设置了值，则可取消此判空操作
            if (cfgPlatAttr.getShareWeekWriteFlag() != null && cfgPlatAttr.getShareWeekWriteFlag()) {
                platShareWeekFlag = true;
            }
        }
        if (platShareFlag) {
            RConstantPermDto dto = new RConstantPermDto();
            dto.setModule("finance");
            dto.setKey(MenuConstant.web_plat_share_menu);
            result.add(dto);
        }
        if (platShareWeekFlag) {
            RConstantPermDto dto = new RConstantPermDto();
            dto.setModule("finance");
            dto.setKey(MenuConstant.weekly_plat_share);
            result.add(dto);
        }
        return result;
    }

    private CfgPlatAttrVo isUserHasShareDetailEditPerm(Long userId) {
        Long userProjectId = userMapper.selectUserProjectId(userId); // 用户所在平台
        List<CfgPlatAttrVo> cfgPlatAttrVos = cfgPlatAttrMapper.selectAllWithPlatName(); // 当前所有在填写分摊的平台
        List<CfgPlatAttrVo> matchedPlats = cfgPlatAttrVos.stream().filter(c -> c != null
                && c.getShareDetailFlag()
                && c.getPlatId().equals(userProjectId)).collect(Collectors.toList());
        return matchedPlats.size() > 0 ? matchedPlats.get(0) : null;
    }

}
