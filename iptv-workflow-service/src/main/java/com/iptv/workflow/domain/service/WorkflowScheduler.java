// iptv-workflow/src/main/java/com/iptv/workflow/domain/service/WorkflowScheduler.java
package com.iptv.workflow.domain.service;

import com.workflow.common.event.WorkflowCreationEvent;
import com.iptv.workflow.common.exception.WorkflowNotFoundException;
import com.workflow.common.step.ServiceType;
import com.workflow.common.step.StepTypeStrategy;
import com.iptv.workflow.domain.model.workflow.ExecutionPlan;
import com.iptv.workflow.domain.model.workflow.PlanStatus;
import com.iptv.workflow.domain.model.workflow.Workflow;
import com.iptv.workflow.domain.model.workflow.WorkflowStatus;
import com.iptv.workflow.infrastructure.messaging.publisher.WorkflowStepRequestPublisher;
import com.iptv.workflow.infrastructure.persistence.entity.WorkflowEntity;
import com.iptv.workflow.infrastructure.persistence.mapper.WorkflowMapper;
import com.iptv.workflow.infrastructure.persistence.repository.WorkflowRepository;
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
        log.info("Creating new workflow for event: {}", event);

        ServiceType serviceType = ServiceType.fromCode(event.getServiceType());
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

        Set<StepTypeStrategy> initialSteps = plan.getNextExecutableSteps();
        log.info("Starting initial steps {} for workflow {}",
                initialSteps.stream().map(StepTypeStrategy::getStepName).collect(Collectors.joining(", ")),
                savedWorkflow.getId());

        initialSteps.forEach(step -> {
            stepRequestPublisher.publishStepRequest(savedWorkflow);
            log.debug("Published request for step {} of workflow {}",
                    step.getStepName(), savedWorkflow.getId());
        });
    }

    private StepTypeStrategy getInitialStep(ServiceType serviceType) {
        Class<? extends StepTypeStrategy> stepTypeClass = serviceType.getStepTypeClass();
        return stepTypeClass.getEnumConstants()[0]; // 첫 번째 단계 반환
    }

    @Transactional
    public void handleStepCompletion(String workflowId, StepTypeStrategy completedStep) {
        ExecutionPlan plan = activePlans.get(workflowId);
        if (plan == null) {
            log.warn("No active plan found for workflow {}", workflowId);
            return;
        }

        try {
            plan.markStepCompleted(completedStep);
            log.info("Step {} completed for workflow {}",
                    completedStep.getStepName(), workflowId);

            updateWorkflowEntity(workflowId, completedStep);

            if (plan.getStatus() == PlanStatus.COMPLETED) {
                log.info("Workflow {} completed successfully", workflowId);
                activePlans.remove(workflowId);
            }
        } catch (Exception e) {
            log.error("Failed to handle step completion for workflow {}: {}",
                    workflowId, e.getMessage(), e);
            handleStepFailure(workflowId, completedStep, e);
        }
    }

    @Transactional
    public void handleStepFailure(String workflowId, StepTypeStrategy failedStep, Exception error) {
        ExecutionPlan plan = activePlans.get(workflowId);
        if (plan == null) {
            log.warn("No active plan found for workflow {}", workflowId);
            return;
        }

        log.error("Step {} failed for workflow {}: {}",
                failedStep.getStepName(), workflowId, error.getMessage());
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

    private void updateWorkflowEntity(String workflowId, StepTypeStrategy completedStep) {
        WorkflowEntity entity = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new WorkflowNotFoundException(workflowId));
        Workflow workflow = workflowMapper.toDomain(entity);

        log.debug("Before completion - Active steps: {}, Completed steps: {}",
                workflow.getActiveSteps().stream().map(StepTypeStrategy::getStepName).collect(Collectors.joining(", ")),
                workflow.getCompletedSteps().stream().map(StepTypeStrategy::getStepName).collect(Collectors.joining(", ")));

        workflow.completeStep(completedStep);

        Set<? extends StepTypeStrategy> nextSteps = completedStep.getNextSteps();
        log.debug("Next steps from getNextSteps: {}",
                nextSteps.stream().map(StepTypeStrategy::getStepName).collect(Collectors.joining(", ")));

        Set<StepTypeStrategy> executableSteps = nextSteps.stream()
                .filter(step -> step.canStart(workflow.getCompletedSteps()))
                .collect(Collectors.toSet());

        log.debug("Filtered executable steps: {}",
                executableSteps.stream().map(StepTypeStrategy::getStepName).collect(Collectors.joining(", ")));

        executableSteps.forEach(step -> {
            workflow.startStep(step);
            log.debug("Starting step {} for workflow {}",
                    step.getStepName(), workflowId);
            stepRequestPublisher.publishStepRequest(workflow);
            log.debug("Published request for step {} of workflow {}",
                    step.getStepName(), workflowId);
        });

        log.debug("After update - Active steps: {}, Completed steps: {}",
                workflow.getActiveSteps().stream().map(StepTypeStrategy::getStepName).collect(Collectors.joining(", ")),
                workflow.getCompletedSteps().stream().map(StepTypeStrategy::getStepName).collect(Collectors.joining(", ")));

        workflowRepository.save(workflowMapper.toEntity(workflow));
    }

    private Workflow getWorkflow(String workflowId) {
        return workflowRepository.findById(workflowId)
                .map(workflowMapper::toDomain)
                .orElseThrow(() -> new WorkflowNotFoundException(workflowId));
    }
}