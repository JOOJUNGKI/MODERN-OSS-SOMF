
package com.workflow.infrastructure.messaging.publisher;

import com.workflow.domain.event.WorkflowStepEvent;
import com.workflow.domain.model.workflow.Workflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowStepRequestPublisher {
   private final KafkaTemplate<String, WorkflowStepEvent> kafkaTemplate;
   
   @Value("${kafka.topics.step.request}")
   private String requestTopic;

   public void publishStepRequest(Workflow workflow) {
       WorkflowStepEvent event = WorkflowStepEvent.builder()
               .workflowId(workflow.getId())
               .stepType(workflow.getCurrentStep())
               .timestamp(LocalDateTime.now())
               .build();

       kafkaTemplate.send(requestTopic, workflow.getId(), event)
               .whenComplete((result, ex) -> {
                   if (ex == null) {
                       log.debug("Successfully sent step request: {}", event);
                   } else {
                       log.error("Failed to send step request: {}", event, ex);
                   }
               });
   }
}
