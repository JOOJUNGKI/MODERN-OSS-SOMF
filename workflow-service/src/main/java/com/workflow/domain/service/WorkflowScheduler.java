package com.workflow.domain.service;

import com.workflow.common.exception.WorkflowNotFoundException;
import com.workflow.domain.event.WorkflowCreationEvent;
import com.workflow.domain.model.step.StepType;
import com.workflow.domain.model.workflow.ExecutionPlan;
import com.workflow.domain.model.workflow.PlanStatus;
import com.workflow.domain.model.workflow.Workflow;
import com.workflow.domain.model.workflow.WorkflowStatus;
import com.workflow.infrastructure.messaging.publisher.WorkflowStepRequestPublisher;
import com.workflow.infrastructure.persistence.entity.WorkflowEntity;
import com.workflow.infrastructure.persistence.mapper.WorkflowMapper;
import com.workflow.infrastructure.persistence.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowScheduler {
    private final WorkflowRepository workflowRepository;
    private final WorkflowMapper workflowMapper;
    private final WorkflowStepRequestPublisher stepRequestPublisher;
    private final Map<String, ExecutionPlan> activePlans = new ConcurrentHashMap<>();

    @Transactional
    public void scheduleWorkflow(WorkflowCreationEvent event) {
        log.debug("Creating new workflow for event: {}", event);

        final Workflow workflow = Workflow.create(
                event.getOrderNumber(),
                event.getOrderSeq(),
                event.getServiceType(),
                event.getOrderType(),
                event.getCustName(),
                event.getAddress());

        WorkflowEntity savedEntity = workflowRepository.save(workflowMapper.toEntity(workflow));
        final Workflow savedWorkflow = workflowMapper.toDomain(savedEntity);

        ExecutionPlan plan = ExecutionPlan.createInitialPlan(savedWorkflow);
        activePlans.put(savedWorkflow.getId(), plan);

        Set<StepType> initialSteps = plan.getNextExecutableSteps();
        log.info("Starting initial steps {} for workflow {}", initialSteps, savedWorkflow.getId());

        initialSteps.forEach(step -> {
            stepRequestPublisher.publishStepRequest(savedWorkflow, step);
            log.debug("Published request for step {} of workflow {}", step, savedWorkflow.getId());
        });
    }

    @Transactional
    public void handleStepCompletion(String workflowId, StepType completedStep) {
        ExecutionPlan plan = activePlans.get(workflowId);
        if (plan == null) {
            log.warn("No active plan found for workflow {}", workflowId);
            return;
        }

        try {
            plan.markStepCompleted(completedStep);
            log.info("Step {} completed for workflow {}", completedStep, workflowId);

            // updateWorkflowEntity 내부에서 다음 단계 처리 및 메시지 발행까지 모두 처리
            updateWorkflowEntity(workflowId, completedStep);

            if (plan.getStatus() == PlanStatus.COMPLETED) {
                log.info("Workflow {} completed successfully", workflowId);
                activePlans.remove(workflowId);
            }
        } catch (Exception e) {
            log.error("Failed to handle step completion for workflow {}: {}", workflowId, e.getMessage(), e);
            handleStepFailure(workflowId, completedStep, e);
        }
    }

    @Transactional
    public void handleStepFailure(String workflowId, StepType failedStep, Exception error) {
        ExecutionPlan plan = activePlans.get(workflowId);
        if (plan == null) {
            log.warn("No active plan found for workflow {}", workflowId);
            return;
        }

        log.error("Step {} failed for workflow {}: {}", failedStep, workflowId, error.getMessage());
        plan.markStepFailed(failedStep);

        try {
            WorkflowEntity entity = workflowRepository.findById(workflowId)
                    .orElseThrow(() -> new WorkflowNotFoundException(workflowId));
            entity.setStatus(WorkflowStatus.FAILED);
            workflowRepository.save(entity);
        } catch (Exception e) {
            log.error("Failed to update workflow status for failure: {}", e.getMessage(), e);
        } finally {
            activePlans.remove(workflowId);
        }
    }

    private void updateWorkflowEntity(String workflowId, StepType completedStep) {
        WorkflowEntity entity = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new WorkflowNotFoundException(workflowId));
        Workflow workflow = workflowMapper.toDomain(entity);

        log.debug("[Debug] Before completion - Active steps: {}, Completed steps: {}",
                workflow.getActiveSteps(), workflow.getCompletedSteps());

        workflow.completeStep(completedStep);

        Set<StepType> nextSteps = completedStep.getNextSteps(workflow.getCompletedSteps());
        log.debug("[Debug] Next steps from getNextSteps: {}", nextSteps);

        Set<StepType> executableSteps = nextSteps.stream()
                .filter(step -> step.canStart(workflow.getCompletedSteps()))
                .collect(Collectors.toSet());
        log.debug("[Debug] Filtered executable steps: {}", executableSteps);

        executableSteps.forEach(step -> {
            workflow.startStep(step);
            log.debug("[Debug] Starting step {} for workflow {}", step, workflowId);
            stepRequestPublisher.publishStepRequest(workflow, step);
            log.debug("[Debug] Published request for step {} of workflow {}", step, workflowId);
        });

        log.debug("[Debug] After update - Active steps: {}, Completed steps: {}",
                workflow.getActiveSteps(), workflow.getCompletedSteps());

        workflowRepository.save(workflowMapper.toEntity(workflow));
    }

    private Workflow getWorkflow(String workflowId) {
        return workflowRepository.findById(workflowId)
                .map(workflowMapper::toDomain)
                .orElseThrow(() -> new WorkflowNotFoundException(workflowId));
    }
}