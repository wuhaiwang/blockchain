package com.seasun.management.vo;

import java.math.BigDecimal;
import java.util.List;

public class FnPlatShareBatchVo {

    private Long platId;
    private int year;
    private int month;
    private List<ProjectShareInfo> values;

    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platId) {
        this.platId = platId;
    }

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

    public List<ProjectShareInfo> getValues() {
        return values;
    }

    public void setValues(List<ProjectShareInfo> values) {
        this.values = values;
    }

    public static class ProjectShareInfo {
        private Long projectId;
        private BigDecimal sharePro;

        public Long getProjectId() {
            return projectId;
        }

        public void setProjectId(Long projectId) {
            this.projectId = projectId;
        }

        public BigDecimal getSharePro() {
            return sharePro;
        }

        public void setSharePro(BigDecimal sharePro) {
            this.sharePro = sharePro;
        }
    }

}
