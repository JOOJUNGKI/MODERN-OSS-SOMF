
package com.workflow.acl.api.controller;


import com.workflow.acl.api.dto.LegacyWorkflowRequest;
import com.workflow.acl.service.LegacyWorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/legacy/workflows")
@RequiredArgsConstructor
@Tag(name = "레거시 워크플로우 연동 API")
public class LegacyWorkflowController {
    private final LegacyWorkflowService legacyWorkflowService;

    @PostMapping("/create")
    @Operation(summary = "워크플로우 생성 요청")
    public ResponseEntity<Map<String, String>> requestWorkflowCreation(
            @RequestBody @Valid LegacyWorkflowRequest request) {
        String requestId = legacyWorkflowService.requestWorkflowCreation(request);
        return ResponseEntity.accepted()
            .body(Map.of("requestId", requestId, "message", "요청이 접수되었습니다."));
    }

    @PostMapping("/test")
    @Operation(summary = "테스트")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("호출");
    }
}
