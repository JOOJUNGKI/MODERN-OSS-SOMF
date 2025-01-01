package com.workflow.common.event;

public enum StepType {
    ACQUISITION("입수", 1),
    FACILITY("시설", 2),
    SITE("현장", 3),
    EQUIPMENT("장치", 3),
    MASTER("원부", 5),
    COMPLETION("준공", 6);

    private final String description;
    private final int order;

    StepType(String description, int order) {
        this.description = description;
        this.order = order;
    }

    public String getDescription() {
        return description;
    }

    public int getOrder() {
        return order;
    }
}
