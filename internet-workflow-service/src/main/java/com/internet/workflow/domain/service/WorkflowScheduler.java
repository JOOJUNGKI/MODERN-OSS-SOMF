package com.internet.workflow.domain.service;

import com.workflow.common.event.StepType;
import com.workflow.common.event.WorkflowCreationEvent;
import com.internet.workflow.common.exception.WorkflowNotFoundException;
import com.internet.workflow.domain.model.workflow.ExecutionPlan;
import com.internet.workflow.domain.model.workflow.PlanStatus;
import com.internet.workflow.domain.model.workflow.Workflow;
import com.internet.workflow.domain.model.workflow.WorkflowStatus;
import com.internet.workflow.infrastructure.messaging.publisher.WorkflowStepRequestPublisher;
import com.internet.workflow.infrastructure.persistence.entity.WorkflowEntity;
import com.internet.workflow.infrastructure.persistence.mapper.WorkflowMapper;
import com.internet.workflow.infrastructure.persistence.repository.WorkflowRepository;
import com.workflow.common.step.ServiceType;
import com.workflow.common.step.StepTypeStrategy;
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
        log.info("[Scheduler] Starting handleStepCompletion: workflowId={}, step={}",
                workflowId, completedStep.getStepName());

        ExecutionPlan plan = activePlans.get(workflowId);
        if (plan == null) {
            log.warn("[Scheduler] No active plan found for workflow {}", workflowId);
            return;
        }

        try {
            log.info("[Scheduler] Marking step as completed: workflowId={}, step={}",
                    workflowId, completedStep.getStepName());

            plan.markStepCompleted(completedStep);

            log.info("[Scheduler] Updating workflow entity");
            updateWorkflowEntity(workflowId, completedStep);

            if (plan.getStatus() == PlanStatus.COMPLETED) {
                log.info("[Scheduler] Workflow {} completed successfully", workflowId);
                activePlans.remove(workflowId);
            }

            log.info("[Scheduler] Successfully completed step handling: workflowId={}, step={}",
                    workflowId, completedStep.getStepName());
        } catch (Exception e) {
            log.error("[Scheduler] Failed to handle step completion: workflowId={}, step={}, error={}",
                    workflowId, completedStep.getStepName(), e.getMessage(), e);
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

    @Transactional
    private void updateWorkflowEntity(String workflowId, StepTypeStrategy completedStep) {
        WorkflowEntity entity = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new WorkflowNotFoundException(workflowId));
        Workflow workflow = workflowMapper.toDomain(entity);

        log.debug("Before completion - Active steps: {}, Completed steps: {}",
                workflow.getActiveSteps().stream().map(StepTypeStrategy::getStepName).collect(Collectors.joining(", ")),
                workflow.getCompletedSteps().stream().map(StepTypeStrategy::getStepName).collect(Collectors.joining(", ")));

        // 스텝 완료 처리
        workflow.completeStep(completedStep);

        // 다음 스텝 가져오기
        Set<? extends StepTypeStrategy> nextSteps = completedStep.getNextSteps();
        log.debug("Next steps for {} are: {}",
                completedStep.getStepName(),
                nextSteps.stream().map(StepTypeStrategy::getStepName).collect(Collectors.joining(", ")));

        // 실행 가능한 다음 스텝들 찾기
        Set<StepTypeStrategy> executableSteps = nextSteps.stream()
                .filter(step -> step.canStart(workflow.getCompletedSteps()))
                .collect(Collectors.toSet());

        log.debug("Executable steps: {}",
                executableSteps.stream().map(StepTypeStrategy::getStepName).collect(Collectors.joining(", ")));

        // 다음 스텝들 시작
        for (StepTypeStrategy nextStep : executableSteps) {
            log.info("Starting next step: {} for workflow: {}", nextStep.getStepName(), workflowId);
            workflow.startStep(nextStep);

            // 각 스텝에 대해 이벤트 발행
            log.info("Publishing request for step: {} of workflow: {}", nextStep.getStepName(), workflowId);
            stepRequestPublisher.publishStepRequest(workflow);
        }

        log.debug("After completion - Active steps: {}, Completed steps: {}",
                workflow.getActiveSteps().stream().map(StepTypeStrategy::getStepName).collect(Collectors.joining(", ")),
                workflow.getCompletedSteps().stream().map(StepTypeStrategy::getStepName).collect(Collectors.joining(", ")));

        // 워크플로우 상태 저장
        workflowRepository.save(workflowMapper.toEntity(workflow));
    }

    private Workflow getWorkflow(String workflowId) {
        return workflowRepository.findById(workflowId)
                .map(workflowMapper::toDomain)
                .orElseThrow(() -> new WorkflowNotFoundException(workflowId));
    }
}