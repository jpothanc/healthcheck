package com.ibit.unit;

import com.ibit.config.AppConfig;
import com.ibit.models.DataSourceInfo;
import com.ibit.models.HealthCheckInfo;
import com.ibit.models.HealthCheckInfoList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class BaseTests {
    @Value("${spring.profiles.active}")
    protected String activeProfile;
    protected static AppConfig originalConfig;
    protected DataSourceInfo getDatSourceInfo(String group) {
        return new DataSourceInfo() {{
            setGroup(group);
            setConnectionString(group + "connectionString");
            setName(group + "name");
            setDescription(group + "description");
            setHealthQuery(group + "query");
        }};
    }

    protected Boolean validateHealthCheckItems(HealthCheckInfoList hc, List<String> healthyDsNames, List<String> unHealthyDsNames){

        var healthy = unHealthyDsNames.size() == 0;
        assertEquals(hc.isHealthy(), healthy);
        var itemsToCheck = 3;
        assertTrue(hc.getItems() == itemsToCheck);
        assertTrue(hc.getHealthyItems() == (itemsToCheck - unHealthyDsNames.size()));
        assertTrue(hc.getUnhealthyItems() == unHealthyDsNames.size());
        assertTrue(!hc.getElapsed().isEmpty());

        for (var name:healthyDsNames) {
            assertTrue(hc.getHealthCheckInfoMap().containsKey(name));
            var hcInfo = hc.getHealthCheckInfoMap().get(name);
            assertTrue(validateHealthCheckInfo(hcInfo,true));

        }
        for (var name:unHealthyDsNames) {
            assertTrue(hc.getHealthCheckInfoMap().containsKey(name));
            var hcInfo = hc.getHealthCheckInfoMap().get(name);
            assertTrue(validateHealthCheckInfo(hcInfo,false));
        }

        if(unHealthyDsNames.size() > 0) {
            var list = hc.toResult();
            assertTrue(list.get(0).isHealthy() == false);
        }

        return true;
    }

    private Boolean validateHealthCheckInfo(HealthCheckInfo hcInfo, Boolean isHealthy){

        assertTrue(hcInfo.isHealthy() == isHealthy);

        if(isHealthy)
            assertTrue(hcInfo.getError().isEmpty());
        else
            assertFalse(hcInfo.getError().isEmpty());

        assertFalse(hcInfo.getGroup().isEmpty());
        assertFalse(hcInfo.getTimestamp().isEmpty());
        assertFalse(hcInfo.getElapsed().isEmpty());
        assertFalse(hcInfo.getDescription().isEmpty());
        return true;
    }

    protected Pair<List<String>, List<String>> getTestDataSourceNames(String[] excludes){

        var allDs = new ArrayList<> (List.of("referenceData","vision","productService"));
        List<String> excludeDs;

        if(excludes.length > 0) {
            excludeDs = new ArrayList<>(List.of(excludes));
            for (int i = allDs.size(); --i >= 0; ) {
                if (excludeDs.contains(allDs.get(i)))
                    allDs.remove(i);
            }
        }
        else {
            excludeDs = new ArrayList<String>();
        }
        return new Pair<>(allDs, excludeDs);
    }
}
