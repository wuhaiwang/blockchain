package com.seasun.management.dto;

import com.seasun.management.model.UserPsychologicalConsultation;

public class UserPsychologicalConsultationDto extends UserPsychologicalConsultation {

    private String username;

    private String loginId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
}
