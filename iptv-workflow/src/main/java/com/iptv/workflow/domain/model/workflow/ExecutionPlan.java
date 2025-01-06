// File: myprj6/workflow-service/src/main/java/com/workflow/domain/model/workflow/ExecutionPlan.java
package com.iptv.workflow.domain.model.workflow;

import com.workflow.common.event.IPTVStepType;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Getter
@Builder
public class ExecutionPlan {
    private final String workflowId;
    private final String orderNumber;
    @Builder.Default
    private final Map<IPTVStepType, StepState> stepStates = new EnumMap<>(IPTVStepType.class);
    @Builder.Default
    private final Map<IPTVStepType, Set<IPTVStepType>> dependencies = new EnumMap<>(IPTVStepType.class);
    private PlanStatus status;

    public static ExecutionPlan createInitialPlan(Workflow workflow) {
        ExecutionPlan plan = ExecutionPlan.builder()
                .workflowId(workflow.getId())
                .orderNumber(workflow.getOrderNumber())
                .status(PlanStatus.CREATED)
                .build();

        // 모든 단계를 PENDING 상태로 초기화
        Arrays.stream(IPTVStepType.values())
                .forEach(step -> plan.stepStates.put(step, StepState.PENDING));

        // 의존성 설정
        Arrays.stream(IPTVStepType.values())
                .forEach(step -> plan.dependencies.put(step, step.getDependencies()));

        log.debug("Created initial execution plan for workflow: {}", workflow.getId());
        return plan;
    }

    public Set<IPTVStepType> getNextExecutableSteps() {
        Set<IPTVStepType> executableSteps = new HashSet<>();
        for (Map.Entry<IPTVStepType, StepState> entry : stepStates.entrySet()) {
            if (entry.getValue() == StepState.PENDING && isDependenciesMet(entry.getKey())) {
                executableSteps.add(entry.getKey());
                log.debug("Found executable step: {} for workflow: {}", entry.getKey(), workflowId);
            }
        }
        log.debug("Next executable steps for workflow {}: {}", workflowId, executableSteps);
        return executableSteps;
    }

    private boolean isDependenciesMet(IPTVStepType step) {
        Set<IPTVStepType> deps = dependencies.get(step);
        boolean met = deps.stream().allMatch(dep -> stepStates.get(dep) == StepState.COMPLETED);
        log.debug("Dependencies for step {}: {}, all met: {}", step, deps, met);
        return met;
    }

    public void markStepCompleted(IPTVStepType step) {
        log.debug("Marking step {} as completed for workflow: {}", step, workflowId);
        stepStates.put(step, StepState.COMPLETED);
        updatePlanStatus();
    }

    public void markStepFailed(IPTVStepType step) {
        log.debug("Marking step {} as failed for workflow: {}", step, workflowId);
        stepStates.put(step, StepState.FAILED);
        this.status = PlanStatus.FAILED;
    }

    private void updatePlanStatus() {
        if (stepStates.values().stream().allMatch(state -> state == StepState.COMPLETED)) {
            this.status = PlanStatus.COMPLETED;
            log.debug("All steps completed for workflow: {}", workflowId);
        } else if (stepStates.values().stream().anyMatch(state -> state == StepState.FAILED)) {
            this.status = PlanStatus.FAILED;
            log.debug("Workflow {} failed due to step failure", workflowId);
        } else {
            this.status = PlanStatus.IN_PROGRESS;
            log.debug("Workflow {} is in progress", workflowId);
        }
    }
}