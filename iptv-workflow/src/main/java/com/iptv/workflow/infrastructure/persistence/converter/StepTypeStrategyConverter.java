package com.iptv.workflow.infrastructure.persistence.converter;

import com.workflow.common.step.IPTVStepType;
import com.workflow.common.step.InternetStepType;
import com.workflow.common.step.StepTypeStrategy;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StepTypeStrategyConverter implements AttributeConverter<StepTypeStrategy, String> {

    @Override
    public String convertToDatabaseColumn(StepTypeStrategy attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getClass().getSimpleName() + ":" + attribute.getStepName();
    }

    @Override
    public StepTypeStrategy convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }

        String[] parts = dbData.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid format for StepTypeStrategy: " + dbData);
        }

        String className = parts[0];
        String stepName = parts[1];

        return switch (className) {
            case "IPTVStepType" -> IPTVStepType.valueOf(stepName);
            case "InternetStepType" -> InternetStepType.valueOf(stepName);
            default -> throw new IllegalArgumentException("Unknown StepType class: " + className);
        };
    }
}