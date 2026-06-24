package com.foodgpt.service;

import com.foodgpt.entity.WeightRecord;

import java.time.LocalDate;
import java.util.List;

public interface WeightTrackService {
    List<WeightRecord> getWeightRecords(LocalDate startDate, LocalDate endDate);
    void saveWeightRecord(WeightRecord record);
    void deleteWeightRecord(Long id);
    List<WeightRecord> getRecentRecords(int limit);
}
