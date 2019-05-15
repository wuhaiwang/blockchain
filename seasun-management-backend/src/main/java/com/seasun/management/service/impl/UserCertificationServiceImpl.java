package com.seasun.management.service.impl;

import com.seasun.management.constant.SyncType;
import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.UserCertificationMapper;
import com.seasun.management.model.UserCertification;
import com.seasun.management.service.UserCertificationService;
import com.seasun.management.vo.BaseSyncVo;
import com.seasun.management.vo.UserCertificationSyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCertificationServiceImpl extends AbstractSyncService implements UserCertificationService {
    @Autowired
    private UserCertificationMapper userCertificationMapper;

    @Override
    public void sync(BaseSyncVo baseSyncVo) {
        if (!(baseSyncVo instanceof UserCertificationSyncVo)) {
            throw new ClassCastException("baseSynVo对象 不能转换为 UserCertificationSyncVo 类");
        }
        UserCertificationSyncVo userCertificationSyncVo = (UserCertificationSyncVo) baseSyncVo;
        UserCertification userCertification = userCertificationSyncVo.getData();
        if (null == userCertification.getId()) {
            throw new ParamException("id can not be null...");
        }
        if (null == userCertification.getUserId()) {
            throw new ParamException("user_id can not be null...");
        }

        if (userCertificationSyncVo.getType().equals(SyncType.add)) {
            userCertificationMapper.insertWithId(userCertification);
        } else if (userCertificationSyncVo.getType().equals(SyncType.update)) {
            userCertificationMapper.updateByPrimaryKeySelective(userCertification);
        } else if (userCertificationSyncVo.getType().equals(SyncType.delete)) {
            userCertificationMapper.deleteByPrimaryKey(userCertification.getId());
        }
    }
}
