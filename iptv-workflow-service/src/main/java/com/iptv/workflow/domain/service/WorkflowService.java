package com.iptv.workflow.domain.service;

import com.iptv.workflow.api.dto.WorkflowHistoryResponse;
import com.iptv.workflow.api.dto.WorkflowResponse;
import com.workflow.common.event.WorkflowCreationEvent;
import com.iptv.workflow.common.exception.WorkflowNotFoundException;
import com.workflow.common.step.ServiceType;
import com.workflow.common.step.StepTypeStrategy;
import com.iptv.workflow.domain.model.workflow.Workflow;
import com.iptv.workflow.infrastructure.persistence.mapper.WorkflowMapper;
import com.iptv.workflow.infrastructure.persistence.repository.WorkflowRepository;
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
        validateServiceType(event.getServiceType());
        scheduler.scheduleWorkflow(event);
    }

    private void validateServiceType(String serviceType) {
        ServiceType type = ServiceType.fromCode(serviceType);
        if (type != ServiceType.IPTV) {
            //throw new UnsupportedServiceTypeException(serviceType);
            log.debug("InvalidateServiceType(Not IPTV)");

        }
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

    public void handleStepCompletion(String workflowId, StepTypeStrategy completedStep) {
        log.debug("Handling step completion: {} for workflow: {}", completedStep.getStepName(), workflowId);

        Workflow workflow = getWorkflowById(workflowId);
        validateStepType(completedStep, workflow.getServiceType());

        scheduler.handleStepCompletion(workflowId, completedStep);
    }

    private void validateStepType(StepTypeStrategy stepType, String serviceType) {
        ServiceType type = ServiceType.fromCode(serviceType);
        if (!stepType.getClass().equals(type.getStepTypeClass())) {
            throw new IllegalArgumentException("Invalid step type for service: " + serviceType);
        }
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
