package com.ibit.services;

import com.ibit.cache.MemoryCache;
import com.ibit.config.AppConfig;
import com.ibit.config.ConfigLoader;
import com.ibit.factory.HealthCheckFactory;
import com.ibit.models.HealthCheckInfo;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.ibit.internal.Constants.CACHED_CHECKER_INSTANCES;
import static com.ibit.internal.Constants.CACHED_HEALTH_CHECK_INFO;
import static com.ibit.internal.Helper.getElapsedTime;

/****************************************************************************************
 * HealthCheckServiceImpl is the implementation of the HealthCheckService interface.
 * The class provides methods to start and stop the health check service.
 * The class runs health check for all data sources at regular intervals.
 * The class sends real-time health check notifications to all subscribed clients on the /topic/healthCheck channel.
 ****************************************************************************************/
@Service
@EnableScheduling
public class HealthCheckServiceImpl implements HealthCheckService {
    private static final Logger logger = LoggerFactory.getLogger(HealthCheckServiceImpl.class);
    private ConfigLoader configLoader;
    private AppConfig appConfig;
    private HealthCheckFactory healthCheckFactory;
    private MemoryCache<String, Object> memoryCache;
    private SimpMessagingTemplate messagingTemplate;
    private Disposable disposable;

    @Autowired
    public HealthCheckServiceImpl(ConfigLoader configLoader, AppConfig appConfig,
                                  HealthCheckFactory healthCheckFactory,
                                  MemoryCache<String, Object> memoryCache,
                                  SimpMessagingTemplate messagingTemplate) {

        this.configLoader = configLoader;
        this.appConfig = appConfig;
        this.healthCheckFactory = healthCheckFactory;
        this.memoryCache = memoryCache;
        this.messagingTemplate = messagingTemplate;
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

    /**
     * Enable health check timer.
     * The method creates a timer that runs health check at regular intervals.
     * The interval is defined in the application configuration.
     * The method uses RxJava Observable to create the timer.
     * Timer is used for real-time health check notifications for WebSocket clients.
     */
    private void enableHealthCheckTimer() {

        this.disposable = Observable.interval(appConfig.getHealthCheckInterval(),
                        appConfig.getHealthCheckInterval(), TimeUnit.SECONDS)
                .subscribe(count -> runHealthCheck(), throwable -> System.err.println("Error: " + throwable));
    }

    @Override
    public HealthCheckInfoList getHealthCheck() {
        return runHealthCheck();
    }

    /**
     * Run health check for all data sources.
     * The method creates health checker instances for all data sources and runs health check for each data source.
     * It compares the health check results with the previous health check results and sends notifications to clients if there is a change in health status.
     * The health check results are stored in the memory cache for the next health check.
     * @return
     */
    private HealthCheckInfoList runHealthCheck() {

        var healthCheckerInstances = (HealthCheckerInstances) memoryCache.get(CACHED_CHECKER_INSTANCES);
        if (healthCheckerInstances == null) {
            healthCheckerInstances = createHealthCheckerInstances().orElse(new HealthCheckerInstances());
            logger.info("Creating Health Checker Instances : " + healthCheckerInstances.getHealthCheckers().size());
        }


        Map<String, HealthCheckInfo> healthCheckInfoMap = new HashMap<>();
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
                if (healthCheckInfoMap.containsKey(key))
                    continue;

                healthCheckInfoMap.put(key, res);
                itemsChecked++;
                logger.info("Health Check Result for " + key + " = " + res);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        var healthCheckInfoList = HealthCheckInfoList.builder().
                healthCheckInfoMap(healthCheckInfoMap).
                isHealthy(isHealthy).
                elapsed(getElapsedTime(startTime)).
                items(itemsChecked).
                healthyItems(healthyItems)
                .build().toResult();

        memoryCache.put(CACHED_HEALTH_CHECK_INFO, healthCheckInfoList);

        //commented out the alert check for testing purposes
        // if (alert) {
        logger.info("Health Check detected an change in health status.Sending notifications to clients.");
        sendNotification(healthCheckInfoList);
        // }
        return healthCheckInfoList;
    }

    /**
     * Create health checker instances for all data sources.
     * Health checker instances are created based on the data sources defined in the application configuration.
     *
     * @return
     */
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

    /**
     * The method sends health check notifications to all subscribed clients on the /topic/healthCheck channel.
     *
     * @param healthCheckInfoList
     */
    public void sendNotification(HealthCheckInfoList healthCheckInfoList) {
        //messagingTemplate.convertAndSendToUser("user", "/queue/healthCheck", healthCheckInfoList.toResult());
        messagingTemplate.convertAndSend("/topic/healthCheck", healthCheckInfoList.toResult());
    }
}
