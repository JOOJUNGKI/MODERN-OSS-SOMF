
package com.workflow.internet.master.infrastructure.persistence.mapper;

import com.workflow.common.event.WorkflowStepEvent;
import com.workflow.common.model.step.StepHistory;
import com.workflow.internet.master.infrastructure.persistence.entity.StepHistoryEntity;
import org.springframework.stereotype.Component;

@Component
public class StepMapper {
   public StepHistoryEntity toEntity(StepHistory stepHistory) {
       StepHistoryEntity entity = new StepHistoryEntity();
       entity.setOrderNumber(stepHistory.getOrderNumber());
       entity.setStepTypeStrategy(stepHistory.getStepType());
       return entity;
   }

   public StepHistory toDomain(StepHistoryEntity entity) {
       return StepHistory.builder()
               .stepType(entity.getStepTypeStrategy())
               .serviceType(entity.getServiceType())
               .startAt(entity.getStartAt())
               .endAt(entity.getEndAt())
               .build();
   }

    public StepHistoryEntity toEntity(WorkflowStepEvent event) {
        StepHistoryEntity entity = new StepHistoryEntity();
        entity.setWorkflowId(event.getWorkflowId());
        entity.setOrderNumber(event.getOrderNumber());
        entity.setStepTypeStrategy(event.getStepType());
        entity.setServiceType(event.getServiceType());
        entity.setOrderType(event.getOrderType());
        entity.setOrderSeq(event.getOrderSeq());
        entity.setCustName(event.getCustName());
        entity.setAddress(event.getAddress());
        return entity;
    }

    public WorkflowStepEvent toEvent(StepHistoryEntity entity) {
       return WorkflowStepEvent.builder()
               .workflowId(entity.getWorkflowId())
               .orderNumber(entity.getOrderNumber())
               .serviceType(entity.getServiceType())
               .stepTypeName(entity.getStepTypeStrategy().getStepName())
               .timestamp(entity.getEndAt())
               .build();
    }
}
