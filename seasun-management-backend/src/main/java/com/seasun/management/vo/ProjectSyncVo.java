package com.seasun.management.vo;

import com.seasun.management.model.Project;


public class ProjectSyncVo extends BaseSyncVo {
    private ProjectInfo data;

    public ProjectInfo getData() {
        return data;
    }

    public void setData(ProjectInfo data) {
        this.data = data;
    }

    public static class ProjectInfo extends Project {
        private String orderCenterCode;

        public String getOrderCenterCode() {
            return orderCenterCode;
        }

        public void setOrderCenterCode(String orderCenterCode) {
            this.orderCenterCode = orderCenterCode;
        }
    }
}
