package com.seasun.management.service.impl;

import com.seasun.management.constant.SyncType;
import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.UserFavoriteMapper;
import com.seasun.management.model.UserFavorite;
import com.seasun.management.service.UserFavoriteService;
import com.seasun.management.vo.BaseSyncVo;
import com.seasun.management.vo.UserFavoriteSyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserFavoriteServiceImpl extends AbstractSyncService implements UserFavoriteService {
    @Autowired
    private UserFavoriteMapper userFavoriteMapper;

    @Override
    public void sync(BaseSyncVo baseSyncVo) {
        if (!(baseSyncVo instanceof UserFavoriteSyncVo)) {
            throw new ClassCastException("baseSynVo对象 不能转换为 UserFavoriteSyncVo 类");
        }
        UserFavoriteSyncVo userFavoriteSyncVo = (UserFavoriteSyncVo) baseSyncVo;
        UserFavorite userFavorite = userFavoriteSyncVo.getData();
        if (null == userFavorite.getId()) {
            throw new ParamException("id can not be null...");
        }
        if (null == userFavorite.getUserId()) {
            throw new ParamException("user_id can not be null...");
        }

        if (userFavoriteSyncVo.getType().equals(SyncType.add)) {
            userFavoriteMapper.insertWithId(userFavorite);
        } else if (userFavoriteSyncVo.getType().equals(SyncType.update)) {
            userFavoriteMapper.updateByPrimaryKeySelective(userFavorite);
        } else if (userFavoriteSyncVo.getType().equals(SyncType.delete)) {
            userFavoriteMapper.deleteByPrimaryKey(userFavorite.getId());
        }
    }
}
