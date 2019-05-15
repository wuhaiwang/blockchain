package com.seasun.management.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ApDanmaku {

	private Long id;
	
	private Integer year;
	
	private Long userId;
	
	private String userName;
	
	private String danmaku;
	
	private Integer showFlag;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
    private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Integer getYear() {
		return year;
	}
	
	public void setYear(Integer year) {
		this.year = year;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDanmaku() {
		return danmaku;
	}

	public void setDanmaku(String danmaku) {
		this.danmaku = danmaku;
	}
	
	public Integer getShowFlag() {
		return showFlag;
	}
	
	public void setShowFlag(Integer showFlag) {
		this.showFlag = showFlag;
	}
	
	public Date getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public Date getUpdateTime() {
		return updateTime;
	}
	
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
