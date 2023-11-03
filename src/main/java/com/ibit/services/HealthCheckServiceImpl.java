package com.ibit.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibit.config.AppConfig;
import com.ibit.config.ConfigLoader;
import com.ibit.factory.HealthCheckFactory;
import com.ibit.internal.Constants;
import com.ibit.models.HealthCheckInfo;
import com.ibit.models.HealthCheckInfoList;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
public class HealthCheckServiceImpl implements HealthCheckService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    ConfigLoader configLoader;
    @Autowired
    AppConfig appConfig;
    @Autowired
    HealthCheckFactory healthCheckFactory;
    private final Environment environment;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(HealthCheckServiceImpl.class);
    private Disposable disposable;

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

            logger.info("Health Check Timer Enabled = " + appConfig.isEnableHealthCheckTimer());
            if (appConfig.isEnableHealthCheckTimer()) {
                enableHealthCheckTimer();
            }

        } catch (Exception e) {
            logger.error("HealthCheckService Initialization failed." + e.getMessage());
        }
    }

    @Override
    public void stop() {
        this.disposable.dispose();
        logger.info("Stopping HealthCheckService.");
    }

    private void enableHealthCheckTimer() {
        this.disposable = Observable.interval(appConfig.getHealthCheckInterval(), appConfig.getHealthCheckInterval(), TimeUnit.SECONDS).subscribe(count -> runTimelyHealthCheck(), throwable -> System.err.println("Error: " + throwable));
    }

    @Override
    public HealthCheckInfoList getHealthCheck() throws IOException {
        String configFile = "classpath:" + "health.response" + ".json";
        Resource resource = resourceLoader.getResource(configFile);
        var healthCheckInfoList = new HealthCheckInfoList();
        healthCheckInfoList.setHealthCheckInfoList(objectMapper.readValue(resource.getInputStream(), new TypeReference<List<HealthCheckInfo>>() {
        }));
        return healthCheckInfoList;
    }

    private void runTimelyHealthCheck() {
        logger.info("getDataSources = " + appConfig.getDataSources().size());
        appConfig.getDataSources().keySet().forEach(x -> System.out.println(x.toString()));
        var dataSources = appConfig.getDataSources();
        if (dataSources == null) return;

        var healthCheckInfoList = new HealthCheckInfoList();

        for (var key : dataSources.keySet()) {
            var ds = appConfig.getDataSources().get(key);
            if (ds == null) continue;
            var checker = healthCheckFactory.getHealthChecker(ds);

            try {
                var res = checker.get().ping().get();
                healthCheckInfoList.getHealthCheckInfoList().add(res);
                System.out.println("Health Check Result for " + key + " = " + res);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            sendNotification(healthCheckInfoList);
        }
    }

    public void sendNotification(HealthCheckInfoList healthCheckInfoList) {
        logger.info("Sending HealthCheck Notification");
        messagingTemplate.convertAndSend(Constants.HEALTH_CHECK_SOCKET_RESPONSE_DESTINATION, healthCheckInfoList);
    }
}
