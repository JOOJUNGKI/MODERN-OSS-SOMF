package com.workflow.domain.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.workflow.domain.model.step.StepType;
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
   private Integer orderSeq;
   private String serviceType;
   private String orderType;
   private String custName;
   private String address;

   @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
   private LocalDateTime timestamp;
}
