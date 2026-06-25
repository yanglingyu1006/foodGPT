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
    private Spinner<Integer> heightSpinner;
    @FXML
    private Spinner<Integer> weightSpinner;
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
        heightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 250, 165));
        weightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(30, 200, 55));
        ageSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 100, 25));
        activityComboBox.getItems().addAll("久坐型", "轻度活动", "中度活动", "高度活动");
        activityComboBox.setValue("中度活动");
        loadData();
    }

    private void loadData() {
        if (bodyDataService != null) {
            BodyData data = bodyDataService.getBodyData();
            if (data != null) {
                if (heightSpinner != null) {
                    heightSpinner.getValueFactory().setValue((int) data.getHeight().doubleValue());
                }
                if (weightSpinner != null) {
                    weightSpinner.getValueFactory().setValue((int) data.getWeight().doubleValue());
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
        bodyData.setHeight(heightSpinner.getValue().doubleValue());
        bodyData.setWeight(weightSpinner.getValue().doubleValue());
        bodyData.setAge(ageSpinner.getValue());
        bodyData.setActivityLevel(ActivityLevel.fromLabel(activityComboBox.getValue()).name());
        bodyData.setCreateTime(LocalDateTime.now());
        bodyData.setUpdateTime(LocalDateTime.now());

        updateCalculations(bodyData);
        bodyDataService.saveBodyData(bodyData);
        showAlert("保存成功");
    }

    private void updateCalculations() {
        double height = heightSpinner.getValue().doubleValue();
        double weight = weightSpinner.getValue().doubleValue();
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
