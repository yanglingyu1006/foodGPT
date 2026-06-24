package com.foodgpt.service;

import com.foodgpt.entity.BodyData;

public interface BodyDataService {
    BodyData getBodyData();
    void saveBodyData(BodyData bodyData);
    void updateBodyData(BodyData bodyData);
}
