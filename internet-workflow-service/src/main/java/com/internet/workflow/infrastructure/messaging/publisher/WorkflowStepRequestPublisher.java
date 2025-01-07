package com.internet.workflow.infrastructure.messaging.publisher;

import com.internet.workflow.domain.model.workflow.Workflow;
import com.workflow.common.event.WorkflowStepEvent;
import com.workflow.common.step.ServiceType;
import com.workflow.common.step.StepTypeStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WorkflowStepRequestPublisher {
    private final KafkaTemplate<String, WorkflowStepEvent> kafkaTemplate;
    private final Set<String> sentMessages = ConcurrentHashMap.newKeySet();

    private static final String TOPIC_FORMAT = "workflow.internet.step.%s.request";

    public WorkflowStepRequestPublisher(KafkaTemplate<String, WorkflowStepEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishStepRequest(Workflow workflow) {
        workflow.getActiveSteps().forEach(stepType -> {
            String messageKey = generateMessageKey(workflow.getId(), stepType);
            if (sentMessages.add(messageKey)) {  // Set.add()는 이미 존재하면 false를 반환
                publishEvent(createStepEvent(workflow, stepType));
            } else {
                log.debug("Skipping duplicate message for step {} of workflow {}",
                        stepType.getStepName(), workflow.getId());
            }
        });
    }

    private void publishEvent(WorkflowStepEvent event) {
        String topic = getTopicForStepType(event.getStepType());
        kafkaTemplate.send(topic, event.getWorkflowId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Successfully sent step request for step: {} of workflow: {}",
                                event.getStepType().getStepName(), event.getWorkflowId());
                    } else {
                        log.error("Failed to send step request: {} for workflow: {}",
                                event.getStepType(), event.getWorkflowId(), ex);
                        // 실패 시 메시지 키 제거하여 재시도 가능하도록 함
                        sentMessages.remove(generateMessageKey(event.getWorkflowId(), event.getStepType()));
                    }
                });
    }

    private String generateMessageKey(String workflowId, StepTypeStrategy stepType) {
        return workflowId + "_" + stepType.getStepName();
    }

    private String getTopicForStepType(StepTypeStrategy stepType) {
        return String.format(TOPIC_FORMAT, stepType.getStepName().toLowerCase());
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

    // 워크플로우가 완료되면 해당 메시지 키들을 정리
    public void clearSentMessages(String workflowId) {
        sentMessages.removeIf(key -> key.startsWith(workflowId + "_"));
    }
}