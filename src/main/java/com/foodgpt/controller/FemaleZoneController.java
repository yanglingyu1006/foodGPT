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

/**
 * 女性专区控制器
 * 
 * 功能模块：
 * 1. 健康目标设定 - 减脂/增肌/维持三选一，保存到数据库
 * 2. 生理周期管理 - 记录经期开始日期和周期长度，计算当前阶段
 * 3. 周期阶段显示 - 经期/卵泡期/排卵期/黄体期，动态更新指示器
 * 4. 专属食谱 - 根据周期阶段推荐适合的菜谱
 * 5. 专属匹配 - 根据偏好食材和营养缺口匹配菜谱
 * 6. 营养缺口分析 - 对比实际摄入与目标，生成补充建议
 * 
 * @author FoodGPT
 */
public class FemaleZoneController {

    // ==================== FXML 组件注入 ====================
    
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
    @FXML
    private ProgressIndicator menstruationIndicator;
    @FXML
    private ProgressIndicator follicularIndicator;
    @FXML
    private ProgressIndicator ovulationIndicator;
    @FXML
    private ProgressIndicator lutealIndicator;

    private CycleService cycleService;
    private BodyDataService bodyDataService;
    private RecipeService recipeService;
    private MealRecordService mealRecordService;
    private NutritionService nutritionService;
    private HealthGoalService healthGoalService;
    private UserPreferenceService userPreferenceService;
    private MainLayoutController mainLayoutController;
    private HealthGoal currentGoal;

    /** 注入周期服务 */
    public void setService(CycleService cycleService) {
        this.cycleService = cycleService;
    }

    /** 注入身体数据、菜谱、用餐记录、营养服务 */
    public void setServices(BodyDataService bodyDataService, RecipeService recipeService,
                            MealRecordService mealRecordService, NutritionService nutritionService) {
        this.bodyDataService = bodyDataService;
        this.recipeService = recipeService;
        this.mealRecordService = mealRecordService;
        this.nutritionService = nutritionService;
    }

    /** 注入健康目标服务 */
    public void setHealthGoalService(HealthGoalService healthGoalService) {
        this.healthGoalService = healthGoalService;
    }

    /** 注入用户偏好服务 */
    public void setUserPreferenceService(UserPreferenceService userPreferenceService) {
        this.userPreferenceService = userPreferenceService;
    }

    /** 设置主布局控制器，用于跨页面刷新 */
    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
    }

    /**
     * 刷新页面数据（从其他页面切换回来时调用）
     */
    public void refresh() {
        loadData();
    }

    /** FXML 初始化：设置周期长度Spinner默认值，加载数据 */
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

    /** 加载数据：周期阶段、周期记录、健康目标 */
    private void loadData() {
        System.out.println("[FemaleZone] loadData() 开始...");
        if (cycleService != null) {
            String phase = cycleService.getCurrentPhase();
            System.out.println("[FemaleZone] 当前阶段: " + phase);
            CyclePhase cyclePhase = CyclePhase.valueOf(phase);
            if (cyclePhaseLabel != null) {
                cyclePhaseLabel.setText("当前阶段: " + cyclePhase.getLabel());
            }

            // 更新四个阶段指示器
            updatePhaseIndicators(cyclePhase);

            CycleRecord current = cycleService.getCurrentCycle();
            if (current != null) {
                System.out.println("[FemaleZone] 周期记录: id=" + current.getId() + ", startDate=" + current.getStartDate() + ", cycleLength=" + current.getCycleLength());
                if (startDatePicker != null) {
                    startDatePicker.setValue(current.getStartDate());
                }
                if (cycleLengthSpinner != null) {
                    cycleLengthSpinner.getValueFactory().setValue(current.getCycleLength());
                }
            } else {
                System.out.println("[FemaleZone] 没有周期记录");
            }
        } else {
            System.out.println("[FemaleZone] cycleService 为 null！");
        }

        loadCurrentGoal();
    }

    /** 更新周期阶段指示器：当前阶段高亮，其余置灰 */
    private void updatePhaseIndicators(CyclePhase phase) {
        if (menstruationIndicator == null || follicularIndicator == null
                || ovulationIndicator == null || lutealIndicator == null) {
            return;
        }
        // 将所有指示器重置为 0，当前阶段的设置为 1
        menstruationIndicator.setProgress(0);
        follicularIndicator.setProgress(0);
        ovulationIndicator.setProgress(0);
        lutealIndicator.setProgress(0);

        switch (phase) {
            case MENSTRUATION:
                menstruationIndicator.setProgress(1);
                break;
            case FOLLICULAR:
                follicularIndicator.setProgress(1);
                break;
            case OVULATION:
                ovulationIndicator.setProgress(1);
                break;
            case LUTEAL:
                lutealIndicator.setProgress(1);
                break;
        }
        System.out.println("[FemaleZone] 阶段指示器已更新为: " + phase.getLabel());
    }

    /** 加载当前健康目标，更新标签和单选按钮状态 */
    private void loadCurrentGoal() {
        System.out.println("[FemaleZone] loadCurrentGoal() 开始...");
        if (healthGoalService != null) {
            currentGoal = healthGoalService.getCurrentGoal();
            System.out.println("[FemaleZone] 从DB加载目标: " + (currentGoal != null ? currentGoal.getGoalType() : "null"));
        } else {
            System.out.println("[FemaleZone] healthGoalService 为 null！");
        }

        if (currentGoalLabel != null) {
            if (currentGoal != null) {
                String label = HealthGoalType.valueOf(currentGoal.getGoalType()).getLabel();
                currentGoalLabel.setText("当前目标: " + label);
                System.out.println("[FemaleZone] 更新标签: " + label);
            } else {
                currentGoalLabel.setText("当前目标: 未设置");
                System.out.println("[FemaleZone] 更新标签: 未设置");
            }
        }

        // 同步 radio button 选中状态
        updateGoalRadioButtons();
    }

    /** 更新健康目标单选按钮选中状态 */
    private void updateGoalRadioButtons() {
        if (currentGoal == null || weightLossRadio == null || muscleGainRadio == null || maintainRadio == null) {
            return;
        }
        try {
            HealthGoalType goalType = HealthGoalType.valueOf(currentGoal.getGoalType());
            switch (goalType) {
                case WEIGHT_LOSS:
                    weightLossRadio.setSelected(true);
                    break;
                case MUSCLE_GAIN:
                    muscleGainRadio.setSelected(true);
                    break;
                case MAINTAIN:
                    maintainRadio.setSelected(true);
                    break;
            }
            System.out.println("[FemaleZone] radio button 同步到: " + goalType.getLabel());
        } catch (IllegalArgumentException e) {
            System.err.println("[FemaleZone] 未知的目标类型: " + currentGoal.getGoalType());
        }
    }

    /** 保存周期记录：更新已有记录或新增记录 */
    @FXML
    private void handleSaveCycle() {
        System.out.println("[FemaleZone] handleSaveCycle() 被调用");
        if (startDatePicker == null || cycleLengthSpinner == null) {
            System.out.println("[FemaleZone] startDatePicker 或 cycleLengthSpinner 为 null！");
            return;
        }

        CycleRecord record = cycleService != null ? cycleService.getCurrentCycle() : null;
        boolean isUpdate = (record != null);

        if (record == null) {
            record = new CycleRecord();
            record.setUserId(1);
            record.setCreateTime(LocalDateTime.now());
        }

        record.setStartDate(startDatePicker.getValue());
        record.setCycleLength(cycleLengthSpinner.getValue());
        record.setUpdateTime(LocalDateTime.now());

        if (isUpdate) {
            cycleService.updateCycleRecord(record);
            System.out.println("[FemaleZone] 更新已有周期记录: id=" + record.getId() + ", startDate=" + record.getStartDate());
        } else {
            cycleService.saveCycleRecord(record);
            System.out.println("[FemaleZone] 新增周期记录: startDate=" + record.getStartDate());
        }

        loadData();
        showAlert("保存成功");
    }

    /** 保存健康目标：减脂/增肌/维持 */
    @FXML
    private void handleSaveGoal() {
        System.out.println("[FemaleZone] handleSaveGoal() 被调用");
        if (goalToggleGroup == null) {
            System.out.println("[FemaleZone] goalToggleGroup 为 null！");
            return;
        }

        RadioButton selected = (RadioButton) goalToggleGroup.getSelectedToggle();
        if (selected == null) {
            System.out.println("[FemaleZone] 没有选中任何目标");
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

        System.out.println("[FemaleZone] 保存目标: " + goalType);

        currentGoal = new HealthGoal();
        currentGoal.setUserId(1);
        currentGoal.setGoalType(goalType);
        currentGoal.setCreateTime(LocalDateTime.now());
        currentGoal.setUpdateTime(LocalDateTime.now());

        // 持久化到数据库
        if (healthGoalService != null) {
            healthGoalService.saveGoal(currentGoal);
            System.out.println("[FemaleZone] 目标已保存到数据库");
        } else {
            System.err.println("[FemaleZone] healthGoalService 为 null，无法保存！");
        }

        loadCurrentGoal();
        showAlert("健康目标设置成功");
    }

    // ==================== 一、专属食谱 ====================

    /** 查看专属食谱：根据周期阶段推荐菜谱 */
    @FXML
    private void handleViewRecipes() {
        System.out.println("[FemaleZone] handleViewRecipes() 被调用");

        String phase = cycleService != null ? cycleService.getCurrentPhase() : "FOLLICULAR";
        CyclePhase cyclePhase = CyclePhase.valueOf(phase);
        System.out.println("[FemaleZone] 专属食谱 - 当前阶段: " + cyclePhase.getLabel());

        // 获取用户身体数据
        BodyData bodyData = bodyDataService != null ? bodyDataService.getLatestBodyData() : null;
        int targetCalMin = 0;
        int targetCalMax = 3000;
        if (bodyData != null) {
            targetCalMin = bodyData.getRecommendedCaloriesMin() != null ? bodyData.getRecommendedCaloriesMin() : 0;
            targetCalMax = bodyData.getRecommendedCaloriesMax() != null ? bodyData.getRecommendedCaloriesMax() : 3000;
            System.out.println("[FemaleZone] 推荐热量范围: " + targetCalMin + " ~ " + targetCalMax);
        } else {
            System.out.println("[FemaleZone] bodyData 为 null，使用默认热量范围");
        }

        // 获取所有菜谱
        List<Recipe> allRecipes = recipeService != null ? recipeService.getAllRecipes() : Collections.emptyList();
        System.out.println("[FemaleZone] 菜谱总数: " + allRecipes.size());

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

        List<Recipe> topRecipes = recommended.stream().limit(10).collect(Collectors.toList());
        System.out.println("[FemaleZone] 专属食谱筛选结果: " + recommended.size() + " 条匹配，取前 " + topRecipes.size() + " 条");

        String title = "专属食谱推荐 - " + cyclePhase.getLabel();
        showRecipeDialog(title, topRecipes, cyclePhase);
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

    /** 查看专属匹配：根据偏好食材和营养缺口匹配菜谱 */
    @FXML
    private void handleViewMatching() {
        System.out.println("[FemaleZone] handleViewMatching() 被调用");

        // 从数据库获取用户偏好和忌口
        List<String> favoriteIngredients = userPreferenceService != null
                ? userPreferenceService.getFavorites()
                : Arrays.asList("鸡肉", "鸡蛋", "西红柿", "西兰花", "牛肉");
        List<String> avoidedIngredients = userPreferenceService != null
                ? userPreferenceService.getAvoided()
                : Arrays.asList("辣椒", "大蒜");

        System.out.println("[FemaleZone] 偏好食材: " + favoriteIngredients);
        System.out.println("[FemaleZone] 忌口食材: " + avoidedIngredients);

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

        List<Recipe> topMatched = matched.stream().limit(10).collect(Collectors.toList());
        System.out.println("[FemaleZone] 专属匹配筛选结果: " + matched.size() + " 条匹配，取前 " + topMatched.size() + " 条");

        showMatchingDialog("个性化匹配推荐", topMatched, gaps, favoriteIngredients, avoidedIngredients);
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
        System.out.println("[FemaleZone] handleViewGapAnalysis() 被调用");

        Map<String, Double> gaps = calculateNutritionGaps();
        System.out.printf("[FemaleZone] 营养缺口: 热量=%.0f, 蛋白质=%.1f, 碳水=%.1f, 脂肪=%.1f%n",
                gaps.getOrDefault("calories", 0.0),
                gaps.getOrDefault("protein", 0.0),
                gaps.getOrDefault("carb", 0.0),
                gaps.getOrDefault("fat", 0.0));

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
            System.out.println("[FemaleZone] calculateNutritionGaps: bodyData 为 null，返回全0缺口");
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
        System.out.println("[FemaleZone] calculateNutritionGaps: 今日用餐记录 " + todayRecords.size() + " 条");
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

        System.out.printf("[FemaleZone] 目标: 热量=%.0f, 蛋白质=%.1f, 碳水=%.1f, 脂肪=%.1f%n",
                targetCal, targetProtein, targetCarb, targetFat);
        System.out.printf("[FemaleZone] 实际: 热量=%.0f, 蛋白质=%.1f, 碳水=%.1f, 脂肪=%.1f%n",
                actualCal, actualProtein, actualCarb, actualFat);

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