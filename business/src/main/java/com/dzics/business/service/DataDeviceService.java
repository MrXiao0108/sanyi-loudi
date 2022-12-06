package com.dzics.business.service;

import com.dzics.business.model.vo.DeviceParms;
import com.dzics.common.model.request.datadevice.AddDataDeviceVo;
import com.dzics.common.model.request.datadevice.GetDataDeviceVo;
import com.dzics.common.model.response.Result;
import org.springframework.cache.annotation.CacheEvict;

public interface DataDeviceService {
    @CacheEvict(cacheNames = "dzDataDeviceService.getByType",key = "#dataDeviceVo.deviceType")
    Result add(AddDataDeviceVo dataDeviceVo);

    Result list(GetDataDeviceVo dataDeviceVo);
    @CacheEvict(cacheNames = "dzDataDeviceService.getByType",key = "#dataDeviceVo.deviceType")
    Result update(AddDataDeviceVo dataDeviceVo);
    @CacheEvict(cacheNames = "dzDataDeviceService.getByType",key = "#dataDeviceVo.deviceType")
    Result del(AddDataDeviceVo dataDeviceVo);

    Result getByKey(Long deviceKey);

    Result getDzEquipment(DeviceParms deviceParms);

    Result getByKeyDeviceAll(DeviceParms deviceParms);

    Integer getById(Long deviceKey);

}
