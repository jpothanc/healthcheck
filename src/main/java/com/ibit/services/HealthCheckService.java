package com.ibit.services;

import com.ibit.models.HealthCheckInfoList;

import java.io.IOException;

public interface HealthCheckService {
    void start();

    void stop();

    HealthCheckInfoList getHealthCheck() throws IOException;
}
