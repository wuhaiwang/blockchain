package com.seasun.management.vo;

import com.seasun.management.model.Project;

import java.util.List;

public class PmPlanDetailAppVo extends Project {

    List<PmPlanDetailVo> planDetail;

    public List<PmPlanDetailVo> getPlanDetail() {
        return planDetail;
    }

    public void setPlanDetail(List<PmPlanDetailVo> planDetail) {
        this.planDetail = planDetail;
    }
}
