package com.seasun.management.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.vo.ProjectMaxMemberVo;

import java.util.List;

public class ProjectMaxMemberDto extends ProjectMaxMemberVo {

    private List<IdNameBaseObject> managers;

    @JsonIgnore
    private String orderStr;

    private List<OrderCode> orders;

    private Boolean activeFlag;

    public static class OrderCode {

        private String code;

        private String city;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }

    public List<IdNameBaseObject> getManagers() {
        return managers;
    }

    public void setManagers(List<IdNameBaseObject> managers) {
        this.managers = managers;
    }

    public String getOrderStr() {
        return orderStr;
    }

    public void setOrderStr(String orderStr) {
        this.orderStr = orderStr;
    }

    public List<OrderCode> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderCode> orders) {
        this.orders = orders;
    }

    public Boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Boolean activeFlag) {
        this.activeFlag = activeFlag;
    }
}
