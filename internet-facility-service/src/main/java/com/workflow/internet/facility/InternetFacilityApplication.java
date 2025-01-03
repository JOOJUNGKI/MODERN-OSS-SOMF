package com.workflow.internet.facility;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class InternetFacilityApplication {
    public static void main(String[] args) {
        SpringApplication.run(InternetFacilityApplication.class, args);
    }
}