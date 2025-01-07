package com.iptv.workflow.infrastructure.persistence.mapper;

import com.iptv.workflow.api.dto.WorkflowHistoryResponse;
import com.iptv.workflow.api.dto.WorkflowResponse;
import com.iptv.workflow.domain.model.step.StepHistory;
import com.iptv.workflow.domain.model.workflow.Workflow;
import com.iptv.workflow.infrastructure.persistence.entity.StepHistoryEntity;
import com.iptv.workflow.infrastructure.persistence.entity.WorkflowEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WorkflowMapper {
    public WorkflowEntity toEntity(Workflow workflow) {
        WorkflowEntity entity = new WorkflowEntity();
        entity.setId(workflow.getId());
        entity.setOrderNumber(workflow.getOrderNumber());
        entity.setOrderSeq(workflow.getOrderSeq());
        entity.setServiceType(workflow.getServiceType());
        entity.setOrderType(workflow.getOrderType());
        entity.setCustName(workflow.getCustName());
        entity.setAddress(workflow.getAddress());
        entity.setStatus(workflow.getStatus());
        entity.setCreatedAt(workflow.getCreatedAt());
        entity.setUpdatedAt(workflow.getUpdatedAt());
        entity.setActiveSteps(workflow.getActiveSteps());
        entity.setCompletedSteps(workflow.getCompletedSteps());
        entity.setStepHistories(workflow.getStepHistories().stream()
                .map(history -> toStepHistoryEntity(history, entity))
                .collect(Collectors.toList()));
        return entity;
    }

    private StepHistoryEntity toStepHistoryEntity(StepHistory history, WorkflowEntity workflow) {
        StepHistoryEntity entity = new StepHistoryEntity();
        entity.setStepTypeStrategy(history.getStepTypeStrategy());  // Changed from getStepType
        entity.setStartTime(history.getStartTime());
        entity.setEndTime(history.getEndTime());
        entity.setWorkflow(workflow);
        return entity;
    }

    private StepHistory toStepHistory(StepHistoryEntity entity) {
        return StepHistory.builder()
                .stepTypeStrategy(entity.getStepTypeStrategy())  // Changed from getStepType
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .build();
    }

    public WorkflowResponse toResponse(WorkflowEntity entity) {
        return WorkflowResponse.builder()
                .id(entity.getId())
                .orderNumber(entity.getOrderNumber())
                .status(entity.getStatus())
                .activeSteps(entity.getActiveSteps())
                .completedSteps(entity.getCompletedSteps())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<WorkflowHistoryResponse> toHistoryResponses(List<StepHistoryEntity> histories) {
        return histories.stream()
                .map(entity -> WorkflowHistoryResponse.builder()
                        .stepType(entity.getStepTypeStrategy())  // Changed from getStepType
                        .startTime(entity.getStartTime())
                        .endTime(entity.getEndTime())
                        .build())
                .collect(Collectors.toList());
    }

    public Workflow toDomain(WorkflowEntity entity) {
        return Workflow.builder()
                .id(entity.getId())
                .orderNumber(entity.getOrderNumber())
                .orderSeq(entity.getOrderSeq())
                .serviceType(entity.getServiceType())
                .orderType(entity.getOrderType())
                .custName(entity.getCustName())
                .address(entity.getAddress())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .activeSteps(entity.getActiveSteps())
                .completedSteps(entity.getCompletedSteps())
                .stepHistories(entity.getStepHistories().stream()
                        .map(this::toStepHistory)
                        .collect(Collectors.toList()))
                .build();
    }
}