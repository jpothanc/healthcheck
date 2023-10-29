package com.ibit.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibit.models.HealthCheckInfo;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class HealthCheckServiceImpl implements HealthCheckService{

    private final Environment environment;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    public HealthCheckServiceImpl(Environment environment, ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.environment = environment;
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<HealthCheckInfo> getHealthCheck() throws IOException {
        String configFile = "classpath:" + "health.response" + ".json";
        Resource resource = resourceLoader.getResource(configFile);
        List<HealthCheckInfo> healthCheckInfos = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<HealthCheckInfo>>() {});
        return healthCheckInfos;
    }
}
