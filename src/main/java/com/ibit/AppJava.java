package com.ibit;

import com.ibit.services.EncryptionService;
import com.ibit.services.HealthCheckService;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SpringBootApplication
public class AppJava implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(AppJava.class, args);
        var app = applicationContext.getBean(HealthCheckService.class);
        app.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppJava.applicationContext = applicationContext;
    }

}
