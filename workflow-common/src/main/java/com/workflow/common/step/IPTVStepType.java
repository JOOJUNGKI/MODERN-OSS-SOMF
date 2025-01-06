package com.workflow.common.step;

import lombok.Getter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum IPTVStepType implements StepTypeStrategy {
    ACQUISITION("입수", 1),
    CERTIFICATION("인증", 2),
    SITE("현장", 3),
    MASTER("원부", 4),
    COMPLETION("완료", 5);

    private final String description;
    private final int order;
    private final Set<IPTVStepType> dependencies = new HashSet<>();

    IPTVStepType(String description, int order) {
        this.description = description;
        this.order = order;
    }

    static {
        CERTIFICATION.dependencies.add(ACQUISITION);
        SITE.dependencies.add(CERTIFICATION);
        MASTER.dependencies.add(SITE);
        COMPLETION.dependencies.add(MASTER);
    }

    @Override
    public Set<IPTVStepType> getNextSteps() {
        Set<IPTVStepType> nextSteps = new HashSet<>();
        switch (this) {
            case ACQUISITION -> nextSteps.add(CERTIFICATION);
            case CERTIFICATION -> nextSteps.add(SITE);
            case SITE -> nextSteps.add(MASTER);
            case MASTER -> nextSteps.add(COMPLETION);
            case COMPLETION -> {}
        }
        return nextSteps;
    }

    @Override
    public boolean canStart(Set<? extends StepTypeStrategy> completedSteps) {
        // Convert completedSteps to Set<IPTVStepType>
        Set<IPTVStepType> iptvSteps = completedSteps.stream()
                .filter(step -> step instanceof IPTVStepType)
                .map(step -> (IPTVStepType) step)
                .collect(Collectors.toSet());

        return iptvSteps.containsAll(this.dependencies);
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