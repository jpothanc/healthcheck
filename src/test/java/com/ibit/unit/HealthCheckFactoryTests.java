package com.ibit.unit;

import com.ibit.factory.HealthCheckFactoryImpl;
import com.ibit.healthcheckers.DbHealthChecker;
import com.ibit.healthcheckers.WebHealthChecker;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class HealthCheckFactoryTests extends BaseTests {

    @ParameterizedTest
    @ValueSource(strings = {"web", "database"})
    public void HealthCheck_Factory_Returns_Correct_Checker(String group) {
        var factory = new HealthCheckFactoryImpl();
        var checker = factory.getHealthChecker(getDatSourceInfo(group)).get();

        if (group.equals("web"))
            assertTrue(checker instanceof WebHealthChecker);
        else if (group.equals("database"))
            assertTrue(checker instanceof DbHealthChecker);
        else {
            fail("Unexpected group: " + group);
        }
        assertEquals(checker.getName(), group + "name");
       assertTrue(checker.getDataSource() != null);
        assertEquals(checker.getDataSource().getDescription(),  group + "description");
        assertEquals(checker.getDataSource().getHealthQuery(), group + "query");
        assertEquals(checker.getDataSource().getConnectionString(),  group + "connectionString");

    }

    @ParameterizedTest
    @ValueSource(strings = {"xxx"})
    public void HealthCheck_Factory_Returns_Empty_Checker(String group) {
        var factory = new HealthCheckFactoryImpl();
        var checker = factory.getHealthChecker(getDatSourceInfo(group));
        assertFalse(checker.isPresent());
    }

}
