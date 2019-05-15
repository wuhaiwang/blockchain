package com.seasun.management.service.impl;

import com.seasun.management.constant.SyncType;
import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.UserChildrenInfoMapper;
import com.seasun.management.model.UserChildrenInfo;
import com.seasun.management.service.UserChildrenInfoService;
import com.seasun.management.vo.BaseSyncVo;
import com.seasun.management.vo.UserChildrenInfoSyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserChildrenInfoServiceImpl extends AbstractSyncService implements UserChildrenInfoService {
    @Autowired
    private UserChildrenInfoMapper userChildrenInfoMapper;

    @Override
    public void sync(BaseSyncVo baseSyncVo) {
        if (!(baseSyncVo instanceof UserChildrenInfoSyncVo)) {
            throw new ClassCastException("baseSynVo对象 不能转换为 UserChildrenInfoSyncVo 类");
        }
        UserChildrenInfoSyncVo userChildrenInfoSyncVo = (UserChildrenInfoSyncVo) baseSyncVo;
        UserChildrenInfo userChildrenInfo = userChildrenInfoSyncVo.getData();
        if (null == userChildrenInfo.getId()) {
            throw new ParamException("id can not be null...");
        }
        if (null == userChildrenInfo.getUserId()) {
            throw new ParamException("user_id can not be null...");
        }
        if (null == userChildrenInfo.getName()) {
            throw new ParamException("name can not be null...");
        }

        if (userChildrenInfoSyncVo.getType().equals(SyncType.add)) {
            UserChildrenInfo record = userChildrenInfoMapper.selectByUserIdAndName(userChildrenInfo);
            if (null == record) {
                userChildrenInfoMapper.insertSelective(userChildrenInfo);
            }
        } else if (userChildrenInfoSyncVo.getType().equals(SyncType.update)) {
            userChildrenInfoMapper.updateByUserIdAndNameSelective(userChildrenInfo);
        } else if (userChildrenInfoSyncVo.getType().equals(SyncType.delete)) {
            userChildrenInfoMapper.deleteByPrimaryKey(userChildrenInfo.getId());
        }
    }
}
