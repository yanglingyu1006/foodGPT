package com.foodgpt.enums;

public enum HealthGoalType {
    WEIGHT_LOSS("减脂"),
    MUSCLE_GAIN("增肌"),
    MAINTAIN("维持");

    private final String label;

    HealthGoalType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static HealthGoalType fromLabel(String label) {
        for (HealthGoalType type : values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        return MAINTAIN;
    }
}
