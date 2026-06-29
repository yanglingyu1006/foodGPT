package com.foodgpt.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

/**
 * 主布局控制器
 * 
 * 功能模块：
 * 1. 导航标签管理 - 首页/菜谱/记录/分析/专区/AI顾问 六个标签切换
 * 2. 内容区域管理 - 动态加载和切换不同页面的内容
 * 3. 状态栏更新 - 显示今日摄入/蛋白质/碳水/脂肪
 * 4. 跨页面刷新 - 数据修改后通知其他页面刷新
 * 5. 页面跳转 - 提供页面间跳转的统一接口
 * 
 * @author FoodGPT
 */
public class MainLayoutController {

    // ==================== FXML 组件注入 ====================
    
    @FXML
    private StackPane contentArea;

    // 导航标签
    @FXML private Label tabDashboard;
    @FXML private Label tabRecipe;
    @FXML private Label tabMeal;
    @FXML private Label tabNutrition;
    @FXML private Label tabFemale;
    @FXML private Label tabAI;

    // 状态栏
    @FXML private Label statusCalories;
    @FXML private Label statusProtein;
    @FXML private Label statusCarb;
    @FXML private Label statusFat;

    private final List<Label> tabLabels = new ArrayList<>();
    private Label activeTab;

    private Node dashboardContent;
    private Node recipeContent;
    private Node recipeSearchContent;
    private Node mealContent;
    private Node nutritionContent;
    private Node femaleContent;
    private Node aiContent;

    private DashboardController dashboardController;
    private MealRecordController mealRecordController;
    private RecipeManageController recipeManageController;
    private NutritionAnalysisController nutritionAnalysisController;
    private FemaleZoneController femaleZoneController;

    /** FXML 初始化：收集所有导航标签 */
    @FXML
    private void initialize() {
        tabLabels.addAll(List.of(tabDashboard, tabRecipe, tabMeal, tabNutrition, tabFemale, tabAI));
        activeTab = tabDashboard;
    }

    // ==================== 内容设置 ====================
    
    /** 设置首页内容节点 */
    public void setDashboardContent(Node content) { this.dashboardContent = content; }
    /** 设置首页控制器引用 */
    public void setDashboardController(DashboardController c) { this.dashboardController = c; }
    /** 设置菜谱管理内容节点 */
    public void setRecipeContent(Node content) { this.recipeContent = content; }
    /** 设置菜谱搜索内容节点 */
    public void setRecipeSearchContent(Node content) { this.recipeSearchContent = content; }
    /** 设置用餐记录内容节点 */
    public void setMealContent(Node content) { this.mealContent = content; }
    /** 设置用餐记录控制器引用 */
    public void setMealRecordController(MealRecordController c) { this.mealRecordController = c; }
    /** 设置菜谱管理控制器引用 */
    public void setRecipeManageController(RecipeManageController c) { this.recipeManageController = c; }
    /** 设置营养分析内容节点 */
    public void setNutritionContent(Node content) { this.nutritionContent = content; }
    /** 设置营养分析控制器引用 */
    public void setNutritionAnalysisController(NutritionAnalysisController c) { this.nutritionAnalysisController = c; }
    /** 设置女性专区内容节点 */
    public void setFemaleContent(Node content) { this.femaleContent = content; }
    /** 设置女性专区控制器引用 */
    public void setFemaleZoneController(FemaleZoneController c) { this.femaleZoneController = c; }
    /** 设置AI顾问内容节点 */
    public void setAiContent(Node content) { this.aiContent = content; }

    /** 显示首页 */
    public void showDashboard() {
        if (dashboardContent != null) {
            contentArea.getChildren().setAll(dashboardContent);
        }
    }

    /** 跳转到营养分析页面 */
    public void showNutrition() {
        if (nutritionContent != null) {
            switchTab(tabNutrition, nutritionContent);
        }
    }

    /** 跳转到菜谱搜索页面 */
    public void showRecipeSearch() {
        if (recipeSearchContent != null) {
            switchTab(tabRecipe, recipeSearchContent);
        }
    }

    // ==================== 状态栏更新 ====================
    
    /** 更新底部状态栏：今日摄入/蛋白质/碳水/脂肪 */
    public void updateStatusBar(int calories, double protein, double carb, double fat) {
        if (statusCalories != null) statusCalories.setText(calories + " kcal");
        if (statusProtein != null) statusProtein.setText(String.format("%.1f g", protein));
        if (statusCarb != null) statusCarb.setText(String.format("%.1f g", carb));
        if (statusFat != null) statusFat.setText(String.format("%.1f g", fat));
    }

    // ==================== 跨页面刷新 ====================
    
    /** 刷新首页数据 */
    public void refreshDashboard() {
        if (dashboardController != null) {
            System.out.println("[MainLayout] 跨页面刷新 Dashboard");
            dashboardController.refresh();
        }
    }

    /** 刷新菜谱管理页 */
    public void refreshRecipeManage() {
        if (recipeManageController != null) {
            System.out.println("[MainLayout] 跨页面刷新 RecipeManage");
            recipeManageController.refresh();
        }
    }

    /** 刷新用餐记录页 */
    public void refreshMealRecord() {
        if (mealRecordController != null) {
            System.out.println("[MainLayout] 跨页面刷新 MealRecord");
            mealRecordController.refresh();
        }
    }

    // ==================== 标签切换 ====================
    
    /** 切换导航标签：移除旧标签高亮，添加新标签高亮，切换内容 */
    private void switchTab(Label tab, Node content) {
        if (activeTab != null) {
            activeTab.getStyleClass().remove("nav-tab-active");
        }
        tab.getStyleClass().add("nav-tab-active");
        activeTab = tab;
        if (content != null) {
            contentArea.getChildren().setAll(content);
        }
    }

    /** 切换到首页标签 */
    @FXML private void onTabDashboard() {
        switchTab(tabDashboard, dashboardContent);
        if (dashboardController != null) {
            dashboardController.refresh();
        }
    }
    
    /** 切换到菜谱管理标签 */
    public void onTabRecipe() {
        switchTab(tabRecipe, recipeContent);
        if (recipeManageController != null) {
            recipeManageController.refresh();
        }
    }
    
    /** 切换到用餐记录标签 */
    @FXML private void onTabMeal() {
        switchTab(tabMeal, mealContent);
        if (mealRecordController != null) {
            mealRecordController.refresh();
        }
    }
    
    /** 切换到营养分析标签 */
    @FXML private void onTabNutrition() {
        switchTab(tabNutrition, nutritionContent);
        if (nutritionAnalysisController != null) {
            nutritionAnalysisController.refresh();
        }
    }
    
    /** 切换到女性专区标签 */
    @FXML private void onTabFemale() {
        switchTab(tabFemale, femaleContent);
        if (femaleZoneController != null) {
            femaleZoneController.refresh();
        }
    }
    
    /** 切换到AI顾问标签 */
    @FXML private void onTabAI() { switchTab(tabAI, aiContent); }
}