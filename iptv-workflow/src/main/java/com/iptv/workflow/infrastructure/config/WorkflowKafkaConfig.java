package com.iptv.workflow.infrastructure.config;

import com.workflow.common.event.WorkflowCreationEvent;
import com.workflow.common.event.WorkflowStepEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WorkflowKafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    // Producer Configuration
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.TYPE_MAPPINGS,
                "workflow-creation:com.workflow.common.event.WorkflowCreationEvent," +
                        "workflow-step:com.workflow.common.event.WorkflowStepEvent");
        return props;
    }

    @Bean
    public ProducerFactory<String, WorkflowStepEvent> stepEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean(name = "workflowKafkaTemplate")
    public KafkaTemplate<String, WorkflowStepEvent> stepEventKafkaTemplate() {
        return new KafkaTemplate<>(stepEventProducerFactory());
    }

    // Consumer Configurations
    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return props;
    }

    @Bean
    public ConsumerFactory<String, WorkflowCreationEvent> workflowCreationConsumerFactory() {
        JsonDeserializer<WorkflowCreationEvent> deserializer = new JsonDeserializer<>(WorkflowCreationEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, WorkflowCreationEvent> workflowCreationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, WorkflowCreationEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(workflowCreationConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, WorkflowStepEvent> workflowStepConsumerFactory() {
        JsonDeserializer<WorkflowStepEvent> deserializer = new JsonDeserializer<>(WorkflowStepEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, WorkflowStepEvent> workflowStepKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, WorkflowStepEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(workflowStepConsumerFactory());
        return factory;
    }
}