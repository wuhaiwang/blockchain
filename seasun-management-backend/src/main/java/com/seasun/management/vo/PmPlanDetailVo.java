package com.seasun.management.vo;

import com.seasun.management.dto.PmPlanDetailDto;

import java.util.List;

public class PmPlanDetailVo {

    private String time;

    List<PmPlanDetailDto> plans;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<PmPlanDetailDto> getPlans() {
        return plans;
    }

    public void setPlans(List<PmPlanDetailDto> plans) {
        this.plans = plans;
    }

}
