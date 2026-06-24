package com.foodgpt.enums;

public enum RecipeCategory {
    BREAKFAST("早餐"),
    LUNCH("午餐"),
    DINNER("晚餐"),
    SNACK("加餐"),
    OTHER("其他");

    private final String label;

    RecipeCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static RecipeCategory fromLabel(String label) {
        for (RecipeCategory category : values()) {
            if (category.label.equals(label)) {
                return category;
            }
        }
        return OTHER;
    }
}
