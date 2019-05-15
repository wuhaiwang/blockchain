package com.seasun.management.vo;

import com.seasun.management.model.FmGroupConfirmInfo;

import java.util.List;

public class FmGroupConfirmInfoVo extends FmGroupConfirmInfo {
    private String performanceWorkGroupName;

    private String platName;

    private String projectName;

    private String platConfirmerName;

    private String projectConfirmerName;

    private Integer total;

    private Integer sort;

    private List<MemberPerformanceAppVo> memberPerformances;

    public String getPerformanceWorkGroupName() {
        return performanceWorkGroupName;
    }

    public void setPerformanceWorkGroupName(String performanceWorkGroupName) {
        this.performanceWorkGroupName = performanceWorkGroupName;
    }

    public String getPlatName() {
        return platName;
    }

    public void setPlatName(String platName) {
        this.platName = platName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPlatConfirmerName() {
        return platConfirmerName;
    }

    public void setPlatConfirmerName(String platConfirmerName) {
        this.platConfirmerName = platConfirmerName;
    }

    public String getProjectConfirmerName() {
        return projectConfirmerName;
    }

    public void setProjectConfirmerName(String projectConfirmerName) {
        this.projectConfirmerName = projectConfirmerName;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public List<MemberPerformanceAppVo> getMemberPerformances() {
        return memberPerformances;
    }

    public void setMemberPerformances(List<MemberPerformanceAppVo> memberPerformances) {
        this.memberPerformances = memberPerformances;
    }
}
