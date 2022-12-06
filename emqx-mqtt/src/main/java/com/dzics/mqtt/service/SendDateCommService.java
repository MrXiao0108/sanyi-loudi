package com.dzics.mqtt.service;

import com.dzics.common.model.entity.DzDataCollection;
import com.dzics.mqtt.model.entity.DzDataDevice;

import java.util.List;
import java.util.Map;

public interface SendDateCommService {
     List<DzDataDevice> getDataDeviceType(int type);
     List<Map<String, Object>> getMapsBase(long l, DzDataDevice dzDataDevice, Long equipmentId, Map<Long, List<Map<String, Object>>> jc);

     Map<Long, DzDataCollection> getMapDeviceId(List<DzDataCollection> dataColl);

}
