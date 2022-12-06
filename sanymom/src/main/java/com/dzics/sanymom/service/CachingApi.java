package com.dzics.sanymom.service;

import com.dzics.common.model.entity.DzProductionLine;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

public interface CachingApi {
    @Cacheable(cacheNames = "cachingApi.getOrderIdAndLineId", key = "'runOrder'", unless = "#result == null")
    DzProductionLine getOrderIdAndLineId();

    @CachePut(cacheNames = "cachingApi.getOrderIdAndLineId", key = "'runOrder'", unless = "#result == null")
    DzProductionLine updateOrderIdAndLineId();

    @Cacheable(cacheNames = "cachingApi.getMomRunModel", key = "'runMomModel'", unless = "#result == null")
    String getMomRunModel();

    @CachePut(cacheNames = "cachingApi.getMomRunModel", key = "'runMomModel'", unless = "#result == null")
    String updateAgvRunModel(Integer rm,DzProductionLine line);

    String updateModelWhere(Integer rm,DzProductionLine line);
}
