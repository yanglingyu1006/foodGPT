package com.foodgpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.foodgpt.entity.MealRecord;
import com.foodgpt.entity.NutritionRecord;
import com.foodgpt.entity.Recipe;
import com.foodgpt.mapper.MealRecordMapper;
import com.foodgpt.mapper.NutritionRecordMapper;
import com.foodgpt.mapper.RecipeMapper;
import com.foodgpt.service.NutritionService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NutritionServiceImpl implements NutritionService {

    private final NutritionRecordMapper nutritionRecordMapper;
    private final MealRecordMapper mealRecordMapper;
    private final RecipeMapper recipeMapper;

    public NutritionServiceImpl(NutritionRecordMapper nutritionRecordMapper,
                               MealRecordMapper mealRecordMapper,
                               RecipeMapper recipeMapper) {
        this.nutritionRecordMapper = nutritionRecordMapper;
        this.mealRecordMapper = mealRecordMapper;
        this.recipeMapper = recipeMapper;
    }

    @Override
    public NutritionRecord getDailyNutrition(LocalDate date) {
        QueryWrapper<NutritionRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", 1);
        wrapper.eq("record_date", date);
        return nutritionRecordMapper.selectOne(wrapper);
    }

    @Override
    public List<NutritionRecord> getWeeklyNutrition(LocalDate startDate) {
        QueryWrapper<NutritionRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", 1);
        wrapper.ge("record_date", startDate);
        wrapper.le("record_date", startDate.plusDays(6));
        return nutritionRecordMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Object> getTodaySummary() {
        Map<String, Object> summary = new HashMap<>();
        LocalDate today = LocalDate.now();

        double totalProtein = 0;
        double totalCarbohydrate = 0;
        double totalFat = 0;
        int totalCalories = 0;

        List<MealRecord> meals = mealRecordMapper.selectList(
                new QueryWrapper<MealRecord>()
                        .eq("user_id", 1)
                        .eq("record_date", today)
        );

        for (MealRecord meal : meals) {
            Recipe recipe = recipeMapper.selectById(meal.getRecipeId());
            if (recipe != null) {
                double portion = meal.getPortion();
                totalProtein += (recipe.getProtein() != null ? recipe.getProtein() : 0) * portion;
                totalCarbohydrate += (recipe.getCarbohydrate() != null ? recipe.getCarbohydrate() : 0) * portion;
                totalFat += (recipe.getFat() != null ? recipe.getFat() : 0) * portion;
                totalCalories += (recipe.getCalories() != null ? recipe.getCalories() : 0) * portion;
            }
        }

        summary.put("protein", Math.round(totalProtein * 10) / 10.0);
        summary.put("carbohydrate", Math.round(totalCarbohydrate * 10) / 10.0);
        summary.put("fat", Math.round(totalFat * 10) / 10.0);
        summary.put("calories", totalCalories);

        return summary;
    }

    @Override
    public void saveNutritionRecord(NutritionRecord record) {
        record.setUserId(1);
        nutritionRecordMapper.insert(record);
    }

    @Override
    public void updateNutritionRecord(NutritionRecord record) {
        nutritionRecordMapper.updateById(record);
    }
}
