package com.internet.workflow.domain.service;

import com.internet.workflow.api.dto.WorkflowHistoryResponse;
import com.internet.workflow.api.dto.WorkflowResponse;
import com.internet.workflow.infrastructure.messaging.publisher.WorkflowStepRequestPublisher;
import com.workflow.common.event.StepType;
import com.workflow.common.event.WorkflowCreationEvent;
import com.internet.workflow.common.exception.WorkflowNotFoundException;
import com.internet.workflow.domain.model.workflow.Workflow;
import com.internet.workflow.infrastructure.persistence.entity.WorkflowEntity;
import com.internet.workflow.infrastructure.persistence.mapper.WorkflowMapper;
import com.internet.workflow.infrastructure.persistence.repository.WorkflowRepository;
import com.workflow.common.step.InternetStepType;
import com.workflow.common.step.StepTypeStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WorkflowService {
    private final WorkflowRepository workflowRepository;
    private final WorkflowMapper workflowMapper;
    private final WorkflowScheduler scheduler;
    private final WorkflowStepRequestPublisher publisher;
    private final Map<String, Set<StepTypeStrategy>> processedSteps = new ConcurrentHashMap<>();

    @Transactional
    public void handleStepCompletion(String workflowId, StepTypeStrategy stepType) {
        log.info("[Service] Starting step completion - workflowId: {}, step: {}, thread: {}",
                workflowId, stepType.getStepName(), Thread.currentThread().getName());

        // 현재 처리된 스텝들 상태 로깅
        Set<StepTypeStrategy> existingSteps = processedSteps.get(workflowId);
        if (existingSteps != null) {
            log.info("[Service] Current processed steps for workflow {}: {}",
                    workflowId,
                    existingSteps.stream()
                            .map(StepTypeStrategy::getStepName)
                            .collect(Collectors.joining(", ")));
        }

        // 이미 처리된 스텝인지 확인 전에 synchronized 블록 사용
        synchronized (processedSteps) {
            Set<StepTypeStrategy> completedSteps = processedSteps.computeIfAbsent(workflowId,
                    k -> ConcurrentHashMap.newKeySet());

            if (!completedSteps.add(stepType)) {
                log.warn("[Service] Step {} already processed for workflow {}, skipping...",
                        stepType.getStepName(), workflowId);
                return;
            }
            log.info("[Service] Added step {} to processed steps for workflow {}",
                    stepType.getStepName(), workflowId);
        }

        try {
            WorkflowEntity entity = workflowRepository.findById(workflowId)
                    .orElseThrow(() -> new WorkflowNotFoundException(workflowId));

            // 엔티티의 현재 상태 로깅
            log.info("[Service] Current entity state - Completed steps: {}, Active steps: {}",
                    entity.getCompletedSteps().stream()
                            .map(StepTypeStrategy::getStepName)
                            .collect(Collectors.joining(", ")),
                    entity.getActiveSteps().stream()
                            .map(StepTypeStrategy::getStepName)
                            .collect(Collectors.joining(", ")));

            // 스케줄러에 처리 위임
            scheduler.handleStepCompletion(workflowId, stepType);
            log.info("[Service] Successfully completed step {} for workflow {}",
                    stepType.getStepName(), workflowId);

            // COMPLETION 스텝이 완료되면 처리 이력 정리
            if (stepType == InternetStepType.COMPLETION) {
                clearProcessedSteps(workflowId);
                publisher.clearSentMessages(workflowId);  // 발행된 메시지 정리
                log.info("[Service] Cleared all tracking data for completed workflow {}", workflowId);
            }

        } catch (Exception e) {
            log.error("[Service] Error processing step {} for workflow {}: {}",
                    stepType.getStepName(), workflowId, e.getMessage(), e);
            // 실패 시 처리 이력에서 제거
            synchronized (processedSteps) {
                Set<StepTypeStrategy> completedSteps = processedSteps.get(workflowId);
                if (completedSteps != null) {
                    completedSteps.remove(stepType);
                    log.info("[Service] Removed failed step {} from processed steps for workflow {}",
                            stepType.getStepName(), workflowId);
                }
            }
            throw e;
        } finally {
            log.info("[Service] Finished processing step {} for workflow {}",
                    stepType.getStepName(), workflowId);
        }
    }

    private void clearProcessedSteps(String workflowId) {
        processedSteps.remove(workflowId);
    }

    public void handleWorkflowCreation(WorkflowCreationEvent event) {
        log.debug("Handling workflow creation event: {}", event);
        scheduler.scheduleWorkflow(event);
    }

    @Transactional(readOnly = true)
    public WorkflowResponse getWorkflow(String workflowId) {
        return workflowRepository.findById(workflowId)
                .map(workflowMapper::toResponse)
                .orElseThrow(() -> new WorkflowNotFoundException(workflowId));
    }

    @Transactional(readOnly = true)
    public WorkflowResponse getWorkflowWithOrderNumber(String orderNumber) {
        return workflowRepository.findByOrderNumber(orderNumber)
                .map(workflowMapper::toResponse)
                .orElseThrow(() -> new WorkflowNotFoundException("Order not found: " + orderNumber));
    }

    @Transactional(readOnly = true)
    public List<WorkflowHistoryResponse> getWorkflowHistory(String workflowId) {
        return workflowRepository.findById(workflowId)
                .map(entity -> workflowMapper.toHistoryResponses(entity.getStepHistories()))
                .orElseThrow(() -> new WorkflowNotFoundException(workflowId));
    }

    @Transactional(readOnly = true)
    public Workflow getWorkflowById(String workflowId) {
        return workflowRepository.findById(workflowId)
                .map(workflowMapper::toDomain)
                .orElseThrow(() -> new WorkflowNotFoundException(workflowId));
    }

    private void validateWorkflowExists(String workflowId) {
        if (!workflowRepository.existsById(workflowId)) {
            throw new WorkflowNotFoundException(workflowId);
        }
    }
}