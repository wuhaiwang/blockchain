package com.seasun.management.service.permission;

import com.seasun.management.dto.MenuPermDto;
import com.seasun.management.dto.RMenuPerformanceWorkGroupPermDto;
import com.seasun.management.mapper.PerformanceWorkGroupMapper;
import com.seasun.management.mapper.RMenuPerformanceWorkGroupPermMapper;
import com.seasun.management.model.PerformanceWorkGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PerformanceManagerPermCollector extends PermissionCollector {


    @Autowired
    private PerformanceWorkGroupMapper performanceWorkGroupMapper;

    @Autowired
    private RMenuPerformanceWorkGroupPermMapper rMenuPerformanceWorkGroupPermMapper;

    private static final Long performance_work_group_manager_role = 3L;

    @Override
    public List<? extends MenuPermDto> getMenuPermsByUserId(Long userId) {
        List<RMenuPerformanceWorkGroupPermDto> result = null;
        List<PerformanceWorkGroup> performanceWorkGroups = performanceWorkGroupMapper.selectByUserId(userId);
        if (performanceWorkGroups != null && performanceWorkGroups.size() != 0) {
            result = rMenuPerformanceWorkGroupPermMapper.selectAllRMenuPermByRoleId(performance_work_group_manager_role);
        }
        return result == null ? new ArrayList<>() : result;
    }

}