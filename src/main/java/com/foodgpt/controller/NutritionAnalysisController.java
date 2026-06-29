package com.foodgpt.controller;

import com.foodgpt.entity.BodyData;
import com.foodgpt.entity.MealRecord;
import com.foodgpt.entity.Recipe;
import com.foodgpt.enums.MealType;
import com.foodgpt.service.BodyDataService;
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
import javafx.scene.control.ProgressBar;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 营养分析控制器
 * 
 * 功能模块：
 * 1. 营养摄入统计 - 按餐次统计蛋白质、碳水、脂肪摄入
 * 2. 营养柱状图 - 分组柱状图展示各餐次营养素摄入
 * 3. 营养进度条 - 显示蛋白质、碳水、脂肪、热量进度
 * 4. 均衡度评分 - 计算三大营养素比例均衡度
 * 5. 日期切换 - 切换日期查看不同日期的营养数据
 * 
 * @author FoodGPT
 */
public class NutritionAnalysisController {

    // ==================== FXML 组件注入 ====================
    
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
    @FXML
    private ProgressBar proteinProgress;
    @FXML
    private ProgressBar carbProgress;
    @FXML
    private ProgressBar fatProgress;
    @FXML
    private ProgressBar caloriesProgress;

    private MealRecordService mealRecordService;
    private RecipeService recipeService;
    private BodyDataService bodyDataService;

    /** 注入用餐记录和菜谱服务 */
    public void setServices(MealRecordService mealRecordService, RecipeService recipeService) {
        this.mealRecordService = mealRecordService;
        this.recipeService = recipeService;
    }
    
    /** 注入身体数据服务，用于获取推荐热量 */
    public void setBodyDataService(BodyDataService bodyDataService) {
        this.bodyDataService = bodyDataService;
    }

    /**
     * 刷新页面数据（从其他页面切换回来时调用）
     */
    public void refresh() {
        loadData();
    }

    /** FXML 初始化：设置日期默认值，加载营养数据 */
    @FXML
    private void initialize() {
        if (datePicker != null) {
            datePicker.setValue(LocalDate.now());
            datePicker.setOnAction(e -> loadData());
        }
        loadData();
    }

    /** 加载营养数据：统计各餐次营养素，更新图表和进度条 */
    private void loadData() {
        if (mealRecordService != null && recipeService != null) {
            LocalDate date = datePicker != null ? (datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now()) : LocalDate.now();
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

            if (nutritionChart != null) {
                nutritionChart.getData().clear();
                nutritionChart.getData().addAll(proteinSeries, carbSeries, fatSeries);
            }

            double proteinRatio = totalCalories > 0 ? (totalProtein * 4) / totalCalories : 0;
            double carbRatio = totalCalories > 0 ? (totalCarb * 4) / totalCalories : 0;
            double fatRatio = totalCalories > 0 ? (totalFat * 9) / totalCalories : 0;
            double balanceScore = NutritionCalculator.calculateBalanceScore(proteinRatio, carbRatio, fatRatio);

            if (proteinLabel != null) {
                proteinLabel.setText(String.format("蛋白质: %.1f g (%.0f%%)", totalProtein, proteinRatio * 100));
            }
            if (carbLabel != null) {
                carbLabel.setText(String.format("碳水: %.1f g (%.0f%%)", totalCarb, carbRatio * 100));
            }
            if (fatLabel != null) {
                fatLabel.setText(String.format("脂肪: %.1f g (%.0f%%)", totalFat, fatRatio * 100));
            }
            if (caloriesLabel != null) {
                caloriesLabel.setText(String.format("热量: %d kcal", totalCalories));
            }
            if (balanceScoreLabel != null) {
                balanceScoreLabel.setText(String.format("%.1f 分", balanceScore));
            }

            updateProgressBars(totalProtein, totalCarb, totalFat, totalCalories);
        }
    }
    
    /** 更新营养进度条：根据推荐热量计算目标值，设置进度和颜色 */
    private void updateProgressBars(double totalProtein, double totalCarb, double totalFat, int totalCalories) {
        BodyData bodyData = bodyDataService != null ? bodyDataService.getLatestBodyData() : null;
        int targetCalories = 2000;
        double targetProtein = 50;
        double targetCarb = 250;
        double targetFat = 55;
        
        if (bodyData != null && bodyData.getRecommendedCaloriesMin() != null && bodyData.getRecommendedCaloriesMax() != null) {
            targetCalories = (bodyData.getRecommendedCaloriesMin() + bodyData.getRecommendedCaloriesMax()) / 2;
            targetProtein = targetCalories * 0.25 / 4;
            targetCarb = targetCalories * 0.50 / 4;
            targetFat = targetCalories * 0.25 / 9;
        }
        
        if (proteinProgress != null) {
            double progress = Math.min(1.0, totalProtein / targetProtein);
            proteinProgress.setProgress(progress);
            proteinProgress.setStyle(getProgressColor(progress));
        }
        if (carbProgress != null) {
            double progress = Math.min(1.0, totalCarb / targetCarb);
            carbProgress.setProgress(progress);
            carbProgress.setStyle(getProgressColor(progress));
        }
        if (fatProgress != null) {
            double progress = Math.min(1.0, totalFat / targetFat);
            fatProgress.setProgress(progress);
            fatProgress.setStyle(getProgressColor(progress));
        }
        if (caloriesProgress != null) {
            double progress = Math.min(1.0, totalCalories / (double) targetCalories);
            caloriesProgress.setProgress(progress);
            caloriesProgress.setStyle(getProgressColor(progress));
        }
    }
    
    /** 根据进度值返回颜色样式：<50%黄色，50-100%绿色，>100%红色 */
    private String getProgressColor(double progress) {
        if (progress < 0.5) {
            return "-fx-accent: #E6A23C;";
        } else if (progress > 1.0) {
            return "-fx-accent: #F56C6C;";
        } else {
            return "-fx-accent: #67C23A;";
        }
    }

    /** 手动刷新按钮：重新加载营养数据 */
    @FXML
    private void handleRefresh() {
        loadData();
    }
}
