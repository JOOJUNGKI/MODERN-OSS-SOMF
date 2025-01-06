
package com.workflow.iptv.certification.infrastructure.messaging.publisher;

import com.workflow.common.event.WorkflowStepEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowStepResponsePublisher {
    private final KafkaTemplate<String, WorkflowStepEvent> kafkaTemplate;

    @Value("${kafka.topics.iptv.step.response}")
    private String responseIptvStepTopic;


    public void publishIptvStepResponse(WorkflowStepEvent event) {
        kafkaTemplate.send(responseIptvStepTopic, event.getWorkflowId(), event)
                .whenComplete((result, ex) -> {
                            if (ex == null) {
                                log.debug("Successfully sent step request: {}", event);
                            } else {
                                log.error("Failed to send step request: {}", event, ex);
                            }
                        }
                );
    }
}
