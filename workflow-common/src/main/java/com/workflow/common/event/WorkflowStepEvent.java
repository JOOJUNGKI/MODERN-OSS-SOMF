package com.workflow.common.event;

import com.workflow.common.step.ServiceType;
import com.workflow.common.step.StepTypeStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowStepEvent {
    private String workflowId;
    private ServiceType serviceType;
    private String stepTypeName;
    private String orderNumber;
    private Integer orderSeq;
    private String orderType;
    private String custName;
    private String address;
    private LocalDateTime timestamp;

    public StepTypeStrategy getStepType() {
        Class<? extends StepTypeStrategy> stepTypeClass = serviceType.getStepTypeClass();
        for (StepTypeStrategy step : stepTypeClass.getEnumConstants()) {
            if (step.getStepName().equals(stepTypeName)) {
                return step;
            }
        }
        throw new IllegalArgumentException("Unknown step type: " + stepTypeName);
    }

    public void setStepType(StepTypeStrategy stepType) {
        this.stepTypeName = stepType.getStepName();
    }
}