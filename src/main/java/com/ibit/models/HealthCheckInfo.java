package com.ibit.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.ibit.internal.Helper.*;

@Getter
@Setter
@ToString
public class HealthCheckInfo {
    public String name;
    public String description;
    public String group;
    public boolean isHealthy;
    public String error;
    public String link;
    public String timestamp;
    public String elapsed;

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
