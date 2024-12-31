package com.workflow.provisioning.domain.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.workflow.provisioning.domain.model.step.StepType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowStepEvent {
   private String workflowId;
   private StepType stepType;
   private String payload;
   private String orderNumber;
   private String lob;

   @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
   private LocalDateTime timestamp;
}
