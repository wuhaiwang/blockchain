package com.seasun.management.vo;

import com.seasun.management.model.*;
import com.seasun.management.vo.UserDetailSyncVo.*;

public class UserSyncVo extends BaseSyncVo {

    private DataVo data;

    public DataVo getData() {
        return data;
    }

    public void setData(DataVo data) {
        this.data = data;
    }

    public static class DataVo {

        private UserInfo baseInfo;
        private UserDetailInfo userDetail;

        public UserInfo getBaseInfo() {
            return baseInfo;
        }

        public void setBaseInfo(UserInfo baseInfo) {
            this.baseInfo = baseInfo;
        }

        public UserDetailInfo getUserDetail() {
            return userDetail;
        }

        public void setUserDetail(UserDetailInfo userDetail) {
            this.userDetail = userDetail;
        }
    }

    public static class UserInfo extends User {
        private String costCenterCode;
        private String orderCenterCode;

        public String getCostCenterCode() {
            return costCenterCode;
        }

        public void setCostCenterCode(String costCenterCode) {
            this.costCenterCode = costCenterCode;
        }

        public String getOrderCenterCode() {
            return orderCenterCode;
        }

        public void setOrderCenterCode(String orderCenterCode) {
            this.orderCenterCode = orderCenterCode;
        }
    }
}
