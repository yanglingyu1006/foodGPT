package com.foodgpt.config;

import com.foodgpt.util.JsonUtil;
import lombok.Data;

import java.io.File;

@Data
public class AppConfig {
    private static final String CONFIG_PATH = "config/app-config.json";
    private static AppConfig instance;

    private DatabaseConfig database = new DatabaseConfig();
    private ApiConfig api = new ApiConfig();
    private AppSettings app = new AppSettings();
    private UserSettings user = new UserSettings();

    @Data
    public static class AppSettings {
        private String theme = "light";
        private String language = "zh_CN";
        private boolean autoCheckUpdate = true;
        private int dataBackupInterval = 7;
    }

    @Data
    public static class UserSettings {
        private int id = 1;
        private String name = "";
    }

    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = loadConfig();
        }
        return instance;
    }

    private static AppConfig loadConfig() {
        File configFile = new File(CONFIG_PATH);
        if (configFile.exists()) {
            try {
                return JsonUtil.readFromFile(CONFIG_PATH, AppConfig.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        AppConfig defaultConfig = new AppConfig();
        saveConfig(defaultConfig);
        return defaultConfig;
    }

    public static void saveConfig(AppConfig config) {
        try {
            File configDir = new File("config");
            if (!configDir.exists()) {
                configDir.mkdirs();
            }
            JsonUtil.writeToFile(config, CONFIG_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        saveConfig(instance);
    }
}
