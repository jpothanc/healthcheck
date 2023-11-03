package com.ibit.healthcheckers;

import com.ibit.models.DatasourceSetting;
import com.ibit.models.HealthCheckInfo;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

@Component("database")
public class DbHealthChecker implements HealthChecker {

    private DatasourceSetting setting;
    @Override
    public HealthChecker setRequest(DatasourceSetting setting) {
        this.setting = setting;
        return this;
    }

    @Override
    public CompletableFuture<HealthCheckInfo> ping() {
        return CompletableFuture.supplyAsync(()->pingInternal(this.setting));

    }

    private HealthCheckInfo pingInternal(DatasourceSetting setting) {
        var res = new HealthCheckInfo(setting);
        try (Connection connection = DriverManager.getConnection(
                setting.getConnectionString(),
                setting.getUsername(),
                setting.getPassword())) {
            if (connection != null) {
                System.out.println("Connected to the database");
                Statement statement = connection.createStatement();

                String sqlQuery = setting.getHealthQuery();
                ResultSet resultSet = statement.executeQuery(sqlQuery);
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                Long rowId = 0L;
                // Process the query results
                while (resultSet.next()) {

                }

                resultSet.close();
                statement.close();
                res.status = "up";
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            res.error = e.getMessage();
        }

        return res;
    }
}
