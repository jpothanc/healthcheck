package com.ibit.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HealthCheckInfo {
    public String name;
    public String description;
    public String group;
    public String status;
    public String error;
    public String link;
    public String timestamp;
}
