package com.dzics.mqtt.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.model.entity.DzDataCollection;
import com.dzics.mqtt.commom.doccmd.*;
import com.dzics.mqtt.commom.IotDeviceType;
import com.dzics.mqtt.model.entity.DzDataDevice;
import com.dzics.mqtt.service.MqttService;
import com.dzics.mqtt.service.DzDataCollectionService;
import com.dzics.mqtt.service.DzDataDeviceService;
import com.dzics.mqtt.service.SendDateCommService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 又定时任务向 iot emqx 发送 采集数据
 *
 * @author ZhangChengJun
 * Date 2021/7/5.
 * @since
 */
@Component
@Slf4j
public class WorkReportTask {
    @Autowired
    private MqttService mqttService;

    @Autowired
    private DzDataDeviceService dzDataDeviceService;
    @Autowired
    private DzDataCollectionService dzDataCollectionService;

    @Autowired
    private SendDateCommService sendDateCommService;

    /**
     * 焊接机器人数据发送
     */
//    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    public void hjRob() {
//      获取所有数控设备
        long l = System.currentTimeMillis();
        List<DzDataDevice> dzDataDevices = sendDateCommService.getDataDeviceType(IotDeviceType.HJ);
        if (CollectionUtils.isNotEmpty(dzDataDevices)) {
            List<Long> deviceIds = dzDataDevices.stream().map(s -> s.getEquipmentId()).collect(Collectors.toList());
            List<DzDataCollection> dataColl = dzDataCollectionService.getDeviceIdDzDataColl(deviceIds);
            if (CollectionUtils.isNotEmpty(dataColl)) {
                Map<Long, DzDataCollection> mapDeviceId = sendDateCommService.getMapDeviceId(dataColl);
                for (DzDataDevice dzDataDevice : dzDataDevices) {
                    Long equipmentId = dzDataDevice.getEquipmentId();
                    //      根据设备ID 加载 最新值数据
                    DzDataCollection dzDataCollections = mapDeviceId.get(equipmentId);
                    if (dzDataCollections == null) {
                        log.warn("上报焊接机器人采集数据错误,根据设备equipmentId:{} 查询值:{}", equipmentId, dzDataCollections);
                        continue;
                    }
                    Map<Long, List<Map<String, Object>>> jc = MapStaticDeviceBase.jc;
                    List<Map<String, Object>> mapBs = jc.get(equipmentId);

                    if (mapBs != null) {
                        for (Map<String, Object> mapB : mapBs) {
                            mapB.put("ts", l);
                        }
                    } else {
                        mapBs = getMapsBaseHj(l, dzDataDevice, equipmentId, jc);
                    }
                    List<Map<String, Object>> mpSend = new ArrayList<>();
                    mpSend.addAll(mapBs);
//                采集信息
                    Map<String, Object> mpl = JSONObject.parseObject(JSON.toJSONString(dzDataCollections));
//                状态信息
                    Map<String, Object> mps1 = new HashMap<>();
                    mps1.put("Status", mpl.get(HjCmd.Status));
                    mps1.put("ts", mpl.get(JcCmd.bas + HjCmd.Status));
                    mapBs.add(mps1);

/*
            mps.put("Alarm", mpl.get(HjCmd.Alarm));
            mps.put("Mode", mpl.get(HjCmd.Mode));
//                角度坐标
            mps.put("J1", mpl.get(HjCmd.J1));
            mps.put("J2", mpl.get(HjCmd.J2));
            mps.put("J3", mpl.get(HjCmd.J3));
            mps.put("J4", mpl.get(HjCmd.J4));
            mps.put("J5", mpl.get(HjCmd.J5));
            mps.put("J6", mpl.get(HjCmd.J6));
//                用户坐标（用户当前激活的自定义坐标系）
            mps.put("UX", mpl.get(HjCmd.UX));
            mps.put("UY", mpl.get(HjCmd.UY));
            mps.put("UZ", mpl.get(HjCmd.UZ));
            mps.put("UW", mpl.get(HjCmd.UW));
            mps.put("UP", mpl.get(HjCmd.UP));
            mps.put("UR", mpl.get(HjCmd.UR));
//                世界坐标 （机器 人基坐 标系）
            mps.put("WX", mpl.get(HjCmd.WX));
            mps.put("WY", mpl.get(HjCmd.WY));
            mps.put("WZ", mpl.get(HjCmd.WZ));
            mps.put("WW", mpl.get(HjCmd.WW));
            mps.put("WP", mpl.get(HjCmd.WP));
            mps.put("WR", mpl.get(HjCmd.WR));
//                程序信息
            mps.put("NcStatus", mpl.get(HjCmd.NcStatus));
            mps.put("MainPgm", mpl.get(HjCmd.MainPgm));
            mps.put("CurPgm", mpl.get(HjCmd.CurPgm));
            mps.put("CycSec", mpl.get(HjCmd.CycSec));
            mps.put("CurSeq", mpl.get(HjCmd.CurSeq));
//                报警信息
            mps.put("AlarmMsg", mpl.get(HjCmd.AlarmMsg));
//                焊接信息
            mps.put("WireSpeed", mpl.get(HjCmd.WireSpeed));
            mps.put("Current", mpl.get(HjCmd.Current));
            mps.put("Voltage", mpl.get(HjCmd.Voltage));
            mps.put("SearchSignal", mpl.get(HjCmd.SearchSignal));
            mps.put("ClearSignal", mpl.get(HjCmd.ClearSignal));
            mps.put("TurnPosEnb", mpl.get(HjCmd.TurnPosEnb));
            mps.put("WeldDetect", mpl.get(HjCmd.WeldDetect));
            mqttService.sendRealTimeData(Arrays.asList(mps));*/
                }
            } else {
                log.warn("焊接机器人数据不存在:deviceIds: {}", deviceIds);
            }

        } else {
            log.warn("焊接机器人设备不存在:IotDeviceType: {}", IotDeviceType.HJ);
        }

    }


    private List<Map<String, Object>> getMapsBaseHj(long l, DzDataDevice dzDataDevice, Long equipmentId, Map<Long, List<Map<String, Object>>> jc) {
//                TODO 基础信息不全 缺失
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
        mp4.put("AssetNo", assetsEncoding);
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
        mp7.put("CncType", "");
        mp7.put("ts", l);
        mapBs.add(mp7);

        Map<String, Object> mp8 = new HashMap<>();
        mp8.put("SerNum", "");
        mp8.put("ts", l);
        mapBs.add(mp8);

        Map<String, Object> mp9 = new HashMap<>();
        mp9.put("NcVer", "");
        mp9.put("ts", l);
        mapBs.add(mp9);

        Map<String, Object> mp10 = new HashMap<>();
        mp10.put("WeldType", "");
        mp10.put("ts", l);
        mapBs.add(mp10);

        jc.put(equipmentId, mapBs);
        return mapBs;
    }


}

