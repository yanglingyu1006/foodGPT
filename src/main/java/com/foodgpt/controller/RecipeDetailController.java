package com.foodgpt.controller;

import com.foodgpt.entity.Recipe;
import com.foodgpt.enums.RecipeCategory;
import com.foodgpt.service.RecipeService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RecipeDetailController {

    @FXML
    private Label recipeNameLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label caloriesLabel;
    @FXML
    private VBox ingredientsContainer;
    @FXML
    private VBox stepsContainer;
    @FXML
    private Label proteinLabel;
    @FXML
    private Label carbLabel;
    @FXML
    private Label fatLabel;
    @FXML
    private Button saveBtn;
    @FXML
    private Button closeBtn;

    private Recipe recipe;
    private RecipeService recipeService;
    private boolean saved = false;

    public void setRecipeService(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        displayRecipe();
    }

    @FXML
    private void initialize() {
        if (closeBtn != null) {
            closeBtn.setOnAction(e -> closeWindow());
        }
        if (saveBtn != null) {
            saveBtn.setOnAction(e -> handleSave());
        }
    }

    private void displayRecipe() {
        if (recipe == null) return;

        if (recipeNameLabel != null) {
            recipeNameLabel.setText(recipe.getName());
        }

        if (categoryLabel != null) {
            String category = "其他";
            if (recipe.getCategory() != null && !recipe.getCategory().isEmpty()) {
                try {
                    category = RecipeCategory.valueOf(recipe.getCategory()).getLabel();
                } catch (IllegalArgumentException ignored) {
                    category = recipe.getCategory();
                }
            }
            categoryLabel.setText("分类: " + category);
        }

        if (caloriesLabel != null) {
            int cal = recipe.getCalories() != null ? recipe.getCalories() : 0;
            caloriesLabel.setText("热量: " + cal + " kcal");
        }

        // 显示食材清单
        if (ingredientsContainer != null) {
            ingredientsContainer.getChildren().clear();
            List<String> ingredients = recipe.getIngredients();
            if (ingredients != null && !ingredients.isEmpty()) {
                for (String ing : ingredients) {
                    Label label = new Label("  " + ing);
                    label.setStyle("-fx-font-size: 14px; -fx-padding: 2px 0;");
                    ingredientsContainer.getChildren().add(label);
                }
            } else {
                ingredientsContainer.getChildren().add(new Label("  暂无食材信息"));
            }
        }

        // 显示烹饪步骤
        if (stepsContainer != null) {
            stepsContainer.getChildren().clear();
            List<String> steps = recipe.getSteps();
            if (steps != null && !steps.isEmpty()) {
                for (int i = 0; i < steps.size(); i++) {
                    Label label = new Label((i + 1) + ". " + steps.get(i));
                    label.setStyle("-fx-font-size: 14px; -fx-padding: 3px 0; -fx-wrap-text: true;");
                    stepsContainer.getChildren().add(label);
                }
            } else {
                stepsContainer.getChildren().add(new Label("  暂无烹饪步骤"));
            }
        }

        // 营养素信息
        if (proteinLabel != null) {
            double p = recipe.getProtein() != null ? recipe.getProtein() : 0;
            proteinLabel.setText(String.format("蛋白质: %.1fg", p));
        }
        if (carbLabel != null) {
            double c = recipe.getCarbohydrate() != null ? recipe.getCarbohydrate() : 0;
            carbLabel.setText(String.format("碳水: %.1fg", c));
        }
        if (fatLabel != null) {
            double f = recipe.getFat() != null ? recipe.getFat() : 0;
            fatLabel.setText(String.format("脂肪: %.1fg", f));
        }

        // 如果是本地菜谱，隐藏保存按钮
        if (saveBtn != null && recipe.getId() != null && recipe.getId() > 0) {
            saveBtn.setVisible(false);
        }
    }

    @FXML
    private void handleSave() {
        if (recipeService == null) {
            showAlert("无法保存：服务未初始化");
            return;
        }

        Recipe newRecipe = new Recipe();
        newRecipe.setName(recipe.getName());
        newRecipe.setCategory(recipe.getCategory() != null ? recipe.getCategory() : "OTHER");
        newRecipe.setIngredients(recipe.getIngredients() != null ? recipe.getIngredients() : new ArrayList<>());
        newRecipe.setSteps(recipe.getSteps() != null ? recipe.getSteps() : new ArrayList<>());
        newRecipe.setProtein(recipe.getProtein());
        newRecipe.setCarbohydrate(recipe.getCarbohydrate());
        newRecipe.setFat(recipe.getFat());
        newRecipe.setCalories(recipe.getCalories());
        newRecipe.setImageUrl(recipe.getImageUrl());
        newRecipe.setDescription(recipe.getDescription());
        newRecipe.setSource(recipe.getSource());
        newRecipe.setCreateTime(LocalDateTime.now());
        newRecipe.setUpdateTime(LocalDateTime.now());

        recipeService.saveRecipe(newRecipe);
        saved = true;
        System.out.println("[RecipeDetail] 菜谱已保存: name=" + newRecipe.getName() + ", id=" + newRecipe.getId());
        showAlert("菜谱已保存到本地");
        closeWindow();
    }

    @FXML
    private void handleClose() {
        closeWindow();
    }

    private void closeWindow() {
        if (closeBtn != null) {
            Stage stage = (Stage) closeBtn.getScene().getWindow();
            stage.close();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * 打开菜谱详情窗口（静态方法）
     */
    public static RecipeDetailController show(Recipe recipe, RecipeService recipeService) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    RecipeDetailController.class.getResource("/fxml/recipeDetail.fxml")
            );
            // 必须先 setController 再 load，因为 FXML 中不再使用 fx:controller
            RecipeDetailController controller = new RecipeDetailController();
            loader.setController(controller);

            Stage stage = new Stage();
            stage.setTitle("菜谱详情");
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(
                    RecipeDetailController.class.getResource("/css/main.css").toExternalForm()
            );
            stage.setScene(scene);
            stage.setMinWidth(520);
            stage.setMinHeight(600);

            controller.setRecipeService(recipeService);
            controller.setRecipe(recipe);
            stage.showAndWait();
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}