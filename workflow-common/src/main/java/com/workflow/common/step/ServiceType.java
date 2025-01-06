package com.workflow.common.step;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServiceType {
    INTERNET("Internet", InternetStepType.class),
    IPTV("IPTV", IPTVStepType.class);

    private final String code;
    private final Class<? extends StepTypeStrategy> stepTypeClass;

    public static ServiceType fromCode(String code) {
        for (ServiceType type : values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown service type: " + code);
    }
}
