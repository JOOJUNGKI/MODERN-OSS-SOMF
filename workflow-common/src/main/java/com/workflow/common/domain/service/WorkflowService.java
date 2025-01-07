package com.workflow.common.domain.service;

import com.workflow.common.event.StepType;
import com.workflow.common.event.WorkflowCreationEvent;
import com.workflow.common.metrics.MetricsConfig;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {
    private final MetricsConfig metricsConfig;

    public WorkflowService(MetricsConfig metricsConfig) {
        this.metricsConfig = metricsConfig;
    }

    public void handleWorkflowCreation(WorkflowCreationEvent event) {
        Timer.Sample sample = Timer.start();
        try {
            // Existing logic
            metricsConfig.workflowCreationCounter().increment();
        } finally {
            sample.stop(metricsConfig.workflowProcessingTimer());
        }
    }

    public void handleStepCompletion(String workflowId, StepType completedStep) {
        Timer.Sample sample = Timer.start();
        try {
            // Existing logic
            metricsConfig.stepCompletionCounter(completedStep.name()).increment();
        } finally {
            sample.stop(metricsConfig.stepProcessingTimer(completedStep.name()));
        }
    }
}