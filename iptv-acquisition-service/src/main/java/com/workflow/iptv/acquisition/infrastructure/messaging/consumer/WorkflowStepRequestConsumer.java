
package com.workflow.iptv.acquisition.infrastructure.messaging.consumer;

import com.workflow.common.event.WorkflowStepEvent;
import com.workflow.iptv.acquisition.domain.service.IptvAcquisitionService;
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

   private final IptvAcquisitionService iptvAcquisitionService;

    @KafkaListener(topics = "${kafka.topics.iptv.step.request}")
    public void handleIptvStepResponse(WorkflowStepEvent event) {
        log.debug("[IPTV(ACQUISITION)]Received step response: {}", event);
        try {
            iptvAcquisitionService.doProcess(event);
            log.info("Successfully processed step completion for workflow: {}", event.getWorkflowId());
        } catch (Exception e) {
            log.error("Failed to process step completion for workflow: {}", event.getWorkflowId(), e);
        }
    }
}
