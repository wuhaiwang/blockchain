package com.seasun.management.service.impl;

import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.UserMapper;
import com.seasun.management.model.User;
import com.seasun.management.service.RUserFloorService;
import com.seasun.management.service.impl.AbstractSyncService;
import com.seasun.management.vo.BaseSyncVo;
import com.seasun.management.vo.RUserFloorSyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RUserFloorServiceImpl extends AbstractSyncService implements RUserFloorService {
    @Autowired
    UserMapper userMapper;

    @Override
    public void sync(BaseSyncVo baseSyncVo) {

        if (!(baseSyncVo instanceof RUserFloorSyncVo)) {
            throw new ClassCastException("baseSynVo对象 不能转换为 RUserFloorSyncVo 类");
        }
        RUserFloorSyncVo rUserFloorSyncVo = (RUserFloorSyncVo) baseSyncVo;
        if (null == rUserFloorSyncVo.getData().getUserId() || null == rUserFloorSyncVo.getData().getFloorId()) {
            throw new ParamException("both userId and groupId can not be null");
        }

        User user = userMapper.selectByPrimaryKey(rUserFloorSyncVo.getData().getUserId());
        user.setFloorId(rUserFloorSyncVo.getData().getFloorId());
        userMapper.updateByPrimaryKeySelective(user);
    }
}
