package com.foodgpt.controller;

import com.foodgpt.entity.MealRecord;
import com.foodgpt.entity.Recipe;
import com.foodgpt.enums.MealType;
import com.foodgpt.service.MealRecordService;
import com.foodgpt.service.RecipeService;
import com.foodgpt.util.NutritionCalculator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用餐记录控制器
 * 
 * 功能模块：
 * 1. 用餐记录添加 - 选择日期、餐次、菜谱、份量后添加记录
 * 2. 用餐记录删除 - 删除选中的用餐记录
 * 3. 记录列表展示 - 按日期和餐次筛选显示用餐记录
 * 4. 每日营养汇总 - 显示当日总热量、蛋白质、碳水、脂肪
 * 5. 日期切换 - 切换日期自动刷新记录列表
 * 
 * @author FoodGPT
 */
public class MealRecordController {

    // ==================== FXML 组件注入 ====================
    
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> mealTypeComboBox;
    @FXML
    private ComboBox<String> recipeComboBox;
    @FXML
    private Spinner<Double> portionSpinner;
    @FXML
    private ListView<MealRecord> recordListView;
    @FXML
    private Button addBtn;
    @FXML
    private Label totalCaloriesLabel;
    @FXML
    private Label totalProteinLabel;
    @FXML
    private Label totalCarbLabel;
    @FXML
    private Label totalFatLabel;

    private MealRecordService mealRecordService;
    private RecipeService recipeService;
    private MainLayoutController mainLayoutController;

    /** 注入用餐记录和菜谱服务 */
    public void setServices(MealRecordService mealRecordService, RecipeService recipeService) {
        this.mealRecordService = mealRecordService;
        this.recipeService = recipeService;
    }

    /** 设置主布局控制器，用于跨页面刷新 */
    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
    }

    /**
     * 刷新页面数据（从其他页面切换回来时调用）
     */
    public void refresh() {
        loadRecipes();
        loadRecords();
        updateDailySummary();
    }

    /** FXML 初始化：设置日期默认值、餐次下拉框、份量选择器，加载数据 */
    @FXML
    private void initialize() {
        datePicker.setValue(LocalDate.now());
        mealTypeComboBox.getItems().addAll("早餐", "午餐", "晚餐", "加餐");
        mealTypeComboBox.setValue("午餐");
        portionSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 5, 1, 0.1));
        
        if (datePicker != null) {
            datePicker.setOnAction(e -> {
                loadRecords();
                updateDailySummary();
            });
        }
        
        loadRecipes();
        loadRecords();
        updateDailySummary();
    }

    /** 加载所有菜谱到下拉框 */
    private void loadRecipes() {
        if (recipeService != null && recipeComboBox != null) {
            List<Recipe> recipes = recipeService.getAllRecipes();
            ObservableList<String> names = FXCollections.observableArrayList();
            for (Recipe r : recipes) {
                names.add(r.getName());
            }
            recipeComboBox.setItems(names);
        }
    }

    /** 加载当前日期和餐次的用餐记录列表 */
    private void loadRecords() {
        if (mealRecordService != null && recipeService != null && recordListView != null) {
            LocalDate date = datePicker != null ? (datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now()) : LocalDate.now();
            String mealType = mealTypeComboBox != null ? mealTypeComboBox.getValue() : null;
            String typeCode = mealType != null ? MealType.fromLabel(mealType).name() : null;

            List<MealRecord> records = mealRecordService.getMealRecords(date, typeCode);
            ObservableList<MealRecord> items = FXCollections.observableArrayList(records);
            recordListView.setItems(items);
            recordListView.setCellFactory(param -> new ListCell<MealRecord>() {
                @Override
                protected void updateItem(MealRecord item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        Recipe recipe = recipeService.getRecipeById(item.getRecipeId());
                        String recipeName = recipe != null ? recipe.getName() : "未知";
                        String mealTypeLabel = MealType.valueOf(item.getMealType()).getLabel();
                        setText(String.format("%s - %s - %.1f份", mealTypeLabel, recipeName, item.getPortion()));
                    }
                }
            });
        }
    }

    /** 更新当日营养汇总显示（总热量、蛋白质、碳水、脂肪） */
    private void updateDailySummary() {
        if (mealRecordService != null && recipeService != null) {
            LocalDate date = datePicker != null ? (datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now()) : LocalDate.now();
            List<MealRecord> records = mealRecordService.getMealRecords(date, null);
            
            double totalProtein = 0, totalCarb = 0, totalFat = 0;
            int totalCalories = 0;
            
            for (MealRecord record : records) {
                Recipe recipe = recipeService.getRecipeById(record.getRecipeId());
                if (recipe != null) {
                    double portion = record.getPortion();
                    totalProtein += (recipe.getProtein() != null ? recipe.getProtein() : 0) * portion;
                    totalCarb += (recipe.getCarbohydrate() != null ? recipe.getCarbohydrate() : 0) * portion;
                    totalFat += (recipe.getFat() != null ? recipe.getFat() : 0) * portion;
                }
            }
            
            totalCalories = (int) NutritionCalculator.calculateCalories(totalProtein, totalCarb, totalFat);
            
            if (totalCaloriesLabel != null) {
                totalCaloriesLabel.setText(String.format("%d kcal", totalCalories));
            }
            if (totalProteinLabel != null) {
                totalProteinLabel.setText(String.format("%.1f g", totalProtein));
            }
            if (totalCarbLabel != null) {
                totalCarbLabel.setText(String.format("%.1f g", totalCarb));
            }
            if (totalFatLabel != null) {
                totalFatLabel.setText(String.format("%.1f g", totalFat));
            }
        }
    }

    /** 添加用餐记录，刷新首页营养进度 */
    @FXML
    private void handleAdd() {
        String recipeName = recipeComboBox.getValue();
        if (recipeName == null || recipeName.isEmpty()) {
            showAlert("请选择菜谱");
            return;
        }

        List<Recipe> recipes = recipeService.searchRecipes(recipeName, "");
        if (recipes.isEmpty()) {
            showAlert("菜谱不存在");
            return;
        }

        MealRecord record = new MealRecord();
        record.setUserId(1);
        record.setRecipeId(recipes.get(0).getId());
        record.setMealType(MealType.fromLabel(mealTypeComboBox.getValue()).name());
        record.setPortion(portionSpinner.getValue());
        record.setPortionUnit("份");
        record.setRecordDate(datePicker.getValue());
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        mealRecordService.saveMealRecord(record);
        loadRecords();
        updateDailySummary();
        System.out.println("[MealRecord] 用餐记录已添加，刷新首页营养进度");
        if (mainLayoutController != null) {
            mainLayoutController.refreshDashboard();
        }
        showAlert("记录成功");
    }

    /** 删除选中的用餐记录，刷新首页营养进度 */
    @FXML
    private void handleDelete() {
        MealRecord selected = recordListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            mealRecordService.deleteMealRecord(selected.getId());
            loadRecords();
            updateDailySummary();
            System.out.println("[MealRecord] 用餐记录已删除，刷新首页营养进度");
            if (mainLayoutController != null) {
                mainLayoutController.refreshDashboard();
            }
            showAlert("删除成功");
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
