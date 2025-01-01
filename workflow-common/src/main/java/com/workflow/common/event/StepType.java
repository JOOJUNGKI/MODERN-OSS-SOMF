package com.workflow.common.event;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public enum StepType {
    // 먼저 enum 상수들을 정의
    ACQUISITION("입수", 1),
    FACILITY("시설", 2),
    SITE("현장", 3),
    EQUIPMENT("장치", 3),
    MASTER("원부", 5),
    COMPLETION("준공", 6);

    private final String description;
    private final int order;
    private final Set<StepType> dependencies;

    StepType(String description, int order) {
        this.description = description;
        this.order = order;
        this.dependencies = new HashSet<>();
    }

    // static 초기화 블록을 사용하여 의존성 초기화
    static {
        FACILITY.dependencies.add(ACQUISITION);
        SITE.dependencies.add(FACILITY);
        EQUIPMENT.dependencies.add(FACILITY);
        MASTER.dependencies.addAll(Set.of(SITE, EQUIPMENT));
        COMPLETION.dependencies.add(MASTER);
    }

    public Set<StepType> getNextSteps() {
        Set<StepType> nextSteps = new HashSet<>();

        switch (this) {
            case ACQUISITION -> nextSteps.add(FACILITY);
            case FACILITY -> {
                nextSteps.add(SITE);
                nextSteps.add(EQUIPMENT);
            }
            case SITE, EQUIPMENT -> {
                if (areParallelStepsCompleted()) {
                    nextSteps.add(MASTER);
                }
            }
            case MASTER -> nextSteps.add(COMPLETION);
            case COMPLETION -> { }
        }

        return nextSteps;
    }

    private boolean areParallelStepsCompleted() {
        return true;
    }

    public boolean canStart(Set<StepType> completedSteps) {
        return completedSteps.containsAll(dependencies);
    }

    public Set<StepType> getDependencies() {
        return new HashSet<>(dependencies);
    }
}
