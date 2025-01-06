
package com.workflow.acl.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.internet.creation.request}")
    private String internetCreationTopic;

    @Value("${kafka.topics.iptv.creation.request}")
    private String iptvCreationTopic;

    @Bean
    public NewTopic internetCreationTopic() {
        return TopicBuilder.name(internetCreationTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic iptvCreationTopic() {
        return TopicBuilder.name(iptvCreationTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}