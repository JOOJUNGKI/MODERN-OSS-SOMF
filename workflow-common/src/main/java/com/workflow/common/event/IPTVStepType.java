package com.workflow.common.event;

import lombok.Getter;
import java.util.HashSet;
import java.util.Set;

@Getter
public enum IPTVStepType {
    ACQUISITION("입수", 1),
    AUTHENTICATION("인증", 2),
    SITE("현장", 3),
    MASTER("원부", 4),
    COMPLETION("완료", 5);

    private final String description;
    private final int order;
    private final Set<IPTVStepType> dependencies;

    IPTVStepType(String description, int order) {
        this.description = description;
        this.order = order;
        this.dependencies = new HashSet<>();
    }

    static {
        AUTHENTICATION.dependencies.add(ACQUISITION);
        SITE.dependencies.add(AUTHENTICATION);
        MASTER.dependencies.add(SITE);
        COMPLETION.dependencies.add(MASTER);
    }

    public Set<IPTVStepType> getNextSteps() {
        Set<IPTVStepType> nextSteps = new HashSet<>();

        switch (this) {
            case ACQUISITION -> nextSteps.add(AUTHENTICATION);
            case AUTHENTICATION -> nextSteps.add(SITE);
            case SITE -> nextSteps.add(MASTER);
            case MASTER -> nextSteps.add(COMPLETION);
            case COMPLETION -> { }
        }

        return nextSteps;
    }

    public boolean canStart(Set<IPTVStepType> completedSteps) {
        return completedSteps.containsAll(dependencies);
    }

    public Set<IPTVStepType> getDependencies() {
        return new HashSet<>(dependencies);
    }
}