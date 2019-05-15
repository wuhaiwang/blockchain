package com.seasun.management.dto;

import java.util.List;

public class PerformanceSubGroupInfoDto {

    private Long groupId;

    private String groupName;

    private Integer total;

    private String manager;

    private List<UserPerformanceDto> userPerformanceDtos;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public List<UserPerformanceDto> getUserPerformanceDtos() {
        return userPerformanceDtos;
    }

    public void setUserPerformanceDtos(List<UserPerformanceDto> userPerformanceDtos) {
        this.userPerformanceDtos = userPerformanceDtos;
    }
}
