package com.foodgpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.foodgpt.entity.MealRecord;
import com.foodgpt.mapper.MealRecordMapper;
import com.foodgpt.service.MealRecordService;

import java.time.LocalDate;
import java.util.List;

public class MealRecordServiceImpl implements MealRecordService {

    private final MealRecordMapper mealRecordMapper;

    public MealRecordServiceImpl(MealRecordMapper mealRecordMapper) {
        this.mealRecordMapper = mealRecordMapper;
    }

    @Override
    public List<MealRecord> getMealRecords(LocalDate date, String mealType) {
        QueryWrapper<MealRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", 1);
        wrapper.eq("record_date", date);
        if (mealType != null && !mealType.isEmpty()) {
            wrapper.eq("meal_type", mealType);
        }
        return mealRecordMapper.selectList(wrapper);
    }

    @Override
    public void saveMealRecord(MealRecord record) {
        record.setUserId(1);
        mealRecordMapper.insert(record);
    }

    @Override
    public void deleteMealRecord(Long id) {
        mealRecordMapper.deleteById(id);
    }

    @Override
    public void updateMealRecord(MealRecord record) {
        mealRecordMapper.updateById(record);
    }

    @Override
    public List<MealRecord> getTodayRecords() {
        return getMealRecords(LocalDate.now(), null);
    }
}
