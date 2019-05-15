package com.seasun.management.vo;

import com.seasun.management.dto.UserPsychologicalConsultationDto;

import java.util.List;

public class UserPsychologicalConsultationVo {

    private String LastModifyTime;

    private int totalCount;

    private List<UserPsychologicalConsultationDto> users;


    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }


    public String getLastModifyTime() {
        return LastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        LastModifyTime = lastModifyTime;
    }

    public List<UserPsychologicalConsultationDto> getUsers() {
        return users;
    }

    public void setUsers(List<UserPsychologicalConsultationDto> users) {
        this.users = users;
    }
}
