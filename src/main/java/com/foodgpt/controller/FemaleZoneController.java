package com.foodgpt.controller;

import com.foodgpt.entity.*;
import com.foodgpt.enums.CyclePhase;
import com.foodgpt.enums.HealthGoalType;
import com.foodgpt.service.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FemaleZoneController {

    @FXML
    private Label cyclePhaseLabel;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private Spinner<Integer> cycleLengthSpinner;
    @FXML
    private Button saveCycleBtn;
    @FXML
    private ToggleGroup goalToggleGroup;
    @FXML
    private RadioButton weightLossRadio;
    @FXML
    private RadioButton muscleGainRadio;
    @FXML
    private RadioButton maintainRadio;
    @FXML
    private Label currentGoalLabel;
    @FXML
    private Button saveGoalBtn;

    private CycleService cycleService;
    private BodyDataService bodyDataService;
    private RecipeService recipeService;
    private MealRecordService mealRecordService;
    private NutritionService nutritionService;
    private HealthGoalService healthGoalService;
    private UserPreferenceService userPreferenceService;
    private MainLayoutController mainLayoutController;
    private HealthGoal currentGoal;

    public void setService(CycleService cycleService) {
        this.cycleService = cycleService;
    }

    public void setServices(BodyDataService bodyDataService, RecipeService recipeService,
                            MealRecordService mealRecordService, NutritionService nutritionService) {
        this.bodyDataService = bodyDataService;
        this.recipeService = recipeService;
        this.mealRecordService = mealRecordService;
        this.nutritionService = nutritionService;
    }

    public void setHealthGoalService(HealthGoalService healthGoalService) {
        this.healthGoalService = healthGoalService;
    }

    public void setUserPreferenceService(UserPreferenceService userPreferenceService) {
        this.userPreferenceService = userPreferenceService;
    }

    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
    }

    @FXML
    private void initialize() {
        if (cycleLengthSpinner != null) {
            cycleLengthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(20, 45, 28));
        }

        goalToggleGroup = new ToggleGroup();
        if (weightLossRadio != null) {
            weightLossRadio.setToggleGroup(goalToggleGroup);
        }
        if (muscleGainRadio != null) {
            muscleGainRadio.setToggleGroup(goalToggleGroup);
        }
        if (maintainRadio != null) {
            maintainRadio.setToggleGroup(goalToggleGroup);
            maintainRadio.setSelected(true);
        }

        loadData();
    }

    private void loadData() {
        if (cycleService != null) {
            String phase = cycleService.getCurrentPhase();
            if (cyclePhaseLabel != null) {
                cyclePhaseLabel.setText("当前阶段: " + CyclePhase.valueOf(phase).getLabel());
            }

            CycleRecord current = cycleService.getCurrentCycle();
            if (current != null) {
                if (startDatePicker != null) {
                    startDatePicker.setValue(current.getStartDate());
                }
                if (cycleLengthSpinner != null) {
                    cycleLengthSpinner.getValueFactory().setValue(current.getCycleLength());
                }
            }
        }

        loadCurrentGoal();
    }

    private void loadCurrentGoal() {
        if (healthGoalService != null) {
            currentGoal = healthGoalService.getCurrentGoal();
        }
        if (currentGoalLabel != null) {
            if (currentGoal != null) {
                currentGoalLabel.setText("当前目标: " + HealthGoalType.valueOf(currentGoal.getGoalType()).getLabel());
            } else {
                currentGoalLabel.setText("当前目标: 未设置");
            }
        }
    }

    @FXML
    private void handleSaveCycle() {
        if (startDatePicker == null || cycleLengthSpinner == null) {
            return;
        }

        CycleRecord record = new CycleRecord();
        record.setUserId(1);
        record.setStartDate(startDatePicker.getValue());
        record.setCycleLength(cycleLengthSpinner.getValue());
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        cycleService.saveCycleRecord(record);
        loadData();
        showAlert("保存成功");
    }

    @FXML
    private void handleSaveGoal() {
        if (goalToggleGroup == null) {
            return;
        }

        RadioButton selected = (RadioButton) goalToggleGroup.getSelectedToggle();
        if (selected == null) {
            showAlert("请选择健康目标");
            return;
        }

        String goalType;
        if (selected == weightLossRadio) {
            goalType = HealthGoalType.WEIGHT_LOSS.name();
        } else if (selected == muscleGainRadio) {
            goalType = HealthGoalType.MUSCLE_GAIN.name();
        } else {
            goalType = HealthGoalType.MAINTAIN.name();
        }

        currentGoal = new HealthGoal();
        currentGoal.setUserId(1);
        currentGoal.setGoalType(goalType);
        currentGoal.setCreateTime(LocalDateTime.now());
        currentGoal.setUpdateTime(LocalDateTime.now());

        // 持久化到数据库
        if (healthGoalService != null) {
            healthGoalService.saveGoal(currentGoal);
        }

        loadCurrentGoal();
        showAlert("健康目标设置成功");
    }

    // ==================== 一、专属食谱 ====================

    @FXML
    private void handleViewRecipes() {
        String phase = cycleService != null ? cycleService.getCurrentPhase() : "FOLLICULAR";
        CyclePhase cyclePhase = CyclePhase.valueOf(phase);

        // 获取用户身体数据
        BodyData bodyData = bodyDataService != null ? bodyDataService.getLatestBodyData() : null;
        int targetCalMin = 0;
        int targetCalMax = 3000;
        if (bodyData != null) {
            targetCalMin = bodyData.getRecommendedCaloriesMin() != null ? bodyData.getRecommendedCaloriesMin() : 0;
            targetCalMax = bodyData.getRecommendedCaloriesMax() != null ? bodyData.getRecommendedCaloriesMax() : 3000;
        }

        // 获取所有菜谱
        List<Recipe> allRecipes = recipeService != null ? recipeService.getAllRecipes() : Collections.emptyList();

        // 根据周期阶段筛选推荐营养素
        List<Recipe> recommended = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            double score = 0;
            // 热量在目标范围内
            if (recipe.getCalories() != null && recipe.getCalories() >= targetCalMin * 0.7
                    && recipe.getCalories() <= targetCalMax * 1.1) {
                score += 30;
            }
            // 根据周期阶段加权
            switch (cyclePhase) {
                case MENSTRUATION:
                    // 经期：补铁、补血，高蛋白
                    if (recipe.getProtein() != null && recipe.getProtein() >= 20) score += 25;
                    if (recipe.getFat() != null && recipe.getFat() <= 15) score += 15;
                    break;
                case FOLLICULAR:
                    // 卵泡期：高蛋白、适量碳水
                    if (recipe.getProtein() != null && recipe.getProtein() >= 15) score += 20;
                    if (recipe.getCarbohydrate() != null && recipe.getCarbohydrate() >= 30) score += 15;
                    break;
                case OVULATION:
                    // 排卵期：均衡营养
                    if (recipe.getProtein() != null && recipe.getProtein() >= 12) score += 10;
                    if (recipe.getCarbohydrate() != null && recipe.getCarbohydrate() >= 25) score += 10;
                    if (recipe.getFat() != null && recipe.getFat() >= 10 && recipe.getFat() <= 20) score += 10;
                    break;
                case LUTEAL:
                    // 黄体期：低盐、高纤维、稳血糖
                    if (recipe.getCarbohydrate() != null && recipe.getCarbohydrate() >= 20 && recipe.getCarbohydrate() <= 50) score += 20;
                    if (recipe.getFat() != null && recipe.getFat() <= 12) score += 15;
                    break;
            }
            // 根据健康目标加权
            if (currentGoal != null) {
                HealthGoalType goalType = HealthGoalType.valueOf(currentGoal.getGoalType());
                switch (goalType) {
                    case WEIGHT_LOSS:
                        if (recipe.getCalories() != null && recipe.getCalories() <= 400) score += 20;
                        if (recipe.getProtein() != null && recipe.getProtein() >= 20) score += 15;
                        break;
                    case MUSCLE_GAIN:
                        if (recipe.getProtein() != null && recipe.getProtein() >= 25) score += 25;
                        if (recipe.getCarbohydrate() != null && recipe.getCarbohydrate() >= 40) score += 15;
                        break;
                    case MAINTAIN:
                        if (recipe.getCalories() != null && recipe.getCalories() >= 300 && recipe.getCalories() <= 600) score += 20;
                        break;
                }
            }
            if (score >= 25) {
                recommended.add(recipe);
            }
        }

        // 按得分排序
        recommended.sort((a, b) -> {
            double sa = getRecipeScore(a, cyclePhase);
            double sb = getRecipeScore(b, cyclePhase);
            return Double.compare(sb, sa);
        });

        String title = "专属食谱推荐 - " + cyclePhase.getLabel();
        showRecipeDialog(title, recommended.stream().limit(10).collect(Collectors.toList()), cyclePhase);
    }

    private double getRecipeScore(Recipe recipe, CyclePhase phase) {
        double score = 0;
        switch (phase) {
            case MENSTRUATION:
                if (recipe.getProtein() != null && recipe.getProtein() >= 20) score += 25;
                if (recipe.getFat() != null && recipe.getFat() <= 15) score += 15;
                break;
            case FOLLICULAR:
                if (recipe.getProtein() != null && recipe.getProtein() >= 15) score += 20;
                if (recipe.getCarbohydrate() != null && recipe.getCarbohydrate() >= 30) score += 15;
                break;
            case OVULATION:
                if (recipe.getProtein() != null && recipe.getProtein() >= 12) score += 10;
                if (recipe.getCarbohydrate() != null && recipe.getCarbohydrate() >= 25) score += 10;
                break;
            case LUTEAL:
                if (recipe.getCarbohydrate() != null && recipe.getCarbohydrate() >= 20 && recipe.getCarbohydrate() <= 50) score += 20;
                if (recipe.getFat() != null && recipe.getFat() <= 12) score += 15;
                break;
        }
        if (recipe.getCalories() != null) score += 10;
        return score;
    }

    // ==================== 二、专属匹配 ====================

    @FXML
    private void handleViewMatching() {
        // 从数据库获取用户偏好和忌口
        List<String> favoriteIngredients = userPreferenceService != null
                ? userPreferenceService.getFavorites()
                : Arrays.asList("鸡肉", "鸡蛋", "西红柿", "西兰花", "牛肉");
        List<String> avoidedIngredients = userPreferenceService != null
                ? userPreferenceService.getAvoided()
                : Arrays.asList("辣椒", "大蒜");

        // 计算当日营养缺口
        Map<String, Double> gaps = calculateNutritionGaps();

        // 获取所有菜谱
        List<Recipe> allRecipes = recipeService != null ? recipeService.getAllRecipes() : Collections.emptyList();

        // 按匹配度筛选
        List<Recipe> matched = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            double matchScore = 0;

            // 包含偏爱食材
            if (recipe.getIngredients() != null) {
                for (String ingredient : recipe.getIngredients()) {
                    for (String fav : favoriteIngredients) {
                        if (ingredient.contains(fav)) {
                            matchScore += 15;
                            break;
                        }
                    }
                }
                // 不含忌口食材
                boolean hasAvoided = false;
                for (String ingredient : recipe.getIngredients()) {
                    for (String avoid : avoidedIngredients) {
                        if (ingredient.contains(avoid)) {
                            hasAvoided = true;
                            break;
                        }
                    }
                    if (hasAvoided) break;
                }
                if (hasAvoided) continue; // 跳过含忌口食材的菜谱
            }

            // 弥补营养缺口
            double proteinGap = gaps.getOrDefault("protein", 0.0);
            double carbGap = gaps.getOrDefault("carb", 0.0);
            double fatGap = gaps.getOrDefault("fat", 0.0);

            if (proteinGap > 0 && recipe.getProtein() != null) {
                matchScore += Math.min(recipe.getProtein() / Math.max(proteinGap, 1), 1) * 25;
            }
            if (carbGap > 0 && recipe.getCarbohydrate() != null) {
                matchScore += Math.min(recipe.getCarbohydrate() / Math.max(carbGap, 1), 1) * 20;
            }
            if (fatGap > 0 && recipe.getFat() != null) {
                matchScore += Math.min(recipe.getFat() / Math.max(fatGap, 1), 1) * 15;
            }

            if (matchScore >= 10) {
                matched.add(recipe);
            }
        }

        // 按匹配度排序
        matched.sort((a, b) -> {
            double sa = calculateMatchScore(a, favoriteIngredients, gaps);
            double sb = calculateMatchScore(b, favoriteIngredients, gaps);
            return Double.compare(sb, sa);
        });

        showMatchingDialog("个性化匹配推荐", matched.stream().limit(10).collect(Collectors.toList()), gaps, favoriteIngredients, avoidedIngredients);
    }

    private double calculateMatchScore(Recipe recipe, List<String> favorites, Map<String, Double> gaps) {
        double score = 0;
        if (recipe.getIngredients() != null) {
            for (String ingredient : recipe.getIngredients()) {
                for (String fav : favorites) {
                    if (ingredient.contains(fav)) {
                        score += 15;
                        break;
                    }
                }
            }
        }
        double proteinGap = gaps.getOrDefault("protein", 0.0);
        double carbGap = gaps.getOrDefault("carb", 0.0);
        double fatGap = gaps.getOrDefault("fat", 0.0);
        if (proteinGap > 0 && recipe.getProtein() != null) {
            score += Math.min(recipe.getProtein() / Math.max(proteinGap, 1), 1) * 25;
        }
        if (carbGap > 0 && recipe.getCarbohydrate() != null) {
            score += Math.min(recipe.getCarbohydrate() / Math.max(carbGap, 1), 1) * 20;
        }
        if (fatGap > 0 && recipe.getFat() != null) {
            score += Math.min(recipe.getFat() / Math.max(fatGap, 1), 1) * 15;
        }
        return score;
    }

    // ==================== 三、营养缺口分析 ====================

    @FXML
    private void handleViewGapAnalysis() {
        Map<String, Double> gaps = calculateNutritionGaps();

        StringBuilder message = new StringBuilder();
        message.append("========== 营养缺口分析 ==========\n\n");

        double proteinGap = gaps.getOrDefault("protein", 0.0);
        double carbGap = gaps.getOrDefault("carb", 0.0);
        double fatGap = gaps.getOrDefault("fat", 0.0);
        double calGap = gaps.getOrDefault("calories", 0.0);

        message.append(String.format("热量缺口: %.0f kcal\n", calGap));
        message.append(String.format("蛋白质缺口: %.1f g\n", proteinGap));
        message.append(String.format("碳水缺口: %.1f g\n", carbGap));
        message.append(String.format("脂肪缺口: %.1f g\n\n", fatGap));

        message.append("========== 补充建议 ==========\n\n");

        if (proteinGap > 5) {
            message.append("蛋白质不足，建议补充：\n");
            message.append("  - 鸡胸肉 (100g ≈ 31g 蛋白质)\n");
            message.append("  - 鸡蛋 (1个 ≈ 6g 蛋白质)\n");
            message.append("  - 牛奶 (250ml ≈ 8g 蛋白质)\n");
            message.append("  - 豆腐 (100g ≈ 8g 蛋白质)\n");
            message.append("  - 牛肉 (100g ≈ 26g 蛋白质)\n\n");
        }

        if (carbGap > 10) {
            message.append("碳水不足，建议补充：\n");
            message.append("  - 米饭 (100g ≈ 28g 碳水)\n");
            message.append("  - 全麦面包 (100g ≈ 40g 碳水)\n");
            message.append("  - 燕麦 (100g ≈ 66g 碳水)\n");
            message.append("  - 红薯 (100g ≈ 20g 碳水)\n\n");
        }

        if (fatGap > 5) {
            message.append("脂肪不足，建议补充：\n");
            message.append("  - 牛油果 (100g ≈ 15g 脂肪)\n");
            message.append("  - 坚果 (30g ≈ 15g 脂肪)\n");
            message.append("  - 橄榄油 (1勺 ≈ 14g 脂肪)\n");
            message.append("  - 三文鱼 (100g ≈ 13g 脂肪)\n\n");
        }

        if (proteinGap < -5 || carbGap < -10 || fatGap < -5) {
            message.append("部分营养素摄入超标，建议：\n");
            if (proteinGap < -5) message.append("  - 适当减少肉类摄入\n");
            if (carbGap < -10) message.append("  - 减少主食分量\n");
            if (fatGap < -5) message.append("  - 减少油炸食品摄入\n");
            message.append("\n");
        }

        if (proteinGap <= 5 && carbGap <= 10 && fatGap <= 5
                && proteinGap >= -5 && carbGap >= -10 && fatGap >= -5) {
            message.append("各营养素摄入均衡，请继续保持！\n");
        }

        showDialog("营养缺口分析", message.toString());
    }

    private Map<String, Double> calculateNutritionGaps() {
        Map<String, Double> gaps = new HashMap<>();
        gaps.put("protein", 0.0);
        gaps.put("carb", 0.0);
        gaps.put("fat", 0.0);
        gaps.put("calories", 0.0);

        // 获取用户身体数据计算目标
        BodyData bodyData = bodyDataService != null ? bodyDataService.getLatestBodyData() : null;
        if (bodyData == null) {
            return gaps;
        }

        double targetCal = (bodyData.getRecommendedCaloriesMin() != null && bodyData.getRecommendedCaloriesMax() != null)
                ? (bodyData.getRecommendedCaloriesMin() + bodyData.getRecommendedCaloriesMax()) / 2.0
                : 2000;
        double targetProtein = bodyData.getWeight() != null ? bodyData.getWeight() * 1.2 : 60;
        double targetCarb = targetCal * 0.55 / 4;
        double targetFat = targetCal * 0.25 / 9;

        // 查询当日实际摄入
        List<MealRecord> todayRecords = mealRecordService != null ? mealRecordService.getTodayRecords() : Collections.emptyList();
        double actualProtein = 0, actualCarb = 0, actualFat = 0, actualCal = 0;

        for (MealRecord record : todayRecords) {
            if (record.getRecipeId() != null) {
                Recipe recipe = recipeService.getRecipeById(record.getRecipeId());
                if (recipe != null) {
                    double portion = record.getPortion() != null ? record.getPortion() : 1.0;
                    actualProtein += (recipe.getProtein() != null ? recipe.getProtein() : 0) * portion;
                    actualCarb += (recipe.getCarbohydrate() != null ? recipe.getCarbohydrate() : 0) * portion;
                    actualFat += (recipe.getFat() != null ? recipe.getFat() : 0) * portion;
                    actualCal += (recipe.getCalories() != null ? recipe.getCalories() : 0) * portion;
                }
            }
        }

        gaps.put("protein", targetProtein - actualProtein);
        gaps.put("carb", targetCarb - actualCarb);
        gaps.put("fat", targetFat - actualFat);
        gaps.put("calories", targetCal - actualCal);

        return gaps;
    }

    // ==================== 对话框显示 ====================

    private void showRecipeDialog(String title, List<Recipe> recipes, CyclePhase phase) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("根据" + phase.getLabel() + "阶段推荐以下菜谱：");

        StringBuilder content = new StringBuilder();
        if (recipes.isEmpty()) {
            content.append("暂无符合条件的推荐菜谱，请先添加菜谱数据。");
        } else {
            for (int i = 0; i < recipes.size(); i++) {
                Recipe r = recipes.get(i);
                content.append(String.format("%d. %s  [%s]\n", i + 1, r.getName(), r.getCategory()));
                content.append(String.format("   热量: %s kcal | 蛋白质: %s g | 碳水: %s g | 脂肪: %s g\n",
                        r.getCalories(), r.getProtein(), r.getCarbohydrate(), r.getFat()));
                if (r.getDescription() != null && !r.getDescription().isEmpty()) {
                    content.append("   ").append(r.getDescription()).append("\n");
                }
                content.append("\n");
            }
        }

        TextArea textArea = new TextArea(content.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(500, 400);

        alert.getDialogPane().setContent(textArea);
        alert.getDialogPane().setPrefSize(550, 500);
        alert.showAndWait();
    }

    private void showMatchingDialog(String title, List<Recipe> recipes, Map<String, Double> gaps,
                                     List<String> favorites, List<String> avoideds) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("根据偏好和营养缺口匹配结果：");

        StringBuilder content = new StringBuilder();
        content.append("【偏好食材】").append(String.join("、", favorites)).append("\n");
        content.append("【忌口食材】").append(String.join("、", avoideds)).append("\n\n");
        content.append(String.format("【营养缺口】蛋白质: %.1fg | 碳水: %.1fg | 脂肪: %.1fg\n\n",
                gaps.getOrDefault("protein", 0.0),
                gaps.getOrDefault("carb", 0.0),
                gaps.getOrDefault("fat", 0.0)));

        if (recipes.isEmpty()) {
            content.append("暂无完全匹配的推荐菜谱。");
        } else {
            for (int i = 0; i < recipes.size(); i++) {
                Recipe r = recipes.get(i);
                content.append(String.format("%d. %s  [%s]\n", i + 1, r.getName(), r.getCategory()));
                content.append(String.format("   热量: %s kcal | 蛋白质: %s g | 碳水: %s g | 脂肪: %s g\n",
                        r.getCalories(), r.getProtein(), r.getCarbohydrate(), r.getFat()));
                content.append("\n");
            }
        }

        TextArea textArea = new TextArea(content.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(500, 400);

        alert.getDialogPane().setContent(textArea);
        alert.getDialogPane().setPrefSize(550, 500);
        alert.showAndWait();
    }

    private void showDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);

        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(450, 350);

        alert.getDialogPane().setContent(textArea);
        alert.getDialogPane().setPrefSize(500, 420);
        alert.showAndWait();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}