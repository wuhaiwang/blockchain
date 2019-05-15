package com.seasun.management.service.impl;

import com.seasun.management.dto.AppGuiCategoryDto;
import com.seasun.management.mapper.CfgGuiCategoryMapper;
import com.seasun.management.mapper.CfgSystemParamMapper;
import com.seasun.management.model.CfgGuiCategory;
import com.seasun.management.model.CfgSystemParam;
import com.seasun.management.service.CfgSystemService;
import com.seasun.management.util.MySystemParamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
public class CfgSystemServiceImpl implements CfgSystemService {

    @Autowired
    private CfgGuiCategoryMapper cfgGuiCategoryMapper;

    @Autowired
    private CfgSystemParamMapper cfgSystemParamMapper;

    @Override
    public AppGuiCategoryDto getAppDiscoveryInfo() {
        AppGuiCategoryDto result = new AppGuiCategoryDto();

        // 论坛开始开关
        boolean forumStartFlag = false;
        CfgSystemParam cfgSystemParam = cfgSystemParamMapper.selectByName(MySystemParamUtils.Key.ForumStartFlag);
        if (cfgSystemParam != null) {
            forumStartFlag= Boolean.parseBoolean(cfgSystemParam.getValue());
        }

        // 发现-轮播图
        List<CfgGuiCategory> guiCategorys = cfgGuiCategoryMapper.selectAll();
        Iterator<CfgGuiCategory> iterator = guiCategorys.iterator();
        while (iterator.hasNext()) {
            CfgGuiCategory item = iterator.next();
            if (CfgGuiCategory.Type.hotSpot.equals(item.getType())) {
                result.setHotSpot(item);
                iterator.remove();
                break;
            }
        }

        result.setForumStartFlag(forumStartFlag);
        result.setBanner(guiCategorys);
        return result;
    }
}
