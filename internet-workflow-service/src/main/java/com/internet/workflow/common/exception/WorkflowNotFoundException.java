package com.internet.workflow.common.exception;

public class WorkflowNotFoundException extends RuntimeException {
    public WorkflowNotFoundException(String workflowId) {
        super("Workflow not found with id: " + workflowId);
    }
}
