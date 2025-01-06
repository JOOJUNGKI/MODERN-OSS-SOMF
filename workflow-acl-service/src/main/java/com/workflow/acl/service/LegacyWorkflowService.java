package com.workflow.acl.service;

import com.workflow.acl.api.dto.LegacyWorkflowRequest;
import com.workflow.common.event.WorkflowCreationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LegacyWorkflowService {
    private final KafkaTemplate<String, WorkflowCreationEvent> kafkaTemplate;

    @Value("${kafka.topics.internet.creation.request}")
    private String internetCreationTopic;

    @Value("${kafka.topics.iptv.creation.request}")
    private String iptvCreationTopic;

    public String requestWorkflowCreation(LegacyWorkflowRequest request) {
        String requestId = UUID.randomUUID().toString();
        WorkflowCreationEvent event = createEvent(requestId, request);

        String topic = getTopicByServiceType(request.getServiceType());

        kafkaTemplate.send(topic, requestId, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Successfully sent event to {}: {}", topic, event);
                    } else {
                        log.error("Failed to send event to {}: {}", topic, event, ex);
                    }
                });

        return requestId;
    }

    private String getTopicByServiceType(String serviceType) {
        return switch (serviceType) {
            case "Internet" -> internetCreationTopic;
            case "IPTV" -> iptvCreationTopic;
            default -> throw new IllegalArgumentException(
                    "Unsupported service type: " + serviceType);
        };
    }

    private WorkflowCreationEvent createEvent(String requestId, LegacyWorkflowRequest request) {
        return WorkflowCreationEvent.builder()
                .requestId(requestId)
                .orderNumber(request.getOrderNumber())
                .orderSeq(request.getOrderSeq())
                .serviceType(request.getServiceType())
                .orderType(request.getOrderType())
                .custName(request.getCustName())
                .address(request.getAddress())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
