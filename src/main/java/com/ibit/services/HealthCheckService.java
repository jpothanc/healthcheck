package com.ibit.services;

import com.ibit.models.HealthCheckInfo;

import java.io.IOException;
import java.util.List;

public interface HealthCheckService {
    List<HealthCheckInfo> getHealthCheck() throws IOException;
}
