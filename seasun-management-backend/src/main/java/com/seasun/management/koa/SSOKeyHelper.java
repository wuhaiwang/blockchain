package com.seasun.management.koa;

import com.seasun.management.mapper.CfgSsoKeyMapper;
import com.seasun.management.model.CfgSsoKey;
import com.seasun.management.util.MyBeanUtils;

public class SSOKeyHelper {

    public static CfgSsoKey getSSoKey() {
        CfgSsoKeyMapper cfgSsoKeyMapper = MyBeanUtils.getBean(CfgSsoKeyMapper.class);
        CfgSsoKey cfgSsoKey = cfgSsoKeyMapper.selectOne();
        return cfgSsoKey;

    }
}
