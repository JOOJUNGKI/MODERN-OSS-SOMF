package com.workflow.infrastructure.messaging.consumer;

import com.workflow.common.event.StepType;
import com.workflow.domain.service.WorkflowService;
import com.workflow.common.event.WorkflowStepEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowStepResponseConsumer {
    private final WorkflowService workflowService;
    private final ConcurrentHashMap<String, Set<StepType>> processedSteps = new ConcurrentHashMap<>();

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
        String key = event.getWorkflowId() + "_" + event.getStepType();

        // 이미 처리된 스텝인지 확인
        if (isStepAlreadyProcessed(event.getWorkflowId(), event.getStepType())) {
            log.debug("Step already processed: {} for workflow: {}",
                    event.getStepType(), event.getWorkflowId());
            return;
        }

        try {
            workflowService.handleStepCompletion(event.getWorkflowId(), event.getStepType());
            markStepAsProcessed(event.getWorkflowId(), event.getStepType());
            log.info("Successfully processed step completion for workflow: {}",
                    event.getWorkflowId());
        } catch (Exception e) {
            log.error("Failed to process step completion for workflow: {}",
                    event.getWorkflowId(), e);
        }
    }

    private boolean isStepAlreadyProcessed(String workflowId, StepType stepType) {
        return processedSteps.containsKey(workflowId) &&
                processedSteps.get(workflowId).contains(stepType);
    }

    private void markStepAsProcessed(String workflowId, StepType stepType) {
        processedSteps.computeIfAbsent(workflowId, k -> ConcurrentHashMap.newKeySet())
                .add(stepType);
    }

    // 워크플로우가 완료되면 처리 이력 정리
    public void clearProcessedSteps(String workflowId) {
        processedSteps.remove(workflowId);
    }
}