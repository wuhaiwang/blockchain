package com.seasun.management.exception;

public class UserSalaryChangeException extends RuntimeException {
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public UserSalaryChangeException(int code, String message) {
        super(message);
        this.code = code;
    }
}
