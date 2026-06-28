package com.foodgpt.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

public class MainLayoutController {

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

    @FXML
    private void initialize() {
        tabLabels.addAll(List.of(tabDashboard, tabRecipe, tabMeal, tabNutrition, tabFemale, tabAI));
        activeTab = tabDashboard;
    }

    // --- 内容设置 ---
    public void setDashboardContent(Node content) { this.dashboardContent = content; }
    public void setDashboardController(DashboardController c) { this.dashboardController = c; }
    public void setRecipeContent(Node content) { this.recipeContent = content; }
    public void setRecipeSearchContent(Node content) { this.recipeSearchContent = content; }
    public void setMealContent(Node content) { this.mealContent = content; }
    public void setMealRecordController(MealRecordController c) { this.mealRecordController = c; }
    public void setRecipeManageController(RecipeManageController c) { this.recipeManageController = c; }
    public void setNutritionContent(Node content) { this.nutritionContent = content; }
    public void setNutritionAnalysisController(NutritionAnalysisController c) { this.nutritionAnalysisController = c; }
    public void setFemaleContent(Node content) { this.femaleContent = content; }
    public void setFemaleZoneController(FemaleZoneController c) { this.femaleZoneController = c; }
    public void setAiContent(Node content) { this.aiContent = content; }

    public void showDashboard() {
        if (dashboardContent != null) {
            contentArea.getChildren().setAll(dashboardContent);
        }
    }

    public void showNutrition() {
        if (nutritionContent != null) {
            switchTab(tabNutrition, nutritionContent);
        }
    }

    public void showRecipeSearch() {
        if (recipeSearchContent != null) {
            switchTab(tabRecipe, recipeSearchContent);
        }
    }

    // --- 状态栏更新 ---
    public void updateStatusBar(int calories, double protein, double carb, double fat) {
        if (statusCalories != null) statusCalories.setText(calories + " kcal");
        if (statusProtein != null) statusProtein.setText(String.format("%.1f g", protein));
        if (statusCarb != null) statusCarb.setText(String.format("%.1f g", carb));
        if (statusFat != null) statusFat.setText(String.format("%.1f g", fat));
    }

    // --- 跨页面刷新（数据修改后通知其他页面） ---
    public void refreshDashboard() {
        if (dashboardController != null) {
            System.out.println("[MainLayout] 跨页面刷新 Dashboard");
            dashboardController.refresh();
        }
    }

    public void refreshRecipeManage() {
        if (recipeManageController != null) {
            System.out.println("[MainLayout] 跨页面刷新 RecipeManage");
            recipeManageController.refresh();
        }
    }

    public void refreshMealRecord() {
        if (mealRecordController != null) {
            System.out.println("[MainLayout] 跨页面刷新 MealRecord");
            mealRecordController.refresh();
        }
    }

    // --- 标签切换 ---
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

    @FXML private void onTabDashboard() {
        switchTab(tabDashboard, dashboardContent);
        if (dashboardController != null) {
            dashboardController.refresh();
        }
    }
    public void onTabRecipe() {
        switchTab(tabRecipe, recipeContent);
        if (recipeManageController != null) {
            recipeManageController.refresh();
        }
    }
    @FXML private void onTabMeal() {
        switchTab(tabMeal, mealContent);
        if (mealRecordController != null) {
            mealRecordController.refresh();
        }
    }
    @FXML private void onTabNutrition() {
        switchTab(tabNutrition, nutritionContent);
        if (nutritionAnalysisController != null) {
            nutritionAnalysisController.refresh();
        }
    }
    @FXML private void onTabFemale() {
        switchTab(tabFemale, femaleContent);
        if (femaleZoneController != null) {
            femaleZoneController.refresh();
        }
    }
    @FXML private void onTabAI() { switchTab(tabAI, aiContent); }
}