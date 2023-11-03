package com.ibit.factory;

import com.ibit.healthcheckers.HealthChecker;
import com.ibit.models.DatasourceSetting;

import java.util.Optional;

public interface HealthCheckFactory {
    Optional<HealthChecker> getHealthChecker(DatasourceSetting setting);
}
