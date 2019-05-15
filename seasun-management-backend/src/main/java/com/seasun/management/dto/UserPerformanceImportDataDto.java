package com.seasun.management.dto;

import java.util.List;

public class UserPerformanceImportDataDto {
    private int year;

    private int month;

    private List<UserPerformanceDto> userPerformances;

    private List<String> errorList;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public List<UserPerformanceDto> getUserPerformances() {
        return userPerformances;
    }

    public void setUserPerformances(List<UserPerformanceDto> userPerformances) {
        this.userPerformances = userPerformances;
    }

    public List<String> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }
}
