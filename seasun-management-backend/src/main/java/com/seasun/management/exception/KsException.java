package com.seasun.management.exception;

public class KsException extends RuntimeException {
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public KsException(int code, String message) {
        super(message);
        this.code = code;
    }
}
