package com.foodgpt.controller;

import com.foodgpt.entity.Recipe;
import com.foodgpt.service.ExternalRecipeService;
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
    private Button saveBtn;
    @FXML
    private ListView<Recipe> resultListView;
    @FXML
    private RadioButton localRadio;
    @FXML
    private RadioButton onlineRadio;
    @FXML
    private ToggleGroup searchTypeGroup;

    private RecipeService recipeService;
    private ExternalRecipeService externalRecipeService;

    public void setService(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
    
    public void setExternalRecipeService(ExternalRecipeService externalRecipeService) {
        this.externalRecipeService = externalRecipeService;
    }

    @FXML
    private void initialize() {
        if (localRadio != null && onlineRadio != null) {
            searchTypeGroup = new ToggleGroup();
            localRadio.setToggleGroup(searchTypeGroup);
            onlineRadio.setToggleGroup(searchTypeGroup);
            localRadio.setSelected(true);
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        if (keyword == null || keyword.trim().isEmpty()) {
            showAlert("请输入搜索关键词");
            return;
        }

        List<Recipe> recipes = new ArrayList<>();
        
        if (onlineRadio != null && onlineRadio.isSelected() && externalRecipeService != null) {
            recipes = externalRecipeService.searchRecipes(keyword);
            if (recipes.isEmpty()) {
                showAlert("未找到相关菜谱，请检查API配置或尝试本地搜索");
            }
        } else {
            recipes = recipeService.searchRecipes(keyword, "");
        }

        ObservableList<Recipe> items = FXCollections.observableArrayList(recipes);
        if (resultListView != null) {
            resultListView.setItems(items);
            resultListView.setCellFactory(param -> new ListCell<Recipe>() {
                @Override
                protected void updateItem(Recipe item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        String source = item.getSource() != null ? item.getSource() : "本地";
                        setText(String.format("[%s] %s - %d kcal", source, item.getName(), item.getCalories()));
                    }
                }
            });
        }
    }

    @FXML
    private void handleSaveRecipe() {
        Recipe selected = resultListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("请先选择要保存的菜谱");
            return;
        }

        Recipe newRecipe = new Recipe();
        newRecipe.setName(selected.getName());
        newRecipe.setCategory(selected.getCategory() != null ? selected.getCategory() : "OTHER");
        newRecipe.setIngredients(selected.getIngredients() != null ? selected.getIngredients() : new ArrayList<>());
        newRecipe.setSteps(selected.getSteps() != null ? selected.getSteps() : new ArrayList<>());
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

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
