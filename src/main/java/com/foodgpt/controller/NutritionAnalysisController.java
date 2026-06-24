package com.foodgpt.controller;

import com.foodgpt.entity.MealRecord;
import com.foodgpt.entity.Recipe;
import com.foodgpt.enums.MealType;
import com.foodgpt.service.MealRecordService;
import com.foodgpt.service.RecipeService;
import com.foodgpt.util.NutritionCalculator;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class NutritionAnalysisController {

    @FXML
    private DatePicker datePicker;
    @FXML
    private BarChart<String, Number> nutritionChart;
    @FXML
    private Label proteinLabel;
    @FXML
    private Label carbLabel;
    @FXML
    private Label fatLabel;
    @FXML
    private Label caloriesLabel;
    @FXML
    private Label balanceScoreLabel;

    private MealRecordService mealRecordService;
    private RecipeService recipeService;

    public void setServices(MealRecordService mealRecordService, RecipeService recipeService) {
        this.mealRecordService = mealRecordService;
        this.recipeService = recipeService;
        loadData();
    }

    private void loadData() {
        LocalDate date = datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now();
        List<MealRecord> records = mealRecordService.getMealRecords(date, null);

        double totalProtein = 0, totalCarb = 0, totalFat = 0;
        int totalCalories = 0;

        XYChart.Series<String, Number> proteinSeries = new XYChart.Series<>();
        proteinSeries.setName("蛋白质");
        XYChart.Series<String, Number> carbSeries = new XYChart.Series<>();
        carbSeries.setName("碳水");
        XYChart.Series<String, Number> fatSeries = new XYChart.Series<>();
        fatSeries.setName("脂肪");

        for (MealType mealType : MealType.values()) {
            double mealProtein = 0, mealCarb = 0, mealFat = 0;
            for (MealRecord record : records) {
                if (mealType.name().equals(record.getMealType())) {
                    Recipe recipe = recipeService.getRecipeById(record.getRecipeId());
                    if (recipe != null) {
                        double portion = record.getPortion();
                        mealProtein += (recipe.getProtein() != null ? recipe.getProtein() : 0) * portion;
                        mealCarb += (recipe.getCarbohydrate() != null ? recipe.getCarbohydrate() : 0) * portion;
                        mealFat += (recipe.getFat() != null ? recipe.getFat() : 0) * portion;
                    }
                }
            }
            proteinSeries.getData().add(new XYChart.Data<>(mealType.getLabel(), mealProtein));
            carbSeries.getData().add(new XYChart.Data<>(mealType.getLabel(), mealCarb));
            fatSeries.getData().add(new XYChart.Data<>(mealType.getLabel(), mealFat));

            totalProtein += mealProtein;
            totalCarb += mealCarb;
            totalFat += mealFat;
        }

        totalCalories = (int) NutritionCalculator.calculateCalories(totalProtein, totalCarb, totalFat);

        nutritionChart.getData().clear();
        nutritionChart.getData().addAll(proteinSeries, carbSeries, fatSeries);

        double proteinRatio = totalCalories > 0 ? (totalProtein * 4) / totalCalories : 0;
        double carbRatio = totalCalories > 0 ? (totalCarb * 4) / totalCalories : 0;
        double fatRatio = totalCalories > 0 ? (totalFat * 9) / totalCalories : 0;
        double balanceScore = NutritionCalculator.calculateBalanceScore(proteinRatio, carbRatio, fatRatio);

        proteinLabel.setText(String.format("蛋白质: %.1f g (%.0f%%)", totalProtein, proteinRatio * 100));
        carbLabel.setText(String.format("碳水: %.1f g (%.0f%%)", totalCarb, carbRatio * 100));
        fatLabel.setText(String.format("脂肪: %.1f g (%.0f%%)", totalFat, fatRatio * 100));
        caloriesLabel.setText(String.format("热量: %d kcal", totalCalories));
        balanceScoreLabel.setText(String.format("均衡度: %.1f", balanceScore));
    }

    @FXML
    private void initialize() {
        datePicker.setValue(LocalDate.now());
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }
}
