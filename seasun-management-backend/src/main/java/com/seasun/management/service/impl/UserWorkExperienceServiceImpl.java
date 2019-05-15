package com.seasun.management.service.impl;

import com.seasun.management.constant.SyncType;
import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.UserWorkExperienceMapper;
import com.seasun.management.model.UserWorkExperience;
import com.seasun.management.service.UserWorkExperienceService;
import com.seasun.management.vo.BaseSyncVo;
import com.seasun.management.vo.UserWorkExperienceSyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserWorkExperienceServiceImpl extends AbstractSyncService implements UserWorkExperienceService {
    @Autowired
    private UserWorkExperienceMapper userWorkExperienceMapper;

    @Override
    public void sync(BaseSyncVo baseSyncVo) {
        if (!(baseSyncVo instanceof UserWorkExperienceSyncVo)) {
            throw new ClassCastException("baseSynVo对象 不能转换为 UserWorkExperienceSyncVo 类");
        }
        UserWorkExperienceSyncVo userWorkExperienceSyncVo = (UserWorkExperienceSyncVo) baseSyncVo;
        UserWorkExperience userWorkExperience = userWorkExperienceSyncVo.getData();
        if (null == userWorkExperience.getId()) {
            throw new ParamException("id can not be null...");
        }
        if (null == userWorkExperience.getUserId()) {
            throw new ParamException("user_id can not be null...");
        }

        if (userWorkExperienceSyncVo.getType().equals(SyncType.add)) {
            userWorkExperienceMapper.insertWithId(userWorkExperience);
        } else if (userWorkExperienceSyncVo.getType().equals(SyncType.update)) {
            userWorkExperienceMapper.updateByPrimaryKeySelective(userWorkExperience);
        } else if (userWorkExperienceSyncVo.getType().equals(SyncType.delete)) {
            userWorkExperienceMapper.deleteByPrimaryKey(userWorkExperience.getId());
        }
    }
}
