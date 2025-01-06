package com.workflow.iptv.completion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class IptvCompletionApplication {
    public static void main(String[] args) {
        SpringApplication.run(IptvCompletionApplication.class, args);
    }
}