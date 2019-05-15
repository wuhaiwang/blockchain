package com.seasun.management.dto;

public class UserDormitoryReportDto{
    /**辅助查询使用*/
    private Long companyId;//公司ID
    private Long projectId;//项目ID
    private String bedroomType;//房间类型
    private String berthType;//上下铺(公寓为空)
    private Integer days;//入住人天数

    /**报表导出使用*/
    private String companyCode;//公司编码,报表导出使用
    private String projectName;//项目,报表导出使用

    private Integer allDormitoryRoom;//员工宿舍入住人天数
    private String allDormitoryPercent;//员工宿舍占比

    private Integer allApartmentRoom;//公寓总人天数

    private Integer aRoom;//公寓主卧人天数
    private String aRoomPercent;//公寓主卧占比

    private Integer bRoom;//公寓次卧人天数
    private String bRoomPercent;//公寓次卧占比

    private Integer cRoom;//公寓第三间房人天数
    private String cRoomPercent;//公寓第三间房占比

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getBedroomType() {
        return bedroomType;
    }

    public void setBedroomType(String bedroomType) {
        this.bedroomType = bedroomType;
    }

    public String getBerthType() {
        return berthType;
    }

    public void setBerthType(String berthType) {
        this.berthType = berthType;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getAllDormitoryRoom() {
        return allDormitoryRoom;
    }

    public void setAllDormitoryRoom(Integer allDormitoryRoom) {
        this.allDormitoryRoom = allDormitoryRoom;
    }

    public String getAllDormitoryPercent() {
        return allDormitoryPercent;
    }

    public void setAllDormitoryPercent(String allDormitoryPercent) {
        this.allDormitoryPercent = allDormitoryPercent;
    }

    public Integer getAllApartmentRoom() {
        return allApartmentRoom;
    }

    public void setAllApartmentRoom(Integer allApartmentRoom) {
        this.allApartmentRoom = allApartmentRoom;
    }

    public Integer getaRoom() {
        return aRoom;
    }

    public void setaRoom(Integer aRoom) {
        this.aRoom = aRoom;
    }

    public String getaRoomPercent() {
        return aRoomPercent;
    }

    public void setaRoomPercent(String aRoomPercent) {
        this.aRoomPercent = aRoomPercent;
    }

    public Integer getbRoom() {
        return bRoom;
    }

    public void setbRoom(Integer bRoom) {
        this.bRoom = bRoom;
    }

    public String getbRoomPercent() {
        return bRoomPercent;
    }

    public void setbRoomPercent(String bRoomPercent) {
        this.bRoomPercent = bRoomPercent;
    }

    public Integer getcRoom() {
        return cRoom;
    }

    public void setcRoom(Integer cRoom) {
        this.cRoom = cRoom;
    }

    public String getcRoomPercent() {
        return cRoomPercent;
    }

    public void setcRoomPercent(String cRoomPercent) {
        this.cRoomPercent = cRoomPercent;
    }
}
