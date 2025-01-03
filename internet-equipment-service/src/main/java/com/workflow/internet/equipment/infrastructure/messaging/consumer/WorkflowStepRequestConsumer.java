
package com.workflow.internet.equipment.infrastructure.messaging.consumer;

import com.workflow.common.event.WorkflowStepEvent;
import com.workflow.internet.equipment.domain.service.InternetEquipmentService;
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

   private final InternetEquipmentService internetEquipmentService;

    @KafkaListener(topics = "${kafka.topics.internet.step.request}")
    public void handleInternetStepResponse(WorkflowStepEvent event) {
        log.debug("[INTERNET(SITE)]Received step response: {}", event);
        try {
            internetEquipmentService.doProcess(event);
            log.info("Successfully processed step completion for workflow: {}", event.getWorkflowId());
        } catch (Exception e) {
            log.error("Failed to process step completion for workflow: {}", event.getWorkflowId(), e);
        }
    }
}
