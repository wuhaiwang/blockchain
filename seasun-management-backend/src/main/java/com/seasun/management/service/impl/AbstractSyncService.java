package com.seasun.management.service.impl;

import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.exception.ParamException;
import com.seasun.management.exception.SyncException;
import com.seasun.management.service.SyncService;
import com.seasun.management.vo.BaseSyncVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractSyncService implements SyncService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSyncService.class);

    abstract void sync(BaseSyncVo baseSyncVo) throws Exception;

    @Override
    public CommonResponse run(BaseSyncVo baseSyncVo) {
        try {
            logger.info(baseSyncVo.getTargetName() + " sync begin ...");
            this.sync(baseSyncVo);
        } catch (ParamException e) {
            e.printStackTrace();
            logger.error(baseSyncVo.getTargetName() + " param error：{}", e.getMessage());
            throw new ParamException(baseSyncVo.getTargetName() + "param error:" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(baseSyncVo.getTargetName() + " sync failed：{}", e.getMessage());
            throw new SyncException(baseSyncVo.getTargetName() + "sync failed:" + e.getMessage());
        }
        return new CommonResponse(0, "success");
    }
}
