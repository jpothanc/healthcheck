package com.ibit.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.ibit.internal.Helper.*;

@Getter
@Setter
@ToString
public class HealthCheckInfo {
    private String name;
    private String description;
    private String group;
    private boolean isHealthy;
    private String error;
    private String link;
    private String timestamp;
    private String elapsed;

    public HealthCheckInfo() {
        isHealthy = false;
    }
    public HealthCheckInfo(DataSourceInfo setting) {
        isHealthy = false;
        name = setting.getName();
        description = setting.getDescription();
        group = setting.getGroup();
        timestamp = getCurrentTime();
        error = "";
        link = "";
        elapsed="";
    }
}
