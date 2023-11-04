package com.ibit.healthcheckers;

import com.ibit.models.DataSourceInfo;
import com.ibit.models.HealthCheckInfo;

import java.util.concurrent.CompletableFuture;

public interface HealthChecker {
    HealthChecker setDataSource(DataSourceInfo setting);

    DataSourceInfo getDataSource();

    String getName();

    CompletableFuture<HealthCheckInfo> ping();
}
