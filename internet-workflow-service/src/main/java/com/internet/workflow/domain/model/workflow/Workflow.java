
package com.internet.workflow.domain.model.workflow;

import com.internet.workflow.domain.model.step.StepHistory;
import com.workflow.common.step.InternetStepType;
import com.workflow.common.step.StepTypeStrategy;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Builder
public class Workflow {
    private final String id;
    private final String orderNumber;
    private final Integer orderSeq;
    private final String serviceType;
    private final String orderType;
    private final String custName;
    private final String address;
    private WorkflowStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private Set<StepTypeStrategy> activeSteps = new HashSet<>();

    @Builder.Default
    private Set<StepTypeStrategy> completedSteps = new HashSet<>();

    @Builder.Default
    private List<StepHistory> stepHistories = new ArrayList<>();

    public static Workflow create(String orderNumber, Integer orderSeq,
                                  String serviceType, String orderType, String custName, String address) {
        if (!"INTERNET".equalsIgnoreCase(serviceType)) {
            throw new IllegalArgumentException("Only Internet service type is supported");
        }

        Workflow workflow = Workflow.builder()
                .id(UUID.randomUUID().toString())
                .orderNumber(orderNumber)
                .orderSeq(orderSeq)
                .serviceType(serviceType)
                .orderType(orderType)
                .custName(custName)
                .address(address)
                .status(WorkflowStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        workflow.startStep(InternetStepType.ACQUISITION);
        return workflow;
    }

    public void startStep(StepTypeStrategy stepType) {
        if (stepType.canStart(completedSteps) && !activeSteps.contains(stepType)) {
            activeSteps.add(stepType);
            StepHistory history = StepHistory.builder()
                    .stepTypeStrategy(stepType)  // StepHistory의 필드명을 stepTypeStrategy로 변경
                    .startTime(LocalDateTime.now())
                    .build();
            stepHistories.add(history);

            if (WorkflowStatus.CREATED.equals(status)) {  // == 대신 equals 사용
                status = WorkflowStatus.IN_PROGRESS;
            }

            this.updatedAt = LocalDateTime.now();
        }
    }

    public void completeStep(StepTypeStrategy completedStep) {
        if (!activeSteps.contains(completedStep)) {
            throw new IllegalStateException("Step is not active: " + completedStep);
        }

        // 현재 단계 완료 처리
        StepHistory currentHistory = getCurrentStepHistory(completedStep);
        currentHistory.complete();

        activeSteps.remove(completedStep);
        completedSteps.add(completedStep);

        updateWorkflowStatus();
        this.updatedAt = LocalDateTime.now();
    }

    private void updateWorkflowStatus() {
        boolean allCompleted = Arrays.stream(InternetStepType.values())
                .allMatch(step -> completedSteps.contains(step));

        if (allCompleted) {
            this.status = WorkflowStatus.COMPLETED;
        }
    }

    private StepHistory getCurrentStepHistory(StepTypeStrategy stepType) {
        return stepHistories.stream()
                .filter(history -> history.getStepTypeStrategy().equals(stepType)  // stepType -> stepTypeStrategy
                        && history.getEndTime() == null)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Active history not found for step: %s", stepType.getStepName())));
    }

    public List<StepHistory> getStepHistories() {
        return Collections.unmodifiableList(stepHistories);
    }
}