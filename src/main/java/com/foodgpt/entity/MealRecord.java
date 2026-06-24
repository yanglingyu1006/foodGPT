package com.foodgpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("meal_record")
public class MealRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer userId = 1;
    private Long recipeId;
    private String mealType;
    private Double portion;
    private String portionUnit = "份";
    private LocalDate recordDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
