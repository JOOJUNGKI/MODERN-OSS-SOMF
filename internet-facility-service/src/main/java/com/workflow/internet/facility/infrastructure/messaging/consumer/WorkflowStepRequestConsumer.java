
package com.workflow.internet.facility.infrastructure.messaging.consumer;

import com.workflow.common.event.WorkflowStepEvent;
import com.workflow.internet.facility.domain.service.InternetFacilityService;
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

   private final InternetFacilityService internetFacilityService;

    @KafkaListener(topics = "${kafka.topics.internet.step.request}")
    public void handleInternetStepResponse(WorkflowStepEvent event) {
        log.debug("[INTERNET(FACILITY)]Received step response: {}", event);
        try {
            internetFacilityService.doProcess(event);
            log.info("Successfully processed step completion for workflow: {}", event.getWorkflowId());
        } catch (Exception e) {
            log.error("Failed to process step completion for workflow: {}", event.getWorkflowId(), e);
        }
    }
}
