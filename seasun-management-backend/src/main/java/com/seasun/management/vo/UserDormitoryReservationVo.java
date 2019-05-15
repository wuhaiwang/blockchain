package com.seasun.management.vo;

import com.seasun.management.model.UserDormitoryReservation;

public class UserDormitoryReservationVo extends UserDormitoryReservation {

    private String checkInDate;

    private String checkOutDate;

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
}
