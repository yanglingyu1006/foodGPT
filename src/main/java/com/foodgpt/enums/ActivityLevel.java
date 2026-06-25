package com.foodgpt.enums;

public enum ActivityLevel {
    SEDENTARY("久坐型", 1.2),
    MILD("轻度活动", 1.375),
    MODERATE("中度活动", 1.55),
    ACTIVE("高度活动", 1.725);

    private final String label;
    private final double factor;

    ActivityLevel(String label, double factor) {
        this.label = label;
        this.factor = factor;
    }

    public String getLabel() {
        return label;
    }

    public double getFactor() {
        return factor;
    }

    public static ActivityLevel fromLabel(String label) {
        for (ActivityLevel level : values()) {
            if (level.getLabel().equals(label)) {
                return level;
            }
        }
        return MODERATE;
    }
}
