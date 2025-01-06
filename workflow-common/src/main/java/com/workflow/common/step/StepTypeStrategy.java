package com.workflow.common.step;

import java.util.Set;

public interface StepTypeStrategy {
    String getStepName();  // name() 대신 getStepName() 사용
    String getDescription();
    int getOrder();
    Set<? extends StepTypeStrategy> getDependencies();
    Set<? extends StepTypeStrategy> getNextSteps();
    boolean canStart(Set<? extends StepTypeStrategy> completedSteps);
}