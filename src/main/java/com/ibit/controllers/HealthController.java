package com.ibit.controllers;

import com.ibit.internal.Constants;
import com.ibit.models.HealthCheckInfoList;
import com.ibit.services.HealthCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Random;


@RestController
@RequestMapping("api/v1/health")
@CrossOrigin(origins = "*") // Allow all origins
@Tag(name = "Health Check Service", description = "API for Health Check Service")
public class HealthController {

    @Autowired
    private HealthCheckService healthCheckService;
    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);
    private static boolean useMock = false;
    @GetMapping("/")
    public String info() {
        return "Health Check Service";
    }

    @Operation(summary = "Health Check Service Ping", description = "Health Check Service Ping")
    @GetMapping("ping")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("healthy");
    }

    @Operation(summary = "Health Check Service Check", description = "Get all health check information")
    @GetMapping("/check")
    public Mono<ResponseEntity<HealthCheckInfoList>> getHealth() {

        try {
            return Mono.just(ResponseEntity.ok(healthCheckService.getHealthCheck()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Operation(summary = "Health Check Service Mock", description = "Get all health check information with mock data")
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
        var hcItem = healthCheckInfoList.getHealthCheckInfoSortedList().get(index);
        hcItem.setHealthy(false);
        hcItem.setError("Service is down");
        healthCheckInfoList.setHealthy(false);
        healthCheckInfoList.setUnhealthyItems(1);
        healthCheckInfoList.setHealthyItems(healthCheckInfoList.getItems() -1);
        healthCheckInfoList.toResult();
    }

    /**
     * The method processes health check notifications sent by clients through WebSocket and broadcasts
     * these notifications to all subscribed clients on the /topic/healthCheck channel.
     * It also logs the health check information for monitoring purposes.
     * @param notification
     * @return
     */
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
