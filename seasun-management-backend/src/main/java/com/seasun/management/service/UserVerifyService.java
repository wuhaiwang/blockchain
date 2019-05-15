package com.seasun.management.service;

public interface UserVerifyService {

    boolean verifyToken(String token, int type);

    String verifyPassword(String userName, String password, int type);

    String verifyPasswordByCurrentUser(String password);

    boolean getDynamicPassword(String userName);

    String verifyDynamicPassword(String userName, String password);

    void clearUserToken(int type);

    String addTestUser(String userName);
}
