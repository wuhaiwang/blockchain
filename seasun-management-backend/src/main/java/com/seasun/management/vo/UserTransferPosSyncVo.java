package com.seasun.management.vo;

import com.seasun.management.model.UserTransferPost;

public class UserTransferPosSyncVo extends BaseSyncVo {
    public UserTransferPosInfo data;

    public UserTransferPosInfo getData() {
        return data;
    }

    public void setData(UserTransferPosInfo data) {
        this.data = data;
    }

    public static class UserTransferPosInfo extends UserTransferPost {
        public String preCostCenterCode;
        public String preOrderCenterCode;
        public String newCostCenterCode;
        public String newOrderCenterCode;

        public String getPreCostCenterCode() {
            return preCostCenterCode;
        }

        public void setPreCostCenterCode(String preCostCenterCode) {
            this.preCostCenterCode = preCostCenterCode;
        }

        public String getPreOrderCenterCode() {
            return preOrderCenterCode;
        }

        public void setPreOrderCenterCode(String preOrderCenterCode) {
            this.preOrderCenterCode = preOrderCenterCode;
        }

        public String getNewCostCenterCode() {
            return newCostCenterCode;
        }

        public void setNewCostCenterCode(String newCostCenterCode) {
            this.newCostCenterCode = newCostCenterCode;
        }

        public String getNewOrderCenterCode() {
            return newOrderCenterCode;
        }

        public void setNewOrderCenterCode(String newOrderCenterCode) {
            this.newOrderCenterCode = newOrderCenterCode;
        }
    }
}
