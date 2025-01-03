package com.workflow.internet.completion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class InternetCompletionApplication {
    public static void main(String[] args) {
        SpringApplication.run(InternetCompletionApplication.class, args);
    }
}