package com.seasun.management.service;

import java.util.Map;

public interface DiscuzService {
    /**
     * 用户登录
     * 1. get user token
     * 2. check if discuz password exists
     * 3. register discuz user
     * 4. login discuz user
     * 5. return login JS code
     * @param loginId
     * @return
     */
    public Map<String,String> userLogin(String token);
}
