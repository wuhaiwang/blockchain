package com.seasun.management.vo;

public class HrOverviewAppVo {

    private Integer year;

    private Integer month;

    private Long id;

    private String name;

    private Long totalPeople;

    private Long servingPeople;

    private Long leavingPeople;
    
    private Long totalIntern;

    private Float averageAge;

    private Integer maxMember;

    private Long workGroupId;

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

    public Long getTotalPeople() {
        return totalPeople;
    }

    public void setTotalPeople(Long totalPeople) {
        this.totalPeople = totalPeople;
    }

    public Long getServingPeople() {
        return servingPeople;
    }

    public void setServingPeople(Long servingPeople) {
        this.servingPeople = servingPeople;
    }

    public Long getLeavingPeople() {
        return leavingPeople;
    }

    public void setLeavingPeople(Long leavingPeople) {
        this.leavingPeople = leavingPeople;
    }
    
    public Long getTotalIntern() {
		return totalIntern;
	}
    
    public void setTotalIntern(Long totalIntern) {
		this.totalIntern = totalIntern;
	}

    public Float getAverageAge() {
        return averageAge;
    }

    public void setAverageAge(Float averageAge) {
        this.averageAge = averageAge;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
