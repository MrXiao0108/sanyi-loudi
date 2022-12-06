package com.dzics.mqtt.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.model.entity.DzDataCollection;
import com.dzics.mqtt.commom.IotDeviceType;
import com.dzics.mqtt.commom.doccmd.QxCmd;
import com.dzics.mqtt.model.entity.DzDataDevice;
import com.dzics.mqtt.service.DzDataCollectionService;
import com.dzics.mqtt.service.MqttService;
import com.dzics.mqtt.service.SendDateCommService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CleanMachineTask {
    @Autowired
    private MqttService mqttService;

    @Autowired
    private DzDataCollectionService dzDataCollectionService;

    @Autowired
    private SendDateCommService sendDateCommService;

    /**
     * 清洗机数据发送
     */
    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    public void qxCNC() {
//      获取所有数控设备
        long l = System.currentTimeMillis();
        List<DzDataDevice> dzDataDevices = sendDateCommService.getDataDeviceType(IotDeviceType.QX);
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
                        log.warn("上报清洗机采集数据错误,根据设备equipmentId:{} 查询值:{}", equipmentId, dzDataCollections);
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
                    Map<String, Object> mps7 = new HashMap<>();
                    String connState = (String) mpl.get(QxCmd.connState);
                    String status = (String) mpl.get(QxCmd.Status);
                    String stopState = (String) mpl.get(QxCmd.stopState);
                    String alarm = (String) mpl.get(QxCmd.Alarm);
                    int workStateDef = 2;
                    //  1作业 2：待机 3：故障 4：关机
                    if (!StringUtils.isEmpty(status)) {
                        long updateTime = new Date((long)mpl.get("updateTime")).getTime();
                        if ((updateTime + (10 * 1000)) > l) {
                            //连机
                            if ("1".equals(connState)) {
                                //报警，设置故障
                                if ("1".equals(alarm) || "1".equals(stopState) || "4".equals(status)) {
                                    workStateDef = 3;
                                }else{
                                    if ("3".equals(status)) {
                                        workStateDef = 1;
                                    }
                                    if ("0".equals(status) || "1".equals(status) ||"2".equals(status)) {
                                        workStateDef = 2;
                                    }
                                }
                            }else{
                                workStateDef = 4;
                            }
                        }
                    }
                    //判断当前清洗机的状态是否为告警
                    if(workStateDef == 3){
                        //判断是不是内部清洗机
                        if(dzDataDevice.getIsInside() != null){
                            //状态变更为待料状态
                            workStateDef = 2;
                            //告警重置
                            alarm = String.valueOf(0);
                        }
                    }
                    mps7.put("Status", workStateDef);
                    mps7.put("ts", l);
                    mpSend.add(mps7);
                    if (4 != workStateDef) {
                        Map<String, Object> mps8 = new HashMap<>();
                        if (StringUtils.isEmpty(alarm)) {
                            log.warn("mpl:{}", mpl);
                            log.warn("dzDataCollections:{}", dzDataCollections);
                            log.warn("清洗机告警状态不存在: {} ", alarm);
                        } else {
                            mps8.put("Alarm", Integer.valueOf(alarm));
                            mps8.put("ts", l);
                            mpSend.add(mps8);
                        }
//                水洗清洗参数
                        String cleanTime = (String) mpl.get(QxCmd.CleanTime);
                        if (StringUtils.isEmpty(cleanTime)) {
                            log.warn("mpl:{}", mpl);
                            log.warn("dzDataCollections:{}", dzDataCollections);
                            log.warn("清洗机清洗时长不存在: {} ", cleanTime);
                        } else {
                            BigDecimal bigDecimal = new BigDecimal(cleanTime);
                            Map<String, Object> mps9 = new HashMap<>();
                            mps9.put("CleanTIme", bigDecimal.intValue());
                            mps9.put("ts", l);
                            mpSend.add(mps9);
                        }
                    }
                    mqttService.sendRealTimeData(mpSend, dzDataDevice.getDeviceType(), dzDataDevice.getDeviceId(), dzDataDevice.getAssetsEncoding());
                    log.info("发送清洗机实时数据：{}", mpSend);
                    log.info("----------Test----------当前设备ID：{}，当前数据最后修改时间：{}，代码当前时间：{}",dzDataDevice.getEquipmentId(),dzDataDevice.getUpdateTime(),l);
                }
            } else {
                log.warn("清洗机设备检测数据不存在:deviceIds: {}", deviceIds);
            }
        } else {
            log.warn("清洗机设备不存在:IotDeviceType: {}", IotDeviceType.QX);
        }

    }

}
