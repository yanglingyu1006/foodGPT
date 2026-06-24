package com.foodgpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("recipe")
public class Recipe {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String category;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> ingredients;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> steps;
    private Double protein;
    private Double carbohydrate;
    private Double fat;
    private Integer calories;
    private String imageUrl;
    private String description;
    private String source;
    private LocalDateTime createTime;
}
