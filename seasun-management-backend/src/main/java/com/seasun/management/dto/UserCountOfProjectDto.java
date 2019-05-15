package com.seasun.management.dto;

import java.util.List;

/**
 * Created by hedahai on 2017/7/27.
 */
public class UserCountOfProjectDto {
    private Long projectId;
    private List<UserCountByYearMonth> userCount;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<UserCountByYearMonth> getUserCount() {
        return userCount;
    }

    public void setUserCount(List<UserCountByYearMonth> userCount) {
        this.userCount = userCount;
    }

    public static class UserCountByYearMonth {
        private Integer year;
        private Integer month;
        private Integer userCount;

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public Integer getMonth() {
            return month;
        }

        public void setMonth(Integer month) {
            this.month = month;
        }

        public Integer getUserCount() {
            return userCount;
        }

        public void setUserCount(Integer userCount) {
            this.userCount = userCount;
        }
    }
}
