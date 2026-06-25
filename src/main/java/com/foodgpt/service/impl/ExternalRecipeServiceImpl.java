package com.foodgpt.service.impl;

import com.foodgpt.config.ApiConfig;
import com.foodgpt.entity.Recipe;
import com.foodgpt.service.ExternalRecipeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.ArrayList;
import java.util.List;

public class ExternalRecipeServiceImpl implements ExternalRecipeService {

    private final ApiConfig.RecipeSearchConfig config;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public ExternalRecipeServiceImpl(ApiConfig.RecipeSearchConfig config) {
        this.config = config;
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<Recipe> searchRecipes(String keyword) {
        List<Recipe> recipes = new ArrayList<>();
        
        if (config.getBaseUrl() == null || config.getBaseUrl().isEmpty()) {
            return recipes;
        }

        try {
            String url = config.getBaseUrl() + "/recipes/search?query=" + keyword;
            
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + config.getApiKey())
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    JsonNode root = objectMapper.readTree(json);
                    JsonNode results = root.path("results");
                    
                    for (JsonNode node : results) {
                        Recipe recipe = new Recipe();
                        recipe.setName(node.path("title").asText());
                        recipe.setDescription(node.path("summary").asText());
                        recipe.setImageUrl(node.path("image").asText());
                        recipe.setCalories(node.path("calories").asInt());
                        recipe.setProtein(node.path("protein").asDouble());
                        recipe.setCarbohydrate(node.path("carbs").asDouble());
                        recipe.setFat(node.path("fat").asDouble());
                        recipe.setSource("外部API");
                        recipes.add(recipe);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("搜索菜谱失败: " + e.getMessage());
        }
        
        return recipes;
    }

    @Override
    public Recipe getRecipeDetail(String recipeId) {
        Recipe recipe = new Recipe();
        
        if (config.getBaseUrl() == null || config.getBaseUrl().isEmpty()) {
            return recipe;
        }

        try {
            String url = config.getBaseUrl() + "/recipes/" + recipeId + "/information";
            
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + config.getApiKey())
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    JsonNode root = objectMapper.readTree(json);
                    
                    recipe.setName(root.path("title").asText());
                    recipe.setDescription(root.path("summary").asText());
                    recipe.setImageUrl(root.path("image").asText());
                    recipe.setCalories(root.path("calories").asInt());
                    recipe.setProtein(root.path("protein").asDouble());
                    recipe.setCarbohydrate(root.path("carbs").asDouble());
                    recipe.setFat(root.path("fat").asDouble());
                    recipe.setSource("外部API");
                    
                    List<String> ingredients = new ArrayList<>();
                    JsonNode ingredientsNode = root.path("extendedIngredients");
                    for (JsonNode ing : ingredientsNode) {
                        ingredients.add(ing.path("original").asText());
                    }
                    recipe.setIngredients(ingredients);
                    
                    List<String> steps = new ArrayList<>();
                    JsonNode stepsNode = root.path("analyzedInstructions");
                    if (stepsNode.isArray() && stepsNode.size() > 0) {
                        JsonNode stepList = stepsNode.get(0).path("steps");
                        for (JsonNode step : stepList) {
                            steps.add(step.path("step").asText());
                        }
                    }
                    recipe.setSteps(steps);
                }
            }
        } catch (Exception e) {
            System.err.println("获取菜谱详情失败: " + e.getMessage());
        }
        
        return recipe;
    }
}