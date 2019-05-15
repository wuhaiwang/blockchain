package com.seasun.management.service.impl;

import com.seasun.management.constant.SyncType;
import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.UserBankCardMapper;
import com.seasun.management.model.UserBankCard;
import com.seasun.management.service.UserBankCardService;
import com.seasun.management.util.MyEncryptorUtils;
import com.seasun.management.vo.BaseSyncVo;
import com.seasun.management.vo.UserBankCardSyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserBankCardServiceImpl extends AbstractSyncService implements UserBankCardService {
    @Autowired
    private UserBankCardMapper userBankCardMapper;

    @Override
    public void sync(BaseSyncVo baseSyncVo) throws Exception {

        if (!(baseSyncVo instanceof UserBankCardSyncVo)) {
            throw new ClassCastException("baseSynVo对象 不能转换为 UserBankCardSyncVo 类");
        }
        UserBankCardSyncVo userBankCardSyncVo = (UserBankCardSyncVo) baseSyncVo;
        UserBankCard userBankCard = userBankCardSyncVo.getData();
        if (null == userBankCard.getId()) {
            throw new ParamException("id can not be null...");
        }
        if (null == userBankCard.getUserId()) {
            throw new ParamException("user_id can not be null...");
        }

        if (userBankCardSyncVo.getType().equals(SyncType.add)) {
            userBankCardMapper.setActiveFalseByUserId(userBankCard.getUserId());

            userBankCard.setCardBin(MyEncryptorUtils.encryptByAES(userBankCard.getCardBin()));
            userBankCardMapper.insertWithId(userBankCard);
        } else if (userBankCardSyncVo.getType().equals(SyncType.delete)) {
            userBankCardMapper.deleteByPrimaryKey(userBankCard.getId());
        }
    }
}
