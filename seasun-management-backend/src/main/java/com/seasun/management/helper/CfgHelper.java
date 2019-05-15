package com.seasun.management.helper;

import com.seasun.management.mapper.CfgPlatAttrMapper;
import com.seasun.management.model.CfgPlatAttr;
import com.seasun.management.util.MyBeanUtils;

public class CfgHelper {

    public static boolean isUserOldShareConfig(Long platId) {
        CfgPlatAttrMapper cfgPlatAttrMapper = MyBeanUtils.getBean(CfgPlatAttrMapper.class);
        CfgPlatAttr cfgPlatAttr = cfgPlatAttrMapper.selectByPlatId(platId);
        return !cfgPlatAttr.getShareDetailFlag();
    }

}
