package com.seasun.management.service.impl;

import com.seasun.management.constant.ErrorCode;
import com.seasun.management.controller.response.AddRecordResponse;
import com.seasun.management.dto.MenuDto;
import com.seasun.management.exception.UserInvalidOperateException;
import com.seasun.management.mapper.MenuMapper;
import com.seasun.management.model.Menu;
import com.seasun.management.service.MenuService;
import com.seasun.management.util.MyTokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    MenuMapper menuMapper;

    @Override
    public List<MenuDto> findAll() {
        return menuMapper.selectAll();
    }

    @Override
    public AddRecordResponse insertOne(MenuDto menu) {

        if (StringUtils.isBlank(menu.getKey()) || StringUtils.isBlank(menu.getType())) {
            return new AddRecordResponse(ErrorCode.SYSTEM_ERROR, "参数不能为空", null);
        }
        Menu data = menuMapper.selectByKey(menu.getKey());
        if (data == null) {
            menuMapper.insertOne(menu);
            return new AddRecordResponse(menu.getId());
        }
        return new AddRecordResponse(ErrorCode.SYSTEM_ERROR, "菜单信息已存在", null);
    }

    @Override
    public void checkUserPermission(String menuKey) {
        int count =  menuMapper.countUserMenuPermByMenuKey(menuKey, MyTokenUtils.getCurrentUserId());
        if (count<=0) throw new UserInvalidOperateException("该用户暂时没有该菜单的使用权限");
    }
}
