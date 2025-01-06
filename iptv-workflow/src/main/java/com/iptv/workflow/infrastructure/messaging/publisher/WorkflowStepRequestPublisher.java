package com.iptv.workflow.infrastructure.messaging.publisher;

import com.workflow.common.event.StepType;
import com.workflow.common.event.WorkflowStepEvent;
import com.iptv.workflow.domain.model.workflow.Workflow;
import com.workflow.common.step.StepTypeStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import com.workflow.common.step.ServiceType;

@Slf4j
@Component
public class WorkflowStepRequestPublisher {
    private final KafkaTemplate<String, WorkflowStepEvent> kafkaTemplate;

    private static final String TOPIC_FORMAT = "workflow.iptv.step.%s.request";

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

    private WorkflowStepEvent createStepEvent(Workflow workflow, StepTypeStrategy stepType) {
        return WorkflowStepEvent.builder()
                .workflowId(workflow.getId())
                .stepTypeName(stepType.getStepName())
                .orderNumber(workflow.getOrderNumber())
                .orderSeq(workflow.getOrderSeq())
                .serviceType(ServiceType.fromCode(workflow.getServiceType()))
                .orderType(workflow.getOrderType())
                .custName(workflow.getCustName())
                .address(workflow.getAddress())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private String getTopicForStepType(StepTypeStrategy stepType) {
        return String.format(TOPIC_FORMAT, stepType.getStepName().toLowerCase());
    }

    private void logPublishResult(WorkflowStepEvent event, Throwable ex) {
        if (ex == null) {
            log.debug("Successfully sent step request for step: {} of workflow: {}",
                    event.getStepType(), event.getWorkflowId());
        } else {
            log.error("Failed to send step request: {} for workflow: {}",
                    event.getStepType(), event.getWorkflowId(), ex);
        }
    }
}