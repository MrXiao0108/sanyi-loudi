package com.dzics.mqtt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.entity.DzDataCollection;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-07-22
 */
public interface DzDataCollectionService extends IService<DzDataCollection> {


//    @Cacheable(cacheNames = {"dataCollectionService.cacheDeviceId"}, key = "#deviceId",unless = "#result == null")
List<DzDataCollection> getDeviceIdDzDataColl(  List<Long> deviceIds );

}
