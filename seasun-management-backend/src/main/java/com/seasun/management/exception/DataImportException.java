package com.seasun.management.exception;

public class DataImportException extends RuntimeException {

    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataImportException(int code, String message) {
        super(message);
        this.code = code;
    }

}
