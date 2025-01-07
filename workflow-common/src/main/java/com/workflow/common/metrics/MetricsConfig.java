// workflow-common/src/main/java/com/workflow/common/metrics/MetricsConfig.java
package com.workflow.common.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class MetricsConfig {
    private final MeterRegistry registry;

    public MetricsConfig(MeterRegistry registry) {
        this.registry = registry;
    }

    // Workflow Metrics
    public Counter workflowCreationCounter() {
        return Counter.builder("workflow_creation_total")
                .description("Total number of workflows created")
                .register(registry);
    }

    public Counter workflowCompletionCounter() {
        return Counter.builder("workflow_completion_total")
                .description("Total number of workflows completed")
                .register(registry);
    }

    public Timer workflowProcessingTimer() {
        return Timer.builder("workflow_processing_time")
                .description("Time taken to process workflows")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .sla(
                        Duration.ofSeconds(5),
                        Duration.ofSeconds(10),
                        Duration.ofSeconds(15)
                )
                .register(registry);
    }

    // Step Metrics
    public Counter stepCompletionCounter(String stepType) {
        return Counter.builder("step_completion_total")
                .tag("step", stepType)
                .description("Total number of steps completed")
                .register(registry);
    }

    public Timer stepProcessingTimer(String stepType) {
        return Timer.builder("step_processing_time")
                .tag("step", stepType)
                .description("Time taken to process steps")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .sla(
                        Duration.ofMillis(100),
                        Duration.ofMillis(500),
                        Duration.ofSeconds(1)
                )
                .register(registry);
    }

    // Message Queue Metrics
    public Counter messageProcessedCounter(String topic) {
        return Counter.builder("message_processed_total")
                .tag("topic", topic)
                .description("Total number of messages processed")
                .register(registry);
    }

    public Timer messageProcessingTimer(String topic) {
        return Timer.builder("message_processing_time")
                .tag("topic", topic)
                .description("Time taken to process messages")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .sla(
                        Duration.ofMillis(50),
                        Duration.ofMillis(100),
                        Duration.ofMillis(200)
                )
                .register(registry);
    }
}