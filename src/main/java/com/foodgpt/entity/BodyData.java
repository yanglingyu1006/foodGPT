package com.foodgpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("body_data")
public class BodyData {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer userId = 1;
    private Double height;
    private Double weight;
    private Integer age;
    private String activityLevel;
    private Double bmi;
    private Double bmr;
    private Integer recommendedCaloriesMin;
    private Integer recommendedCaloriesMax;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
