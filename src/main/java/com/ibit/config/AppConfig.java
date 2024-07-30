package com.ibit.config;


import com.ibit.models.DataSourceInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.Map;

/****************************************************************************************
 * AppConfig is a configuration class.
 * The class holds the application configuration.
 * Configuration is loaded from the appsettings.json file based on the active profile.
 ****************************************************************************************/
@Configuration
@Scope("singleton")
@Getter
@Setter
public class AppConfig {
    private boolean enableHealthCheckTimer;
    private int healthCheckInterval;
    private Map<String, DataSourceInfo> dataSources = new HashMap<>();
}
