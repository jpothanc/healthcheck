package com.ibit.healthcheckers;

import com.ibit.models.DataSourceInfo;
import com.ibit.models.HealthCheckInfo;
import lombok.Getter;

import java.util.concurrent.CompletableFuture;

@Getter
public abstract class HealthChecker {

    protected DataSourceInfo dataSourceInfo;

    public DataSourceInfo getDataSource() {
        return this.dataSourceInfo;
    }

    public HealthChecker setDataSource(DataSourceInfo dsInfo) {
        this.dataSourceInfo = dsInfo;
        return this;
    }

    public String getName() {
        return this.dataSourceInfo.getName();
    }

    public abstract CompletableFuture<HealthCheckInfo> ping();
}
