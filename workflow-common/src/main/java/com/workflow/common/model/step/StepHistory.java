package com.workflow.common.model.step;

import com.workflow.common.event.StepType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StepHistory {
    private final StepType stepType;

    private final String serviceType;

    private final String orderNumber;

    private final LocalDateTime startAt;
    private LocalDateTime endAt;

    public void complete() {
        this.endAt = LocalDateTime.now();
    }
}