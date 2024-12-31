
package com.workflow.provisioning.infrastructure.persistence.mapper;

import com.workflow.provisioning.domain.event.WorkflowStepEvent;
import com.workflow.provisioning.domain.model.step.StepHistory;
import com.workflow.provisioning.infrastructure.persistence.entity.StepHistoryEntity;
import org.springframework.stereotype.Component;

@Component
public class StepMapper {
   public StepHistoryEntity toEntity(StepHistory stepHistory) {
       StepHistoryEntity entity = new StepHistoryEntity();
       entity.setOrderNumber(stepHistory.getOrderNumber());
       entity.setStepType(stepHistory.getStepType());
       return entity;
   }

   public StepHistory toDomain(StepHistoryEntity entity) {
       return StepHistory.builder()
               .stepType(entity.getStepType())
               .lobType(entity.getLob())
               .startAt(entity.getStartAt())
               .endAt(entity.getEndAt())
               .build();
   }

    public StepHistoryEntity toEntity(WorkflowStepEvent event) {
        StepHistoryEntity entity = new StepHistoryEntity();
        entity.setOrderNumber(event.getOrderNumber());
        entity.setStepType(event.getStepType());
        return entity;
    }
}
