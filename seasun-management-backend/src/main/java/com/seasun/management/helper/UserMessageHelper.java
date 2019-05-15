package com.seasun.management.helper;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.constant.ApConstant;
import com.seasun.management.model.ApDraw;
import com.seasun.management.model.UserMessage;
import com.seasun.management.vo.FmGroupConfirmInfoVo;

public class UserMessageHelper {

    public static UserMessage getSubmitFixMemberUserMessage(Long receiverId, String content, int year, int month) {
        UserMessage userMessage = initFixMemberUserMessage(receiverId, content);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("year", year);
        jsonObject.put("month", month);
        jsonObject.put("type", "project");
        userMessage.setParams(jsonObject.toJSONString());
        return userMessage;
    }

    public static UserMessage getConfirmFixMemberUserMessage(Long receiverId, FmGroupConfirmInfoVo fmGroupConfirmInfo, String content) {
        UserMessage userMessage = initFixMemberUserMessage(receiverId, content);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", fmGroupConfirmInfo.getId());
        jsonObject.put("year", fmGroupConfirmInfo.getYear());
        jsonObject.put("month", fmGroupConfirmInfo.getMonth());
        jsonObject.put("type", "plat");
        userMessage.setParams(jsonObject.toJSONString());
        return userMessage;
    }

    private static UserMessage initFixMemberUserMessage(Long receiverId, String content) {
        UserMessage userMessage = new UserMessage();
        userMessage.setType(UserMessage.Type.system);
        userMessage.setLocation(UserMessage.Location.performanceFixMember);
        userMessage.setReceiver(receiverId);
        userMessage.setContent(content);
        return userMessage;
    }
    
    public static UserMessage getApDrawMessage(ApDraw apDraw) {
    		String content = String.format(ApConstant.AP_DRAW_MESSAGE, apDraw.getYear(), apDraw.getAwardName());
    		UserMessage userMessage = initApDrawMessage(apDraw.getUserId(), content);
    		JSONObject jsonObject = new JSONObject();
    		jsonObject.put("code", apDraw.getCode());
    		jsonObject.put("awardName", apDraw.getAwardName());
    		userMessage.setParams(jsonObject.toJSONString());
    		return userMessage;
    }
    
    private static UserMessage initApDrawMessage(Long receiverId, String content) {
    		UserMessage userMessage = new UserMessage();
    		userMessage.setType(UserMessage.Type.system);
    		userMessage.setLocation(UserMessage.Location.anuualDraw);
    		userMessage.setReceiver(receiverId);
    		userMessage.setContent(content);
    		return userMessage;
    }
}
