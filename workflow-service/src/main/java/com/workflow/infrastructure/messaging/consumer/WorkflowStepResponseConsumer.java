package com.workflow.infrastructure.messaging.consumer;

import com.workflow.domain.service.WorkflowService;
import com.workflow.common.event.WorkflowStepEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowStepResponseConsumer {
    private final WorkflowService workflowService;

    @KafkaListener(
            topics = "${kafka.topics.internet.step.response}",
            containerFactory = "workflowStepKafkaListenerContainerFactory"
    )
    public void handleStepResponse(WorkflowStepEvent event) {
        log.debug("Received step response: {}", event);
        try {
            workflowService.handleStepCompletion(
                    event.getWorkflowId(),
                    event.getStepType()
            );
            log.info("Successfully processed step completion for workflow: {}", event.getWorkflowId());
        } catch (Exception e) {
            log.error("Failed to process step completion for workflow: {}", event.getWorkflowId(), e);
        }
    }
}