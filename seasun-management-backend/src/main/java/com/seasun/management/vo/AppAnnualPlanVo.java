package com.seasun.management.vo;

import java.util.Date;
import java.util.List;

public class AppAnnualPlanVo {

    private List<AnnualPlan> annualPlans;

    private Date lastUpdateTime;

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public List<AnnualPlan> getAnnualPlans() {
        return annualPlans;
    }

    public void setAnnualPlans(List<AnnualPlan> annualPlans) {
        this.annualPlans = annualPlans;
    }

    public static class AnnualPlan {
        private List<PMilestoneDto> planList;
        private String milestoneDay;

        public List<PMilestoneDto> getPlanList() {
            return planList;
        }

        public void setPlanList(List<PMilestoneDto> planList) {
            this.planList = planList;
        }

        public String getMilestoneDay() {
            return milestoneDay;
        }

        public void setMilestoneDay(String milestoneDay) {
            this.milestoneDay = milestoneDay;
        }
    }

}
