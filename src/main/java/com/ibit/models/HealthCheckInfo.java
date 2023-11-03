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
    public String status;
    public String error;
    public String link;
    public String timestamp;

    public HealthCheckInfo() {
        status = "down";
    }
    public HealthCheckInfo(DataSourceInfo setting) {
        status = "down";
        name = setting.getName();
        description = setting.getDescription();
        group = setting.getGroup();
        timestamp = getCurrentTime();
        error = "";
        link = "";
    }
}
