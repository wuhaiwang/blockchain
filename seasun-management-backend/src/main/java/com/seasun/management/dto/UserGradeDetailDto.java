package com.seasun.management.dto;

import java.util.List;

public class UserGradeDetailDto extends UserGradeDto {
    public String post;
    public String workGroup;
    public Long employeeNo;

    public List<UserGradeHistoryDto> historyGrade;

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getWorkGroup() {
        return workGroup;
    }

    public void setWorkGroup(String workGroup) {
        this.workGroup = workGroup;
    }

    public Long getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(Long employeeNo) {
        this.employeeNo = employeeNo;
    }

    public List<UserGradeHistoryDto> getHistoryGrade() {
        return historyGrade;
    }

    public void setHistoryGrade(List<UserGradeHistoryDto> historyGrade) {
        this.historyGrade = historyGrade;
    }
}
