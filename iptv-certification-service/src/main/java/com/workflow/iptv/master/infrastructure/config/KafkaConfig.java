
package com.workflow.iptv.master.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaConfig {
    @Value("${kafka.topics.iptv.step.request}")
    private String iptvStepRequestTopic;

    @Value("${kafka.topics.iptv.step.response}")
    private String iptvStepResponseTopic;

    @Bean
    public NewTopic iptvStepRequestTopic() {
        return TopicBuilder.name(iptvStepRequestTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic iptvStepResponseTopic() {
        return TopicBuilder.name(iptvStepResponseTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public KafkaAdmin.NewTopics topics() {
        return new KafkaAdmin.NewTopics(
                iptvStepRequestTopic(),
                iptvStepResponseTopic()
        );
    }
}
