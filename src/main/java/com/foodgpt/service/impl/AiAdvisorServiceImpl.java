package com.foodgpt.service.impl;

import com.foodgpt.config.AppConfig;
import com.foodgpt.util.JsonUtil;
import com.foodgpt.util.OkHttpUtil;
import com.foodgpt.service.AiAdvisorService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AiAdvisorServiceImpl implements AiAdvisorService {

    private final AppConfig appConfig;

    public AiAdvisorServiceImpl(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public String getAdvice(String userMessage) {
        String apiKey = appConfig.getApi().getDeepseek().getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            return "请先在设置中配置DeepSeek API密钥";
        }

        String systemPrompt = "你是一位专业的女性健康饮食顾问。请根据用户的身体数据、生理周期阶段、健康目标和饮食偏好，提供个性化的饮食建议。";

        Map<String, Object> request = new HashMap<>();
        request.put("model", appConfig.getApi().getDeepseek().getModel());
        request.put("temperature", appConfig.getApi().getDeepseek().getTemperature());
        request.put("max_tokens", appConfig.getApi().getDeepseek().getMaxTokens());
        request.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userMessage)
        ));

        try {
            String json = JsonUtil.toJson(request);
            String response = OkHttpUtil.postJson(
                    appConfig.getApi().getDeepseek().getBaseUrl(),
                    json,
                    apiKey
            );
            return parseResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
            return "API调用失败: " + e.getMessage();
        }
    }

    @Override
    public String getDietaryAdvice(String bodyData, String cyclePhase, String healthGoal) {
        String prompt = String.format(
                "用户身体数据：%s\n当前周期阶段：%s\n健康目标：%s\n请提供个性化的饮食建议。",
                bodyData, cyclePhase, healthGoal
        );
        return getAdvice(prompt);
    }

    private String parseResponse(String response) {
        try {
            Map<String, Object> result = JsonUtil.fromJson(response, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) result.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
