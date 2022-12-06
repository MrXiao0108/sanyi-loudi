package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.model.entity.DzDataCollection;
import com.dzics.data.acquisition.service.AccDataCollectionService;
import com.dzics.common.service.DzDataCollectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class AccDataCollectionServiceImpl implements AccDataCollectionService {
    @Autowired
    private DzDataCollectionService dzDataCollectionService;


    @Override
    public DzDataCollection cacheDeviceId(Long deviceId) {
        QueryWrapper<DzDataCollection> wp = new QueryWrapper<>();
        wp.eq("device_id", deviceId);
        DzDataCollection one = dzDataCollectionService.getOne(wp);
        return one;
    }

    @Override
    public DzDataCollection updateDeviceId(DzDataCollection dzDataCollection) {
        QueryWrapper<DzDataCollection> wp = new QueryWrapper<>();
        wp.eq("device_id", dzDataCollection.getDeviceId());
        boolean update = dzDataCollectionService.update(dzDataCollection, wp);
        if (update){
            return dzDataCollection;
        }else {
            return null;
        }
    }

    @Override
    public boolean instert(DzDataCollection dzDataCollection) {
        return dzDataCollectionService.save(dzDataCollection);
    }
}
