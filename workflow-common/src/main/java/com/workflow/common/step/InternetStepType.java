package com.workflow.common.step;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Getter
public enum InternetStepType implements StepTypeStrategy {
    ACQUISITION("입수", 1),
    FACILITY("시설", 2),
    SITE("현장", 3),
    EQUIPMENT("장치", 4),
    MASTER("원부", 5),
    COMPLETION("준공", 6);

    private final String description;
    private final int order;
    private final Set<InternetStepType> dependencies = new HashSet<>();

    InternetStepType(String description, int order) {
        this.description = description;
        this.order = order;
    }

    static {
        FACILITY.dependencies.add(ACQUISITION);
        SITE.dependencies.add(FACILITY);
        EQUIPMENT.dependencies.add(FACILITY);
        MASTER.dependencies.addAll(Set.of(SITE, EQUIPMENT));
        COMPLETION.dependencies.add(MASTER);
    }

    @Override
    public Set<InternetStepType> getNextSteps() {
        Set<InternetStepType> nextSteps = new HashSet<>();
        switch (this) {
            case ACQUISITION -> {
                nextSteps.add(FACILITY);
                log.debug("ACQUISITION's next step is FACILITY");
            }
            case FACILITY -> {
                nextSteps.add(SITE);
                nextSteps.add(EQUIPMENT);
                log.debug("FACILITY's next steps are SITE and EQUIPMENT");
            }
            case SITE, EQUIPMENT -> {
                // Changed: SITE나 EQUIPMENT 완료 시 MASTER를 다음 스텝으로 추가
                nextSteps.add(MASTER);
                log.debug("{}'s next step is MASTER", this.name());
            }
            case MASTER -> {
                nextSteps.add(COMPLETION);
                log.debug("MASTER's next step is COMPLETION");
            }
            case COMPLETION -> {
                log.debug("COMPLETION has no next steps");
            }
        }
        return nextSteps;
    }

    @Override
    public boolean canStart(Set<? extends StepTypeStrategy> completedSteps) {
        // 완료된 스텝들을 InternetStepType으로 변환
        Set<InternetStepType> internetSteps = completedSteps.stream()
                .filter(step -> step instanceof InternetStepType)
                .map(step -> (InternetStepType) step)
                .collect(Collectors.toSet());

        boolean canStart = switch (this) {
            case MASTER -> internetSteps.contains(SITE) && internetSteps.contains(EQUIPMENT);
            default -> internetSteps.containsAll(this.dependencies);
        };

        log.info("Checking if {} can start. Completed steps: {}, Dependencies: {}, Result: {}",
                this.name(),
                internetSteps.stream().map(Enum::name).collect(Collectors.joining(", ")),
                this.dependencies.stream().map(Enum::name).collect(Collectors.joining(", ")),
                canStart);

        return canStart;
    }

    @Override
    public String getStepName() {
        return this.name();
    }

    @Override
    public Set<? extends StepTypeStrategy> getDependencies() {
        return new HashSet<>(dependencies);
    }
}