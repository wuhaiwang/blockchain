package com.seasun.management.service;

import com.seasun.management.controller.response.AddRecordResponse;
import com.seasun.management.vo.RMenuProjectRolePermVo;

import java.util.List;

public interface RMenuProjectRolePermService {

    RMenuProjectRolePermVo getListByProjectRoleId (final Long roleId);

    AddRecordResponse insertProjectRoleMenu (Long projectRoleId, List<Long> menuId);

    Boolean deleteProjectRoleMenu (Long projectRoleId, Long menuId);

}
