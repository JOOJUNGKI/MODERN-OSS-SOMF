
package com.workflow.provisioning.infrastructure.messaging.publisher;

import com.workflow.provisioning.domain.event.WorkflowStepEvent;
import com.workflow.provisioning.domain.service.ProvisioningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowStepResponsePublisher {
    private final KafkaTemplate<String, WorkflowStepEvent> kafkaTemplate;

    @Value("${kafka.topics.iptv.step.response}")
    private String responseIptvStepTopic;

    @Value("${kafka.topics.internet.step.response}")
    private String responseInternetStepTopic;

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

    public void publishInternetStepResponse(WorkflowStepEvent event) {
        kafkaTemplate.send(responseInternetStepTopic, event.getWorkflowId(), event)
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
