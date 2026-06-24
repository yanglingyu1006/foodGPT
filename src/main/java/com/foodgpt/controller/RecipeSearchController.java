package com.foodgpt.controller;

import com.foodgpt.entity.Recipe;
import com.foodgpt.service.RecipeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RecipeSearchController {

    @FXML
    private TextField searchField;
    @FXML
    private Button searchBtn;
    @FXML
    private ListView<Recipe> resultListView;

    private RecipeService recipeService;

    public void setService(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        List<Recipe> recipes = recipeService.searchRecipes(keyword, "");

        ObservableList<Recipe> items = FXCollections.observableArrayList(recipes);
        resultListView.setItems(items);
        resultListView.setCellFactory(param -> new ListCell<Recipe>() {
            @Override
            protected void updateItem(Recipe item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - %d kcal", item.getName(), item.getCalories()));
                }
            }
        });
    }

    @FXML
    private void handleSaveRecipe() {
        Recipe selected = resultListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Recipe newRecipe = new Recipe();
            newRecipe.setName(selected.getName());
            newRecipe.setCategory(selected.getCategory());
            newRecipe.setIngredients(selected.getIngredients());
            newRecipe.setSteps(selected.getSteps());
            newRecipe.setProtein(selected.getProtein());
            newRecipe.setCarbohydrate(selected.getCarbohydrate());
            newRecipe.setFat(selected.getFat());
            newRecipe.setCalories(selected.getCalories());
            newRecipe.setImageUrl(selected.getImageUrl());
            newRecipe.setDescription(selected.getDescription());
            newRecipe.setSource(selected.getSource());
            newRecipe.setCreateTime(LocalDateTime.now());

            recipeService.saveRecipe(newRecipe);
            showAlert("保存成功");
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
