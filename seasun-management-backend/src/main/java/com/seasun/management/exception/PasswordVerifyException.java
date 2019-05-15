package com.seasun.management.exception;

public class PasswordVerifyException extends RuntimeException {
    public PasswordVerifyException(String message) {
        super(message);
    }
}
