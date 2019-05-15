package com.seasun.management.service;

import com.seasun.management.model.UserGesture;

public interface UserGestureService {
    void modifyUserGesture(UserGesture userGesture);

    Boolean verifyGesture(String gesture);

    Boolean useGesture(Long userId);
}
