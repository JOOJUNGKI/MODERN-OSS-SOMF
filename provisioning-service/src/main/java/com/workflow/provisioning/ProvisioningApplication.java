package com.workflow.provisioning;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ProvisioningApplication {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}