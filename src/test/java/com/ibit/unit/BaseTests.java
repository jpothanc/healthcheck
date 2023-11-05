package com.ibit.unit;

import com.ibit.config.AppConfig;
import com.ibit.models.DataSourceInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
}
