package com.foodgpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("health_goal")
public class HealthGoal {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer userId = 1;
    private String goalType;
    private Double targetWeight;
    private Integer targetCaloriesMin;
    private Integer targetCaloriesMax;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
