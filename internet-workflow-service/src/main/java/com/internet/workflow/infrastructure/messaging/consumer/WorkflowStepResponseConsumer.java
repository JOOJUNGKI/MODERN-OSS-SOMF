package com.internet.workflow.infrastructure.messaging.consumer;

import com.internet.workflow.domain.service.WorkflowService;
import com.workflow.common.event.StepType;
import com.workflow.common.event.WorkflowStepEvent;
import com.workflow.common.step.StepTypeStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowStepResponseConsumer {
    private final WorkflowService workflowService;

    @KafkaListener(
            topics = {
                    "workflow.internet.step.acquisition.response",
                    "workflow.internet.step.facility.response",
                    "workflow.internet.step.site.response",
                    "workflow.internet.step.equipment.response",
                    "workflow.internet.step.master.response",
                    "workflow.internet.step.completion.response"
            },
            containerFactory = "workflowStepKafkaListenerContainerFactory"
    )
    public void handleStepResponse(WorkflowStepEvent event) {
        log.info("[Consumer] Received event - workflowId: {}, stepType: {}",
                event.getWorkflowId(), event.getStepType().getStepName());
        try {
            workflowService.handleStepCompletion(event.getWorkflowId(), event.getStepType());
        } catch (Exception e) {
            log.error("[Consumer] Error processing event: {}", e.getMessage(), e);
        }
    }
}