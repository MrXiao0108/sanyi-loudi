package com.dzics.mqtt.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.model.entity.DzDataCollection;
import com.dzics.mqtt.commom.IotDeviceType;
import com.dzics.mqtt.commom.doccmd.ByCmd;
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
public class ByRobTask {

    @Autowired
    private MqttService mqttService;

    @Autowired
    private DzDataCollectionService dzDataCollectionService;

    @Autowired
    private SendDateCommService sendDateCommService;

    /**
     * 搬运机器人采集数据发送
     * 1:作业，2:待机、3:报警、4:关机；
     */
    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    public void byRob() {
//      获取所有数控设备
        long l = System.currentTimeMillis();
        List<DzDataDevice> dzDataDevices = sendDateCommService.getDataDeviceType(IotDeviceType.BY);
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
                        log.warn("上报搬运机器人采集数据错误,根据设备equipmentId:{} 查询值:{}", equipmentId, dzDataCollections);
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

//                采集信息
                    Map<String, Object> mpl = JSONObject.parseObject(JSON.toJSONString(dzDataCollections));
//                状态信息
                    Map<String, Object> mps1 = new HashMap<>();
                    String status = (String) mpl.get(ByCmd.Status);
                    String alarm = (String) mpl.get(ByCmd.Alarm);
                    String connState = (String) mpl.get(ByCmd.connState);
                    String standy = (String) mpl.get(ByCmd.Standby);
                    int workStateDef = 4;
                    if (!StringUtils.isEmpty(status)) {
                        long updateTime = new Date((Long) mpl.get("updateTime")).getTime();
                        if ((updateTime + (10 * 1000)) > l) {
                            //连机

                            if ("1".equals(connState)) {
                                //报警，设置故障
                                if ("1".equals(alarm)) {
                                    workStateDef = 3;
                                } else {
                                    //待机状态，设置待机
                                    if ("1".equals(standy)) {
                                        workStateDef = 2;
                                    } else {
                                        if ("1".equals(status)) {
                                            workStateDef = 1;
                                        } else {
                                            workStateDef = 2;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    mps1.put("Status", workStateDef);
                    mps1.put("ts", l);
                    mpSend.add(mps1);
                    if (4 != workStateDef) {
                        if (!StringUtils.isEmpty(alarm)) {
                            Map<String, Object> mps2 = new HashMap<>();
                            mps2.put("Alarm", Integer.valueOf(alarm));
                            mps2.put("ts", l);
                            mpSend.add(mps2);
                        }

                        String model = (String) mpl.get(ByCmd.Mode);
                        if ("1".equals(model)) {
                            model = "6";
                        }
                        Map<String, Object> mps3 = new HashMap<>();
                        mps3.put("Mode", Integer.valueOf(model));
                        mps3.put("ts", l);
                        mpSend.add(mps3);
//                角度坐标
                        Float j1 = 0f;
                        Float j2 = 0f;
                        Float j3 = 0f;
                        Float j4 = 0f;
                        Float j5 = 0f;
                        Float j6 = 0f;
                        String jAl = (String) mpl.get(ByCmd.J1);
                        if (!StringUtils.isEmpty(jAl)) {
                            String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(jAl, "[]");
                            String[] split = deviceItemValue.split(",");
                            if (split.length == 6) {
                                j1 = Float.valueOf(split[0]);
                                j2 = Float.valueOf(split[1]);
                                j3 = Float.valueOf(split[2]);
                                j4 = Float.valueOf(split[3]);
                                j5 = Float.valueOf(split[4]);
                                j6 = Float.valueOf(split[5]);
                            } else {
                                log.warn("指令:{} 分割后长度错误: {}", ByCmd.J1, jAl);
                            }

                        }
                        Map<String, Object> mps4 = new HashMap<>();
                        mps4.put("J1", j1);
                        mps4.put("ts", l);
                        mpSend.add(mps4);

                        Map<String, Object> mps5 = new HashMap<>();
                        mps5.put("J2", j2);
                        mps5.put("ts", l);
                        mpSend.add(mps5);

                        Map<String, Object> mps6 = new HashMap<>();
                        mps6.put("J3", j3);
                        mps6.put("ts", l);
                        mpSend.add(mps6);

                        Map<String, Object> mps7 = new HashMap<>();
                        mps7.put("J4", j4);
                        mps7.put("ts", l);
                        mpSend.add(mps7);

                        Map<String, Object> mps8 = new HashMap<>();
                        mps8.put("J5", j5);
                        mps8.put("ts", l);
                        mpSend.add(mps8);
//
                        Map<String, Object> mps9 = new HashMap<>();
                        mps9.put("J6", j6);
                        mps9.put("ts", l);
                        mpSend.add(mps9);

//                用户坐标（用户当前激活的自定义坐标系）
                        String ual = (String) mpl.get(ByCmd.UX);
                        Float u1 = 0f;
                        Float u2 = 0f;
                        Float u3 = 0f;
                        Float u4 = 0f;
                        Float u5 = 0f;
                        Float u6 = 0f;
                        if (!StringUtils.isEmpty(ual)) {
                            String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(ual, "[]");
                            String[] split = deviceItemValue.split(",");
                            if (split.length == 6) {
                                u1 = Float.valueOf(split[0]);
                                u2 = Float.valueOf(split[1]);
                                u3 = Float.valueOf(split[2]);
                                u4 = Float.valueOf(split[3]);
                                u5 = Float.valueOf(split[4]);
                                u6 = Float.valueOf(split[5]);
                            } else {
                                log.warn("指令:{} 分割后长度错误: {}", ByCmd.UX, ual);
                            }

                        }
                        Map<String, Object> mps10 = new HashMap<>();
                        mps10.put("UX", u1);
                        mps10.put("ts", l);
                        mpSend.add(mps10);

                        Map<String, Object> mps11 = new HashMap<>();
                        mps11.put("UY", u2);
                        mps11.put("ts", l);
                        mpSend.add(mps11);

                        Map<String, Object> mps12 = new HashMap<>();
                        mps12.put("UZ", u3);
                        mps12.put("ts", l);
                        mpSend.add(mps12);

                        Map<String, Object> mps13 = new HashMap<>();
                        mps13.put("UW", u4);
                        mps13.put("ts", l);
                        mpSend.add(mps13);

                        Map<String, Object> mps14 = new HashMap<>();
                        mps14.put("UP", u5);
                        mps14.put("ts", l);
                        mpSend.add(mps14);

                        Map<String, Object> mps15 = new HashMap<>();
                        mps15.put("UR", u6);
                        mps15.put("ts", l);
                        mpSend.add(mps15);

//                世界坐标 （机器 人基坐 标系） [1992.778,1401.033,-14.962,179.55,-3.964,124.624]A504

                        String wxAl = (String) mpl.get(ByCmd.WX);
                        Float w1 = 0f;
                        Float w2 = 0f;
                        Float w3 = 0f;
                        Float w4 = 0f;
                        Float w5 = 0f;
                        Float w6 = 0f;
                        if (!StringUtils.isEmpty(wxAl)) {
                            String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(wxAl, "[]");
                            String[] split = deviceItemValue.split(",");
                            if (split.length == 6) {
                                w1 = Float.valueOf(split[0]);
                                w2 = Float.valueOf(split[1]);
                                w3 = Float.valueOf(split[2]);
                                w4 = Float.valueOf(split[3]);
                                w5 = Float.valueOf(split[4]);
                                w6 = Float.valueOf(split[5]);
                            } else {
                                log.warn("指令:{} 分割后长度错误: {}", ByCmd.WX, wxAl);
                            }

                        }

                        Map<String, Object> mps16 = new HashMap<>();
                        mps16.put("WX", w1);
                        mps16.put("ts", l);
                        mpSend.add(mps16);
                        Map<String, Object> mps17 = new HashMap<>();
                        mps17.put("WY", w2);
                        mps17.put("ts", l);
                        mpSend.add(mps17);

                        Map<String, Object> mps18 = new HashMap<>();
                        mps18.put("WZ", w3);
                        mps18.put("ts", l);
                        mpSend.add(mps18);

                        Map<String, Object> mps19 = new HashMap<>();
                        mps19.put("WW", w4);
                        mps19.put("ts", l);
                        mpSend.add(mps19);

                        Map<String, Object> mps20 = new HashMap<>();
                        mps20.put("WP", w5);
                        mps20.put("ts", l);
                        mpSend.add(mps20);

                        Map<String, Object> mps21 = new HashMap<>();
                        mps21.put("WR", w6);
                        mps21.put("ts", l);
                        mpSend.add(mps21);


                        String ncStatus =  (String) mpl.get(ByCmd.NcStatus);
                        String state = "unknown";
                        if ("0".equals(ncStatus)) {
                            state = "RESET";
                        }
                        if ("1".equals(ncStatus)) {
                            state = "STOP";
                        }
                        if ("2".equals(ncStatus)) {
                            state = "HOLD";
                        }
                        if ("3".equals(ncStatus)) {
                            state = "RUN";
                        }
//               程序信息
                        Map<String, Object> mps22 = new HashMap<>();
                        mps22.put("NcStatus",state);
                        mps22.put("ts", l);
                        mpSend.add(mps22);

                        String curPgm = (String) mpl.get(ByCmd.CurPgm);
                        if (!StringUtils.isEmpty(curPgm)) {
                            String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(curPgm, "[]");
                            Map<String, Object> mps23 = new HashMap<>();
                            mps23.put("CurPgm", deviceItemValue);
                            mps23.put("ts", l);
                            mpSend.add(mps23);
                        }
                        String MainPgm = (String) mpl.get(ByCmd.MainPgm);
                        if (!StringUtils.isEmpty(MainPgm)) {
                            Map<String, Object> mps231 = new HashMap<>();
                            mps231.put("MainPgm", MainPgm);
                            mps231.put("ts", l);
                            mpSend.add(mps231);
                        }
                        String emg = (String) mpl.get(ByCmd.Emg);
                        if (!StringUtils.isEmpty(emg)) {
                            BigDecimal bigDecimal = new BigDecimal(emg);
                            Map<String, Object> mps232 = new HashMap<>();
                            mps232.put("Emg", bigDecimal.intValue());
                            mps232.put("ts", l);
                            mpSend.add(mps232);
                        }

                        String sec = (String) mpl.get(ByCmd.CycSec);
                        if (!StringUtils.isEmpty(sec)) {
                            BigDecimal bigDecimal = new BigDecimal(sec);
                            Map<String, Object> mps24 = new HashMap<>();
                            mps24.put("CycSec", bigDecimal.intValue());
                            mps24.put("ts", l);
                            mpSend.add(mps24);
                        }

                        String seq = (String) mpl.get(ByCmd.CurSeq);
                        if (!StringUtils.isEmpty(seq)) {
                            BigDecimal bigDecimal = new BigDecimal(seq);
                            Map<String, Object> mps25 = new HashMap<>();
                            mps25.put("CurSeq", bigDecimal.intValue());
                            mps25.put("ts", l);
                            mpSend.add(mps25);
                        }

//               报警信息
                        Map<String, Object> mps26 = new HashMap<>();
                        String alarmMsg = (String) mpl.get(ByCmd.AlarmMsg);
                        if (!StringUtils.isEmpty(alarmMsg)) {
                            mps26.put("AlarmMsg", alarmMsg);
                            mps26.put("ts", l);
                            mpSend.add(mps26);
                        } else {
                            if (!StringUtils.isEmpty(alarm)) {
                                if ("0".equals(alarm)) {
                                    alarmMsg = "正常";
                                } else {
                                    alarmMsg = "报警";
                                }
                                mps26.put("AlarmMsg", alarmMsg);
                                mps26.put("ts", l);
                                mpSend.add(mps26);
                            }

                        }

//               搬运信息
                        String fixState = (String) mpl.get(ByCmd.FixState);
                        if (!StringUtils.isEmpty(fixState)) {
                            String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(fixState, "[]");
                            String[] split = deviceItemValue.split(",");
                            Map<String, Object> mps28 = new HashMap<>();
                            mps28.put("FixState", Integer.valueOf(split[0]));
                            mps28.put("ts", l);
                            mpSend.add(mps28);
                        }
                    }
                    mqttService.sendRealTimeData(mpSend, dzDataDevice.getDeviceType(), dzDataDevice.getDeviceId(), dzDataDevice.getAssetsEncoding());
                }
            } else {
                log.warn("搬运机器人数据不存在:deviceIds: {}", deviceIds);
            }

        } else {
            log.warn("搬运机器人设备不存在:IotDeviceType: {}", IotDeviceType.BY);
        }

    }
}
