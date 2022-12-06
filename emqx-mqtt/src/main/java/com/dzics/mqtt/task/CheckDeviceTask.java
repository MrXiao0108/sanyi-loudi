package com.dzics.mqtt.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.model.entity.DzDataCollection;
import com.dzics.mqtt.commom.IotDeviceType;
import com.dzics.mqtt.commom.doccmd.JcCmd;
import com.dzics.mqtt.model.entity.DzDataDevice;
import com.dzics.mqtt.service.MqttService;
import com.dzics.mqtt.service.DzDataCollectionService;
import com.dzics.mqtt.service.SendDateCommService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CheckDeviceTask {
    @Autowired
    private MqttService mqttService;

    @Autowired
    private DzDataCollectionService dzDataCollectionService;

    @Autowired
    private SendDateCommService sendDateCommService;

    /**
     * 检测设备采集数据发送
     */
    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    public void JcSb() {
//      获取所有数控设备
        long l = System.currentTimeMillis();
        List<DzDataDevice> dzDataDevices = sendDateCommService.getDataDeviceType(IotDeviceType.JC);
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
                        log.warn("上报检测设备采集数据错误,根据设备equipmentId:{} 查询值:{}", equipmentId, dzDataCollections);
                        continue;
                    }
                    Map<Long, List<Map<String, Object>>> jc = MapStaticDeviceBase.jc;
                    List<Map<String, Object>> mapBs = jc.get(equipmentId);
                    if (mapBs != null) {
                        for (Map<String, Object> mapB : mapBs) {
                            mapB.put("ts", l);
                        }
                    } else {
                        mapBs = sendDateCommService.getMapsBase(l, dzDataDevice, equipmentId, jc);
                    }
                    List<Map<String, Object>> mpSend = new ArrayList<>();
                    mpSend.addAll(mapBs);
                    //                状态信息
                    Map<String, Object> mpl = JSONObject.parseObject(JSON.toJSONString(dzDataCollections));
                    String status = (String) mpl.get(JcCmd.Status);
                    String alarm = (String) mpl.get(JcCmd.Alarm);
                    String connState = (String) mpl.get(JcCmd.connState);
                    int workStateDef = 4;
                    if (!StringUtils.isEmpty(status)) {
                        long updateTime = new Date((Long) mpl.get("updateTime")).getTime();
                        if ((updateTime + (10 * 1000)) > l) {
                            if ("1".equals(connState)) {
//                            连机
                                if ("1".equals(alarm)) {
//                                报警，设置故障
                                    workStateDef = 3;
                                } else {
                                    if ("3".equals(status)) {
                                        workStateDef = 1;
                                    } else {
                                        workStateDef = 2;
                                    }
                                }
                            }
                        }
                    }

                    Map<String, Object> mps7 = new HashMap<>();
                    mps7.put("Status", workStateDef);
                    mps7.put("ts", l);
                    mpSend.add(mps7);
                    if (4 != workStateDef) {
                        Map<String, Object> mp8 = new HashMap<>();
                        if (alarm == null) {
                            log.warn("mpl:{}", mpl);
                            log.warn("dzDataCollections:{}", dzDataCollections);
                            log.warn("检测设备告警状态不存在：{}", alarm);
                        } else {
                            mp8.put("Alarm", Integer.valueOf(alarm));
                            mp8.put("ts", l);
                            mpSend.add(mp8);
                        }
                    }
                    mqttService.sendRealTimeData(mpSend, dzDataDevice.getDeviceType(), dzDataDevice.getDeviceId(), dzDataDevice.getAssetsEncoding());
//                    log.info("发送检测设备实时数据：{}", mpSend);
                }
            } else {
                log.warn("检测设备数据不存在:IotDeviceType: {}", IotDeviceType.JC);
            }

        } else {
            log.warn("检测设备不存在:IotDeviceType: {}", IotDeviceType.JC);
        }

    }

}
