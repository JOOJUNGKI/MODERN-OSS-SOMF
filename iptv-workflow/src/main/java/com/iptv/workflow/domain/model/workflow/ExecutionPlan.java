package com.iptv.workflow.domain.model.workflow;

import com.workflow.common.step.ServiceType;
import com.workflow.common.step.StepTypeStrategy;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Builder
public class ExecutionPlan {
    private final String workflowId;
    private final String orderNumber;

    @Builder.Default
    private final Map<StepTypeStrategy, StepState> stepStates = new HashMap<>();

    @Builder.Default
    private final Map<StepTypeStrategy, Set<? extends StepTypeStrategy>> dependencies = new HashMap<>();

    private PlanStatus status;

    public static ExecutionPlan createInitialPlan(Workflow workflow) {
        ServiceType serviceType = ServiceType.fromCode(workflow.getServiceType());
        Class<? extends StepTypeStrategy> stepTypeClass = serviceType.getStepTypeClass();
        StepTypeStrategy[] steps = stepTypeClass.getEnumConstants();

        ExecutionPlan plan = ExecutionPlan.builder()
                .workflowId(workflow.getId())
                .orderNumber(workflow.getOrderNumber())
                .status(PlanStatus.CREATED)
                .build();

        // 모든 단계를 PENDING 상태로 초기화
        for (StepTypeStrategy step : steps) {
            plan.stepStates.put(step, StepState.PENDING);
            plan.dependencies.put(step, step.getDependencies());
        }

        log.debug("Created initial execution plan for workflow: {}", workflow.getId());
        return plan;
    }

    public Set<StepTypeStrategy> getNextExecutableSteps() {
        Set<StepTypeStrategy> executableSteps = new HashSet<>();

        stepStates.forEach((step, state) -> {
            if (state == StepState.PENDING && isDependenciesMet(step)) {
                executableSteps.add(step);
                log.debug("Found executable step: {} for workflow: {}",
                        step.getStepName(), workflowId);
            }
        });

        String stepsStr = executableSteps.stream()
                .map(StepTypeStrategy::getStepName)
                .collect(Collectors.joining(", "));
        log.debug("Next executable steps for workflow {}: {}", workflowId, stepsStr);

        return executableSteps;
    }

    private boolean isDependenciesMet(StepTypeStrategy step) {
        Set<? extends StepTypeStrategy> deps = dependencies.get(step);
        if (deps == null || deps.isEmpty()) {
            return true;
        }

        boolean met = deps.stream()
                .allMatch(dep -> StepState.COMPLETED.equals(stepStates.get(dep)));

        String depsStr = deps.stream()
                .map(StepTypeStrategy::getStepName)
                .collect(Collectors.joining(", "));
        log.debug("Dependencies for step {}: {}, all met: {}",
                step.getStepName(), depsStr, met);

        return met;
    }

    public void markStepCompleted(StepTypeStrategy step) {
        log.debug("Marking step {} as completed for workflow: {}",
                step.getStepName(), workflowId);
        stepStates.put(step, StepState.COMPLETED);
        updatePlanStatus();
    }

    public void markStepFailed(StepTypeStrategy step) {
        log.debug("Marking step {} as failed for workflow: {}",
                step.getStepName(), workflowId);
        stepStates.put(step, StepState.FAILED);
        this.status = PlanStatus.FAILED;
    }

    private void updatePlanStatus() {
        long completedCount = stepStates.values().stream()
                .filter(state -> state == StepState.COMPLETED)
                .count();
        long totalCount = stepStates.size();

        if (completedCount == totalCount) {
            this.status = PlanStatus.COMPLETED;
            log.debug("All steps completed for workflow: {}", workflowId);
        } else if (stepStates.values().contains(StepState.FAILED)) {
            this.status = PlanStatus.FAILED;
            log.debug("Workflow {} failed due to step failure", workflowId);
        } else {
            this.status = PlanStatus.IN_PROGRESS;
            log.debug("Workflow {} is in progress", workflowId);
        }
    }
}