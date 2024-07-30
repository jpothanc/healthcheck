package com.ibit.models;

import com.ibit.healthcheckers.HealthChecker;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class HealthCheckerInstances {

    private List<HealthChecker> healthCheckers;
    public HealthCheckerInstances() {
        this.healthCheckers = new ArrayList<>(50);
    }

}
