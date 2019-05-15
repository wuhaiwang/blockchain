package com.seasun.management.dto;

import com.seasun.management.model.User;

import java.util.Date;

public class UserDto extends User implements Cloneable {

    private Date birthday;

    private Date leaveDate;

    private String leaveType;

    private String orderCenterCode;

    private String workStatus;

    private String location;

    private String workGroupName;

    public String getWorkGroupName() {
        return workGroupName;
    }

    public void setWorkGroupName(String workGroupName) {
        this.workGroupName = workGroupName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(Date leaveDate) {
        this.leaveDate = leaveDate;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getOrderCenterCode() {
        return orderCenterCode;
    }

    public void setOrderCenterCode(String orderCenterCode) {
        this.orderCenterCode = orderCenterCode;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public UserDto clone() {
        UserDto dto = null;
        try {
            dto = (UserDto) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return dto;
    }
}
