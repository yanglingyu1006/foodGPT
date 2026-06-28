package com.foodgpt.controller;

import com.foodgpt.entity.Recipe;
import com.foodgpt.enums.RecipeCategory;
import com.foodgpt.service.RecipeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
    private javafx.scene.layout.TilePane recipeTilePane;
    @FXML
    private Label pageLabel;
    @FXML
    private Button prevPageBtn;
    @FXML
    private Button nextPageBtn;

    private static final int PAGE_SIZE = 10;
    private RecipeService recipeService;
    private MainLayoutController mainLayoutController;
    private List<Recipe> allRecipes;
    private int currentPage = 0;

    public void setService(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
    }

    /**
     * 刷新页面数据（从其他页面切换回来时调用）
     */
    public void refresh() {
        System.out.println("[RecipeManage] refresh() 被调用，重新加载菜谱列表...");
        loadRecipes();
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
            System.out.println("[RecipeManage] loadRecipes() 从DB加载到 " + allRecipes.size() + " 条菜谱");
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
        RecipeDetailController detailController = RecipeDetailController.show(selected, recipeService);
        if (detailController != null && detailController.isSaved()) {
            System.out.println("[RecipeManage] 详情中保存了菜谱，刷新列表");
            loadRecipes();
        }
    }

    @FXML
    private void handleDelete() {
        Recipe selected = recipeListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            recipeService.deleteRecipe(selected.getId());
            loadRecipes();
            System.out.println("[RecipeManage] 菜谱已删除，刷新关联页面");
            if (mainLayoutController != null) {
                mainLayoutController.refreshMealRecord();
                mainLayoutController.refreshDashboard();
            }
            showAlert("删除成功");
        }
    }

    @FXML
    private void handleOnlineSearch() {
        if (mainLayoutController != null) {
            mainLayoutController.showRecipeSearch();
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
