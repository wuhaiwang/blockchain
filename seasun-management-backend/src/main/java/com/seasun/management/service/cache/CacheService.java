package com.seasun.management.service.cache;

import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.model.ITCache;

import java.util.List;

public interface CacheService {

    List<ITCache> getAllITCaches();

    String refreshCacheById(Long id);

    void addCache(ITCache itCache);

    String refreshCacheByCacheNameAndStoreKey(String cacheName,Object storeKey);

}
