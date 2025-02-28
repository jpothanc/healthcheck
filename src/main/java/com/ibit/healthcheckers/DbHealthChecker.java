package com.ibit.healthcheckers;

import com.ibit.models.DataSourceInfo;
import com.ibit.models.HealthCheckInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

import static com.ibit.internal.Helper.getElapsedTime;

/****************************************************************************************
 * DbHealthChecker is a concrete implementation of the HealthChecker interface.
 * The class provides methods to ping the database and return the health check information.
 ****************************************************************************************/
@Component("database")
@Slf4j
public class DbHealthChecker extends HealthChecker {
    public static boolean healthy = false;

    @Override
    public CompletableFuture<HealthCheckInfo> ping() {
        return CompletableFuture.supplyAsync(() -> pingInternal(this.dataSourceInfo));
    }

    private HealthCheckInfo pingInternal(DataSourceInfo dsInfo) {

        var res = new HealthCheckInfo(dsInfo);
        long startTime = System.currentTimeMillis();

        healthy = !healthy;

        try (Connection connection = DriverManager.getConnection(
                dsInfo.getConnectionString(),
                dsInfo.getUsername(),
                dsInfo.getPassword())) {

            if (connection != null) {
                System.out.println("Connected to the database" + dsInfo.getName());
                Statement statement = connection.createStatement();

                String sqlQuery = dsInfo.getHealthQuery();
                ResultSet resultSet = statement.executeQuery(sqlQuery);
                ResultSetMetaData metaData = resultSet.getMetaData();

                while (resultSet.next()) {
                }

                resultSet.close();
                statement.close();
                res.setHealthy(true);

            }
        } catch (SQLException e) {
            res.setError("Ping Failed :" + e.getMessage());
            log.error(res.getError());
        } finally {
            res.setElapsed(getElapsedTime(startTime));
        }

        return res;
    }
}
