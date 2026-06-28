package com.foodgpt.controller;

import com.foodgpt.entity.Recipe;
import com.foodgpt.service.ExternalRecipeService;
import com.foodgpt.service.RecipeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

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
    private Button viewDetailBtn;
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
    private MainLayoutController mainLayoutController;

    public void setService(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    public void setExternalRecipeService(ExternalRecipeService externalRecipeService) {
        this.externalRecipeService = externalRecipeService;
    }

    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
    }

    @FXML
    private void initialize() {
        if (localRadio != null && onlineRadio != null) {
            searchTypeGroup = new ToggleGroup();
            localRadio.setToggleGroup(searchTypeGroup);
            onlineRadio.setToggleGroup(searchTypeGroup);
            localRadio.setSelected(true);
        }

        // 双击列表项查看详情
        if (resultListView != null) {
            resultListView.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    handleViewDetail();
                }
            });
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        System.out.println("[RecipeSearch] 搜索关键词：" + keyword);
        System.out.println("[RecipeSearch] 搜索方式：" + (onlineRadio != null && onlineRadio.isSelected() ? "联网搜索" : "本地搜索"));
        System.out.println("[RecipeSearch] externalRecipeService 是否为空：" + (externalRecipeService == null));

        if (keyword == null || keyword.trim().isEmpty()) {
            showAlert("请输入搜索关键词");
            return;
        }

        List<Recipe> recipes = new ArrayList<>();

        if (onlineRadio != null && onlineRadio.isSelected() && externalRecipeService != null) {
            System.out.println("[RecipeSearch] 调用 externalRecipeService.searchRecipes()...");
            recipes = externalRecipeService.searchRecipes(keyword);
            System.out.println("[RecipeSearch] 搜索结果数量：" + recipes.size());
            if (recipes.isEmpty()) {
                showAlert("未找到相关菜谱，请检查API密钥是否配置或尝试本地搜索");
            }
        } else {
            System.out.println("[RecipeSearch] 调用本地搜索...");
            recipes = recipeService.searchRecipes(keyword, "");
            System.out.println("[RecipeSearch] 本地搜索结果数量：" + recipes.size());
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
                        setText(String.format("[%s] %s - %d kcal", source, item.getName(),
                                item.getCalories() != null ? item.getCalories() : 0));
                    }
                }
            });
        } else {
            System.err.println("[RecipeSearch] resultListView 为 null！");
        }
    }

    @FXML
    private void handleViewDetail() {
        Recipe selected = resultListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("请先选择一份菜谱");
            return;
        }
        RecipeDetailController detailController = RecipeDetailController.show(selected, recipeService);
        if (detailController != null && detailController.isSaved() && mainLayoutController != null) {
            System.out.println("[RecipeSearch] 详情中保存了菜谱，刷新菜谱管理页");
            mainLayoutController.refreshRecipeManage();
        }
    }

    @FXML
    private void handleSaveRecipe() {
        Recipe selected = resultListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("请先选择要保存的菜谱");
            return;
        }

        try {
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
            newRecipe.setUpdateTime(LocalDateTime.now());

            recipeService.saveRecipe(newRecipe);
            System.out.println("[RecipeSearch] 菜谱已保存到本地: name=" + newRecipe.getName() + ", id=" + newRecipe.getId() + ", category=" + newRecipe.getCategory());

            // 刷新菜谱管理页
            if (mainLayoutController != null) {
                System.out.println("[RecipeSearch] 调用 refreshRecipeManage()...");
                mainLayoutController.refreshRecipeManage();
            } else {
                System.err.println("[RecipeSearch] mainLayoutController 为 null，无法刷新菜谱管理页！");
            }

            showAlert("保存成功");
        } catch (Exception e) {
            System.err.println("[RecipeSearch] 保存菜谱失败: " + e.getMessage());
            e.printStackTrace();
            showAlert("保存失败: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        if (mainLayoutController != null) {
            mainLayoutController.onTabRecipe();
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