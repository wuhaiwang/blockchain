package com.seasun.management.dto.dataCenter;


import java.util.List;

public class DividedDataByMonthDate {

    private List<VFactJx3StatDDto> previousMonthData;
    private List<VFactJx3StatDDto> currentMonthData;
    private VFactJx3StatDDto previousData;
    private VFactJx3StatDDto beforeYesterday;

    public List<VFactJx3StatDDto> getPreviousMonthData() {
        return previousMonthData;
    }

    public void setPreviousMonthData(List<VFactJx3StatDDto> previousMonthData) {
        this.previousMonthData = previousMonthData;
    }

    public List<VFactJx3StatDDto> getCurrentMonthData() {
        return currentMonthData;
    }

    public void setCurrentMonthData(List<VFactJx3StatDDto> currentMonthData) {
        this.currentMonthData = currentMonthData;
    }

    public VFactJx3StatDDto getPreviousData() {
        return previousData;
    }

    public void setPreviousData(VFactJx3StatDDto previousData) {
        this.previousData = previousData;
    }

    public VFactJx3StatDDto getBeforeYesterday() {
        return beforeYesterday;
    }

    public void setBeforeYesterday(VFactJx3StatDDto beforeYesterday) {
        this.beforeYesterday = beforeYesterday;
    }
}