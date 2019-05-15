package com.seasun.management.controller.cache;

import com.seasun.management.constant.ErrorCode;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.model.ITCache;
import com.seasun.management.service.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CacheController {

    private static final Logger logger = LoggerFactory.getLogger(CacheController.class);

    @Autowired
    CacheService cacheService;

    @RequestMapping(value = "/apis/auth/caches", method = RequestMethod.GET)
    public ResponseEntity<?> getAllCaches() {
        logger.info("begin getAllCaches...");
        List<ITCache> caches = cacheService.getAllITCaches();
        logger.info("end getAllCaches...");
        return ResponseEntity.ok(caches);
    }

    @RequestMapping(value = "apis/auth/cache", method = RequestMethod.POST)
    public ResponseEntity<?> addCaches(@RequestBody ITCache cache) {
        logger.info("begin addCaches...");
        cacheService.addCache(cache);
        logger.info("end addCaches...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "apis/auth/cache/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> refreshCache(@PathVariable Long id) {
        logger.info("begin refreshCacheById...");
        String result = cacheService.refreshCacheById(id);
        logger.info("end refreshCacheById...");
        if (result == null) {
            return ResponseEntity.ok(new CommonResponse());
        }
        return ResponseEntity.ok(new CommonResponse(ErrorCode.USER_INVALID_OPERATE_ERROR, result));
    }
}
