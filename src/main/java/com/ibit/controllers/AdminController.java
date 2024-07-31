package com.ibit.controllers;

import com.ibit.cache.MemoryCache;
import com.ibit.internal.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/admin")
@Slf4j
public class AdminController {
    MemoryCache<String, Object> memoryCache;

    @Autowired
    public AdminController(MemoryCache<String, Object> memoryCache) {
        this.memoryCache = memoryCache;
    }


    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(){
        log.info("Refreshing Health Check Cache.");
        memoryCache.remove(Constants.CACHED_HEALTH_CHECK_INFO);
        memoryCache.remove(Constants.CACHED_CHECKER_INSTANCES);
        return  ResponseEntity.ok("Success");
    }
}
