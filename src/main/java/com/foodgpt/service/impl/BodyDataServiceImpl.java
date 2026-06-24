package com.foodgpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.foodgpt.entity.BodyData;
import com.foodgpt.mapper.BodyDataMapper;
import com.foodgpt.service.BodyDataService;

public class BodyDataServiceImpl implements BodyDataService {

    private final BodyDataMapper bodyDataMapper;

    public BodyDataServiceImpl(BodyDataMapper bodyDataMapper) {
        this.bodyDataMapper = bodyDataMapper;
    }

    @Override
    public BodyData getBodyData() {
        QueryWrapper<BodyData> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", 1);
        return bodyDataMapper.selectOne(wrapper);
    }

    @Override
    public void saveBodyData(BodyData bodyData) {
        bodyData.setUserId(1);
        bodyDataMapper.insert(bodyData);
    }

    @Override
    public void updateBodyData(BodyData bodyData) {
        bodyDataMapper.updateById(bodyData);
    }
}
