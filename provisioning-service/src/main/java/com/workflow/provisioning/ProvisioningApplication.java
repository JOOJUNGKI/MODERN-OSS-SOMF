package com.workflow.provisioning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EntityScan(basePackages = {"com.workflow.provisioning.infrastructure.persistence.entity"})
@EnableJpaRepositories(basePackages = {"com.workflow.provisioning.infrastructure.persistence.repository"})
public class ProvisioningApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProvisioningApplication.class, args);
    }
}