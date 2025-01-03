package com.workflow.internet.acquisition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EntityScan(basePackages = {"com.workflow.internet.acquisition.infrastructure.persistence.entity", "com.workflow.common.entity"})
@EnableJpaRepositories(basePackages = {"com.workflow.internet.acquisition.infrastructure.persistence.repository"})
public class InternetAcquisitionApplication {
    public static void main(String[] args) {
        SpringApplication.run(InternetAcquisitionApplication.class, args);
    }
}