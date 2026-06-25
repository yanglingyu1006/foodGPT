package com.foodgpt.service;

import com.foodgpt.entity.BodyData;

public interface BodyDataService {
    BodyData getLatestBodyData();
    void saveBodyData(BodyData bodyData);
    void updateBodyData(BodyData bodyData);
}
