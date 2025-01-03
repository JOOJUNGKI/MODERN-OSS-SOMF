package com.workflow.domain.service;

import com.workflow.api.dto.WorkflowHistoryResponse;
import com.workflow.api.dto.WorkflowResponse;
import com.workflow.common.event.StepType;
import com.workflow.common.event.WorkflowCreationEvent;
import com.workflow.common.exception.WorkflowNotFoundException;
import com.workflow.domain.model.workflow.Workflow;
import com.workflow.domain.model.workflow.WorkflowStatus;
import com.workflow.infrastructure.messaging.publisher.WorkflowStepRequestPublisher;
import com.workflow.infrastructure.persistence.entity.WorkflowEntity;
import com.workflow.infrastructure.persistence.mapper.WorkflowMapper;
import com.workflow.infrastructure.persistence.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WorkflowService {
    private final WorkflowRepository workflowRepository;
    private final WorkflowMapper workflowMapper;
    private final WorkflowScheduler scheduler;

    public void handleWorkflowCreation(WorkflowCreationEvent event) {
        log.debug("Handling workflow creation event: {}", event);
        scheduler.scheduleWorkflow(event);
    }

    @Transactional(readOnly = true)
    public WorkflowResponse getWorkflow(String workflowId) {
        return workflowRepository.findById(workflowId)
                .map(workflowMapper::toResponse)
                .orElseThrow(() -> new WorkflowNotFoundException(workflowId));
    }

    @Transactional(readOnly = true)
    public WorkflowResponse getWorkflowWithOrderNumber(String orderNumber) {
        return workflowRepository.findByOrderNumber(orderNumber)
                .map(workflowMapper::toResponse)
                .orElseThrow(() -> new WorkflowNotFoundException("Order not found: " + orderNumber));
    }

    @Transactional(readOnly = true)
    public List<WorkflowHistoryResponse> getWorkflowHistory(String workflowId) {
        return workflowRepository.findById(workflowId)
                .map(entity -> workflowMapper.toHistoryResponses(entity.getStepHistories()))
                .orElseThrow(() -> new WorkflowNotFoundException(workflowId));
    }

    public void handleStepCompletion(String workflowId, StepType completedStep) {
        log.debug("Handling step completion: {} for workflow: {}", completedStep, workflowId);
        scheduler.handleStepCompletion(workflowId, completedStep);
    }

    @Transactional(readOnly = true)
    public Workflow getWorkflowById(String workflowId) {
        return workflowRepository.findById(workflowId)
                .map(workflowMapper::toDomain)
                .orElseThrow(() -> new WorkflowNotFoundException(workflowId));
    }

    private void validateWorkflowExists(String workflowId) {
        if (!workflowRepository.existsById(workflowId)) {
            throw new WorkflowNotFoundException(workflowId);
        }
    }
}