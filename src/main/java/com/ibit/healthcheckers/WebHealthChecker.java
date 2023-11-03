package com.ibit.healthcheckers;

import com.ibit.models.DatasourceSetting;
import com.ibit.models.HealthCheckInfo;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

@Component("web")
public class WebHealthChecker implements HealthChecker {
    private DatasourceSetting setting;
    @Override
    public HealthChecker setRequest(DatasourceSetting setting) {
        this.setting =  setting;
        return this;
    }

    @Override
    public CompletableFuture<HealthCheckInfo> ping() {
        return CompletableFuture.supplyAsync(()->pingInternal(this.setting));
    }
    private HealthCheckInfo pingInternal(DatasourceSetting setting) {
        var res = new HealthCheckInfo(setting);

        try {
            URL url = new URL(setting.getHealthQuery());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000); // 5 seconds

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                res.status="up";
                return res;
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }
}
