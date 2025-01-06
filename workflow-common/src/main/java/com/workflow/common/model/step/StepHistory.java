package com.workflow.common.model.step;

import com.workflow.common.event.StepType;
import com.workflow.common.step.ServiceType;
import com.workflow.common.step.StepTypeStrategy;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StepHistory {
    private final StepTypeStrategy stepType;

    private final ServiceType serviceType;

    private final String orderNumber;

    private final LocalDateTime startAt;
    private LocalDateTime endAt;

    public void complete() {
        this.endAt = LocalDateTime.now();
    }
}