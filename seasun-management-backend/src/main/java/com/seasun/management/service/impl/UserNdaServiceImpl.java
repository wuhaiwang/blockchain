package com.seasun.management.service.impl;

import com.seasun.management.constant.SyncType;
import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.UserNdaMapper;
import com.seasun.management.model.UserNda;
import com.seasun.management.service.UserNdaService;
import com.seasun.management.vo.BaseSyncVo;
import com.seasun.management.vo.UserNdaSyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserNdaServiceImpl extends AbstractSyncService implements UserNdaService {
    @Autowired
    private UserNdaMapper userNdaMapper;

    @Override
    public void sync(BaseSyncVo baseSyncVo) {
        if (!(baseSyncVo instanceof UserNdaSyncVo)) {
            throw new ClassCastException("baseSynVo对象 不能转换为 UserNdaSyncVo 类");
        }
        UserNdaSyncVo userNdaSyncVo = (UserNdaSyncVo) baseSyncVo;
        UserNda userNda = userNdaSyncVo.getData();
        if (null == userNda.getId()) {
            throw new ParamException("id can not be null...");
        }
        if (null == userNda.getUserId()) {
            throw new ParamException("user_id can not be null...");
        }

        if (userNdaSyncVo.getType().equals(SyncType.add)) {
            UserNda userNdaObj = userNdaMapper.selectByCondition(userNda);
            if (null != userNdaObj) {
                userNdaObj.setActiveFlag(false);
                userNdaMapper.updateByPrimaryKey(userNdaObj);
            }
            userNdaMapper.insertWithId(userNda);
        } else if (userNdaSyncVo.getType().equals(SyncType.update)) {
            userNdaMapper.updateByPrimaryKeySelective(userNda);
        } else if (userNdaSyncVo.getType().equals(SyncType.delete)) {
            userNdaMapper.deleteByPrimaryKey(userNda.getId());
        }
    }
}
