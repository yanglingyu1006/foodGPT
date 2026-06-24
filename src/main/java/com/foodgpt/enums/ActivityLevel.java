package com.foodgpt.enums;

public enum ActivityLevel {
    SEDENTARY("久坐型"),
    MILD("轻度活动"),
    MODERATE("中度活动"),
    ACTIVE("高度活动");

    private final String label;

    ActivityLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static ActivityLevel fromLabel(String label) {
        for (ActivityLevel level : values()) {
            if (level.label.equals(label)) {
                return level;
            }
        }
        return SEDENTARY;
    }
}
