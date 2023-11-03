package com.ibit.healthcheckers;

import com.ibit.models.DatasourceSetting;
import com.ibit.models.HealthCheckInfo;

import java.util.concurrent.CompletableFuture;

public interface HealthChecker {
    HealthChecker setRequest(DatasourceSetting setting);

    CompletableFuture<HealthCheckInfo>  ping();
}
