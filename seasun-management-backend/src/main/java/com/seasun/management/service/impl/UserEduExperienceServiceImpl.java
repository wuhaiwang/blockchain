package com.seasun.management.service.impl;

import com.seasun.management.constant.SyncType;
import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.UserEduExperienceMapper;
import com.seasun.management.model.UserEduExperience;
import com.seasun.management.service.UserEduExperienceService;
import com.seasun.management.vo.BaseSyncVo;
import com.seasun.management.vo.UserEduExperienceSyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserEduExperienceServiceImpl extends AbstractSyncService implements UserEduExperienceService {
    @Autowired
    private UserEduExperienceMapper userEduExperienceMapper;

    @Override
    public void sync(BaseSyncVo baseSyncVo) {
        if (!(baseSyncVo instanceof UserEduExperienceSyncVo)) {
            throw new ClassCastException("baseSynVo对象 不能转换为 UserEduExperienceSyncVo 类");
        }
        UserEduExperienceSyncVo userEduExperienceSyncVo = (UserEduExperienceSyncVo) baseSyncVo;
        UserEduExperience userEduExperience = userEduExperienceSyncVo.getData();
        if (null == userEduExperience.getId()) {
            throw new ParamException("id can not be null...");
        }
        if (null == userEduExperience.getUserId()) {
            throw new ParamException("user_id can not be null...");
        }

        if (userEduExperienceSyncVo.getType().equals(SyncType.add)) {
            userEduExperienceMapper.insertWithId(userEduExperience);
        } else if (userEduExperienceSyncVo.getType().equals(SyncType.update)) {
            userEduExperienceMapper.updateByPrimaryKeySelective(userEduExperience);
        } else if (userEduExperienceSyncVo.getType().equals(SyncType.delete)) {
            userEduExperienceMapper.deleteByPrimaryKey(userEduExperience.getId());
        }
    }
}
