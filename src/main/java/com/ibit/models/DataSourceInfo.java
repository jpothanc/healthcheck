package com.ibit.models;

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

    public String getConnectionString() {
        return String.format(connectionString, username, password);
    }
}
