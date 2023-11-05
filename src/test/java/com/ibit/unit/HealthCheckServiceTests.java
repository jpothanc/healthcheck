package com.ibit.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibit.config.AppConfig;
import com.ibit.config.ConfigLoader;
import com.ibit.services.HealthCheckService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

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
            var pair = getTestDataSourceNames(new String[]{});
            assertTrue(validateHealthCheckItems(hc, pair.getValue0(), pair.getValue1()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @ParameterizedTest
    @ValueSource(strings = {"vision", "productService"})
    public void Health_Check_Should_Return_Failure_For_InValid_DataSources(String unhealthyDs) {

        // Arrange
        try {
            Mockito.doAnswer(in -> {
                loadConfig();
                appConfig.getDataSources().get(unhealthyDs).setHealthQuery("https://invalid.com/");
                return null;
            }).when(configLoader).loadConfig();

            // Verify
            healthCheckService.start();
            var hc = healthCheckService.getHealthCheck();

            // Assert
            var pair = getTestDataSourceNames(new String[]{unhealthyDs});
            assertTrue(validateHealthCheckItems(hc, pair.getValue0(), pair.getValue1()));

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