package com.foodgpt.controller;

import com.foodgpt.entity.BodyData;
import com.foodgpt.entity.WeightRecord;
import com.foodgpt.enums.ActivityLevel;
import com.foodgpt.service.BodyDataService;
import com.foodgpt.service.NutritionService;
import com.foodgpt.service.WeightTrackService;
import com.foodgpt.util.BmiBmrCalculator;
import com.foodgpt.util.NutritionCalculator;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

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
    private WeightTrackService weightTrackService;
    private MainLayoutController mainLayoutController;
    private BodyData currentBodyData;

    public void setServices(BodyDataService bodyDataService, NutritionService nutritionService,
                            WeightTrackService weightTrackService) {
        this.bodyDataService = bodyDataService;
        this.nutritionService = nutritionService;
        this.weightTrackService = weightTrackService;
    }

    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
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
        // 加载身体数据
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

        // 加载体重趋势图
        loadWeightChart();

        // 加载今日营养进度
        loadNutritionProgress();
    }

    // ==================== 体重趋势图 ====================

    private void loadWeightChart() {
        if (weightTrackService == null || weightChart == null) {
            return;
        }

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        List<WeightRecord> records = weightTrackService.getWeightRecords(startDate, endDate);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("体重变化");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        for (WeightRecord record : records) {
            series.getData().add(new XYChart.Data<>(record.getRecordDate().format(formatter), record.getWeight()));
        }

        weightChart.getData().clear();
        weightChart.getData().add(series);

        // 更新当前体重和变化
        if (!records.isEmpty()) {
            WeightRecord latest = records.get(records.size() - 1);
            if (currentWeightLabel != null) {
                currentWeightLabel.setText(String.format("%.1f", latest.getWeight()));
            }
            if (records.size() >= 2) {
                WeightRecord first = records.get(0);
                double change = latest.getWeight() - first.getWeight();
                if (weightChangeLabel != null) {
                    weightChangeLabel.setText(String.format("%+.1f", change));
                }
            }
        }
    }

    // ==================== 营养进度条 ====================

    private void loadNutritionProgress() {
        if (nutritionService == null) {
            return;
        }

        Map<String, Object> summary = nutritionService.getTodaySummary();
        double protein = (double) summary.getOrDefault("protein", 0.0);
        double carbohydrate = (double) summary.getOrDefault("carbohydrate", 0.0);
        double fat = (double) summary.getOrDefault("fat", 0.0);
        int calories = (int) summary.getOrDefault("calories", 0);

        // 计算目标值
        double targetProtein = 60;
        double targetCarb = 250;
        double targetFat = 55;
        int targetCalories = 2000;

        if (currentBodyData != null && currentBodyData.getRecommendedCaloriesMin() != null
                && currentBodyData.getRecommendedCaloriesMax() != null) {
            targetCalories = (currentBodyData.getRecommendedCaloriesMin() + currentBodyData.getRecommendedCaloriesMax()) / 2;
            targetProtein = targetCalories * 0.25 / 4;
            targetCarb = targetCalories * 0.50 / 4;
            targetFat = targetCalories * 0.25 / 9;
        }

        if (proteinProgress != null) {
            proteinProgress.setProgress(Math.min(1.0, protein / targetProtein));
        }
        if (carbProgress != null) {
            carbProgress.setProgress(Math.min(1.0, carbohydrate / targetCarb));
        }
        if (fatProgress != null) {
            fatProgress.setProgress(Math.min(1.0, fat / targetFat));
        }

        // 更新均衡度评分
        if (calories > 0) {
            double proteinRatio = (protein * 4) / calories;
            double carbRatio = (carbohydrate * 4) / calories;
            double fatRatio = (fat * 9) / calories;
            double balanceScore = NutritionCalculator.calculateBalanceScore(proteinRatio, carbRatio, fatRatio);

            if (balanceScoreIndicator != null) {
                balanceScoreIndicator.setProgress(balanceScore / 100.0);
            }
            if (balanceScoreLabel != null) {
                balanceScoreLabel.setText(String.format("%.0f", balanceScore));
            }

            String grade;
            if (balanceScore >= 85) {
                grade = "优秀";
            } else if (balanceScore >= 70) {
                grade = "良好";
            } else if (balanceScore >= 50) {
                grade = "一般";
            } else {
                grade = "需改善";
            }
            if (balanceGradeLabel != null) {
                balanceGradeLabel.setText(grade);
            }
        } else {
            // 无摄入数据时显示默认状态
            if (balanceScoreIndicator != null) {
                balanceScoreIndicator.setProgress(0);
            }
            if (balanceScoreLabel != null) {
                balanceScoreLabel.setText("--");
            }
            if (balanceGradeLabel != null) {
                balanceGradeLabel.setText("暂无数据");
            }
        }

        // 更新状态栏
        if (mainLayoutController != null) {
            mainLayoutController.updateStatusBar(calories, protein, carbohydrate, fat);
        }
    }

    // ==================== 身体数据 ====================

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

    // ==================== 导航 ====================

    @FXML
    private void handleViewAnalysis() {
        if (mainLayoutController != null) {
            mainLayoutController.showNutrition();
        } else {
            showAlert("无法跳转到营养分析页面");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}