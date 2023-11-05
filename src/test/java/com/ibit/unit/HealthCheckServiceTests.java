package com.ibit.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibit.config.AppConfig;
import com.ibit.config.ConfigLoader;
import com.ibit.services.HealthCheckService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
public class HealthCheckServiceTests extends BaseTests {
    @Autowired
    private HealthCheckService healthCheckService;
    @MockBean
    private ConfigLoader configLoader;
    @Autowired
    AppConfig appConfig;

    @Autowired
    private ResourceLoader resourceLoader;

    @Before
    public void setUp() {
        // Initialization logic for each test method
    }

    @Test
    public void Health_Check_Should_Return_Success_For_Valid_DataSources() {
        // Arrange
        try {
            Mockito.doAnswer(in -> {
                loadConfig();
                return null;
            }).when(configLoader).loadConfig();

            // Verify
            healthCheckService.start();
            var hc = healthCheckService.getHealthCheck();

            // Assert
            assertEquals(hc.isHealthy(), true);
            var itemsToCheck = appConfig.getDataSources().size();
            assertTrue(hc.getItems() == itemsToCheck);
            assertTrue(hc.getHealthyItems() == itemsToCheck);
            assertTrue(hc.getUnhealthyItems() == 0);
            assertTrue(!hc.getElapsed().isEmpty());
            assertTrue(!hc.getTimeStamp().isEmpty());


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void Health_Check_Should_Return_Failure_For_InValid_DataSources() {
        // Arrange
        try {
            Mockito.doAnswer(in -> {
                loadConfig();
                appConfig.getDataSources().get("vision").setHealthQuery("https://invalid.com/");
                return null;
            }).when(configLoader).loadConfig();

            // Verify
            healthCheckService.start();
            var hc = healthCheckService.getHealthCheck();

            // Assert
            assertEquals(hc.isHealthy(), false);
            var itemsToCheck = appConfig.getDataSources().size();
            assertTrue(hc.getItems() == itemsToCheck);
            assertTrue(hc.getHealthyItems() == (itemsToCheck - 1));
            assertTrue(hc.getUnhealthyItems() == 1);
            assertTrue(!hc.getElapsed().isEmpty());
            assertTrue(!hc.getTimeStamp().isEmpty());

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void loadConfig() {
        String configFile = "classpath:appsettings-" + activeProfile + ".json";

        ObjectMapper objectMapper = new ObjectMapper();

        Resource resource = resourceLoader.getResource(configFile);
        try {
            originalConfig = objectMapper.readValue(resource.getInputStream(), AppConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        override(originalConfig);


    }

    private void override(AppConfig localConfig) {
        appConfig.setHealthCheckInterval(localConfig.getHealthCheckInterval());
        appConfig.setDataSources(localConfig.getDataSources());
        appConfig.setEnableHealthCheckTimer(localConfig.isEnableHealthCheckTimer());
    }
}