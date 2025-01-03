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

    @Value("${kafka.topics.internet.step.request}")
    private String requestTopic;

    public WorkflowStepRequestPublisher(@Qualifier("workflowKafkaTemplate") KafkaTemplate<String, WorkflowStepEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishStepRequest(Workflow workflow) {
        workflow.getActiveSteps().forEach(stepType -> {
            // StepType 변환이 필요할 수 있음
            StepType commonStepType = StepType.valueOf(stepType.name());

            WorkflowStepEvent event = WorkflowStepEvent.builder()
                    .workflowId(workflow.getId())
                    .stepType(commonStepType)
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
    }
}