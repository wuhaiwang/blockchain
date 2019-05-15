package com.seasun.management.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seasun.management.model.PmMilestone;

import java.util.Date;

public class PMilestoneDto extends PmMilestone{

    private String projectName;
    private String managerName;
    private String type;
    private String channel;
    private String risk;
    private Date milestoneDay;
    private Date projectEstimateDay;

    private Boolean publishFlag;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public Date getMilestoneDay() {
        return milestoneDay;
    }

    public void setMilestoneDay(Date milestoneDay) {
        this.milestoneDay = milestoneDay;
    }

    public Boolean getPublishFlag() {
        return publishFlag;
    }

    public void setPublishFlag(Boolean publishFlag) {
        this.publishFlag = publishFlag;
    }

    public Date getProjectEstimateDay() {
        return projectEstimateDay;
    }

    public void setProjectEstimateDay(Date projectEstimateDay) {
        this.projectEstimateDay = projectEstimateDay;
    }
}
