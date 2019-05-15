package com.seasun.management.vo;

import java.util.Date;
import java.util.List;

public class WebAnnualPlanVo {

    private List<PMilestoneDto> plans;
    private Integer beginYear;
    private Integer endYear;
    private Boolean isPublished;
    private Boolean hasPublishPerm;
    private Integer year;
    private Date lastUpdateTime;

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public List<PMilestoneDto> getPlans() {
        return plans;
    }

    public void setPlans(List<PMilestoneDto> plans) {
        this.plans = plans;
    }

    public Integer getBeginYear() {
        return beginYear;
    }

    public void setBeginYear(Integer beginYear) {
        this.beginYear = beginYear;
    }

    public Integer getEndYear() {
        return endYear;
    }

    public void setEndYear(Integer endYear) {
        this.endYear = endYear;
    }

    public Boolean getPublished() {
        return isPublished;
    }

    public void setPublished(Boolean published) {
        isPublished = published;
    }

    public Boolean getHasPublishPerm() {
        return hasPublishPerm;
    }

    public void setHasPublishPerm(Boolean hasPublishPerm) {
        this.hasPublishPerm = hasPublishPerm;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
