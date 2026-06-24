package com.foodgpt.service;

import com.foodgpt.entity.NutritionRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface NutritionService {
    NutritionRecord getDailyNutrition(LocalDate date);
    List<NutritionRecord> getWeeklyNutrition(LocalDate startDate);
    Map<String, Object> getTodaySummary();
    void saveNutritionRecord(NutritionRecord record);
    void updateNutritionRecord(NutritionRecord record);
}
