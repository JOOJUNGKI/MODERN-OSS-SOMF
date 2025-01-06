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
 
    @Value("${kafka.topics.creation.request}")
    private String creationTopic;

    public String requestWorkflowCreation(LegacyWorkflowRequest request) {
        String requestId = UUID.randomUUID().toString();
        WorkflowCreationEvent event = createEvent(requestId, request);

        kafkaTemplate.send(creationTopic, requestId, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Successfully sent event: {}", event);
                    } else {
                        log.error("Failed to send event: {}", event, ex);
                    }
                });

        return requestId;
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
