package com.seasun.management.dto;

public class UserGradeDto extends WorkGroupUserDto {
    private String grade;
    private String evaluateType;
    private Long workAge;
    private Long workAgeInKs;

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getEvaluateType() {
        return evaluateType;
    }

    public void setEvaluateType(String evaluateType) {
        this.evaluateType = evaluateType;
    }

    public Long getWorkAge() {
        return workAge;
    }

    public void setWorkAge(Long workAge) {
        this.workAge = workAge;
    }

    public Long getWorkAgeInKs() {
        return workAgeInKs;
    }

    public void setWorkAgeInKs(Long workAgeInKs) {
        this.workAgeInKs = workAgeInKs;
    }
}
