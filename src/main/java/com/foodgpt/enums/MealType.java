package com.foodgpt.enums;

public enum MealType {
    BREAKFAST("早餐"),
    LUNCH("午餐"),
    DINNER("晚餐"),
    SNACK("加餐");

    private final String label;

    MealType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static MealType fromLabel(String label) {
        for (MealType type : values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        return BREAKFAST;
    }
}
