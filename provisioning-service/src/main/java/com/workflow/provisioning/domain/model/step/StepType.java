
package com.workflow.provisioning.domain.model.step;

import lombok.Getter;

@Getter
public enum StepType {
    ACQUISITION("입수", 1),
    SITE("현장", 2),
    COMPLETION("준공", 3);

    private final String description;
    private final int order;

    StepType(String description, int order) {
        this.description = description;
        this.order = order;
    }

    public StepType getNextStep() {
        return switch (this) {
            case ACQUISITION -> SITE;
            case SITE -> COMPLETION;
            case COMPLETION -> null;
        };
    }
}
