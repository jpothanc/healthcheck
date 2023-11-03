package com.ibit.factory;

import com.ibit.healthcheckers.DbHealthChecker;
import com.ibit.healthcheckers.HealthChecker;
import com.ibit.healthcheckers.WebHealthChecker;
import com.ibit.models.DatasourceSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HealthCheckFactoryImpl implements HealthCheckFactory{


    private final ApplicationContext applicationContext;
    @Autowired
    public HealthCheckFactoryImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Optional<HealthChecker> getHealthChecker(DatasourceSetting setting) {

        switch (setting.getGroup())
        {
            case "web": {
                return getWebHealthChecker(setting);
            }
            case "database":{
                return getDbHealthChecker(setting);
            }
            default:
                return Optional.empty();
        }
    }
    private Optional<HealthChecker> getWebHealthChecker(DatasourceSetting setting){
        var checker = new WebHealthChecker().setRequest(setting);
        return Optional.of(checker);
    }
    private Optional<HealthChecker> getDbHealthChecker(DatasourceSetting setting){
        var checker = new DbHealthChecker().setRequest(setting);
        return Optional.of(checker);
    }
}
