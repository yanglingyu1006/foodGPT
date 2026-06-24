package com.foodgpt.controller;

import com.foodgpt.entity.Recipe;
import com.foodgpt.enums.RecipeCategory;
import com.foodgpt.service.RecipeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class RecipeManageController {

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private ListView<Recipe> recipeListView;
    @FXML
    private Button searchBtn;

    private RecipeService recipeService;

    public void setService(RecipeService recipeService) {
        this.recipeService = recipeService;
        loadRecipes();
    }

    private void loadRecipes() {
        List<Recipe> recipes = recipeService.getAllRecipes();
        ObservableList<Recipe> items = FXCollections.observableArrayList(recipes);
        recipeListView.setItems(items);
        recipeListView.setCellFactory(param -> new ListCell<Recipe>() {
            @Override
            protected void updateItem(Recipe item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String category = RecipeCategory.valueOf(item.getCategory()).getLabel();
                    setText(String.format("%s - %s - %d kcal", item.getName(), category, item.getCalories()));
                }
            }
        });
    }

    @FXML
    private void initialize() {
        categoryComboBox.getItems().addAll("全部", "早餐", "午餐", "晚餐", "加餐", "其他");
        categoryComboBox.setValue("全部");
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        String category = categoryComboBox.getValue();
        String categoryCode = "全部".equals(category) ? "" : RecipeCategory.fromLabel(category).name();

        List<Recipe> recipes = recipeService.searchRecipes(keyword, categoryCode);
        ObservableList<Recipe> items = FXCollections.observableArrayList(recipes);
        recipeListView.setItems(items);
    }

    @FXML
    private void handleDelete() {
        Recipe selected = recipeListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            recipeService.deleteRecipe(selected.getId());
            loadRecipes();
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
