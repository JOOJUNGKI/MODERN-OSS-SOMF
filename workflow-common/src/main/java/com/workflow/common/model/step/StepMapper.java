package com.workflow.common.model.step;

import com.workflow.common.event.WorkflowStepEvent;
import org.springframework.stereotype.Component;

@Component
public class StepMapper {
    public StepHistoryEntityBase toEntity(StepHistory stepHistory) {
        StepHistoryEntityBase entity = new StepHistoryEntityBase();
        entity.setOrderNumber(stepHistory.getOrderNumber());
        entity.setStepType(stepHistory.getStepType());
        return entity;
    }

    public StepHistory toDomain(StepHistoryEntityBase entity) {
        return StepHistory.builder()
                .stepType(entity.getStepType())
                .serviceType(entity.getServiceType())
                .startAt(entity.getStartAt())
                .endAt(entity.getEndAt())
                .build();
    }

    public StepHistoryEntityBase toEntity(WorkflowStepEvent event) {
        StepHistoryEntityBase entity = new StepHistoryEntityBase();
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

    public WorkflowStepEvent toEvent(StepHistoryEntityBase entity) {
        return WorkflowStepEvent.builder()
                .workflowId(entity.getWorkflowId())
                .orderNumber(entity.getOrderNumber())
                .serviceType(entity.getServiceType())
                .stepType(entity.getStepType())
                .timestamp(entity.getEndAt())
                .build();
    }
}