package com.workflow.domain.model.workflow;

public enum PlanStatus {
    CREATED("생성됨"),
    IN_PROGRESS("진행중"),
    COMPLETED("완료됨"),
    FAILED("실패");

    private final String description;

    PlanStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}