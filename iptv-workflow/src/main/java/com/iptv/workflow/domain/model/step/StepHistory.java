package com.iptv.workflow.domain.model.step;

import com.workflow.common.step.StepTypeStrategy;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StepHistory {
   private final StepTypeStrategy stepTypeStrategy;  // Changed from stepType
   private final LocalDateTime startTime;
   private LocalDateTime endTime;

   public void complete() {
      if (this.endTime != null) {
         throw new IllegalStateException("Step already completed");
      }
      this.endTime = LocalDateTime.now();
   }

   public boolean isCompleted() {
      return this.endTime != null;
   }
}