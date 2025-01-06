package com.workflow.common.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.workflow.common.step.IPTVStepType;
import com.workflow.common.step.InternetStepType;
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

    @JsonIgnore
    public StepTypeStrategy getStepType() {
        if (serviceType == null || stepTypeName == null) {
            return null;
        }

        Class<? extends StepTypeStrategy> stepTypeClass = serviceType.getStepTypeClass();
        if (stepTypeClass == IPTVStepType.class) {
            return IPTVStepType.valueOf(stepTypeName);
        } else if (stepTypeClass == InternetStepType.class) {
            return InternetStepType.valueOf(stepTypeName);
        }
        throw new IllegalArgumentException("Unknown step type: " + stepTypeName);
    }

    @JsonProperty
    public void setStepType(StepTypeStrategy stepType) {
        if (stepType != null) {
            this.stepTypeName = stepType.getStepName();
        }
    }
}