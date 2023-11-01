package com.ibit.controllers;

import com.ibit.models.HealthCheckInfo;
import com.ibit.models.HealthCheckInfoList;
import com.ibit.services.HealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
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
    public String info(){
        return "Health Check Service";
    }
    @GetMapping("/ping")
    public ResponseEntity<String> health(){
        return ResponseEntity.ok("healthy");
    }
    @GetMapping("/check")
    public Mono<ResponseEntity<HealthCheckInfoList>> getHealth(){

        try {
            return Mono.just(ResponseEntity.ok(healthCheckService.getHealthCheck()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @MessageMapping("/sendNotification")
    @SendTo("/topic/notifications")
    public HealthCheckInfoList notification(HealthCheckInfoList notification){

        try {
            System.out.println("Sending Notification:" + notification.getHealthCheckInfoList());
            return notification;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
