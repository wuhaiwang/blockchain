
package com.seasun.management.service.permission;

import com.seasun.management.dto.FmMemberDto;
import com.seasun.management.dto.FmUserRoleDto;
import com.seasun.management.dto.MenuPermDto;
import com.seasun.management.dto.RMenuProjectRolePermDto;
import com.seasun.management.mapper.FmMemberMapper;
import com.seasun.management.mapper.FmUserRoleMapper;
import com.seasun.management.mapper.ProjectMapper;
import com.seasun.management.model.FmRole;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.Menu;
import com.seasun.management.util.MyTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FixMemberPermCollector extends PermissionCollector {

    private static final String fixMemberKey = "fm-performance-detail";
    private static final String fixGroupKey = "fm-group-performance";
    private static final String fixPergKey = "fm-performance";
    private static final String fixModule = "perf";

    @Autowired
    private FmMemberMapper fmMemberMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private FmUserRoleMapper fmUserRoleMapper;

    @Override
    List<? extends MenuPermDto> getMenuPermsByUserId(Long userId) {
        List<RMenuProjectRolePermDto> rolePermDtos = new ArrayList<>();
        if (hasPermission(userId)) {
            RMenuProjectRolePermDto dto1 = new RMenuProjectRolePermDto();
            dto1.setKey(fixMemberKey);
            dto1.setType(Menu.Type.pc);
            dto1.setModule(fixModule);
            rolePermDtos.add(dto1);

            RMenuProjectRolePermDto dto2 = new RMenuProjectRolePermDto();
            dto2.setKey(fixGroupKey);
            dto2.setType(Menu.Type.pc);
            dto2.setModule(fixModule);
            rolePermDtos.add(dto2);

            RMenuProjectRolePermDto dto3 = new RMenuProjectRolePermDto();
            dto3.setKey(fixPergKey);
            dto3.setType(Menu.Type.pc);
            dto3.setModule(fixModule);
            rolePermDtos.add(dto3);
        }
        return rolePermDtos;
    }

    public boolean hasPermission(Long userId, String state) {
        boolean isFixMenu = fixMemberKey.equals(state) || fixGroupKey.equals(state) || fixPergKey.equals(state);
        return isFixMenu&&hasPermission(userId);
    }

    public boolean hasPermission(Long userId) {
        // 判断是否有固化成员权限
        boolean fixMemberFlag = false;
        boolean isBoss = MyTokenUtils.isBoss();
        if (!isBoss) {
            List<FmMemberDto> fmMembers = fmMemberMapper.selectAll();
            Map<Long, List<FmMemberDto>> projectFmMembersMap = fmMembers.stream().collect(Collectors.groupingBy(m -> m.getProjectId()));
            List<IdNameBaseObject> projects = fmUserRoleMapper.selectUserFixSecondProjectsByUserId(userId);

            if (!projects.isEmpty()) {
                for (IdNameBaseObject project : projects) {
                    if (projectFmMembersMap.containsKey(project.getId())) {
                        fixMemberFlag = true;
                        break;
                    }
                }
            } else {
                List<FmUserRoleDto> fmUserRoles = fmUserRoleMapper.selectAllByUserIdAndRoleId(userId, FmRole.Role.projectFixFirstConfirmer);
                for (FmUserRoleDto fmUserRole : fmUserRoles) {
                    if (projectFmMembersMap.containsKey(fmUserRole.getProjectId())) {
                        fixMemberFlag = true;
                        break;
                    }
                }
            }
        }
        return fixMemberFlag;
    }
}