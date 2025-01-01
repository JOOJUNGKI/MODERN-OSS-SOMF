
package com.workflow.provisioning.infrastructure.messaging.consumer;

import com.workflow.common.event.WorkflowStepEvent;
import com.workflow.provisioning.domain.service.ProvisioningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowStepRequestConsumer {
   private final KafkaTemplate<String, WorkflowStepEvent> kafkaTemplate;


   private final ProvisioningService provisioningService;

    @KafkaListener(topics = "${kafka.topics.iptv.step.request}")
    public void handleIptvStepResponse(WorkflowStepEvent event) {
        log.debug("[IPTV]Received step response: {}", event);
        try {
            provisioningService.doProcess(event);
            //provisioningService.handleStepCompletion(event.getWorkflowId(), event.getPayload());
            log.info("Successfully processed step completion for workflow: {}", event.getWorkflowId());
        } catch (Exception e) {
            log.error("Failed to process step completion for workflow: {}", event.getWorkflowId(), e);
        }
    }

    @KafkaListener(topics = "${kafka.topics.internet.step.request}")
    public void handleInternetStepResponse(WorkflowStepEvent event) {
        log.debug("[INTERNET]Received step response: {}", event);
        try {
            provisioningService.doProcess(event);
            //provisioningService.handleStepCompletion(event.getWorkflowId(), event.getPayload());
            log.info("Successfully processed step completion for workflow: {}", event.getWorkflowId());
        } catch (Exception e) {
            log.error("Failed to process step completion for workflow: {}", event.getWorkflowId(), e);
        }
    }
}
