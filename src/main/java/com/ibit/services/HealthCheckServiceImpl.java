package com.ibit.services;

import com.ibit.cache.MemoryCache;
import com.ibit.config.AppConfig;
import com.ibit.config.ConfigLoader;
import com.ibit.factory.HealthCheckFactory;
import com.ibit.models.HealthCheckInfoList;
import com.ibit.models.HealthCheckerInstances;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.ibit.internal.Constants.CACHED_CHECKER_INSTANCES;
import static com.ibit.internal.Constants.CACHED_HEALTH_CHECK_INFO;
import static com.ibit.internal.Helper.getElapsedTime;

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

    @Autowired
    MemoryCache<String, Object> memoryCache;

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckServiceImpl.class);
    private Disposable disposable;

    public HealthCheckServiceImpl() {
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
        this.disposable = Observable.interval(appConfig.getHealthCheckInterval(), appConfig.getHealthCheckInterval(), TimeUnit.SECONDS).subscribe(count -> runHealthCheck(), throwable -> System.err.println("Error: " + throwable));
    }

    @Override
    public HealthCheckInfoList getHealthCheck() {
        return runHealthCheck();
    }

    private HealthCheckInfoList runHealthCheck() {

        var healthCheckerInstances = (HealthCheckerInstances) memoryCache.get(CACHED_CHECKER_INSTANCES);
        if (healthCheckerInstances == null) {
            healthCheckerInstances = createHealthCheckerInstances().orElse(new HealthCheckerInstances());
            logger.info("Creating Health Checker Instances : " + healthCheckerInstances.getHealthCheckers().size());
        }

        var healthCheckInfoList = new HealthCheckInfoList();
        var previousHc = (HealthCheckInfoList) memoryCache.get(CACHED_HEALTH_CHECK_INFO);
        if (previousHc != null) {
            System.out.println("Previous Count: " + previousHc.getHealthCheckInfoMap().size());
        }

        boolean isHealthy = true;
        boolean alert = false;
        int healthyItems = 0;
        int itemsChecked = 0;
        long startTime = System.currentTimeMillis();
        for (var checker : healthCheckerInstances.getHealthCheckers()) {

            try {
                var key = checker.getName();
                var res = checker.ping().get();
                if (res.isHealthy())
                    healthyItems++;

                isHealthy = isHealthy && res.isHealthy();


                if (previousHc != null && previousHc.getHealthCheckInfoMap().containsKey(key)) {
                    var prev = previousHc.getHealthCheckInfoMap().get(key);
                    alert = (prev.isHealthy() != res.isHealthy());
                }
                if(healthCheckInfoList.getHealthCheckInfoMap().containsKey(key))
                    continue;

                healthCheckInfoList.getHealthCheckInfoMap().put(key, res);
                itemsChecked++;
                logger.info("Health Check Result for " + key + " = " + res);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        healthCheckInfoList.setHealthy(isHealthy);
        healthCheckInfoList.setElapsed(getElapsedTime(startTime));
        healthCheckInfoList.setItems(itemsChecked);
        healthCheckInfoList.setHealthyItems(healthyItems);
        healthCheckInfoList.toResult();

        memoryCache.put(CACHED_HEALTH_CHECK_INFO, healthCheckInfoList);

       // if (alert) {
            logger.info("Health Check detected an change in health status.Sending notifications to clients.");
            sendNotification(healthCheckInfoList);
       // }
        return healthCheckInfoList;
    }

    private Optional<HealthCheckerInstances> createHealthCheckerInstances() {
        var dataSources = appConfig.getDataSources();
        if (dataSources == null) return Optional.of(null);

        var instances = new HealthCheckerInstances();
        for (var key : dataSources.keySet()) {
            var ds = appConfig.getDataSources().get(key);
            if (ds == null) continue;
            var checker = healthCheckFactory.getHealthChecker(ds);
            if (checker.isEmpty()) {
                logger.info("Could not create health checker for :" + ds.getName());
                continue;
            }
            instances.getHealthCheckers().add(checker.get());
        }
        return Optional.of(instances);
    }



    public void sendNotification(HealthCheckInfoList healthCheckInfoList) {

        messagingTemplate.convertAndSend("/topic/healthCheck", healthCheckInfoList.toResult());
    }
}
