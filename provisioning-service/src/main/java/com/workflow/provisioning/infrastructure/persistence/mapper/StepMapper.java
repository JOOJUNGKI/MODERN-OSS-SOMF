
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
               .serviceType(entity.getServiceType())
               .startAt(entity.getStartAt())
               .endAt(entity.getEndAt())
               .build();
   }

    public StepHistoryEntity toEntity(WorkflowStepEvent event) {
        StepHistoryEntity entity = new StepHistoryEntity();
        entity.setWorkflowId(event.getWorkflowId());
        entity.setOrderNumber(event.getOrderNumber());
        entity.setStepType(event.getStepType());
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
               .stepType(entity.getStepType())
               .timestamp(entity.getEndAt())
               .build();
    }
}
