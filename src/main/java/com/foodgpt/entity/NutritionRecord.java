package com.foodgpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("nutrition_record")
public class NutritionRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer userId = 1;
    private LocalDate recordDate;
    private String mealType;
    private Double protein;
    private Double carbohydrate;
    private Double fat;
    private Integer calories;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
