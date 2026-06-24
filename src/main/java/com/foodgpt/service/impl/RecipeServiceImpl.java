package com.foodgpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.foodgpt.entity.Recipe;
import com.foodgpt.mapper.RecipeMapper;
import com.foodgpt.service.RecipeService;

import java.util.List;

public class RecipeServiceImpl implements RecipeService {

    private final RecipeMapper recipeMapper;

    public RecipeServiceImpl(RecipeMapper recipeMapper) {
        this.recipeMapper = recipeMapper;
    }

    @Override
    public List<Recipe> searchRecipes(String keyword, String category) {
        QueryWrapper<Recipe> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("name", keyword);
        }
        if (category != null && !category.isEmpty() && !"ALL".equals(category)) {
            wrapper.eq("category", category);
        }
        wrapper.orderByDesc("create_time");
        return recipeMapper.selectList(wrapper);
    }

    @Override
    public void saveRecipe(Recipe recipe) {
        recipeMapper.insert(recipe);
    }

    @Override
    public void deleteRecipe(Long id) {
        recipeMapper.deleteById(id);
    }

    @Override
    public Recipe getRecipeById(Long id) {
        return recipeMapper.selectById(id);
    }

    @Override
    public List<Recipe> getRecipesByCategory(String category) {
        QueryWrapper<Recipe> wrapper = new QueryWrapper<>();
        wrapper.eq("category", category);
        return recipeMapper.selectList(wrapper);
    }

    @Override
    public List<Recipe> getAllRecipes() {
        return recipeMapper.selectList(null);
    }
}
