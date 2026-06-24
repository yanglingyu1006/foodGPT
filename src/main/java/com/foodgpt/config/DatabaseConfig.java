package com.foodgpt.config;

import lombok.Data;

@Data
public class DatabaseConfig {
    private String path = "data/foodgpt.db";
    private boolean autoCreate = true;
}
