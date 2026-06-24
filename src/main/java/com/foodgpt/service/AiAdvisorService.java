package com.foodgpt.service;

public interface AiAdvisorService {
    String getAdvice(String userMessage);
    String getDietaryAdvice(String bodyData, String cyclePhase, String healthGoal);
}
