package com.foodgpt.util;

import com.foodgpt.enums.ActivityLevel;

public class BmiBmrCalculator {

    public static double calculateBmi(double heightCm, double weightKg) {
        double heightM = heightCm / 100.0;
        return Math.round(weightKg / (heightM * heightM) * 100.0) / 100.0;
    }

    public static double calculateBmrFemale(double heightCm, double weightKg, int age) {
        return Math.round((10 * weightKg) + (6.25 * heightCm) - (5 * age) - 161);
    }

    public static int calculateRecommendedCaloriesMin(double bmr, ActivityLevel activityLevel) {
        double factor = getActivityFactor(activityLevel);
        return (int) Math.round(bmr * factor * 0.95);
    }

    public static int calculateRecommendedCaloriesMax(double bmr, ActivityLevel activityLevel) {
        double factor = getActivityFactor(activityLevel);
        return (int) Math.round(bmr * factor * 1.05);
    }

    private static double getActivityFactor(ActivityLevel level) {
        return switch (level) {
            case SEDENTARY -> 1.2;
            case MILD -> 1.375;
            case MODERATE -> 1.55;
            case ACTIVE -> 1.725;
        };
    }

    public static String getBmiCategory(double bmi) {
        if (bmi < 18.5) return "偏瘦";
        if (bmi < 24) return "正常";
        if (bmi < 28) return "超重";
        return "肥胖";
    }

    public static double getBmiProgress(double bmi) {
        return Math.min(1.0, Math.max(0.0, (bmi - 18.5) / (28 - 18.5)));
    }
}
