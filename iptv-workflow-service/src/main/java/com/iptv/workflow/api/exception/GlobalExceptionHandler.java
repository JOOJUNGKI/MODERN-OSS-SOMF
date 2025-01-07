// File: myprj6/workflow-service/src/main/java/com/workflow/api/exception/GlobalExceptionHandler.java
package com.iptv.workflow.api.exception;

import com.iptv.workflow.common.exception.WorkflowNotFoundException;
import jakarta.persistence.OptimisticLockException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ConcurrentModificationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WorkflowNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWorkflowNotFoundException(WorkflowNotFoundException e) {
        log.error("Workflow not found: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("WORKFLOW_NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException e) {
        log.error("Workflow state error", e);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("INVALID_WORKFLOW_STATE", e.getMessage()));
    }

    @ExceptionHandler({OptimisticLockException.class, OptimisticLockingFailureException.class})
    public ResponseEntity<ErrorResponse> handleOptimisticLockingFailure(Exception e) {
        log.error("Optimistic locking failure detected", e);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("CONCURRENT_MODIFICATION",
                        "데이터가 다른 프로세스에 의해 수정되었습니다. 다시 시도해주세요."));
    }

    @ExceptionHandler(ConcurrentModificationException.class)
    public ResponseEntity<ErrorResponse> handleConcurrentModification(
            ConcurrentModificationException e) {
        log.error("Concurrent modification detected", e);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("CONCURRENT_MODIFICATION",
                        "워크플로우가 다른 프로세스에 의해 수정되었습니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception e) {
        log.error("Unexpected error occurred", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_SERVER_ERROR",
                        "내부 서버 오류가 발생했습니다."));
    }
}

@Getter
@AllArgsConstructor
class ErrorResponse {
    private String code;
    private String message;
}