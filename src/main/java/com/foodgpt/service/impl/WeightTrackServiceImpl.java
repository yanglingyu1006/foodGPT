package com.foodgpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.foodgpt.entity.WeightRecord;
import com.foodgpt.mapper.WeightRecordMapper;
import com.foodgpt.service.WeightTrackService;

import java.time.LocalDate;
import java.util.List;

public class WeightTrackServiceImpl implements WeightTrackService {

    private final WeightRecordMapper weightRecordMapper;

    public WeightTrackServiceImpl(WeightRecordMapper weightRecordMapper) {
        this.weightRecordMapper = weightRecordMapper;
    }

    @Override
    public List<WeightRecord> getWeightRecords(LocalDate startDate, LocalDate endDate) {
        QueryWrapper<WeightRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", 1);
        wrapper.ge("record_date", startDate);
        wrapper.le("record_date", endDate);
        wrapper.orderByAsc("record_date");
        return weightRecordMapper.selectList(wrapper);
    }

    @Override
    public void saveWeightRecord(WeightRecord record) {
        record.setUserId(1);
        weightRecordMapper.insert(record);
    }

    @Override
    public void deleteWeightRecord(Long id) {
        weightRecordMapper.deleteById(id);
    }

    @Override
    public List<WeightRecord> getRecentRecords(int limit) {
        QueryWrapper<WeightRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", 1);
        wrapper.orderByDesc("record_date");
        wrapper.last("LIMIT " + limit);
        return weightRecordMapper.selectList(wrapper);
    }
}
