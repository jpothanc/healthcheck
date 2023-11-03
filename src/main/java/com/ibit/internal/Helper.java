package com.ibit.internal;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Helper {
    public static String getCurrentTime(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }
}
