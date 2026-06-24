package com.foodgpt.service;

import com.foodgpt.entity.MealRecord;

import java.time.LocalDate;
import java.util.List;

public interface MealRecordService {
    List<MealRecord> getMealRecords(LocalDate date, String mealType);
    void saveMealRecord(MealRecord record);
    void deleteMealRecord(Long id);
    void updateMealRecord(MealRecord record);
    List<MealRecord> getTodayRecords();
}
