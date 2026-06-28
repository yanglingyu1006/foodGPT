package com.foodgpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.foodgpt.entity.UserPreference;
import com.foodgpt.mapper.UserPreferenceMapper;
import com.foodgpt.service.UserPreferenceService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserPreferenceServiceImpl implements UserPreferenceService {

    private final UserPreferenceMapper userPreferenceMapper;

    public UserPreferenceServiceImpl(UserPreferenceMapper userPreferenceMapper) {
        this.userPreferenceMapper = userPreferenceMapper;
    }

    @Override
    public List<UserPreference> getAllPreferences() {
        QueryWrapper<UserPreference> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", 1);
        return userPreferenceMapper.selectList(wrapper);
    }

    @Override
    public List<String> getFavorites() {
        List<String> favorites = new ArrayList<>();
        List<UserPreference> prefs = getAllPreferences();
        for (UserPreference pref : prefs) {
            if ("favorite".equals(pref.getPreferenceType()) && pref.getContent() != null) {
                favorites.addAll(Arrays.asList(pref.getContent().split(",")));
            }
        }
        return favorites.isEmpty() ? getDefaultFavorites() : favorites;
    }

    @Override
    public List<String> getAvoided() {
        List<String> avoided = new ArrayList<>();
        List<UserPreference> prefs = getAllPreferences();
        for (UserPreference pref : prefs) {
            if ("avoided".equals(pref.getPreferenceType()) && pref.getContent() != null) {
                avoided.addAll(Arrays.asList(pref.getContent().split(",")));
            }
        }
        return avoided.isEmpty() ? getDefaultAvoided() : avoided;
    }

    @Override
    public void savePreference(UserPreference preference) {
        preference.setUserId(1);
        userPreferenceMapper.insert(preference);
    }

    @Override
    public void deletePreference(Long id) {
        userPreferenceMapper.deleteById(id);
    }

    private List<String> getDefaultFavorites() {
        return Arrays.asList("鸡肉", "鸡蛋", "西红柿", "西兰花", "牛肉");
    }

    private List<String> getDefaultAvoided() {
        return Arrays.asList("辣椒", "大蒜");
    }
}