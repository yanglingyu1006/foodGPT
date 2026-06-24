package com.foodgpt.util;

public class NutritionCalculator {

    public static double calculateCalories(double protein, double carbohydrate, double fat) {
        return (protein * 4) + (carbohydrate * 4) + (fat * 9);
    }

    public static double calculateProteinPercentage(double protein, double totalCalories) {
        if (totalCalories <= 0) return 0;
        return Math.round((protein * 4 / totalCalories) * 100);
    }

    public static double calculateCarbPercentage(double carbohydrate, double totalCalories) {
        if (totalCalories <= 0) return 0;
        return Math.round((carbohydrate * 4 / totalCalories) * 100);
    }

    public static double calculateFatPercentage(double fat, double totalCalories) {
        if (totalCalories <= 0) return 0;
        return Math.round((fat * 9 / totalCalories) * 100);
    }

    public static double calculateBalanceScore(double proteinRatio, double carbRatio, double fatRatio) {
        double idealProtein = 0.25;
        double idealCarb = 0.50;
        double idealFat = 0.25;

        double proteinDiff = Math.abs(proteinRatio - idealProtein);
        double carbDiff = Math.abs(carbRatio - idealCarb);
        double fatDiff = Math.abs(fatRatio - idealFat);

        double avgDiff = (proteinDiff + carbDiff + fatDiff) / 3;
        double score = Math.max(0, 100 - avgDiff * 200);
        return Math.round(score * 10) / 10;
    }
}
