package com.seasun.management.service.redis;


import com.seasun.management.vo.UserMiniVo;

import java.util.List;

public interface RedisService {

    boolean set(String key, String value);

    String get(String key);

    boolean expire(String key, long expire);

    boolean setObject(String key, Object object);

    <T> T getObject(String key, T object);

    long lPush(String key, Object obj);

    long rPush(String key, Object obj);

    String lPop(String key);

    void publish(String channel, String content);

    List<String> getPushServers();

    List<UserMiniVo> getOnlineUserFromPushServer(String host);
}
