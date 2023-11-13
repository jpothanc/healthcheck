package com.ibit.healthcheckers;

import com.ibit.models.DataSourceInfo;
import com.ibit.models.HealthCheckInfo;
import com.ibit.services.HealthCheckServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

import static com.ibit.internal.Helper.getElapsedTime;

@Component("web")
public class WebHealthChecker implements HealthChecker {
    private DataSourceInfo dataSourceInfo;
    private static final Logger logger = LoggerFactory.getLogger(HealthCheckServiceImpl.class);

    @Override
    public HealthChecker setDataSource(DataSourceInfo setting) {
        this.dataSourceInfo = setting;
        return this;
    }

    @Override
    public DataSourceInfo getDataSource() {
        return this.dataSourceInfo;
    }

    @Override
    public String getName() {
        return this.dataSourceInfo.getName();
    }

    @Override
    public CompletableFuture<HealthCheckInfo> ping() {
        return CompletableFuture.supplyAsync(() -> pingInternal(this.dataSourceInfo));
    }

    private HealthCheckInfo pingInternal(DataSourceInfo dsInfo) {

        var res = new HealthCheckInfo(dsInfo);
        long startTime = System.currentTimeMillis();

        try {
            URL url = new URL(dsInfo.getHealthQuery());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
                res.setHealthy(true);

            connection.disconnect();
        } catch (Exception e) {
            res.setError("Ping Failed :" + e.getMessage());
            logger.error(res.getError());
        }
        finally {
            res.setElapsed(getElapsedTime(startTime));
        }

        return res;
    }
}
