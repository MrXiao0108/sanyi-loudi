package com.dzics.mqtt.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.model.entity.DzDataCollection;
import com.dzics.mqtt.commom.IotDeviceType;
import com.dzics.mqtt.commom.doccmd.JzjCmd;
import com.dzics.mqtt.model.entity.DzDataDevice;
import com.dzics.mqtt.service.DzDataCollectionService;
import com.dzics.mqtt.service.MqttService;
import com.dzics.mqtt.service.SendDateCommService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Classname HeatHandleTask
 * @Description 校直机上传IOT
 * @Date 2022/4/26 11:54
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class JzjHandleTask {

    @Autowired
    private MqttService mqttService;

    @Autowired
    private DzDataCollectionService dzDataCollectionService;

    @Autowired
    private SendDateCommService sendDateCommService;


    /**
     * 矫直机数据发送
     *
     * @return
     */
    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    public void numericalControlData() {
        long timeMillis = System.currentTimeMillis();
        List<DzDataDevice> dzDataDevices = sendDateCommService.getDataDeviceType(IotDeviceType.JZJ);
        if (CollectionUtils.isNotEmpty(dzDataDevices)) {
            List<Long> deviceIds = dzDataDevices.stream().map(DzDataDevice::getEquipmentId).collect(Collectors.toList());
            List<DzDataCollection> dataColl = dzDataCollectionService.getDeviceIdDzDataColl(deviceIds);
            if (CollectionUtils.isNotEmpty(dataColl)) {
                Map<Long, DzDataCollection> mapDeviceId = sendDateCommService.getMapDeviceId(dataColl);
                for (DzDataDevice dzDataDevice : dzDataDevices) {
                    Long equipmentId = dzDataDevice.getEquipmentId();
                    Long deviceId = dzDataDevice.getDeviceId();
                    DzDataCollection dzDataCollections = mapDeviceId.get(equipmentId);
                    if (dzDataCollections == null) {
                        log.warn("上报数控设备采集数据错误,根据设备equipmentId:{} 查询值:{}", equipmentId, dzDataCollections);
                        continue;
                    }
                    Map<Long, List<Map<String, Object>>> jc = MapStaticDeviceBase.jc;
                    List<Map<String, Object>> mapBs = jc.get(equipmentId);
                    if (mapBs != null) {
                        for (Map<String, Object> mapB : mapBs) {
                            mapB.put("ts", timeMillis);
                        }
                    } else {
                        mapBs = sendDateCommService.getMapsBase(timeMillis, dzDataDevice, equipmentId, jc);
                    }
                    List<Map<String, Object>> mpSend = new ArrayList<>(mapBs);
//            检测数据
                    Map<String, Object> mpl = JSONObject.parseObject(JSON.toJSONString(dzDataCollections));
//                    状态信息
                    String status = (String) mpl.get(JzjCmd.Status);
                    String alram = (String) mpl.get(JzjCmd.Alarm);
                    String connState = (String) mpl.get(JzjCmd.connState);
                    int workStateDef = 4;
                    if (!org.apache.commons.lang3.StringUtils.isEmpty(status)) {
                        long updateTime = new Date((Long) mpl.get("updateTime")).getTime();
                        if ((updateTime + (10 * 1000)) > timeMillis) {
                            if ("1".equals(connState)) {
//                            连机
                                if ("1".equals(alram)) {
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


                    Map<String, Object> mps1 = new HashMap<>();
                    mps1.put("Status", workStateDef);
                    mps1.put("ts", timeMillis);
                    mpSend.add(mps1);
                    if (4 != workStateDef) {

                        if (!org.apache.commons.lang3.StringUtils.isEmpty(alram)) {
                            Map<String, Object> mps2 = new HashMap<>();
                            mps2.put("Alarm", Integer.valueOf(alram));
                            mps2.put("ts", timeMillis);
                            mpSend.add(mps2);
                        }

                        String realLength = (String) mpl.get(JzjCmd.RealLength);
                        if (!org.apache.commons.lang3.StringUtils.isEmpty(realLength)) {
                            String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(realLength, "[]");
                            if (!org.apache.commons.lang3.StringUtils.isEmpty(deviceItemValue)) {
                                BigDecimal bigDecimal = new BigDecimal(deviceItemValue);
                                Map<String, Object> mps190 = new HashMap<>();
                                mps190.put("RealLength", bigDecimal.floatValue());
                                mps190.put("ts", timeMillis);
                                mpSend.add(mps190);
                            }
                        }


                        String realAngle = (String) mpl.get(JzjCmd.RealAngle);
                        if (!org.apache.commons.lang3.StringUtils.isEmpty(realAngle)) {
                            String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(realAngle, "[]");
                            if (!org.apache.commons.lang3.StringUtils.isEmpty(deviceItemValue)) {
                                BigDecimal bigDecimal = new BigDecimal(deviceItemValue);
                                Map<String, Object> mps190 = new HashMap<>();
                                mps190.put("RealAngle", bigDecimal.floatValue());
                                mps190.put("ts", timeMillis);
                                mpSend.add(mps190);
                            }
                        }
                    }

//                    存储本次发送的部分数据
                    mqttService.sendRealTimeData(mpSend, dzDataDevice.getDeviceType(), dzDataDevice.getDeviceId(), dzDataDevice.getAssetsEncoding());
//                    log.info("发送校直机实时数据：{}", mpSend);
                }
            } else {
                log.warn("数控设备数据不存在:deviceIds: {}", deviceIds);
            }

        } else {
            log.warn("数控设备不存在:IotDeviceType: {}", IotDeviceType.SK);
        }

    }
}
