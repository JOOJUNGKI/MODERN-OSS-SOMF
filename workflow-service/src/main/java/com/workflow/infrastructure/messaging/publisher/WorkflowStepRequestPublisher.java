package com.workflow.infrastructure.messaging.publisher;

import com.workflow.common.event.WorkflowStepEvent;
import com.workflow.common.event.StepType;  // common 모듈의 StepType 사용
import com.workflow.domain.model.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class WorkflowStepRequestPublisher {
    private final KafkaTemplate<String, WorkflowStepEvent> kafkaTemplate;
    private static final String TOPIC_FORMAT = "workflow.internet.step.%s.request";

    public WorkflowStepRequestPublisher(@Qualifier("workflowKafkaTemplate") KafkaTemplate<String, WorkflowStepEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishStepRequest(Workflow workflow) {
        workflow.getActiveSteps().forEach(stepType ->
                publishEvent(createStepEvent(workflow, stepType))
        );
    }

    private void publishEvent(WorkflowStepEvent event) {
        String topic = getTopicForStepType(event.getStepType());
        kafkaTemplate.send(topic, event.getWorkflowId(), event)
                .whenComplete((result, ex) -> logPublishResult(event, ex));
    }

    private WorkflowStepEvent createStepEvent(Workflow workflow, StepType stepType) {
        return WorkflowStepEvent.builder()
                .workflowId(workflow.getId())
                .stepType(stepType)
                .orderNumber(workflow.getOrderNumber())
                .orderSeq(workflow.getOrderSeq())
                .serviceType(workflow.getServiceType())
                .orderType(workflow.getOrderType())
                .custName(workflow.getCustName())
                .address(workflow.getAddress())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private String getTopicForStepType(StepType stepType) {
        return String.format(TOPIC_FORMAT, stepType.name().toLowerCase());
    }

    private void logPublishResult(WorkflowStepEvent event, Throwable ex) {
        if (ex == null) {
            log.debug("Successfully sent step request for step: {} of workflow: {}",
                    event.getStepType(), event.getWorkflowId());
        } else {
            log.error("Failed to send step request: {} for workflow: {}",
                    event.getStepType(), event.getWorkflowId(), ex);
        }
    }}