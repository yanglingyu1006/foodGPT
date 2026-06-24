package com.foodgpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("cycle_record")
public class CycleRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer userId = 1;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer cycleLength = 28;
    private String phase;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
