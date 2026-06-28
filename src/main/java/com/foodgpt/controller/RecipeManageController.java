package com.foodgpt.controller;

import com.foodgpt.entity.Recipe;
import com.foodgpt.enums.RecipeCategory;
import com.foodgpt.service.RecipeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;

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
    @FXML
    private TilePane recipeTilePane;
    @FXML
    private Label pageLabel;
    @FXML
    private Button prevPageBtn;
    @FXML
    private Button nextPageBtn;

    private static final int PAGE_SIZE = 10;
    private RecipeService recipeService;
    private List<Recipe> allRecipes;
    private int currentPage = 0;

    public void setService(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @FXML
    private void initialize() {
        if (categoryComboBox != null) {
            categoryComboBox.getItems().addAll("全部", "早餐", "午餐", "晚餐", "加餐", "其他");
            categoryComboBox.setValue("全部");
        }
        loadRecipes();
    }

    private void loadRecipes() {
        if (recipeService != null) {
            allRecipes = recipeService.getAllRecipes();
            currentPage = 0;
            showPage();
        }
    }

    private void showPage() {
        if (recipeListView == null) {
            return;
        }

        int totalPages = Math.max(1, (int) Math.ceil((double) allRecipes.size() / PAGE_SIZE));
        if (currentPage >= totalPages) {
            currentPage = totalPages - 1;
        }
        if (currentPage < 0) {
            currentPage = 0;
        }

        int from = currentPage * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, allRecipes.size());
        List<Recipe> pageItems = allRecipes.subList(from, to);

        ObservableList<Recipe> items = FXCollections.observableArrayList(pageItems);
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

        if (pageLabel != null) {
            pageLabel.setText(String.format("第 %d/%d 页", currentPage + 1, totalPages));
        }
        if (prevPageBtn != null) {
            prevPageBtn.setDisable(currentPage == 0);
        }
        if (nextPageBtn != null) {
            nextPageBtn.setDisable(currentPage >= totalPages - 1);
        }
    }

    @FXML
    private void handlePrevPage() {
        if (currentPage > 0) {
            currentPage--;
            showPage();
        }
    }

    @FXML
    private void handleNextPage() {
        int totalPages = Math.max(1, (int) Math.ceil((double) allRecipes.size() / PAGE_SIZE));
        if (currentPage < totalPages - 1) {
            currentPage++;
            showPage();
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        String category = categoryComboBox.getValue();
        String categoryCode = "全部".equals(category) ? "" : RecipeCategory.fromLabel(category).name();

        allRecipes = recipeService.searchRecipes(keyword, categoryCode);
        currentPage = 0;
        showPage();
    }

    @FXML
    private void handleViewDetail() {
        Recipe selected = recipeListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("请先选择一份菜谱");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("菜谱详情");
        alert.setHeaderText(selected.getName());

        String category = RecipeCategory.valueOf(selected.getCategory()).getLabel();
        String content = String.format(
                "分类: %s\n热量: %d kcal\n\n营养素:\n  蛋白质: %.1f g\n  碳水化合物: %.1f g\n  脂肪: %.1f g\n\n食材清单:\n%s\n\n烹饪步骤:\n%s",
                category,
                selected.getCalories() != null ? selected.getCalories() : 0,
                selected.getProtein() != null ? selected.getProtein() : 0,
                selected.getCarbohydrate() != null ? selected.getCarbohydrate() : 0,
                selected.getFat() != null ? selected.getFat() : 0,
                selected.getIngredients() != null ? selected.getIngredients() : "暂无",
                selected.getSteps() != null ? selected.getSteps() : "暂无"
        );

        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(500);
        textArea.setPrefRowCount(15);

        alert.getDialogPane().setContent(textArea);
        alert.getDialogPane().setMinWidth(550);
        alert.showAndWait();
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
