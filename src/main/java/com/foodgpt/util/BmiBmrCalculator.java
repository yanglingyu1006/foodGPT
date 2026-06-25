package com.foodgpt.util;

import com.foodgpt.enums.ActivityLevel;

public class BmiBmrCalculator {

    public static double calculateBmi(double heightCm, double weightKg) {
        double heightM = heightCm / 100.0;
        return Math.round(weightKg / (heightM * heightM) * 100.0) / 100.0;
    }

    public static double calculateBmrFemale(double height, double weight, int age) {
        return 10 * weight + 6.25 * height - 5 * age - 161;
    }

    public static int calculateRecommendedCaloriesMin(double bmr, ActivityLevel level) {
        return (int)(bmr * level.getFactor() - 300);
    }

    public static int calculateRecommendedCaloriesMax(double bmr, ActivityLevel level) {
        return (int)(bmr * level.getFactor() + 100);
    }

    public static String getBmiCategory(double bmi) {
        if (bmi < 18.5) return "偏瘦";
        else if (bmi < 24) return "正常";
        else if (bmi < 28) return "偏胖";
        else return "肥胖";
    }

    public static double getBmiProgress(double bmi) {
        return Math.min(1.0, Math.max(0.0, (bmi - 18.5) / (28 - 18.5)));
    }
}
