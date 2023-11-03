package com.ibit.healthcheckers;

import com.ibit.models.DataSourceInfo;
import com.ibit.models.HealthCheckInfo;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

@Component("database")
public class DbHealthChecker implements HealthChecker {

    private DataSourceInfo dataSourceInfo;

    @Override
    public HealthChecker setDataSource(DataSourceInfo setting) {
        this.dataSourceInfo = setting;
        return this;
    }

    @Override
    public CompletableFuture<HealthCheckInfo> ping() {
        return CompletableFuture.supplyAsync(() -> pingInternal(this.dataSourceInfo));
    }

    private HealthCheckInfo pingInternal(DataSourceInfo setting) {

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
