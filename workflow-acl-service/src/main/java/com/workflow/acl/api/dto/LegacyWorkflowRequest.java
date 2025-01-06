
package com.workflow.acl.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
@Schema(description = "레거시 워크플로우 생성 요청")
public class LegacyWorkflowRequest {
    @NotBlank(message = "주문번호는 필수입니다")
    @Schema(description = "주문번호", example = "ORD20240101")
    private String orderNumber;
    
    @NotNull(message = "주문순번은 필수입니다")
    @Schema(description = "주문순번", example = "1")
    private Integer orderSeq;
    
    @NotBlank(message = "서비스유형은 필수입니다")
    @Pattern(regexp = "^(Internet|IPTV)$", message = "서비스유형은 'Internet', 'IPTV' 만 가능합니다")
    @Schema(description = "서비스유형", example = "Internet", allowableValues = {"Internet","IPTV"})
    private String serviceType;
    
    @NotBlank(message = "주문유형은 필수입니다")
    @Schema(description = "주문유형", example = "New")
    private String orderType;
    
    @NotBlank(message = "고객명은 필수입니다")
    @Schema(description = "고객명", example = "홍길동")
    private String custName;
    
    @NotBlank(message = "주소는 필수입니다")
    @Schema(description = "주소", example = "서울시 강남구")
    private String address;
}
