package com.foodgpt.service;

import com.foodgpt.entity.Recipe;

import java.util.List;

public interface ExternalRecipeService {
    List<Recipe> searchRecipes(String keyword);
    Recipe getRecipeDetail(String recipeId);
}