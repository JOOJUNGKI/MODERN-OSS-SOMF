// File: myprj6/workflow-service/src/main/java/com/workflow/infrastructure/messaging/publisher/WorkflowStepRequestPublisher.java
package com.workflow.infrastructure.messaging.publisher;

import com.workflow.domain.event.WorkflowStepEvent;
import com.workflow.domain.model.workflow.Workflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowStepRequestPublisher {
    private final KafkaTemplate<String, WorkflowStepEvent> kafkaTemplate;

    @Value("${kafka.topics.step.request}")
    private String requestTopic;

    public void publishStepRequest(Workflow workflow) {
        workflow.getActiveSteps().forEach(stepType -> {
            WorkflowStepEvent event = WorkflowStepEvent.builder()
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

            kafkaTemplate.send(requestTopic, workflow.getId(), event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.debug("Successfully sent step request for step: {} of workflow: {}",
                                    stepType, workflow.getId());
                        } else {
                            log.error("Failed to send step request: {} for workflow: {}",
                                    stepType, workflow.getId(), ex);
                        }
                    });
        });
    }
}