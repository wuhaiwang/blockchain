package com.seasun.management.service.impl;

import com.mysql.jdbc.StringUtils;
import com.seasun.management.constant.SyncType;
import com.seasun.management.controller.response.AddRecordResponse;
import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.ProjectMapper;
import com.seasun.management.mapper.RUserProjectPermMapper;
import com.seasun.management.model.RUserProjectPerm;
import com.seasun.management.service.RUserProjectPermService;
import com.seasun.management.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RUserProjectPermServiceImpl extends AbstractSyncService implements RUserProjectPermService {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    @Autowired
    RUserProjectPermMapper rUserProjectPermMapper;

    @Autowired
    ProjectMapper projectMapper;

    @Override
    public void sync(BaseSyncVo baseSyncVo) {

        if (!(baseSyncVo instanceof RUserProjectPermSyncVo)) {
            throw new ClassCastException("baseSynVo对象 不能转换为 RUserProjectPermSyncVo 类");
        }
        RUserProjectPermSyncVo rUserProjectPermSyncVo = (RUserProjectPermSyncVo) baseSyncVo;
        if (null == rUserProjectPermSyncVo.getData().getUserId()) {
            throw new ParamException("both userId can not be null");
        }

        if (rUserProjectPermSyncVo.getType().equals(SyncType.add)) {
            RUserProjectPerm rUserProjectPerm = rUserProjectPermMapper.selectByUserIdAndProjectId(rUserProjectPermSyncVo.getData().getUserId(), rUserProjectPermSyncVo.getData().getProjectId());
            if (null != rUserProjectPerm) {
                rUserProjectPermMapper.updateSelective(rUserProjectPermSyncVo.getData());
            } else {
                rUserProjectPermMapper.insert(rUserProjectPermSyncVo.getData());
            }
        } else if (rUserProjectPermSyncVo.getType().equals(SyncType.delete)) {
            rUserProjectPermMapper.deleteSelective(rUserProjectPermSyncVo.getData());
        }
    }

    @Override
    public RUserProjectPerm selectRUserProjectPerm(RUserProjectPerm rUserProjectPerm) {
        return rUserProjectPermMapper.selectSelective(rUserProjectPerm);
    }

    @Override
    public RUserProjectPermListVo selectAllByUserId(Long userId) {
        RUserProjectPermListVo classifyPermVo = new RUserProjectPermListVo();
        List<RUserProjectPermVo> systemRoles = new ArrayList<>();
        List<RUserProjectPermVo> projectRoles = new ArrayList<>();
        List<RUserProjectPermListVo.RUserProjectPermPlusVo> fixRoles = new ArrayList<>();
        List<MiniProjectVo> allProject = projectMapper.selectAllMiniProject();
        List<RUserProjectPermVo> result = rUserProjectPermMapper.selectByUserIdAndOrderByProjectRoleIdAsc(userId);
        for (RUserProjectPermVo vo : result) {
            switch (vo.getSystemFlag()) {
                case 0: {
                    projectRoles.add(vo);
                    break;
                }
                case 1: {
                    systemRoles.add(vo);
                    break;
                }
                case 2: {
                    RUserProjectPermListVo.RUserProjectPermPlusVo plusVo = new RUserProjectPermListVo.RUserProjectPermPlusVo();
                    BeanUtils.copyProperties(vo, plusVo);
                    MiniProjectVo miniProjectVo = allProject.stream().filter(p -> p.getId().equals(vo.getProjectId())).findFirst().orElse(null);
                    if (miniProjectVo != null) {
                        String serviceLine = miniProjectVo.getServiceLine();
                        if (StringUtils.isEmptyOrWhitespaceOnly(serviceLine)) {
                            plusVo.setType(serviceLine);
                        } else {
                            plusVo.setType(miniProjectVo.getServiceLine().equals("平台") ? "plat" : "project");
                        }
                    }
                    fixRoles.add(plusVo);
                    break;
                }
                default:
                    break;
            }
        }
        classifyPermVo.setFixRoles(fixRoles);
        classifyPermVo.setSystemRoles(systemRoles);
        classifyPermVo.setProjectRoles(projectRoles);
        return classifyPermVo;
    }

    @Override
    public AddRecordResponse insert(RUserProjectPerm record) {
        RUserProjectPerm data = selectRUserProjectPerm(record);
        if (null == data) {
            rUserProjectPermMapper.insert(record);
            return new AddRecordResponse(0, "成功", record.getId());
        } else {
            return new AddRecordResponse(-1, "用户已是该组的分摊负责人", 0L);
        }
    }

    @Override
    public void delete(Long id) {
        rUserProjectPermMapper.deleteByPrimaryKey(id);
    }
}
