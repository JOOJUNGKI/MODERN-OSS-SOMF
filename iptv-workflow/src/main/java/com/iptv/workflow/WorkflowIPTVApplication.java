
package com.iptv.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/* */

@SpringBootApplication(scanBasePackages = {
        "com.iptv.workflow",
        "com.workflow.common"  // workflow-common의 base package
})
//@EntityScan("com.workflow.domain.model")
@EntityScan(basePackages = {"com.iptv.workflow.infrastructure.persistence.entity"})
@EnableJpaRepositories(basePackages = {"com.iptv.workflow.infrastructure.persistence.repository"})

public class WorkflowIPTVApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkflowIPTVApplication.class, args);
    }
}
