package com.seasun.management.vo;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class BatchSyncVo extends BaseSyncVo {

    private List<BatchRequest> requests;

    public List<BatchRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<BatchRequest> requests) {
        this.requests = requests;
    }

    public static class BatchRequest {
        private String targetName;
        private JSONObject data;

        public String getTargetName() {
            return targetName;
        }

        public void setTargetName(String targetName) {
            this.targetName = targetName;
        }

        public JSONObject getData() {
            return data;
        }

        public void setData(JSONObject data) {
            this.data = data;
        }
    }
}
