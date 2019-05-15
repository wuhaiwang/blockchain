package com.seasun.management.vo.dataCenter;


import java.math.BigDecimal;

public class BossDailyVo {
    private String name;//指标名称
    private String dayMom;//日环比
    private String weekAn;//周同比
    private String monthAn;//月同比
    private String yearMom;//年环比
    private String indexValue;//指标值
    private Integer sort;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDayMom() {
        return dayMom;
    }

    public void setDayMom(String dayMom) {
        this.dayMom = dayMom;
    }

    public String getWeekAn() {
        return weekAn;
    }

    public void setWeekAn(String weekAn) {
        this.weekAn = weekAn;
    }

    public String getMonthAn() {
        return monthAn;
    }

    public void setMonthAn(String monthAn) {
        this.monthAn = monthAn;
    }

    public String getYearMom() {
        return yearMom;
    }

    public void setYearMom(String yearMom) {
        this.yearMom = yearMom;
    }

    public String getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(String indexValue) {
        this.indexValue = indexValue;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "BossDailyVo{" +
                "name='" + name + '\'' +
                ", dayMom=" + dayMom +
                ", weekAn=" + weekAn +
                ", monthAn=" + monthAn +
                ", yearMom=" + yearMom +
                ", indexValue=" + indexValue +
                ", sort=" + sort +
                '}';
    }
}
