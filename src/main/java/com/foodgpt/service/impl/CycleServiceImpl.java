package com.foodgpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.foodgpt.entity.CycleRecord;
import com.foodgpt.enums.CyclePhase;
import com.foodgpt.mapper.CycleRecordMapper;
import com.foodgpt.service.CycleService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CycleServiceImpl implements CycleService {

    private final CycleRecordMapper cycleRecordMapper;

    public CycleServiceImpl(CycleRecordMapper cycleRecordMapper) {
        this.cycleRecordMapper = cycleRecordMapper;
    }

    @Override
    public CycleRecord getCurrentCycle() {
        QueryWrapper<CycleRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", 1);
        wrapper.isNull("end_date");
        wrapper.orderByDesc("start_date");
        wrapper.last("LIMIT 1"); 
        return cycleRecordMapper.selectOne(wrapper);
    }

    @Override
    public List<CycleRecord> getCycleHistory() {
        QueryWrapper<CycleRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", 1);
        wrapper.orderByDesc("start_date");
        return cycleRecordMapper.selectList(wrapper);
    }

    @Override
    public void saveCycleRecord(CycleRecord record) {
        record.setUserId(1);
        cycleRecordMapper.insert(record);
    }

    @Override
    public void updateCycleRecord(CycleRecord record) {
        cycleRecordMapper.updateById(record);
    }

    @Override
    public String getCurrentPhase() {
        CycleRecord current = getCurrentCycle();
        if (current == null) {
            return CyclePhase.FOLLICULAR.name();
        }

        LocalDate startDate = current.getStartDate();
        LocalDate today = LocalDate.now();
        long daysSinceStart = ChronoUnit.DAYS.between(startDate, today);

        int cycleLength = current.getCycleLength() != null ? current.getCycleLength() : 28;

        if (daysSinceStart <= 5) {
            return CyclePhase.MENSTRUATION.name();
        } else if (daysSinceStart <= 14) {
            return CyclePhase.FOLLICULAR.name();
        } else if (daysSinceStart <= 17) {
            return CyclePhase.OVULATION.name();
        } else if (daysSinceStart <= cycleLength) {
            return CyclePhase.LUTEAL.name();
        } else {
            return CyclePhase.MENSTRUATION.name();
        }
    }
}
