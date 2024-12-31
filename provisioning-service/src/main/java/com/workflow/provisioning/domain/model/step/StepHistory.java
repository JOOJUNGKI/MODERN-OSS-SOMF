package com.workflow.provisioning.domain.model.step;

import com.workflow.provisioning.domain.model.lob.LobType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StepHistory {
   private final StepType stepType;

   private final LobType lobType;

   private final String orderNumber;

   private final LocalDateTime startAt;
   private LocalDateTime endAt;

   public void complete() {
       this.endAt = LocalDateTime.now();
   }
}
