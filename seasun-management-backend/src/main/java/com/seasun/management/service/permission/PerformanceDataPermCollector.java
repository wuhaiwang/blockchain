package com.seasun.management.service.permission;

import com.seasun.management.dto.MenuPermDto;
import com.seasun.management.dto.RMenuProjectRolePermDto;
import com.seasun.management.mapper.RMenuPerformanceWorkGroupPermMapper;
import com.seasun.management.model.PerformanceWorkGroupRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PerformanceDataPermCollector extends PermissionCollector {

    @Autowired
    private RMenuPerformanceWorkGroupPermMapper rMenuPerformanceWorkGroupPermMapper;

    @Override
    public List<? extends MenuPermDto> getMenuPermsByUserId(Long userId) {

        List<RMenuProjectRolePermDto> result = rMenuPerformanceWorkGroupPermMapper.selectAllPerformanceDataPerm(userId, PerformanceWorkGroupRole.roles);
        return result == null ? new ArrayList<>() : result;

    }
}
