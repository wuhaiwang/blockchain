package com.seasun.management.vo;

public class UserSalaryGroupSimpleVo {
    private Long id;
    private Long userId;
    private String name;
    private Integer score;
    private String evaluateType;
    private String status;
    private Integer increaseSalary;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getEvaluateType() {
        return evaluateType;
    }

    public void setEvaluateType(String evaluateType) {
        this.evaluateType = evaluateType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getIncreaseSalary() {
        return increaseSalary;
    }

    public void setIncreaseSalary(Integer increaseSalary) {
        this.increaseSalary = increaseSalary;
    }
}
