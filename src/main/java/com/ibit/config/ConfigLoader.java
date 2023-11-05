package com.ibit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibit.services.HealthCheckServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

@Configuration
public class ConfigLoader {
    @Value("${spring.profiles.active}")
    private String activeProfile;
    @Autowired
    private AppConfig appConfig;

    private final Environment environment;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(HealthCheckServiceImpl.class);

    @Autowired
    public ConfigLoader(Environment environment, ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.environment = environment;
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
        this.appConfig = appConfig;
    }

    public void loadConfig() throws IOException {
        String configFile = "classpath:appsettings-" + activeProfile + ".json";
        logger.info("Loading configuration : " + configFile);

        Resource resource = resourceLoader.getResource(configFile);
        var appConfig = objectMapper.readValue(resource.getInputStream(), AppConfig.class);
        override(appConfig);
    }

    private void override(AppConfig localConfig) {
        appConfig.setHealthCheckInterval(localConfig.getHealthCheckInterval());
        appConfig.setDataSources(localConfig.getDataSources());
        appConfig.setEnableHealthCheckTimer(localConfig.isEnableHealthCheckTimer());
    }
}

