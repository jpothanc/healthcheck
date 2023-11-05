package com.ibit.factory;

import com.ibit.healthcheckers.DbHealthChecker;
import com.ibit.healthcheckers.HealthChecker;
import com.ibit.healthcheckers.WebHealthChecker;
import com.ibit.internal.Constants;
import com.ibit.models.DataSourceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HealthCheckFactoryImpl implements HealthCheckFactory{

    @Override
    public Optional<HealthChecker> getHealthChecker(DataSourceInfo dataSourceInfo) {

        switch (dataSourceInfo.getGroup())
        {
            case Constants.HEALTH_CHECK_WEB_GROUP: {
                return getWebHealthChecker(dataSourceInfo);
            }
            case Constants.HEALTH_CHECK_DB_GROUP:{
                return getDbHealthChecker(dataSourceInfo);
            }
            default:
                return Optional.empty();
        }
    }
    private Optional<HealthChecker> getWebHealthChecker(DataSourceInfo dataSourceInfo){
        var checker = new WebHealthChecker().setDataSource(dataSourceInfo);
        return Optional.of(checker);
    }
    private Optional<HealthChecker> getDbHealthChecker(DataSourceInfo dataSourceInfo){
        var checker = new DbHealthChecker().setDataSource(dataSourceInfo);
        return Optional.of(checker);
    }
}
