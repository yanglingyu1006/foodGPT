package com.foodgpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.foodgpt.entity.HealthGoal;
import com.foodgpt.mapper.HealthGoalMapper;
import com.foodgpt.service.HealthGoalService;

public class HealthGoalServiceImpl implements HealthGoalService {

    private final HealthGoalMapper healthGoalMapper;

    public HealthGoalServiceImpl(HealthGoalMapper healthGoalMapper) {
        this.healthGoalMapper = healthGoalMapper;
    }

    @Override
    public HealthGoal getCurrentGoal() {
        QueryWrapper<HealthGoal> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", 1)
               .orderByDesc("update_time")
               .last("LIMIT 1");
        return healthGoalMapper.selectOne(wrapper);
    }

    @Override
    public void saveGoal(HealthGoal goal) {
        goal.setUserId(1);
        healthGoalMapper.insert(goal);
    }

    @Override
    public void updateGoal(HealthGoal goal) {
        healthGoalMapper.updateById(goal);
    }
}