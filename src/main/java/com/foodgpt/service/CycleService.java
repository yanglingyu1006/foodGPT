package com.foodgpt.service;

import com.foodgpt.entity.CycleRecord;

import java.time.LocalDate;
import java.util.List;

public interface CycleService {
    CycleRecord getCurrentCycle();
    List<CycleRecord> getCycleHistory();
    void saveCycleRecord(CycleRecord record);
    void updateCycleRecord(CycleRecord record);
    String getCurrentPhase();
}
