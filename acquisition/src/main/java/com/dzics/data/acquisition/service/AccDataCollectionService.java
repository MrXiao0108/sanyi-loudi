package com.dzics.data.acquisition.service;

import com.dzics.common.model.entity.DzDataCollection;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

/**
 * 采集数据存储 跟新
 */
public interface AccDataCollectionService {


    @Cacheable(cacheNames = {"dataCollectionService.cacheDeviceId"}, key = "#deviceId", unless = "#result == null")
    DzDataCollection cacheDeviceId(Long deviceId);

    @CachePut(cacheNames = {"dataCollectionService.cacheDeviceId"},key = "#dzDataCollection.deviceId", unless = "#result == null")
    DzDataCollection updateDeviceId(DzDataCollection dzDataCollection);

    boolean instert(DzDataCollection dzDataCollection);

}
