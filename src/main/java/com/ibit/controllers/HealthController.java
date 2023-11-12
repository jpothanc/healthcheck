package com.ibit.controllers;

import com.ibit.internal.Constants;
import com.ibit.models.HealthCheckInfoList;
import com.ibit.services.HealthCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.util.Random;
import java.io.IOException;


@RestController
@RequestMapping("api/v1/health")
public class HealthController {

    @Autowired
    private HealthCheckService healthCheckService;
    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);
    private static boolean useMock = false;
    @GetMapping("/")
    public String info() {
        return "Health Check Service";
    }

    @GetMapping("/ping")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("healthy");
    }

    @GetMapping("/check")
    public Mono<ResponseEntity<HealthCheckInfoList>> getHealth() {

        try {
            return Mono.just(ResponseEntity.ok(healthCheckService.getHealthCheck()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/mock")
    public Mono<ResponseEntity<HealthCheckInfoList>> getMockHealth() {

        try {
            var hc = healthCheckService.getHealthCheck();
            useMock = !useMock;
            if(useMock)
                mockTransform(hc);
            return Mono.just(ResponseEntity.ok(hc));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void mockTransform(HealthCheckInfoList healthCheckInfoList){
        Random random = new Random();
        var index = random.nextInt(4);
        var hcItem = healthCheckInfoList.getHealthCheckInfoSotredList().get(index);
        hcItem.setHealthy(false);
        hcItem.setError("Service is down");
        healthCheckInfoList.setHealthy(false);
        healthCheckInfoList.setUnhealthyItems(1);
        healthCheckInfoList.setHealthyItems(healthCheckInfoList.getItems() -1);
    }

    @MessageMapping(Constants.HEALTH_CHECK_SOCKET_INCOMING_MESSAGE)
    @SendTo("/topic/healthCheck")
    public HealthCheckInfoList healthCheck(HealthCheckInfoList notification) {

        try {
            logger.info("Sending Notification:" + notification.getHealthCheckInfoMap());
            return notification;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
