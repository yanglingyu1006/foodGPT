package com.foodgpt.service;

import com.foodgpt.entity.HealthGoal;

public interface HealthGoalService {
    HealthGoal getCurrentGoal();
    void saveGoal(HealthGoal goal);
    void updateGoal(HealthGoal goal);
}