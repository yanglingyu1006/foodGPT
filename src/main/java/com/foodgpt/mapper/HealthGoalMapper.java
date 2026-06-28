package com.foodgpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.foodgpt.entity.HealthGoal;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HealthGoalMapper extends BaseMapper<HealthGoal> {
}