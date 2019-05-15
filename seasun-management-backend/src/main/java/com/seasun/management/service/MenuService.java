package com.seasun.management.service;

import com.seasun.management.controller.response.AddRecordResponse;
import com.seasun.management.dto.MenuDto;
import com.seasun.management.model.Menu;

import java.util.List;

public interface MenuService {

    List<MenuDto> findAll ();

    AddRecordResponse insertOne (MenuDto menu);

    void checkUserPermission (String menuKey);

}
