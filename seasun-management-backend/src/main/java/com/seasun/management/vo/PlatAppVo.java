package com.seasun.management.vo;

public class PlatAppVo {

    private Integer year;

    private Integer month;

    private Long id;

    private String name;

    private Integer memberNumber;

    private Integer serveNumber;

    private Float cost;

    private Float lastMonthCost;

    private Integer maxMember;

    private Long workGroupId;

    private Boolean hasSumShareData;

    private String city;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(Integer memberNumber) {
        this.memberNumber = memberNumber;
    }

    public Integer getServeNumber() {
        return serveNumber;
    }

    public void setServeNumber(Integer serveNumber) {
        this.serveNumber = serveNumber;
    }

    public Float getCost() {
        return cost;
    }

    public void setCost(Float cost) {
        this.cost = cost;
    }

    public Float getLastMonthCost() {
        return lastMonthCost;
    }

    public void setLastMonthCost(Float lastMonthCost) {
        this.lastMonthCost = lastMonthCost;
    }

    public Integer getMaxMember() {
        return maxMember;
    }

    public void setMaxMember(Integer maxMember) {
        this.maxMember = maxMember;
    }

    public Long getWorkGroupId() {
        return workGroupId;
    }

    public void setWorkGroupId(Long workGroupId) {
        this.workGroupId = workGroupId;
    }

    public Boolean getHasSumShareData() {
        return hasSumShareData;
    }

    public void setHasSumShareData(Boolean hasSumShareData) {
        this.hasSumShareData = hasSumShareData;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
