package com.seasun.management.service.cache;

import com.seasun.management.constant.ErrorMessage;
import com.seasun.management.mapper.ITCacheMapper;
import com.seasun.management.mapper.RUserProjectPermMapper;
import com.seasun.management.model.ITCache;
import com.seasun.management.model.ProjectRole;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.RUserProjectPermVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.List;

@Service
public class CacheServiceImpl implements CacheService {

    private static Logger LOGGER = LoggerFactory.getLogger(CacheServiceImpl.class);
    @Autowired
    ITCacheMapper itCacheMapper;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    private RUserProjectPermMapper rUserProjectPermMapper;

    @Override
    public List<ITCache> getAllITCaches() {
        List<ITCache> caches = itCacheMapper.selectAllCaches();
        return caches;
    }

    @Override
    public String refreshCacheById(Long id) {
        Long currentUserId = MyTokenUtils.getCurrentUserId();
        List<RUserProjectPermVo> rUserProjectPermVos = rUserProjectPermMapper.selectByUserIdAndProjectRoleId(currentUserId, ProjectRole.Role.admin_id);
        if (rUserProjectPermVos.isEmpty()) {
            return "您没有权限";
        }
        ITCache cacheResult = itCacheMapper.selectByPrimaryKey(id);
        if (cacheResult == null) {
            return ErrorMessage.PERSISTENT_LAYER_MESSAGE;
        }
        return refreshCacheByCacheNameAndStoreKey(cacheResult.getHashKey(), cacheResult.getEntryKey());
    }

    @Override
    public void addCache(ITCache itCache) {
        itCacheMapper.insert(itCache);
    }

    @Override
    public String refreshCacheByCacheNameAndStoreKey(String cacheName, Object storeKey) {
        Collection<String> cacheNames = cacheManager.getCacheNames();
        boolean hasHashKeyFlag = false;
        for (String cache : cacheNames) {
            if (cache.equals(cacheName)) {
                hasHashKeyFlag = true;
                break;
            }
        }
        if (!hasHashKeyFlag) {
            return "刷新失败，因为cacheName不存在";
        }
        Cache cache = cacheManager.getCache(cacheName);
        if (storeKey == null) {
            storeKey = new SimpleKey();
        }
        cache.evict(storeKey);
        return null;
    }
}
