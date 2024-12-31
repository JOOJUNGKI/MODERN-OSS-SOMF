package com.workflow.domain.model.step;

import lombok.Getter;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;

@Getter
public enum StepType {
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
        initializeDependencies();
    }

    private void initializeDependencies() {
        switch (this) {
            case ACQUISITION -> { } // 시작 단계이므로 의존성 없음
            case FACILITY -> dependencies.add(ACQUISITION);  // 시설은 입수 후
            case SITE -> dependencies.add(FACILITY);        // 현장은 시설 후
            case EQUIPMENT -> dependencies.add(FACILITY);   // 장치도 시설 후
            case MASTER -> {                               // 원부는 현장과 장치 모두 완료 후
                dependencies.add(SITE);
                dependencies.add(EQUIPMENT);
            }
            case COMPLETION -> dependencies.add(MASTER);    // 준공은 원부 후
        }
    }

    public Set<StepType> getNextSteps() {
        Set<StepType> nextSteps = new HashSet<>();

        switch (this) {
            case ACQUISITION -> nextSteps.add(FACILITY);
            case FACILITY -> {
                // 시설 완료 후 현장과 장치 동시 시작
                nextSteps.add(SITE);
                nextSteps.add(EQUIPMENT);
            }
            case SITE, EQUIPMENT -> {
                // 현장이나 장치가 완료되면 다른 쪽도 확인 필요
                if (areParallelStepsCompleted()) {
                    nextSteps.add(MASTER);
                }
            }
            case MASTER -> nextSteps.add(COMPLETION);
            case COMPLETION -> { } // 마지막 단계
        }

        return nextSteps;
    }

    private boolean areParallelStepsCompleted() {
        return true; // 실제 구현에서는 현장과 장치의 완료 상태를 확인하는 로직 필요
    }

    /**
     * 현재 단계가 시작 가능한지 확인
     * @param completedSteps 완료된 단계들
     * @return 시작 가능 여부
     */
    public boolean canStart(Set<StepType> completedSteps) {
        return completedSteps.containsAll(dependencies);
    }

    /**
     * 모든 필수 선행 단계들을 반환
     * @return 선행 단계들
     */
    public Set<StepType> getDependencies() {
        return EnumSet.copyOf(dependencies);
    }
}