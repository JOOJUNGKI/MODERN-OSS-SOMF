package com.workflow.internet.equipment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class InternetEquipmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(InternetEquipmentApplication.class, args);
    }
}