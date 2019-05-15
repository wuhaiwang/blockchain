package com.seasun.management.vo;

public class UserLoginEmailVo {

    private Long id;
    private String name;
    private String loginId;
    private Float totalPro;
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Float getTotalPro() {
        return totalPro;
    }

    public void setTotalPro(Float totalPro) {
        this.totalPro = totalPro;
    }
}
