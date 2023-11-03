package com.ibit.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DatasourceSetting {
    private String name;
    private String description;
    private String group;
    private String type;
    private String connectionString;
    private String username;
    private String password;
    private String healthQuery;

    public String getConnectionString() {
        return String.format(connectionString, username, password);
    }
}
