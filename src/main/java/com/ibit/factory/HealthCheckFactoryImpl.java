package com.ibit.factory;

import com.ibit.healthcheckers.DbHealthChecker;
import com.ibit.healthcheckers.HealthChecker;
import com.ibit.healthcheckers.WebHealthChecker;
import com.ibit.models.DataSourceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HealthCheckFactoryImpl implements HealthCheckFactory{

    @Override
    public Optional<HealthChecker> getHealthChecker(DataSourceInfo setting) {

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
    private Optional<HealthChecker> getWebHealthChecker(DataSourceInfo setting){
        var checker = new WebHealthChecker().setDataSource(setting);
        return Optional.of(checker);
    }
    private Optional<HealthChecker> getDbHealthChecker(DataSourceInfo setting){
        var checker = new DbHealthChecker().setDataSource(setting);
        return Optional.of(checker);
    }
}
