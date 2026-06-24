package com.foodgpt.service;

import com.foodgpt.entity.Recipe;

import java.util.List;

public interface RecipeService {
    List<Recipe> searchRecipes(String keyword, String category);
    void saveRecipe(Recipe recipe);
    void deleteRecipe(Long id);
    Recipe getRecipeById(Long id);
    List<Recipe> getRecipesByCategory(String category);
    List<Recipe> getAllRecipes();
}
