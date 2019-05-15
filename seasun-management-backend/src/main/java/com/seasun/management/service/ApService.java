package com.seasun.management.service;

import java.util.List;

import com.seasun.management.model.ApDanmaku;
import com.seasun.management.model.ApDraw;

public interface ApService {
	
	void addApDraw(ApDraw apDraw);

	List<ApDraw> getApDrawListByUserId(Long userId);

	ApDraw getApDrawByCode(String code);
	
	void handleApDanmaku(Long userId, String content);

	List<ApDanmaku> getApDanmaku();

	Boolean getAnnualPartyStartFlag();
}
