package com.seasun.management.service.impl;

import java.util.Date;
import java.util.List;
import com.seasun.management.mapper.CfgSystemParamMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.seasun.management.constant.ApConstant;
import com.seasun.management.helper.UserMessageHelper;
import com.seasun.management.mapper.ApDanmakuMapper;
import com.seasun.management.mapper.ApDrawMapper;
import com.seasun.management.mapper.UserMapper;
import com.seasun.management.model.ApDanmaku;
import com.seasun.management.model.ApDraw;
import com.seasun.management.model.User;
import com.seasun.management.model.UserMessage;
import com.seasun.management.service.ApService;
import com.seasun.management.service.UserMessageService;

@Service
public class ApServiceImpl implements ApService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ApDrawMapper apDrawMapper;

    @Autowired
    private ApDanmakuMapper apDanmakuMapper;

    @Autowired
    private UserMessageService userMessageService;

    @Autowired
    private CfgSystemParamMapper cfgSystemParamMapper;

    public void addApDraw(ApDraw apDraw) {
        User user = userMapper.selectUserByEmployeeNo(apDraw.getEmployeeNo());
        apDraw.setYear(ApConstant.YEAR);
        apDraw.setUserId(user.getId());
        apDraw.setCode(RandomStringUtils.randomAlphanumeric(20));
        apDraw.setCreateTime(new Date());
        apDraw.setUpdateTime(new Date());
        apDrawMapper.insert(apDraw);

        UserMessage userMessage = UserMessageHelper.getApDrawMessage(apDraw);
        userMessageService.add(userMessage);
    }

    @Override
    public List<ApDraw> getApDrawListByUserId(Long userId) {
        return apDrawMapper.selectByUserId(userId);
    }

    @Override
    public ApDraw getApDrawByCode(String code) {
        return apDrawMapper.selectByCode(code);
    }

    @Override
    public void handleApDanmaku(Long userId, String content) {
        if (userId == null || StringUtils.isEmpty(content) || content.length() > 200) {
            return;
        }

        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return;
        }

        ApDanmaku apDanmaku = new ApDanmaku();
        apDanmaku.setYear(ApConstant.YEAR);
        apDanmaku.setUserId(userId);
        apDanmaku.setUserName(user.getName());
        apDanmaku.setDanmaku(content);
        apDanmaku.setShowFlag(0);
        apDanmaku.setCreateTime(new Date());
        apDanmaku.setUpdateTime(new Date());
        apDanmakuMapper.insert(apDanmaku);
    }

    @Override
    public List<ApDanmaku> getApDanmaku() {
        List<ApDanmaku> danmakuList = apDanmakuMapper.selectNotShowed();
        apDanmakuMapper.updateShowed();
        return danmakuList;
    }

    @Override
    public Boolean getAnnualPartyStartFlag() {
        return cfgSystemParamMapper.selectAnnualPartyStartFlag();
    }
}
