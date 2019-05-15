package com.seasun.management.vo;

import java.util.ArrayList;
import java.util.List;

public class KsDormitoryReserveVo {
    private Integer row;
    private Long pageSize = 20L;
    private Long currentPage;
    private Integer total;
    private List<KsDormitoryReserveVo> modelSet;
    private String id;
    private String name;
    private String mobile;
    private String idCode;
    private String expectCheckInDate;
    private String expectCheckOutDate;
    private String checkInDate;
    private String checkOutDate;
    private String roomNo;
    private String org;
    private String liveState;
    private String bedroomDesc;
    private String berthDesc;
    private String liveStateDesc;
    // 房间详情
    private Boolean queryRealTimeLiveByDate;

    private String checkInDateFrom;
    private String checkInDateTo;
    private String checkOutDateFrom;
    private String checkOutDateTo;
    private String bedroom;
    private String berth;
    private Long userId;
    private Integer gender;

    private String bookBy;
    private String liveNo;
    private Integer type;
    private Long projectId;
    private Long companyId;
    private String projectName;
    private String companyName;
    private Boolean payFlag;
    private Boolean hasChanged;
    private List<KsDormitoryChangeVo> changeLogList;
    private String certificateType;//证件类型

    private String expectCheckInDateFrom;
    private String expectCheckInDateTo;
    private String expectCheckOutDateFrom;
    private String expectCheckOutDateTo;
    private String liveDateFrom;//实际入住时长时间段查询
    private String liveDateTo;//实际入住时长时间段查询
    public static List<String> bedrooms = new ArrayList<String>() {{
        add("A房");
        add("B房");
        add("C房");
        add("主卧");
        add("次卧");
        add("第三房");
    }};
    public static List<String> dormitory = new ArrayList<String>() {{
        add("A房");
        add("B房");
        add("C房");
    }};

    public static List<String> certification = new ArrayList<String>() {{
        add("身份证");
        add("护照");
    }};

    public interface KsDormitoryExcelInfo {
        String type1 = "实习生";
        String type2 = "在职员工";
        String type3 = "外部招待";
        String bedroom1 = "A房";
        String bedroom2 = "B房";
        String bedroom3 = "C房";
        String bedroom4 = "主卧";
        String bedroom5 = "次卧";
        String bedroom6 = "第三间房";
        String berth1 = "上铺";
        String berth2 = "下铺";
    }

    public KsDormitoryReserveVo() {
    }

    public KsDormitoryReserveVo(Integer row, String roomNo, String bedroom, String berth, int type, String name, int gender, String mobile, String certificateType, String idCode, String expectCheckInDate, String expectCheckOutDate) {
        this.row = row;
        this.roomNo = roomNo;
        this.bedroom = bedroom;
        this.berth = berth;
        this.type = type;
        this.name = name;
        this.gender = gender;
        this.mobile = mobile;
        this.certificateType = certificateType;
        this.idCode = idCode;
        this.expectCheckInDate = expectCheckInDate;
        this.expectCheckOutDate = expectCheckOutDate;
    }

    public Boolean getQueryRealTimeLiveByDate() {
        return queryRealTimeLiveByDate;
    }

    public void setQueryRealTimeLiveByDate(Boolean queryRealTimeLiveByDate) {
        this.queryRealTimeLiveByDate = queryRealTimeLiveByDate;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Boolean getPayFlag() {
        return payFlag;
    }

    public void setPayFlag(Boolean payFlag) {
        this.payFlag = payFlag;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public String getExpectCheckInDate() {
        return expectCheckInDate;
    }

    public void setExpectCheckInDate(String expectCheckInDate) {
        this.expectCheckInDate = expectCheckInDate;
    }

    public String getExpectCheckOutDate() {
        return expectCheckOutDate;
    }

    public void setExpectCheckOutDate(String expectCheckOutDate) {
        this.expectCheckOutDate = expectCheckOutDate;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getLiveState() {
        return liveState;
    }

    public void setLiveState(String liveState) {
        this.liveState = liveState;
    }

    public String getBedroomDesc() {
        return bedroomDesc;
    }

    public void setBedroomDesc(String bedroomDesc) {
        this.bedroomDesc = bedroomDesc;
    }

    public String getBerthDesc() {
        return berthDesc;
    }

    public void setBerthDesc(String berthDesc) {
        this.berthDesc = berthDesc;
    }

    public String getLiveStateDesc() {
        return liveStateDesc;
    }

    public void setLiveStateDesc(String liveStateDesc) {
        this.liveStateDesc = liveStateDesc;
    }

    public String getCheckInDateFrom() {
        return checkInDateFrom;
    }

    public void setCheckInDateFrom(String checkInDateFrom) {
        this.checkInDateFrom = checkInDateFrom;
    }

    public String getCheckInDateTo() {
        return checkInDateTo;
    }

    public void setCheckInDateTo(String checkInDateTo) {
        this.checkInDateTo = checkInDateTo;
    }

    public String getCheckOutDateFrom() {
        return checkOutDateFrom;
    }

    public void setCheckOutDateFrom(String checkOutDateFrom) {
        this.checkOutDateFrom = checkOutDateFrom;
    }

    public String getCheckOutDateTo() {
        return checkOutDateTo;
    }

    public void setCheckOutDateTo(String checkOutDateTo) {
        this.checkOutDateTo = checkOutDateTo;
    }

    public String getBedroom() {
        return bedroom;
    }

    public void setBedroom(String bedroom) {
        this.bedroom = bedroom;
    }

    public String getBerth() {
        return berth;
    }

    public void setBerth(String berth) {
        this.berth = berth;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Long currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<KsDormitoryReserveVo> getModelSet() {
        return modelSet;
    }

    public void setModelSet(List<KsDormitoryReserveVo> modelSet) {
        this.modelSet = modelSet;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getBookBy() {
        return bookBy;
    }

    public void setBookBy(String bookBy) {
        this.bookBy = bookBy;
    }

    public String getLiveNo() {
        return liveNo;
    }

    public void setLiveNo(String liveNo) {
        this.liveNo = liveNo;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Boolean getHasChanged() {
        return hasChanged;
    }

    public void setHasChanged(Boolean hasChanged) {
        this.hasChanged = hasChanged;
    }

    public List<KsDormitoryChangeVo> getChangeLogList() {
        return changeLogList;
    }

    public void setChangeLogList(List<KsDormitoryChangeVo> changeLogList) {
        this.changeLogList = changeLogList;
    }

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }

    public String getExpectCheckInDateFrom() {
        return expectCheckInDateFrom;
    }

    public void setExpectCheckInDateFrom(String expectCheckInDateFrom) {
        this.expectCheckInDateFrom = expectCheckInDateFrom;
    }

    public String getExpectCheckInDateTo() {
        return expectCheckInDateTo;
    }

    public void setExpectCheckInDateTo(String expectCheckInDateTo) {
        this.expectCheckInDateTo = expectCheckInDateTo;
    }

    public String getExpectCheckOutDateFrom() {
        return expectCheckOutDateFrom;
    }

    public void setExpectCheckOutDateFrom(String expectCheckOutDateFrom) {
        this.expectCheckOutDateFrom = expectCheckOutDateFrom;
    }

    public String getExpectCheckOutDateTo() {
        return expectCheckOutDateTo;
    }

    public void setExpectCheckOutDateTo(String expectCheckOutDateTo) {
        this.expectCheckOutDateTo = expectCheckOutDateTo;
    }

    public String getLiveDateFrom() {
        return liveDateFrom;
    }

    public void setLiveDateFrom(String liveDateFrom) {
        this.liveDateFrom = liveDateFrom;
    }

    public String getLiveDateTo() {
        return liveDateTo;
    }

    public void setLiveDateTo(String liveDateTo) {
        this.liveDateTo = liveDateTo;
    }

    public static List<String> getBedrooms() {
        return bedrooms;
    }

    public static void setBedrooms(List<String> bedrooms) {
        KsDormitoryReserveVo.bedrooms = bedrooms;
    }

    public static List<String> getDormitory() {
        return dormitory;
    }

    public static void setDormitory(List<String> dormitory) {
        KsDormitoryReserveVo.dormitory = dormitory;
    }

    public static List<String> getCertification() {
        return certification;
    }

    public static void setCertification(List<String> certification) {
        KsDormitoryReserveVo.certification = certification;
    }
}
