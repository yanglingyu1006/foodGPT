package com.foodgpt.controller;

import com.foodgpt.entity.BodyData;
import com.foodgpt.enums.ActivityLevel;
import com.foodgpt.service.BodyDataService;
import com.foodgpt.util.BmiBmrCalculator;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;

public class BodyDataController {

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
    private Label bmrLabel;
    @FXML
    private Label caloriesLabel;
    @FXML
    private Button saveBtn;

    private BodyDataService bodyDataService;

    public void setService(BodyDataService bodyDataService) {
        this.bodyDataService = bodyDataService;
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
            BodyData data = bodyDataService.getLatestBodyData();
            if (data != null) {
                if (heightSpinner != null) {
                    heightSpinner.getValueFactory().setValue(data.getHeight());
                }
                if (weightSpinner != null) {
                    weightSpinner.getValueFactory().setValue(data.getWeight());
                }
                if (ageSpinner != null) {
                    ageSpinner.getValueFactory().setValue(data.getAge());
                }
                if (activityComboBox != null) {
                    activityComboBox.setValue(ActivityLevel.valueOf(data.getActivityLevel()).getLabel());
                }
                updateCalculations();
            }
        }
    }

    @FXML
    private void handleSave() {
        BodyData bodyData = new BodyData();
        bodyData.setUserId(1);
        bodyData.setHeight(heightSpinner.getValue());
        bodyData.setWeight(weightSpinner.getValue());
        bodyData.setAge(ageSpinner.getValue());
        bodyData.setActivityLevel(ActivityLevel.fromLabel(activityComboBox.getValue()).name());
        bodyData.setCreateTime(LocalDateTime.now());
        bodyData.setUpdateTime(LocalDateTime.now());

        updateCalculations(bodyData);
        bodyDataService.saveBodyData(bodyData);
        showAlert("保存成功");
    }

    private void updateCalculations() {
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

        bmiLabel.setText(String.format("BMI: %.2f (%s)", bmi, BmiBmrCalculator.getBmiCategory(bmi)));
        bmrLabel.setText(String.format("基础代谢: %.0f kcal", bmr));
        caloriesLabel.setText(String.format("推荐热量: %d-%d kcal", minCal, maxCal));
    }

    private void updateCalculations(BodyData bodyData) {
        if (activityComboBox.getValue() == null) {
            return;
        }
        ActivityLevel level = ActivityLevel.fromLabel(activityComboBox.getValue());
        double bmi = BmiBmrCalculator.calculateBmi(bodyData.getHeight(), bodyData.getWeight());
        double bmr = BmiBmrCalculator.calculateBmrFemale(bodyData.getHeight(), bodyData.getWeight(), bodyData.getAge());

        bodyData.setBmi(bmi);
        bodyData.setBmr(bmr);
        bodyData.setRecommendedCaloriesMin(BmiBmrCalculator.calculateRecommendedCaloriesMin(bmr, level));
        bodyData.setRecommendedCaloriesMax(BmiBmrCalculator.calculateRecommendedCaloriesMax(bmr, level));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
