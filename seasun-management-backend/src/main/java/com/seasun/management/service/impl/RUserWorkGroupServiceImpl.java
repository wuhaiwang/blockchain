package com.seasun.management.service.impl;

import com.seasun.management.constant.ErrorMessage;
import com.seasun.management.constant.SyncType;
import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.RUserWorkGroupPermMapper;
import com.seasun.management.mapper.UserMapper;
import com.seasun.management.model.ITCache;
import com.seasun.management.model.RUserWorkGroupPerm;
import com.seasun.management.model.User;
import com.seasun.management.model.WorkGroupRole;
import com.seasun.management.service.RUserWorkGroupService;
import com.seasun.management.service.cache.CacheService;
import com.seasun.management.vo.BaseSyncVo;
import com.seasun.management.vo.RUserWorkGroupSyncVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class RUserWorkGroupServiceImpl extends AbstractSyncService implements RUserWorkGroupService {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    @Autowired
    RUserWorkGroupPermMapper rUserWorkGroupPermMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    private CacheService cacheService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sync(BaseSyncVo baseSyncVo) {

        if (!(baseSyncVo instanceof RUserWorkGroupSyncVo)) {
            throw new ClassCastException("baseSynVo对象 不能转换为 RUserWorkGroupSyncVo 类");
        }
        RUserWorkGroupSyncVo rUserWorkGroupSyncVo = (RUserWorkGroupSyncVo) baseSyncVo;
        if (null == rUserWorkGroupSyncVo.getData().getUserId() || null == rUserWorkGroupSyncVo.getData().getWorkGroupId()) {
            throw new ParamException("userId和workGroupId不能为空");
        }
        if (baseSyncVo.getTargetName().equals("user-work-group")) {
            // 同步UserGroup(用户所在的组)
            User user = userMapper.selectByPrimaryKey(rUserWorkGroupSyncVo.getData().getUserId());
            user.setWorkGroupId(rUserWorkGroupSyncVo.getData().getWorkGroupId());
            userMapper.updateByPrimaryKeySelective(user);
        } else if (baseSyncVo.getTargetName().equals("r-user-work-group-perm")) {
            // 同步Group-Leader
            if (rUserWorkGroupSyncVo.getType().equals(SyncType.add)) {
                RUserWorkGroupPerm rUserWorkGroupPerm = rUserWorkGroupPermMapper.selectByUserIdAndWorkGroupId(rUserWorkGroupSyncVo.getData().getUserId(), rUserWorkGroupSyncVo.getData().getWorkGroupId(), WorkGroupRole.Role.hr);
                if (null == rUserWorkGroupPerm) {
                    rUserWorkGroupSyncVo.getData().setWorkGroupRoleId(WorkGroupRole.Role.hr);
                    rUserWorkGroupPermMapper.insert(rUserWorkGroupSyncVo.getData());
                }
            } else if (rUserWorkGroupSyncVo.getType().equals(SyncType.delete)) {
                rUserWorkGroupSyncVo.getData().setWorkGroupRoleId(WorkGroupRole.Role.hr);
                rUserWorkGroupPermMapper.deleteSelective(rUserWorkGroupSyncVo.getData());
            }
        }
        String cacheResult = cacheService.refreshCacheByCacheNameAndStoreKey(ITCache.HashKey.HRCONTACTROOTTREE, ITCache.EntryKey.HRCONTACTROOTTREE);
        if (cacheResult != null) {
            logger.error(String.format(ErrorMessage.CACHE_REFRESH_ERROR, ITCache.HashKey.HRCONTACTROOTTREE, ITCache.EntryKey.HRCONTACTROOTTREE, cacheResult));
        }
    }

    @Override
    public int insert(RUserWorkGroupPerm record) {
        return rUserWorkGroupPermMapper.insert(record);
    }

    @Override
    public List<RUserWorkGroupPerm> selectByWorkGroupId(Long workGroupId) {
        return rUserWorkGroupPermMapper.selectByWorkGroupId(workGroupId);
    }

    @Override
    public RUserWorkGroupPerm selectByUserIdAndWorkGroupId(Long userId, Long workGroupId) {
        return rUserWorkGroupPermMapper.selectByUserIdAndWorkGroupId(userId, workGroupId, WorkGroupRole.Role.hr);
    }

    @Override
    public int deleteByUserIdAndWorkGroupId(Long userId, Long workGroupId) {
        return rUserWorkGroupPermMapper.deleteByUserIdAndWorkGroupIdAndWorkGroupRoleId(userId, workGroupId, WorkGroupRole.Role.hr);
    }
}
