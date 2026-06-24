package com.foodgpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.foodgpt.entity.NutritionRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NutritionRecordMapper extends BaseMapper<NutritionRecord> {
}
