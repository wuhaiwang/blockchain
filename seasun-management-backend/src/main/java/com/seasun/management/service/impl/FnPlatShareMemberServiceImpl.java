package com.seasun.management.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.mapper.*;
import com.seasun.management.model.FnPlatShareMember;
import com.seasun.management.model.ProjectRole;
import com.seasun.management.service.FnPlatShareConfigService;
import com.seasun.management.service.FnPlatShareMemberService;
import com.seasun.management.service.oldShare.OldShareFnPlatShareMemberServiceImpl;
import com.seasun.management.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service(value = "fnPlatShareMemberService")
public class FnPlatShareMemberServiceImpl extends OldShareFnPlatShareMemberServiceImpl {
    @Autowired
    RUserProjectPermMapper rUserProjectPermMapper;

    @Autowired
    UserMapper userMapper;

    @Override
    public List<FnPlatShareMemberVo> getPlatShareMembers(Long platId) {
        // 从人力树(即订单)上获取当前平台的所有成员，作为分摊填写的成员。
        List<FnPlatShareMemberVo> result = new ArrayList<>();
        List<OrgWorkGroupMemberAppVo> platMembers = userMapper.selectAllUserByProjectIdWithoutTrainee(platId);
        for (OrgWorkGroupMemberAppVo platMember : platMembers) {
            FnPlatShareMemberVo fnPlatShareMemberVo = new FnPlatShareMemberVo();
            fnPlatShareMemberVo.setId(platMember.getId());
            fnPlatShareMemberVo.setPlatId(platId);
            fnPlatShareMemberVo.setUserPhoto(platMember.getPhoto());
            fnPlatShareMemberVo.setUserId(platMember.getUserId());
            fnPlatShareMemberVo.setUserName(platMember.getName());
            fnPlatShareMemberVo.setWeight(BigDecimal.ONE);
            fnPlatShareMemberVo.setLoginId(platMember.getLoginId());
            result.add(fnPlatShareMemberVo);
        }
        return result;
    }

    @Override
    public JSONObject insert(FnPlatShareMember fnPlatShareMember) {
        return null;
    }

    @Override
    public void update(FnPlatShareMember fnPlatShareMember) {}

    @Override
    public void delete(Long id) {}

    @Override
    public List<UserLoginEmailVo> getShareMembersConfigInfo(Long platId, Integer year, Integer month) {
        List<UserLoginEmailVo> result = new ArrayList<>();

        // 没有填写的
        List<Long> configUserList = getFnPlatShareConfigMapper().selectMemberByPlatIdAndYearAndMonth(platId, year, month).stream().map(c -> c.getId()).collect(Collectors.toList());
        List<FnPlatShareMemberVo> noConfigMemberList = getPlatShareMembers(platId).stream()
                .filter(m -> (m != null && !configUserList.contains(m.getUserId())))
                .collect(Collectors.toList());
        for (FnPlatShareMemberVo temp : noConfigMemberList) {
            UserLoginEmailVo tempLoginVo = new UserLoginEmailVo();
            BeanUtils.copyProperties(temp, tempLoginVo);
            tempLoginVo.setName(temp.getUserName());
            tempLoginVo.setId(temp.getUserId());
            result.add(tempLoginVo);
        }

        // 已经填写的
        List<UserLoginEmailVo> configMemberList = getFnPlatShareConfigMapper().selectMemberByPlatIdAndYearAndMonth(platId, year, month);
        result.addAll(configMemberList);

        return result;
    }

}
