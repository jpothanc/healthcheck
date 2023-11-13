package com.ibit.internal;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Helper {
    public static String getCurrentTime(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss (dd-MMM-yy)");
        return LocalDateTime.now().format(formatter);
    }
    public static String getElapsedTime(long startTime){
        return  (System.currentTimeMillis() - startTime) + " ms";
    }
}
