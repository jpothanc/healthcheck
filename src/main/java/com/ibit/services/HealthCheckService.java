package com.ibit.services;

import com.ibit.models.HealthCheckInfo;
import com.ibit.models.HealthCheckInfoList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.IOException;
import java.util.List;

public interface HealthCheckService {
     void start();
     void stop();
     HealthCheckInfoList getHealthCheck() throws IOException;
}
