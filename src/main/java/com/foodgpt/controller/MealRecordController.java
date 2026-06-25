package com.foodgpt.controller;

import com.foodgpt.entity.MealRecord;
import com.foodgpt.entity.Recipe;
import com.foodgpt.enums.MealType;
import com.foodgpt.service.MealRecordService;
import com.foodgpt.service.RecipeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MealRecordController {

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

    private MealRecordService mealRecordService;
    private RecipeService recipeService;

    public void setServices(MealRecordService mealRecordService, RecipeService recipeService) {
        this.mealRecordService = mealRecordService;
        this.recipeService = recipeService;
    }

    @FXML
    private void initialize() {
        datePicker.setValue(LocalDate.now());
        mealTypeComboBox.getItems().addAll("早餐", "午餐", "晚餐", "加餐");
        mealTypeComboBox.setValue("午餐");
        portionSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 5, 1, 0.1));
        loadRecipes();
        loadRecords();
    }

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
        showAlert("记录成功");
    }

    @FXML
    private void handleDelete() {
        MealRecord selected = recordListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            mealRecordService.deleteMealRecord(selected.getId());
            loadRecords();
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
