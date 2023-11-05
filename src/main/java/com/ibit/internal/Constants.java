package com.ibit.internal;

public class Constants {
    public static final String HEALTH_CHECK_SOCKET_RESPONSE_DESTINATION = "/topic/healthCheck";
    public static final String HEALTH_CHECK_SOCKET_INCOMING_MESSAGE = "/sendHcNotification";

    public static final String CACHED_CHECKER_INSTANCES = "checker-instances";
    public static final String CACHED_HEALTH_CHECK_INFO = "hc-info";

    public static final String HEALTH_CHECK_WEB_GROUP = "web";
    public static final String HEALTH_CHECK_DB_GROUP = "database";
}
