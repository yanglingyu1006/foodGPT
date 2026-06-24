package com.foodgpt.config;

import lombok.Data;

@Data
public class ApiConfig {
    private DeepseekConfig deepseek = new DeepseekConfig();
    private RecipeSearchConfig recipeSearch = new RecipeSearchConfig();

    @Data
    public static class DeepseekConfig {
        private String baseUrl = "https://api.deepseek.com/chat/completions";
        private String apiKey = "";
        private String model = "deepseek-v4-pro";
        private int maxTokens = 4096;
        private double temperature = 1.0;
    }

    @Data
    public static class RecipeSearchConfig {
        private String baseUrl = "";
        private String apiKey = "";
    }
}
