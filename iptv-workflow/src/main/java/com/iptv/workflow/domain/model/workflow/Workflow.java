
package com.iptv.workflow.domain.model.workflow;

import com.workflow.common.event.IPTVStepType;
import com.iptv.workflow.domain.model.step.StepHistory;
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
    private Set<IPTVStepType> activeSteps = EnumSet.noneOf(IPTVStepType.class);

    @Builder.Default
    private Set<IPTVStepType> completedSteps = EnumSet.noneOf(IPTVStepType.class);

    @Builder.Default
    private List<StepHistory> stepHistories = new ArrayList<>();

    public static Workflow create(String orderNumber, Integer orderSeq,
                                  String serviceType, String orderType, String custName, String address) {
        if (!"IPTV".equals(serviceType)) {
            throw new IllegalArgumentException("Only IPTV service type is supported");
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

        workflow.startStep(IPTVStepType.ACQUISITION);
        return workflow;
    }

    public void startStep(IPTVStepType stepType) {
        if (stepType.canStart(completedSteps) && !activeSteps.contains(stepType)) {
            activeSteps.add(stepType);
            stepHistories.add(StepHistory.builder()
                    .stepType(stepType)
                    .startTime(LocalDateTime.now())
                    .build());

            if (status == WorkflowStatus.CREATED) {
                status = WorkflowStatus.IN_PROGRESS;
            }

            this.updatedAt = LocalDateTime.now();
        }
    }


    public void completeStep(IPTVStepType completedStep) {
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
        boolean allCompleted = Arrays.stream(IPTVStepType.values())
                .allMatch(step -> completedSteps.contains(step));

        if (allCompleted) {
            this.status = WorkflowStatus.COMPLETED;
        }
    }

    private StepHistory getCurrentStepHistory(IPTVStepType stepType) {
        return stepHistories.stream()
                .filter(history -> history.getStepType() == stepType
                        && history.getEndTime() == null)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Active history not found for step: %s", stepType)));
    }

    public List<StepHistory> getStepHistories() {
        return Collections.unmodifiableList(stepHistories);
    }
}