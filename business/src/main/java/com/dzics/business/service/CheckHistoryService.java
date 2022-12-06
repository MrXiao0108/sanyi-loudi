package com.dzics.business.service;

import com.dzics.common.model.entity.DzCheckHistoryItem;
import com.dzics.common.model.request.devicecheck.DeviceCheckVo;
import com.dzics.common.model.request.devicecheck.GetDeviceCheckVo;
import com.dzics.common.model.response.Result;

import java.util.List;

public interface CheckHistoryService {
    Result add(String sub, DeviceCheckVo deviceCheckVo);

    Result list(GetDeviceCheckVo getDeviceCheckVo);

    Result getById(String checkHistoryId);

    Result put(String sub, List<DzCheckHistoryItem> list);

    Result del(String sub, String checkHistoryId);
}
