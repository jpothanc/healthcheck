package com.ibit.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static com.ibit.internal.Helper.getCurrentTime;

@Setter
@Getter
public class HealthCheckInfoList {

    private boolean isHealthy;
    private int items;
    private int healthyItems;
    private int unhealthyItems;
    private String timeStamp;
    private String elapsed;

    @JsonProperty("healthCheckItems")
    private List<HealthCheckInfo> healthCheckInfoSotredList;

    @JsonIgnore
    private Map<String, HealthCheckInfo> healthCheckInfoMap;


    public HealthCheckInfoList() {
        this.healthCheckInfoMap = new HashMap<>();
        this.healthCheckInfoSotredList = new ArrayList<>(50);
    }

    public HealthCheckInfoList toResult(){

        this.healthCheckInfoSotredList.clear();
        setTimeStamp(getCurrentTime());
        setUnhealthyItems(items - healthyItems);

        this.healthCheckInfoSotredList = new ArrayList<>(this.healthCheckInfoMap.values());
        sortOnHealthStatus(this.healthCheckInfoSotredList);
        return this;
    }

    private void sortOnHealthStatus(List<HealthCheckInfo> healthCheckInfoList){
        Comparator<HealthCheckInfo> comparator = (o1, o2) -> {

            if (!o1.isHealthy() && o2.isHealthy()) {
                return -1; // o1 comes before o2
            } else if (o1.isHealthy() && !o2.isHealthy()) {
                return 1; // o2 comes before o1
            } else {
                return 0; // o1 and o2 are considered equal
            }
        };
        this.healthCheckInfoSotredList.sort(comparator);
    }
}
