package com.workflow.iptv.master;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class IptvMasterApplication {
    public static void main(String[] args) {
        SpringApplication.run(IptvMasterApplication.class, args);
    }
}