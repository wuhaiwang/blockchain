package com.seasun.management.vo;

import java.util.List;

public class KsDormitoryVo{
    private Long pageSize = 20L;
    private Long currentPage;
    private Integer total;
    private List<KsDormitoryVo> modelSet;
    private Long id;
    private String houseOwner;
    private Long houseBuildingId;
    private String houseBuildingCode;
    private String floor;
    private String roomNo;
    private String houseRoomProperty;
    private String houseRoomState;
    private int bedroomCount;
    private int livedCount;
    private int berthCount;
    private String networkAccount;
    private String waterAccount;
    private String electricAccount;
    private boolean full;
    private int maxLiveCount;
    private String roomPropertyDesc;
    private String houseOwnerDesc;
    private String date;
    private String leftAvailableCount;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHouseOwner() {
        return houseOwner;
    }

    public void setHouseOwner(String houseOwner) {
        this.houseOwner = houseOwner;
    }

    public Long getHouseBuildingId() {
        return houseBuildingId;
    }

    public void setHouseBuildingId(Long houseBuildingId) {
        this.houseBuildingId = houseBuildingId;
    }

    public String getHouseBuildingCode() {
        return houseBuildingCode;
    }

    public void setHouseBuildingCode(String houseBuildingCode) {
        this.houseBuildingCode = houseBuildingCode;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getHouseRoomProperty() {
        return houseRoomProperty;
    }

    public void setHouseRoomProperty(String houseRoomProperty) {
        this.houseRoomProperty = houseRoomProperty;
    }

    public String getHouseRoomState() {
        return houseRoomState;
    }

    public void setHouseRoomState(String houseRoomState) {
        this.houseRoomState = houseRoomState;
    }

    public int getBedroomCount() {
        return bedroomCount;
    }

    public void setBedroomCount(int bedroomCount) {
        this.bedroomCount = bedroomCount;
    }

    public int getLivedCount() {
        return livedCount;
    }

    public void setLivedCount(int livedCount) {
        this.livedCount = livedCount;
    }

    public int getBerthCount() {
        return berthCount;
    }

    public void setBerthCount(int berthCount) {
        this.berthCount = berthCount;
    }

    public String getNetworkAccount() {
        return networkAccount;
    }

    public void setNetworkAccount(String networkAccount) {
        this.networkAccount = networkAccount;
    }

    public String getWaterAccount() {
        return waterAccount;
    }

    public void setWaterAccount(String waterAccount) {
        this.waterAccount = waterAccount;
    }

    public String getElectricAccount() {
        return electricAccount;
    }

    public void setElectricAccount(String electricAccount) {
        this.electricAccount = electricAccount;
    }

    public boolean isFull() {
        return full;
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

    public void setFull(boolean full) {
        this.full = full;
    }

    public int getMaxLiveCount() {
        return maxLiveCount;
    }

    public void setMaxLiveCount(int maxLiveCount) {
        this.maxLiveCount = maxLiveCount;
    }

    public String getRoomPropertyDesc() {
        return roomPropertyDesc;
    }

    public void setRoomPropertyDesc(String roomPropertyDesc) {
        this.roomPropertyDesc = roomPropertyDesc;
    }

    public String getHouseOwnerDesc() {
        return houseOwnerDesc;
    }

    public void setHouseOwnerDesc(String houseOwnerDesc) {
        this.houseOwnerDesc = houseOwnerDesc;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<KsDormitoryVo> getModelSet() {
        return modelSet;
    }

    public void setModelSet(List<KsDormitoryVo> modelSet) {
        this.modelSet = modelSet;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLeftAvailableCount() {
        return leftAvailableCount;
    }

    public void setLeftAvailableCount(String leftAvailableCount) {
        this.leftAvailableCount = leftAvailableCount;
    }
}
