package com.seasun.management.vo;

import com.seasun.management.dto.PmPlanDetailDto;
import com.seasun.management.model.PmAttachment;

import java.util.Date;
import java.util.List;

public class PmPlanInfoVo {

    private Long projectId;

    private String projectName;

    private String status;

    private int type;

    private Boolean pmManagerFlag;

    private List<OperateAnalysis> operateAnalysis;

    private List<PmPlanDetailDto> operatePlans;

    private List<StatusVo> projectList;

    private ProjectEstimateDay pmMilestone;

    private Date lastPublishedTime;

    private Long pmPlanId;

    public interface Status {
        String INITIALIZED = "未提交";
        String SUBMITTED = "已提交";
        String PUBLISHED = "已发布";
    }

    public Long getPmPlanId() {
        return pmPlanId;
    }

    public void setPmPlanId(Long pmPlanId) {
        this.pmPlanId = pmPlanId;
    }

    public Date getLastPublishedTime() {
        return lastPublishedTime;
    }

    public void setLastPublishedTime(Date lastPublishedTime) {
        this.lastPublishedTime = lastPublishedTime;
    }

    public ProjectEstimateDay getPmMilestone() {
        return pmMilestone;
    }

    public void setPmMilestone(ProjectEstimateDay pmMilestone) {
        this.pmMilestone = pmMilestone;
    }

    public List<PmPlanDetailDto> getOperatePlans() {
        return operatePlans;
    }

    public void setOperatePlans(List<PmPlanDetailDto> operatePlans) {
        this.operatePlans = operatePlans;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<OperateAnalysis> getOperateAnalysis() {
        return operateAnalysis;
    }

    public void setOperateAnalysis(List<OperateAnalysis> operateAnalysis) {
        this.operateAnalysis = operateAnalysis;
    }

    public Boolean getPmManagerFlag() {
        return pmManagerFlag;
    }

    public void setPmManagerFlag(Boolean pmManagerFlag) {
        this.pmManagerFlag = pmManagerFlag;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<StatusVo> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<StatusVo> projectList) {
        this.projectList = projectList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class ProjectEstimateDay {

        private Boolean confirmFlag;

        private Date projectEstimateDay;

        public Boolean getConfirmFlag() {
            return confirmFlag;
        }

        public void setConfirmFlag(Boolean confirmFlag) {
            this.confirmFlag = confirmFlag;
        }

        public Date getProjectEstimateDay() {
            return projectEstimateDay;
        }

        public void setProjectEstimateDay(Date projectEstimateDay) {
            this.projectEstimateDay = projectEstimateDay;
        }
    }

    public static class OperateAnalysis {

        private Long id;

        private int year;

        private int month;

        private MoMAnalysis income;

        private MoMAnalysis cost;

        private MoMAnalysis profit;

        private Boolean editFlag;

        public Boolean getEditFlag() {
            return editFlag;
        }

        public void setEditFlag(Boolean editFlag) {
            this.editFlag = editFlag;
        }

        private List<PmAttachment> attachments;

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

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public MoMAnalysis getIncome() {
            return income;
        }

        public void setIncome(MoMAnalysis income) {
            this.income = income;
        }

        public MoMAnalysis getCost() {
            return cost;
        }

        public void setCost(MoMAnalysis cost) {
            this.cost = cost;
        }

        public MoMAnalysis getProfit() {
            return profit;
        }

        public void setProfit(MoMAnalysis profit) {
            this.profit = profit;
        }

        public List<PmAttachment> getAttachments() {
            return attachments;
        }

        public void setAttachments(List<PmAttachment> attachments) {
            this.attachments = attachments;
        }

        public static class MoMAnalysis {

            private Float twoMonthAgoValue;

            private Float lastMonthAgoValue;

            private Float lastYearMonthVaule;

            private String moM;

            private String yoY;

            private String floatReason;

            public Float getLastYearMonthVaule() {
                return lastYearMonthVaule;
            }

            public void setLastYearMonthVaule(Float lastYearMonthVaule) {
                this.lastYearMonthVaule = lastYearMonthVaule;
            }

            public Float getTwoMonthAgoValue() {
                return twoMonthAgoValue;
            }

            public void setTwoMonthAgoValue(Float twoMonthAgoValue) {
                this.twoMonthAgoValue = twoMonthAgoValue;
            }

            public Float getLastMonthAgoValue() {
                return lastMonthAgoValue;
            }

            public void setLastMonthAgoValue(Float lastMonthAgoValue) {
                this.lastMonthAgoValue = lastMonthAgoValue;
            }

            public String getMoM() {
                return moM;
            }

            public void setMoM(String moM) {
                this.moM = moM;
            }

            public String getFloatReason() {
                return floatReason;
            }

            public void setFloatReason(String floatReason) {
                this.floatReason = floatReason;
            }

            public String getYoY() {
                return yoY;
            }

            public void setYoY(String yoY) {
                this.yoY = yoY;
            }
        }
    }


}
