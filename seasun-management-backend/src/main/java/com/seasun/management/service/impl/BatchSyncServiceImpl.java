package com.seasun.management.service.impl;

import com.seasun.management.helper.SyncHelper;
import com.seasun.management.service.BatchSyncService;
import com.seasun.management.service.SyncService;
import com.seasun.management.vo.BaseSyncVo;
import com.seasun.management.vo.BatchSyncVo;
import com.seasun.management.vo.BatchSyncVo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchSyncServiceImpl extends AbstractSyncService implements BatchSyncService {

    private static final Logger logger = LoggerFactory.getLogger(BatchSyncServiceImpl.class);

    @Override
    public void sync(BaseSyncVo baseSyncVo) {
        if (!(baseSyncVo instanceof BatchSyncVo)) {
            throw new ClassCastException("baseSynVo对象 不能转换为 BatchSyncVo 类");
        }
        BatchSyncVo batchSyncVo = (BatchSyncVo) baseSyncVo;
        List<BatchRequest> requests = batchSyncVo.getRequests();
        for (BatchRequest request : requests) {

            SyncService syncService = SyncHelper.getSyncServiceByTargetName(request.getTargetName());

            BaseSyncVo syncVo = SyncHelper.getSyncVoByTargetName(request.getTargetName(), request.getData());

            syncService.run(syncVo);
        }
    }
}

