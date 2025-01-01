package com.workflow.provisioning.domain.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.workflow.provisioning.domain.model.step.StepType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WorkflowStepEvent {
   private String workflowId;
   private StepType stepType;
   private String payload;

   private String orderNumber;
   private Integer orderSeq;
   private String serviceType;
   private String orderType;
   private String custName;
   private String address;

   @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
   private LocalDateTime timestamp;
}
