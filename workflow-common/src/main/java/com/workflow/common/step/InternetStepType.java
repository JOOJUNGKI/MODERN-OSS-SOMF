package com.workflow.common.step;

import lombok.Getter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
            case ACQUISITION -> nextSteps.add(FACILITY);
            case FACILITY -> {
                nextSteps.add(SITE);
                nextSteps.add(EQUIPMENT);
            }
            case SITE, EQUIPMENT -> {
                // Check if all dependencies for MASTER are completed
                if (MASTER.getDependencies().stream()
                        .allMatch(dep -> dependencies.contains(dep))) {
                    nextSteps.add(MASTER);
                }
            }
            case MASTER -> nextSteps.add(COMPLETION);
            case COMPLETION -> {}
        }
        return nextSteps;
    }

    @Override
    public boolean canStart(Set<? extends StepTypeStrategy> completedSteps) {
        // Convert completedSteps to Set<InternetStepType>
        Set<InternetStepType> internetSteps = completedSteps.stream()
                .filter(step -> step instanceof InternetStepType)
                .map(step -> (InternetStepType) step)
                .collect(Collectors.toSet());

        return internetSteps.containsAll(this.dependencies);
    }

    @Override
    public String getStepName() {    // name() 대신 getStepName() 구현
        return this.name();
    }

    @Override
    public Set<? extends StepTypeStrategy> getDependencies() {
        return new HashSet<>(dependencies);
    }
}