package com.seasun.management.service;

import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.vo.BaseSyncVo;

public interface SyncService {

    /**
     * 同步的步骤，子类无需关注。
     *
     * @param baseSyncVo
     * @return
     */
    CommonResponse run(BaseSyncVo baseSyncVo);

}
