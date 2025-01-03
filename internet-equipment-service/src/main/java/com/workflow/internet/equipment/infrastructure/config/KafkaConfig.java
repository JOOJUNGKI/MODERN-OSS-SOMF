
package com.workflow.internet.equipment.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaConfig {
    @Value("${kafka.topics.internet.step.request}")
    private String internetStepRequestTopic;

    @Value("${kafka.topics.internet.step.response}")
    private String internetStepResponseTopic;

    @Bean
    public NewTopic internetStepRequestTopic() {
        return TopicBuilder.name(internetStepRequestTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic internetStepResponseTopic() {
        return TopicBuilder.name(internetStepResponseTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public KafkaAdmin.NewTopics topics() {
        return new KafkaAdmin.NewTopics(
                internetStepRequestTopic(),
                internetStepResponseTopic()
        );
    }
}
