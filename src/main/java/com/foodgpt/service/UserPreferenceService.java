package com.foodgpt.service;

import com.foodgpt.entity.UserPreference;

import java.util.List;

public interface UserPreferenceService {
    List<UserPreference> getAllPreferences();
    List<String> getFavorites();
    List<String> getAvoided();
    void savePreference(UserPreference preference);
    void deletePreference(Long id);
}