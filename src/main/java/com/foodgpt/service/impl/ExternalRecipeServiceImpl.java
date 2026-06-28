package com.foodgpt.service.impl;

import com.foodgpt.config.AppConfig;
import com.foodgpt.entity.Recipe;
import com.foodgpt.enums.RecipeCategory;
import com.foodgpt.service.ExternalRecipeService;
import com.foodgpt.util.JsonUtil;
import com.foodgpt.util.OkHttpUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class ExternalRecipeServiceImpl implements ExternalRecipeService {

    private final AppConfig appConfig;
    private final ObjectMapper objectMapper;
    private final Map<String, String> recipeIdMap;

    public ExternalRecipeServiceImpl(AppConfig appConfig) {
        this.appConfig = appConfig;
        this.objectMapper = new ObjectMapper();
        this.recipeIdMap = new HashMap<>();
    }

    @Override
    public List<Recipe> searchRecipes(String keyword) {
        List<Recipe> recipes = new ArrayList<>();
        String apiKey = appConfig.getApi().getDeepseek().getApiKey();
        System.out.println("[ExternalRecipe] API Key 是否配置：" + (apiKey != null && !apiKey.isEmpty()));
        System.out.println("[ExternalRecipe] Base URL：" + appConfig.getApi().getDeepseek().getBaseUrl());
        System.out.println("[ExternalRecipe] Model：" + appConfig.getApi().getDeepseek().getModel());
        System.out.println("[ExternalRecipe] 搜索关键词：" + keyword);

        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("DeepSeek API Key 未配置");
            return recipes;
        }

        String systemPrompt = "你是一个专业的菜谱推荐助手。用户会提供食材关键词，请推荐5道包含这些食材的家常菜。" +
                "你必须严格按照JSON数组格式返回，不要包含任何Markdown标记、代码块标记或额外解释。" +
                "每道菜必须包含以下字段：name(菜名,中文), category(分类,必须是BREAKFAST/LUNCH/DINNER/SNACK/OTHER之一), " +
                "ingredients(食材数组,格式为\"食材名称 用量\"), steps(烹饪步骤数组,每个元素是一个步骤描述), " +
                "calories(热量,整数,单位kcal), protein(蛋白质,数值,单位g), carbohydrate(碳水,数值,单位g), fat(脂肪,数值,单位g)。" +
                "示例格式：[{\"name\":\"番茄炒蛋\",\"category\":\"LUNCH\",\"ingredients\":[\"番茄 2个\",\"鸡蛋 2个\"],\"steps\":[\"番茄切块\",\"鸡蛋打散\"],\"calories\":180,\"protein\":12.0,\"carbohydrate\":8.0,\"fat\":10.0}]";

        String userPrompt = "请推荐包含以下食材的5道家常菜：" + keyword;

        Map<String, Object> request = new HashMap<>();
        request.put("model", appConfig.getApi().getDeepseek().getModel());
        request.put("temperature", 0.7);
        request.put("max_tokens", 4096);
        request.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        ));

        try {
            String json = JsonUtil.toJson(request);
            System.out.println("[ExternalRecipe] 发送请求到 DeepSeek API...");
            String response = OkHttpUtil.postJson(
                    appConfig.getApi().getDeepseek().getBaseUrl(),
                    json,
                    apiKey
            );
            System.out.println("[ExternalRecipe] API 响应长度：" + (response != null ? response.length() : 0));
            String content = parseAiResponse(response);
            System.out.println("[ExternalRecipe] AI 返回内容长度：" + (content != null ? content.length() : 0));
            System.out.println("[ExternalRecipe] AI 返回内容前200字符：" + (content != null && content.length() > 200 ? content.substring(0, 200) : content));
            recipes = parseRecipes(content);
            System.out.println("[ExternalRecipe] 解析到菜谱数量：" + recipes.size());
        } catch (Exception e) {
            System.err.println("联网搜索菜谱失败: " + e.getMessage());
            e.printStackTrace();
        }

        return recipes;
    }

    @Override
    public Recipe getRecipeDetail(String recipeId) {
        // 联网搜索的菜谱详情通过重新搜索实现
        Recipe recipe = new Recipe();
        String apiKey = appConfig.getApi().getDeepseek().getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            return recipe;
        }

        String systemPrompt = "你是一个专业的菜谱助手。请提供以下菜谱的详细信息，包括完整的食材清单和详细的烹饪步骤。";
        String userPrompt = "请提供菜谱【" + recipeId + "】的详细信息";

        Map<String, Object> request = new HashMap<>();
        request.put("model", appConfig.getApi().getDeepseek().getModel());
        request.put("temperature", 0.7);
        request.put("max_tokens", 4096);
        request.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        ));

        try {
            String json = JsonUtil.toJson(request);
            String response = OkHttpUtil.postJson(
                    appConfig.getApi().getDeepseek().getBaseUrl(),
                    json,
                    apiKey
            );
            String content = parseAiResponse(response);
            List<Recipe> recipes = parseRecipes(content);
            if (!recipes.isEmpty()) {
                recipe = recipes.get(0);
            }
        } catch (Exception e) {
            System.err.println("获取菜谱详情失败: " + e.getMessage());
        }

        return recipe;
    }

    /**
     * 从 DeepSeek API 响应中提取文本内容
     */
    private String parseAiResponse(String response) {
        try {
            Map<String, Object> result = JsonUtil.fromJson(response, Map.class);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) result.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            }
        } catch (Exception e) {
            System.err.println("解析AI响应失败: " + e.getMessage());
        }
        return "";
    }

    /**
     * 解析 AI 返回的 JSON 数组为 Recipe 列表
     */
    private List<Recipe> parseRecipes(String content) {
        List<Recipe> recipes = new ArrayList<>();
        if (content == null || content.trim().isEmpty()) {
            return recipes;
        }

        try {
            // 清理可能的 Markdown 代码块标记
            String jsonStr = content.trim();
            if (jsonStr.startsWith("```json")) {
                jsonStr = jsonStr.substring(7);
            } else if (jsonStr.startsWith("```")) {
                jsonStr = jsonStr.substring(3);
            }
            if (jsonStr.endsWith("```")) {
                jsonStr = jsonStr.substring(0, jsonStr.length() - 3);
            }
            jsonStr = jsonStr.trim();

            // 找到 JSON 数组的起始位置
            int startIdx = jsonStr.indexOf('[');
            int endIdx = jsonStr.lastIndexOf(']');
            if (startIdx >= 0 && endIdx > startIdx) {
                jsonStr = jsonStr.substring(startIdx, endIdx + 1);
            }

            JsonNode root = objectMapper.readTree(jsonStr);
            if (root.isArray()) {
                recipeIdMap.clear();
                for (JsonNode node : root) {
                    Recipe recipe = new Recipe();
                    recipe.setName(node.path("name").asText("未知菜谱"));

                    String category = node.path("category").asText("OTHER").toUpperCase();
                    try {
                        RecipeCategory.valueOf(category);
                    } catch (IllegalArgumentException e) {
                        category = "OTHER";
                    }
                    recipe.setCategory(category);

                    // 解析食材列表
                    List<String> ingredients = new ArrayList<>();
                    JsonNode ingNode = node.path("ingredients");
                    if (ingNode.isArray()) {
                        for (JsonNode ing : ingNode) {
                            ingredients.add(ing.asText());
                        }
                    }
                    recipe.setIngredients(ingredients);

                    // 解析烹饪步骤
                    List<String> steps = new ArrayList<>();
                    JsonNode stepsNode = node.path("steps");
                    if (stepsNode.isArray()) {
                        for (JsonNode step : stepsNode) {
                            steps.add(step.asText());
                        }
                    }
                    recipe.setSteps(steps);

                    recipe.setCalories(node.path("calories").asInt(0));
                    recipe.setProtein(node.path("protein").asDouble(0.0));
                    recipe.setCarbohydrate(node.path("carbohydrate").asDouble(0.0));
                    recipe.setFat(node.path("fat").asDouble(0.0));
                    recipe.setSource("联网搜索");

                    String recipeId = "ext_" + recipe.getName().hashCode();
                    recipeIdMap.put(recipeId, recipe.getName());
                    recipe.setDescription(recipeId);

                    recipes.add(recipe);
                }
            }
        } catch (Exception e) {
            System.err.println("解析菜谱JSON失败: " + e.getMessage());
            System.err.println("原始内容: " + content);
        }

        return recipes;
    }
}