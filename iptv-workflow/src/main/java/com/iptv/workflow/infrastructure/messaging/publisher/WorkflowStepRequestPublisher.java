package com.iptv.workflow.infrastructure.messaging.publisher;

import com.workflow.common.event.WorkflowStepEvent;
import com.iptv.workflow.domain.model.workflow.Workflow;
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

    @Value("${kafka.topics.iptv.step.request}")
    private String requestTopic;

    public WorkflowStepRequestPublisher(@Qualifier("workflowKafkaTemplate") KafkaTemplate<String, WorkflowStepEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishStepRequest(Workflow workflow) {
        workflow.getActiveSteps().forEach(stepType -> {
            WorkflowStepEvent event = WorkflowStepEvent.builder()
                    .workflowId(workflow.getId())
                    .serviceType(ServiceType.fromCode(workflow.getServiceType()))
                    .stepTypeName(stepType.getStepName())
                    .orderNumber(workflow.getOrderNumber())
                    .orderSeq(workflow.getOrderSeq())
                    .serviceType(ServiceType.fromCode(workflow.getServiceType()))
                    .orderType(workflow.getOrderType())
                    .custName(workflow.getCustName())
                    .address(workflow.getAddress())
                    .timestamp(LocalDateTime.now())
                    .build();

            kafkaTemplate.send(requestTopic, workflow.getId(), event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.debug("Successfully sent step request for step: {} of workflow: {}",
                                    stepType.getStepName(), workflow.getId());
                        } else {
                            log.error("Failed to send step request: {} for workflow: {}",
                                    stepType.getStepName(), workflow.getId(), ex);
                        }
                    });
        });
    }
}