package com.seasun.management.vo;

import com.seasun.management.model.Department;

public class DepartmentSyncVo extends BaseSyncVo {

    private DepartmentInfo data;

    public DepartmentInfo getData() {
        return data;
    }

    public void setData(DepartmentInfo data) {
        this.data = data;
    }

    public static class DepartmentInfo extends Department {

        private String costCenterCode;

        private Long assistantId;

        public String getCostCenterCode() {
            return costCenterCode;
        }

        public void setCostCenterCode(String costCenterCode) {
            this.costCenterCode = costCenterCode;
        }

        public Long getAssistantId() {
            return assistantId;
        }

        public void setAssistantId(Long assistantId) {
            this.assistantId = assistantId;
        }
    }
}
