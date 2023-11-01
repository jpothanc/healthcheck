package com.ibit.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibit.config.AppConfig;
import com.ibit.config.ConfigLoader;
import com.ibit.models.HealthCheckInfo;
import com.ibit.models.HealthCheckInfoList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
@EnableScheduling
public class HealthCheckServiceImpl implements HealthCheckService{

    @Value("${scheduled.fixedDelay}")
    private long fixedDelay;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    ConfigLoader configLoader;
    @Autowired
    AppConfig appConfig;
    private final Environment environment;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(HealthCheckServiceImpl.class);

    public HealthCheckServiceImpl(Environment environment, ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.environment = environment;
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    @Override
    public void start() {
        try {
            logger.info("Starting HealthCheckService.");
            configLoader.loadConfig();
        } catch (Exception e) {
            logger.error("HealthCheckService Initialization failed." + e.getMessage());
        }
    }

    @Override
    public void stop() {
        logger.info("Stopping HealthCheckService.");
    }

    @Override
    public HealthCheckInfoList getHealthCheck() throws IOException {
        String configFile = "classpath:" + "health.response" + ".json";
        Resource resource = resourceLoader.getResource(configFile);
        var healthCheckInfoList = new HealthCheckInfoList();
        healthCheckInfoList.setHealthCheckInfoList(objectMapper.readValue(resource.getInputStream(), new TypeReference<List<HealthCheckInfo>>() {}));
        return healthCheckInfoList;
    }
    @Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
    //@Scheduled(cron  = "${scheduled.cronExpression}")
    public void sendNotification() {
        try {
            logger.info("Sending HealthCheck Notification");
            messagingTemplate.convertAndSend("/topic/notifications", getHealthCheck());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
