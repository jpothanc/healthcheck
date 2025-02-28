package com.ibit.models;

import com.ibit.services.EncryptionService;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DataSourceInfo {

    private String name;
    private String description;
    private String group;
    private String type;
    private String connectionString;
    private String username;
    private String password;
    private String healthQuery;
    private String wiki;

    public String getPassword() {
        return getEncryptedPassword(password);
    }

    private String getEncryptedPassword(String password) {
        if (password.startsWith("ENC")) {
            var extractedPassword = password.substring(4, password.length() - 1);
            var key = System.getenv("HC_ENCRYPTION_KEY");
            return EncryptionService.decrypt(extractedPassword, key);
        }
        return password;
    }

    public String getConnectionString() {
        return String.format(connectionString, username, password);
    }
}
