package com.ibit.controllers;

import com.ibit.cache.MemoryCache;
import com.ibit.internal.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/admin")
public class AdminController {
    MemoryCache<String, Object> memoryCache;

    @Autowired
    public AdminController(MemoryCache<String, Object> memoryCache) {
        this.memoryCache = memoryCache;
    }

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(){
        logger.info("Refreshing Health Check Cache.");
        memoryCache.remove(Constants.CACHED_HEALTH_CHECK_INFO);
        memoryCache.remove(Constants.CACHED_CHECKER_INSTANCES);
        return  ResponseEntity.ok("Success");
    }
}
