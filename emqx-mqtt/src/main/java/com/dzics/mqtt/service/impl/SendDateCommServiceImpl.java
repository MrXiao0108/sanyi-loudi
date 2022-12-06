package com.dzics.mqtt.service.impl;

import com.dzics.common.model.entity.DzDataCollection;
import com.dzics.mqtt.model.entity.DzDataDevice;
import com.dzics.mqtt.service.DzDataCollectionService;
import com.dzics.mqtt.service.DzDataDeviceService;
import com.dzics.mqtt.service.SendDateCommService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SendDateCommServiceImpl implements SendDateCommService {
    @Autowired
    private DzDataDeviceService dzDataDeviceService;
    @Autowired
    private DzDataCollectionService dzDataCollectionService;


    @Override
    public List<DzDataDevice> getDataDeviceType(int type) {
        List<DzDataDevice> dzDataDevices = dzDataDeviceService.getByType(type);
        return dzDataDevices;
    }

    @Override
    public List<Map<String, Object>> getMapsBase(long l, DzDataDevice dzDataDevice, Long equipmentId, Map<Long, List<Map<String, Object>>> jc) {
        List<Map<String, Object>> mapBs = new ArrayList<>();
        String deviceId = dzDataDevice.getDeviceId().toString();
        Map<String, Object> mp1 = new HashMap<>();
        mp1.put("DeviceID", deviceId);
        mp1.put("ts", l);
        mapBs.add(mp1);
        String companyCode = dzDataDevice.getCompanyCode();
        Map<String, Object> mp2 = new HashMap<>();
        mp2.put("CompanyNo", companyCode);
        mp2.put("ts", l);
        mapBs.add(mp2);
        String factoryCode = dzDataDevice.getFactoryCode();
        Map<String, Object> mp3 = new HashMap<>();
        mp3.put("factoryNo", factoryCode);
        mp3.put("ts", l);
        mapBs.add(mp3);
        String assetsEncoding = dzDataDevice.getAssetsEncoding();
        Map<String, Object> mp4 = new HashMap<>();
        mp4.put("AssetNo", "");
        mp4.put("ts", l);
        mapBs.add(mp4);
        String deviceName = dzDataDevice.getDeviceName();
        Map<String, Object> mp5 = new HashMap<>();
        mp5.put("DeviceName", deviceName);
        mp5.put("ts", l);
        mapBs.add(mp5);
        String deviceTypeCode = dzDataDevice.getDeviceTypeCode();
        Map<String, Object> mp6 = new HashMap<>();
        mp6.put("DeviceType", deviceTypeCode);
        mp6.put("ts", l);
        mapBs.add(mp6);
        Map<String, Object> mp7 = new HashMap<>();
        mp7.put("CncType", dzDataDevice.getSystemProductName());
        mp7.put("ts", l);
        mapBs.add(mp7);
        String serNum = dzDataDevice.getSerNum();
        if (!StringUtils.isEmpty(serNum)) {
            HashMap<String, Object> map8 = new HashMap<>();
            map8.put("SerNum", serNum);
            map8.put("ts", l);
            mapBs.add(map8);
        }
        jc.put(equipmentId, mapBs);
        return mapBs;
    }

    @Override
    public Map<Long, DzDataCollection> getMapDeviceId(List<DzDataCollection> dataColl) {
        Map<Long, DzDataCollection> map = new HashMap<>();
        dataColl.forEach(dzDataCollection -> {
            map.put(dzDataCollection.getDeviceId(), dzDataCollection);
        });
        return map;
    }


}
