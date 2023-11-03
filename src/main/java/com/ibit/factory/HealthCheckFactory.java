package com.ibit.factory;

import com.ibit.healthcheckers.HealthChecker;
import com.ibit.models.DataSourceInfo;

import java.util.Optional;

public interface HealthCheckFactory {
    Optional<HealthChecker> getHealthChecker(DataSourceInfo setting);
}
