package com.dzics.mqtt.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.model.entity.DzDataCollection;
import com.dzics.mqtt.commom.IotDeviceType;
import com.dzics.mqtt.commom.doccmd.RclCmd;
import com.dzics.mqtt.model.entity.DzDataDevice;
import com.dzics.mqtt.service.DzDataCollectionService;
import com.dzics.mqtt.service.MqttService;
import com.dzics.mqtt.service.SendDateCommService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Classname HeatHandleTask
 * @Description 热处理上传IOT
 * @Date 2022/4/26 11:54
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class HeatHandleTask {

    @Autowired
    private MqttService mqttService;

    @Autowired
    private DzDataCollectionService dzDataCollectionService;

    @Autowired
    private SendDateCommService sendDateCommService;


    /**
     * 热处理 淬火机  数据发送
     *
     * @return
     */
    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    public void numericalControlData() {
        long timeMillis = System.currentTimeMillis();
        List<DzDataDevice> dzDataDevices = sendDateCommService.getDataDeviceType(IotDeviceType.RCL);
        if (CollectionUtils.isNotEmpty(dzDataDevices)) {
            List<Long> deviceIds = dzDataDevices.stream().map(DzDataDevice::getEquipmentId).collect(Collectors.toList());
            List<DzDataCollection> dataColl = dzDataCollectionService.getDeviceIdDzDataColl(deviceIds);
            if (CollectionUtils.isNotEmpty(dataColl)) {
                Map<Long, DzDataCollection> mapDeviceId = sendDateCommService.getMapDeviceId(dataColl);
                for (DzDataDevice dzDataDevice : dzDataDevices) {
                    Long equipmentId = dzDataDevice.getEquipmentId();
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
                    String status = (String) mpl.get(RclCmd.Status);
                    String alram = (String) mpl.get(RclCmd.Alarm);
                    String connState = (String) mpl.get(RclCmd.connState);
                    int workStateDef = 4;
                    if (!StringUtils.isEmpty(status)) {
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
                    //判断当前淬火机的状态是否为告警
                    if(workStateDef == 3){
                        //状态变更为待料状态
                        workStateDef = 2;
                        //告警重置
                        alram = String.valueOf(0);
                    }
                    Map<String, Object> mps1 = new HashMap<>();
                    mps1.put("Status", workStateDef);
                    mps1.put("ts", timeMillis);
                    mpSend.add(mps1);

                    if (4 != workStateDef) {
                        if (!StringUtils.isEmpty(alram)) {
                            Map<String, Object> mps2 = new HashMap<>();
                            mps2.put("Alarm", Integer.valueOf(alram));
                            mps2.put("ts", timeMillis);
                            mpSend.add(mps2);
                        }

                        String inputVoltage = (String) mpl.get(RclCmd.InputVoltage);
                        if (!StringUtils.isEmpty(inputVoltage)) {
                            String deviceItemValue = StringUtils.strip(inputVoltage, "[]");
                            if (!StringUtils.isEmpty(deviceItemValue)) {
                                BigDecimal bigDecimal = new BigDecimal(deviceItemValue);
                                Map<String, Object> mps190 = new HashMap<>();
                                mps190.put("InputVoltage", bigDecimal.floatValue());
                                mps190.put("ts", timeMillis);
                                mpSend.add(mps190);
                            }
                        }

                        String inputCurrent = (String) mpl.get(RclCmd.InputCurrent);
                        if (!StringUtils.isEmpty(inputCurrent)) {
                            String deviceItemValue = StringUtils.strip(inputCurrent, "[]");
                            if (!StringUtils.isEmpty(deviceItemValue)) {
                                BigDecimal bigDecimal = new BigDecimal(deviceItemValue);
                                Map<String, Object> mps190 = new HashMap<>();
                                mps190.put("InputCurrent", bigDecimal.floatValue());
                                mps190.put("ts", timeMillis);
                                mpSend.add(mps190);
                            }
                        }

                        String inputCurrentFreq = (String) mpl.get(RclCmd.InputCurrentFreq);
                        if (!StringUtils.isEmpty(inputCurrentFreq)) {
                            String deviceItemValue = StringUtils.strip(inputCurrentFreq, "[]");
                            if (!StringUtils.isEmpty(deviceItemValue)) {
                                BigDecimal bigDecimal = new BigDecimal(deviceItemValue);
                                Map<String, Object> mps190 = new HashMap<>();
                                mps190.put("InputCurrentFreq", bigDecimal.floatValue());
                                mps190.put("ts", timeMillis);
                                mpSend.add(mps190);
                            }
                        }


                        String setTime = (String) mpl.get(RclCmd.SetTime);
                        if (!StringUtils.isEmpty(setTime)) {
                            String deviceItemValue = StringUtils.strip(setTime, "[]");
                            if (!StringUtils.isEmpty(deviceItemValue)) {
                                BigDecimal bigDecimal = new BigDecimal(deviceItemValue);
                                Map<String, Object> mps190 = new HashMap<>();
                                mps190.put("SetTime", bigDecimal.intValue());
                                mps190.put("ts", timeMillis);
                                mpSend.add(mps190);
                            }
                        }


                        String actualTime = (String) mpl.get(RclCmd.ActualTime);
                        if (!StringUtils.isEmpty(actualTime)) {
                            String deviceItemValue = StringUtils.strip(actualTime, "[]");
                            if (!StringUtils.isEmpty(deviceItemValue)) {
                                BigDecimal bigDecimal = new BigDecimal(deviceItemValue);
                                Map<String, Object> mps190 = new HashMap<>();
                                mps190.put("ActualTime", bigDecimal.floatValue());
                                mps190.put("ts", timeMillis);
                                mpSend.add(mps190);
                            }
                        }

                        String speed = (String) mpl.get(RclCmd.Speed);
                        if (!StringUtils.isEmpty(speed)) {
                            String deviceItemValue = StringUtils.strip(speed, "[]");
                            if (!StringUtils.isEmpty(deviceItemValue)) {
                                BigDecimal bigDecimal = new BigDecimal(deviceItemValue);
                                Map<String, Object> mps190 = new HashMap<>();
                                mps190.put("Speed", bigDecimal.floatValue());
                                mps190.put("ts", timeMillis);
                                mpSend.add(mps190);
                            }
                        }


                        String workpieceSpeed = (String) mpl.get(RclCmd.WorkpieceSpeed);
                        if (!StringUtils.isEmpty(workpieceSpeed)) {
                            String deviceItemValue = StringUtils.strip(workpieceSpeed, "[]");
                            if (!StringUtils.isEmpty(deviceItemValue)) {
                                BigDecimal bigDecimal = new BigDecimal(deviceItemValue);
                                Map<String, Object> mps190 = new HashMap<>();
                                mps190.put("WorkpieceSpeed", bigDecimal.floatValue());
                                mps190.put("ts", timeMillis);
                                mpSend.add(mps190);
                            }
                        }


                        String coolTemp = (String) mpl.get(RclCmd.CoolTemp);
                        if (!StringUtils.isEmpty(coolTemp)) {
                            String deviceItemValue = StringUtils.strip(coolTemp, "[]");
                            if (!StringUtils.isEmpty(deviceItemValue)) {
                                BigDecimal bigDecimal = new BigDecimal(deviceItemValue);
                                Map<String, Object> mps190 = new HashMap<>();
                                mps190.put("CoolTemp", bigDecimal.floatValue());
                                mps190.put("ts", timeMillis);
                                mpSend.add(mps190);
                            }
                        }


                        String coolOverTempAlarm = (String) mpl.get(RclCmd.CoolOverTempAlarm);
                        if (!StringUtils.isEmpty(coolOverTempAlarm)) {
                            String deviceItemValue = StringUtils.strip(coolOverTempAlarm, "[]");
                            if (!StringUtils.isEmpty(deviceItemValue)) {
                                BigDecimal bigDecimal = new BigDecimal(deviceItemValue);
                                Map<String, Object> mps190 = new HashMap<>();
                                mps190.put("CoolOverTempAlarm", bigDecimal.intValue());
                                mps190.put("ts", timeMillis);
                                mpSend.add(mps190);
                            }
                        }

                        String coolLowTempAlarm = (String) mpl.get(RclCmd.CoolLowTempAlarm);
                        if (!StringUtils.isEmpty(coolLowTempAlarm)) {
                            String deviceItemValue = StringUtils.strip(coolLowTempAlarm, "[]");
                            if (!StringUtils.isEmpty(deviceItemValue)) {
                                BigDecimal bigDecimal = new BigDecimal(deviceItemValue);
                                Map<String, Object> mps190 = new HashMap<>();
                                mps190.put("CoolLowTempAlarm", bigDecimal.intValue());
                                mps190.put("ts", timeMillis);
                                mpSend.add(mps190);
                            }
                        }


                        String coolPress = (String) mpl.get(RclCmd.CoolPress);
                        if (!StringUtils.isEmpty(coolPress)) {
                            String deviceItemValue = StringUtils.strip(coolPress, "[]");
                            if (!StringUtils.isEmpty(deviceItemValue)) {
                                BigDecimal bigDecimal = new BigDecimal(deviceItemValue);
                                Map<String, Object> mps190 = new HashMap<>();
                                mps190.put("CoolPress", bigDecimal.intValue());
                                mps190.put("ts", timeMillis);
                                mpSend.add(mps190);
                            }
                        }


                        String coolFlow = (String) mpl.get(RclCmd.CoolFlow);
                        if (!StringUtils.isEmpty(coolFlow)) {
                            String deviceItemValue = StringUtils.strip(coolFlow, "[]");
                            if (!StringUtils.isEmpty(deviceItemValue)) {
                                BigDecimal bigDecimal = new BigDecimal(deviceItemValue);
                                Map<String, Object> mps190 = new HashMap<>();
                                mps190.put("CoolFlow", bigDecimal.floatValue());
                                mps190.put("ts", timeMillis);
                                mpSend.add(mps190);
                            }
                        }

                        String setCoo = (String) mpl.get(RclCmd.SetCoo);
                        if (!StringUtils.isEmpty(setCoo)) {
                            String deviceItemValue = StringUtils.strip(setCoo, "[]");
                            if (!StringUtils.isEmpty(deviceItemValue)) {
                                BigDecimal bigDecimal = new BigDecimal(deviceItemValue);
                                Map<String, Object> mps190 = new HashMap<>();
                                mps190.put("SetCoo", bigDecimal.intValue());
                                mps190.put("ts", timeMillis);
                                mpSend.add(mps190);
                            }
                        }

                        String actualCoolTime = (String) mpl.get(RclCmd.ActualCoolTime);
                        if (!StringUtils.isEmpty(actualCoolTime)) {
                            String deviceItemValue = StringUtils.strip(actualCoolTime, "[]");
                            if (!StringUtils.isEmpty(deviceItemValue)) {
                                BigDecimal bigDecimal = new BigDecimal(deviceItemValue);
                                Map<String, Object> mps190 = new HashMap<>();
                                mps190.put("ActualCoolTime", bigDecimal.intValue());
                                mps190.put("ts", timeMillis);
                                mpSend.add(mps190);
                            }
                        }
                    }
//                    存储本次发送的部分数据
                    mqttService.sendRealTimeData(mpSend, dzDataDevice.getDeviceType(), dzDataDevice.getDeviceId(), dzDataDevice.getAssetsEncoding());
//                    log.info("发送淬火机实时数据：{}", mpSend);
                }
            } else {
                log.warn("数控设备数据不存在:deviceIds: {}", deviceIds);
            }

        } else {
            log.warn("数控设备不存在:IotDeviceType: {}", IotDeviceType.SK);
        }

    }
}
