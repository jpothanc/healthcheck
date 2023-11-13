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
    private String wiki;
    private boolean isHealthy;
    private String error;
    private String timestamp;
    private String elapsed;
    private String healthQuery;

    public HealthCheckInfo() {
        isHealthy = false;
    }
    public HealthCheckInfo(DataSourceInfo dsInfo) {
        isHealthy = false;
        name = dsInfo.getName();
        description = dsInfo.getDescription();
        group = dsInfo.getGroup();
        healthQuery =dsInfo.getHealthQuery();
        timestamp = getCurrentTime();
        error = "";
        wiki = dsInfo.getWiki();
        elapsed="";
    }
}
