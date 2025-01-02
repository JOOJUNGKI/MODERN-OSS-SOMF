package com.workflow.infrastructure.messaging.consumer;

import com.workflow.api.dto.WorkflowResponse;
import com.workflow.domain.event.WorkflowCreationEvent;
import com.workflow.domain.model.workflow.Workflow;
import com.workflow.domain.service.WorkflowService;
import com.workflow.infrastructure.messaging.publisher.WorkflowStepRequestPublisher;
import com.workflow.infrastructure.persistence.mapper.WorkflowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowCreationConsumer {
    private final WorkflowService workflowService;
    private final WorkflowStepRequestPublisher stepRequestPublisher;
    private final WorkflowMapper workflowMapper;

    @KafkaListener(topics = "${kafka.topics.creation.request}")
    public void handleWorkflowCreation(WorkflowCreationEvent event) {
        log.info("Received workflow creation request: {}", event);
        try {
            workflowService.handleWorkflowCreation(event);  // createWorkflow 대신 handleWorkflowCreation 호출
            log.info("Successfully created workflow for request: {}", event.getRequestId());
        } catch (Exception e) {
            log.error("Failed to create workflow for request: {}", event.getRequestId(), e);
        }
    }
}