package com.foodgpt.controller;

import com.foodgpt.entity.BodyData;
import com.foodgpt.enums.ActivityLevel;
import com.foodgpt.service.BodyDataService;
import com.foodgpt.service.NutritionService;
import com.foodgpt.util.BmiBmrCalculator;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.chart.NumberAxis;

import java.time.LocalDateTime;

public class DashboardController {

    @FXML
    private Spinner<Double> heightSpinner;
    @FXML
    private Spinner<Double> weightSpinner;
    @FXML
    private Spinner<Integer> ageSpinner;
    @FXML
    private ComboBox<String> activityComboBox;
    @FXML
    private Label bmiLabel;
    @FXML
    private Label bmiStatusLabel;
    @FXML
    private Label bmrLabel;
    @FXML
    private Label caloriesLabel;
    @FXML
    private Button saveBtn;
    @FXML
    private ProgressIndicator bmiIndicator;
    @FXML
    private LineChart<String, Number> weightChart;
    @FXML
    private NumberAxis weightYAxis;
    @FXML
    private Label currentWeightLabel;
    @FXML
    private Label weightChangeLabel;
    @FXML
    private ProgressBar proteinProgress;
    @FXML
    private ProgressBar carbProgress;
    @FXML
    private ProgressBar fatProgress;
    @FXML
    private ProgressIndicator balanceScoreIndicator;
    @FXML
    private Label balanceScoreLabel;
    @FXML
    private Label balanceGradeLabel;

    private BodyDataService bodyDataService;
    private NutritionService nutritionService;
    private BodyData currentBodyData;

    public void setServices(BodyDataService bodyDataService, NutritionService nutritionService) {
        this.bodyDataService = bodyDataService;
        this.nutritionService = nutritionService;
    }

    @FXML
    private void initialize() {
        if (heightSpinner != null) {
            heightSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(100.0, 250.0, 165.0, 0.1));
        }
        if (weightSpinner != null) {
            weightSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(30.0, 200.0, 55.0, 0.1));
        }
        if (ageSpinner != null) {
            ageSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 100, 25));
        }
        if (activityComboBox != null) {
            activityComboBox.getItems().addAll("久坐型", "轻度活动", "中度活动", "高度活动");
            activityComboBox.setValue("中度活动");
        }
        loadData();
    }

    private void loadData() {
        if (bodyDataService != null) {
            currentBodyData = bodyDataService.getLatestBodyData();
            if (currentBodyData != null && heightSpinner != null && weightSpinner != null 
                && ageSpinner != null && activityComboBox != null) {
                heightSpinner.getValueFactory().setValue(currentBodyData.getHeight());
                weightSpinner.getValueFactory().setValue(currentBodyData.getWeight());
                ageSpinner.getValueFactory().setValue(currentBodyData.getAge());
                activityComboBox.setValue(ActivityLevel.valueOf(currentBodyData.getActivityLevel()).getLabel());
                updateCalculations();
            } else {
                showEmptyState();
            }
        }
    }

    private void showEmptyState() {
        if (bmiLabel != null) {
            bmiLabel.setText("BMI: 未设置");
        }
        if (bmrLabel != null) {
            bmrLabel.setText("基础代谢: 未设置");
        }
        if (caloriesLabel != null) {
            caloriesLabel.setText("推荐热量: 未设置");
        }
    }

    @FXML
    private void handleSave() {
        if (heightSpinner == null || weightSpinner == null || ageSpinner == null || activityComboBox == null) {
            return;
        }

        BodyData bodyData = new BodyData();
        bodyData.setHeight(heightSpinner.getValue());
        bodyData.setWeight(weightSpinner.getValue());
        bodyData.setAge(ageSpinner.getValue());
        bodyData.setActivityLevel(ActivityLevel.fromLabel(activityComboBox.getValue()).name());
        bodyData.setUpdateTime(LocalDateTime.now());

        updateCalculations(bodyData);

        if (currentBodyData != null && currentBodyData.getId() != null) {
            bodyData.setId(currentBodyData.getId());
            bodyData.setCreateTime(currentBodyData.getCreateTime());
            bodyDataService.updateBodyData(bodyData);
        } else {
            bodyData.setCreateTime(LocalDateTime.now());
            bodyDataService.saveBodyData(bodyData);
            currentBodyData = bodyData;
        }

        showAlert("保存成功");
    }

    private void updateCalculations() {
        if (heightSpinner == null || weightSpinner == null || ageSpinner == null || activityComboBox == null) {
            return;
        }
        if (activityComboBox.getValue() == null) {
            return;
        }

        double height = heightSpinner.getValue();
        double weight = weightSpinner.getValue();
        int age = ageSpinner.getValue();
        ActivityLevel level = ActivityLevel.fromLabel(activityComboBox.getValue());

        double bmi = BmiBmrCalculator.calculateBmi(height, weight);
        double bmr = BmiBmrCalculator.calculateBmrFemale(height, weight, age);
        int minCal = BmiBmrCalculator.calculateRecommendedCaloriesMin(bmr, level);
        int maxCal = BmiBmrCalculator.calculateRecommendedCaloriesMax(bmr, level);

        if (bmiLabel != null) {
            bmiLabel.setText(String.format("%.1f", bmi));
        }
        if (bmiStatusLabel != null) {
            bmiStatusLabel.setText(BmiBmrCalculator.getBmiCategory(bmi));
        }
        if (bmiIndicator != null) {
            bmiIndicator.setProgress(Math.min(1.0, bmi / 35.0));
        }
        if (bmrLabel != null) {
            bmrLabel.setText(String.format("BMR: %.0f kcal", bmr));
        }
        if (caloriesLabel != null) {
            caloriesLabel.setText(String.format("推荐: %d-%d kcal", minCal, maxCal));
        }
    }

    private void updateCalculations(BodyData bodyData) {
        ActivityLevel level = ActivityLevel.fromLabel(activityComboBox.getValue());
        double bmi = BmiBmrCalculator.calculateBmi(bodyData.getHeight(), bodyData.getWeight());
        double bmr = BmiBmrCalculator.calculateBmrFemale(bodyData.getHeight(), bodyData.getWeight(), bodyData.getAge());

        bodyData.setBmi(bmi);
        bodyData.setBmr(bmr);
        bodyData.setRecommendedCaloriesMin(BmiBmrCalculator.calculateRecommendedCaloriesMin(bmr, level));
        bodyData.setRecommendedCaloriesMax(BmiBmrCalculator.calculateRecommendedCaloriesMax(bmr, level));
    }

    @FXML
    private void handleViewAnalysis() {
        showAlert("正在跳转到营养分析...");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
