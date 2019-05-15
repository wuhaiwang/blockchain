package com.seasun.management.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MySalaryKey {

    private AtomicInteger version;

    private String startKey;

    private List<LoginKey> loginKeys = new ArrayList<>();

    public static class LoginKey{
        private Long userId;
        private String loginKey;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getLoginKey() {
            return loginKey;
        }

        public void setLoginKey(String loginKey) {
            this.loginKey = loginKey;
        }
    }

    public AtomicInteger getVersion() {
        return version;
    }

    public void setVersion(AtomicInteger version) {
        this.version = version;
    }

    public String getStartKey() {
        return startKey;
    }

    public void setStartKey(String startKey) {
        this.startKey = startKey;
    }

    public List<LoginKey> getLoginKeys() {
        return loginKeys;
    }

    public void setLoginKeys(List<LoginKey> loginKeys) {
        this.loginKeys = loginKeys;
    }
}
