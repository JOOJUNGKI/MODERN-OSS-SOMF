package com.workflow.iptv.certification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class IptvCertificationApplication {
    public static void main(String[] args) {
        SpringApplication.run(IptvCertificationApplication.class, args);
    }
}