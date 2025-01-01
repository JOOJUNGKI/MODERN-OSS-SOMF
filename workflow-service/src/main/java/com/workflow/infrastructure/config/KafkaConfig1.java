
package com.workflow.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaConfig1 {
   @Value("${kafka.topics.creation.request}")
   private String creationTopic;

   @Value("${kafka.topics.step.request}")
   private String stepRequestTopic;

   @Value("${kafka.topics.step.response}") 
   private String stepResponseTopic;

   @Bean
   public NewTopic workflowCreationTopic() {
       return TopicBuilder.name(creationTopic)
               .partitions(3)
               .replicas(1)
               .build();
   }

   @Bean
   public NewTopic stepRequestTopic() {
       return TopicBuilder.name(stepRequestTopic)
               .partitions(3)
               .replicas(1)
               .build();
   }

   @Bean
   public NewTopic stepResponseTopic() {
       return TopicBuilder.name(stepResponseTopic)
               .partitions(3)
               .replicas(1)
               .build();
   }

   @Bean
   public KafkaAdmin.NewTopics topics() {
       return new KafkaAdmin.NewTopics(
           workflowCreationTopic(),
           stepRequestTopic(),
           stepResponseTopic()
       );
   }
}
