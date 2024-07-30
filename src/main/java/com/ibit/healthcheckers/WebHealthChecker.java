package com.ibit.healthcheckers;

import com.ibit.models.DataSourceInfo;
import com.ibit.models.HealthCheckInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

import static com.ibit.internal.Helper.getElapsedTime;

/****************************************************************************************
 * WebHealthChecker is a concrete implementation of the HealthChecker interface.
 * The class provides methods to ping the web server and return the health check information.
 ****************************************************************************************/
@Component("web")
public class WebHealthChecker extends HealthChecker {
    private static final Logger logger = LoggerFactory.getLogger(WebHealthChecker.class);


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
            res.setHealthy(responseCode == HttpURLConnection.HTTP_OK);
            connection.disconnect();

        } catch (Exception e) {
            res.setError("Ping Failed :" + e.getMessage());
            logger.error(res.getError());
        } finally {
            res.setElapsed(getElapsedTime(startTime));
        }

        return res;
    }
}
