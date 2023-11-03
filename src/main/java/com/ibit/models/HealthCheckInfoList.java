package com.ibit.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class HealthCheckInfoList {

    private List<HealthCheckInfo> healthCheckInfoList;
    public HealthCheckInfoList() {
        this.healthCheckInfoList = new ArrayList<>(50);
    }
}
