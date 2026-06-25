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
    private Node mealContent;
    private Node nutritionContent;
    private Node femaleContent;
    private Node aiContent;

    @FXML
    private void initialize() {
        tabLabels.addAll(List.of(tabDashboard, tabRecipe, tabMeal, tabNutrition, tabFemale, tabAI));
        activeTab = tabDashboard;
    }

    // --- 内容设置 ---
    public void setDashboardContent(Node content) { this.dashboardContent = content; }
    public void setRecipeContent(Node content) { this.recipeContent = content; }
    public void setMealContent(Node content) { this.mealContent = content; }
    public void setNutritionContent(Node content) { this.nutritionContent = content; }
    public void setFemaleContent(Node content) { this.femaleContent = content; }
    public void setAiContent(Node content) { this.aiContent = content; }

    public void showDashboard() {
        if (dashboardContent != null) {
            contentArea.getChildren().setAll(dashboardContent);
        }
    }

    // --- 状态栏更新 ---
    public void updateStatusBar(int calories, double protein, double carb, double fat) {
        if (statusCalories != null) statusCalories.setText(calories + " kcal");
        if (statusProtein != null) statusProtein.setText(String.format("%.1f g", protein));
        if (statusCarb != null) statusCarb.setText(String.format("%.1f g", carb));
        if (statusFat != null) statusFat.setText(String.format("%.1f g", fat));
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

    @FXML private void onTabDashboard() { switchTab(tabDashboard, dashboardContent); }
    @FXML private void onTabRecipe() { switchTab(tabRecipe, recipeContent); }
    @FXML private void onTabMeal() { switchTab(tabMeal, mealContent); }
    @FXML private void onTabNutrition() { switchTab(tabNutrition, nutritionContent); }
    @FXML private void onTabFemale() { switchTab(tabFemale, femaleContent); }
    @FXML private void onTabAI() { switchTab(tabAI, aiContent); }
}