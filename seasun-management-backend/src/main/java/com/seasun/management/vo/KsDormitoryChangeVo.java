package com.seasun.management.vo;

public class KsDormitoryChangeVo {
    private Long id;
    private String name;
    private String changeType;
    private String mobile;
    private String houseRoomProperty;
    private String houseOwner;
    private String fromExpectChenInDate;
    private String fromExpectChenOutDate;
    private String toExpectChenInDate;
    private String toExpectChenOutDate;
    private String reason;
    private String operator;
    private String formatCreateTime;
    private String liveNo;//订单
    private String fromRoomNo;//原房号
    private String fromCardNo;//卡号
    private String fromBedroom;//房间类型
    private String fromBerth;//上下铺
    private String toRoomNo;//新房号
    private String toCardNo;//卡号
    private String toBedroom;//上下铺
    private String toBerth;//床位

    public interface ChangeType {
        // 换房
        String CHANGE_ROOM = "CHANGE_ROOM";
        // 续租
        String RELET = "RELET";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getHouseRoomProperty() {
        return houseRoomProperty;
    }

    public void setHouseRoomProperty(String houseRoomProperty) {
        this.houseRoomProperty = houseRoomProperty;
    }

    public String getHouseOwner() {
        return houseOwner;
    }

    public void setHouseOwner(String houseOwner) {
        this.houseOwner = houseOwner;
    }

    public String getFromExpectChenInDate() {
        return fromExpectChenInDate;
    }

    public void setFromExpectChenInDate(String fromExpectChenInDate) {
        this.fromExpectChenInDate = fromExpectChenInDate;
    }

    public String getFromExpectChenOutDate() {
        return fromExpectChenOutDate;
    }

    public void setFromExpectChenOutDate(String fromExpectChenOutDate) {
        this.fromExpectChenOutDate = fromExpectChenOutDate;
    }

    public String getToExpectChenInDate() {
        return toExpectChenInDate;
    }

    public void setToExpectChenInDate(String toExpectChenInDate) {
        this.toExpectChenInDate = toExpectChenInDate;
    }

    public String getToExpectChenOutDate() {
        return toExpectChenOutDate;
    }

    public void setToExpectChenOutDate(String toExpectChenOutDate) {
        this.toExpectChenOutDate = toExpectChenOutDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getFormatCreateTime() {
        return formatCreateTime;
    }

    public void setFormatCreateTime(String formatCreateTime) {
        this.formatCreateTime = formatCreateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLiveNo() {
        return liveNo;
    }

    public void setLiveNo(String liveNo) {
        this.liveNo = liveNo;
    }

    public String getFromRoomNo() {
        return fromRoomNo;
    }

    public void setFromRoomNo(String fromRoomNo) {
        this.fromRoomNo = fromRoomNo;
    }

    public String getFromCardNo() {
        return fromCardNo;
    }

    public void setFromCardNo(String fromCardNo) {
        this.fromCardNo = fromCardNo;
    }

    public String getFromBedroom() {
        return fromBedroom;
    }

    public void setFromBedroom(String fromBedroom) {
        this.fromBedroom = fromBedroom;
    }

    public String getFromBerth() {
        return fromBerth;
    }

    public void setFromBerth(String fromBerth) {
        this.fromBerth = fromBerth;
    }

    public String getToRoomNo() {
        return toRoomNo;
    }

    public void setToRoomNo(String toRoomNo) {
        this.toRoomNo = toRoomNo;
    }

    public String getToCardNo() {
        return toCardNo;
    }

    public void setToCardNo(String toCardNo) {
        this.toCardNo = toCardNo;
    }

    public String getToBedroom() {
        return toBedroom;
    }

    public void setToBedroom(String toBedroom) {
        this.toBedroom = toBedroom;
    }

    public String getToBerth() {
        return toBerth;
    }

    public void setToBerth(String toBerth) {
        this.toBerth = toBerth;
    }
}
