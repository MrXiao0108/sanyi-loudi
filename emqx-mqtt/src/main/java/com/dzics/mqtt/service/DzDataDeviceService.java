package com.dzics.mqtt.service;

import com.dzics.mqtt.commom.IotDeviceType;
import com.dzics.mqtt.model.entity.DzDataDevice;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * <p>
 * 数据采集设备表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-07-22
 */
public interface DzDataDeviceService extends IService<DzDataDevice> {

    @Cacheable(cacheNames = "dzDataDeviceService.getByType",key = "#sk")
    List<DzDataDevice> getByType(int sk);
}
