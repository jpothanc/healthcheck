package com.ibit.controllers;

import com.ibit.models.HealthCheckInfo;
import com.ibit.services.HealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/health")
public class HealthController {

    @Autowired
    private HealthCheckService healthCheckService;
    @GetMapping("/")
    public String Info(){
        return "Health Check";
    }
    @GetMapping("/check")
    public Mono<ResponseEntity<List<HealthCheckInfo>>> getHealth(){

        try {
            return Mono.just(ResponseEntity.ok(healthCheckService.getHealthCheck()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
