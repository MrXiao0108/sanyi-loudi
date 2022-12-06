package com.dzics.mqtt.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.model.entity.DzDataCollection;
import com.dzics.mqtt.commom.IotDeviceType;
import com.dzics.mqtt.commom.doccmd.JcCmd;
import com.dzics.mqtt.commom.doccmd.SkCmd;
import com.dzics.mqtt.model.entity.DzDataDevice;
import com.dzics.mqtt.model.entity.IotSendData;
import com.dzics.mqtt.service.DzDataCollectionService;
import com.dzics.mqtt.service.IotSendDataService;
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
public class NumericalControlDataTask {

    @Autowired
    private MqttService mqttService;

    @Autowired
    private DzDataCollectionService dzDataCollectionService;

    @Autowired
    private SendDateCommService sendDateCommService;

    @Autowired
    private IotSendDataService iotSendDataService;

    /**
     * 数控设备数据发送
     * {
     * "AlmCode"："401", //报警号
     * "AlmClass"："SV", //报警类型
     * "AlmMsg"："伺服参数错误", //报警信息
     * }
     */
    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    public void numericalControlData() {
//      获取所有数控设备
        long l = System.currentTimeMillis();
        List<DzDataDevice> dzDataDevices = sendDateCommService.getDataDeviceType(IotDeviceType.SK);
        if (CollectionUtils.isNotEmpty(dzDataDevices)) {
            List<Long> deviceIds = dzDataDevices.stream().map(DzDataDevice::getEquipmentId).collect(Collectors.toList());
            List<DzDataCollection> dataColl = dzDataCollectionService.getDeviceIdDzDataColl(deviceIds);
            if (CollectionUtils.isNotEmpty(dataColl)) {
                Map<Long, DzDataCollection> mapDeviceId = sendDateCommService.getMapDeviceId(dataColl);
                for (DzDataDevice dzDataDevice : dzDataDevices) {
                    Long equipmentId = dzDataDevice.getEquipmentId();
                    Long deviceId = dzDataDevice.getDeviceId();
                    String deviceTypeDef = dzDataDevice.getDeviceTypeDef();
                    DzDataCollection dzDataCollections = mapDeviceId.get(equipmentId);
                    if (dzDataCollections == null) {
                        log.warn("上报数控设备采集数据错误,根据设备equipmentId:{} 查询值:{}", equipmentId, dzDataCollections);
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
                    List<Map<String, Object>> mpSend = new ArrayList<>(mapBs);
//            检测数据
                    Map<String, Object> mpl = JSONObject.parseObject(JSON.toJSONString(dzDataCollections));
//                    状态信息
                    String status = (String) mpl.get(JcCmd.Status);
                    String alram = (String) mpl.get(SkCmd.Alarm);
                    String connState = (String) mpl.get(SkCmd.connState);
                    IotSendData iotSendData = new IotSendData();
                    //                  刀具信息
                    String tcode = (String) mpl.get(SkCmd.TCode);
                    if (StringUtils.isEmpty(tcode)) {
                        if ("M".equals(deviceTypeDef) || "G".equals(deviceTypeDef)) {
                            tcode = "1";
                        }
                    }
                    String spinLoad1 = (String) mpl.get(SkCmd.SpinLoad1);
                    String actSpin = (String) mpl.get(SkCmd.ActSpin);
                    String actFeed = (String) mpl.get(SkCmd.ActFeed);
//                    1：作业 2：待机 3：故障 4：关机
                    int workStateDef = 4;
                    if (!StringUtils.isEmpty(status)) {
                        long updateTime = new Date((Long) mpl.get("updateTime")).getTime();
                        if ((updateTime + (10 * 1000)) > l) {
                            if ("1".equals(connState)) {
//                            连机
                                if ("1".equals(alram) || "4".equals(status)) {
//                                报警，设置故障
                                    workStateDef = 3;
                                } else {
                                    if ("G".equals(deviceTypeDef) || "M".equals(deviceTypeDef)) {
                                        //作业
                                        if ("3".equals(status)) {
                                            workStateDef = 1;
                                        }
                                        //待机
                                        if ("0".equals(status) || "1".equals(status) || "2".equals(status)) {
                                            workStateDef = 2;
                                        }
                                    } else {
                                        if ("3".equals(status) && !"0".equals(spinLoad1) && !"0".equals(actSpin) && !"0".equals(actFeed) && !"0".equals(tcode)) {
                                            workStateDef = 1;
                                        } else {
                                            workStateDef = 2;
                                        }
                                    }

                                }
                            }
                        }
                    }


                    Map<String, Object> mps1 = new HashMap<>();
                    mps1.put("Status", workStateDef);
                    mps1.put("ts", l);
                    mpSend.add(mps1);
//                    工作状态为4：      只上传工作状态
                    iotSendData.setEquipmentid(equipmentId);
                    iotSendData.setDeviceid(deviceId);
                    iotSendData.setWorkstate(workStateDef);
                    if (4 == workStateDef) {
//                        log.info("待发送数控设备实时数据：{}", mpSend);
                        mqttService.sendRealTimeData(mpSend, dzDataDevice.getDeviceType(), dzDataDevice.getDeviceId(), dzDataDevice.getAssetsEncoding());
                        iotSendDataService.save(iotSendData);
                        continue;
                    }
                    if (!StringUtils.isEmpty(tcode)) {
                        Map<String, Object> mps141 = new HashMap<>();
                        mps141.put("TCode", Integer.valueOf(tcode));
                        mps141.put("ts", l);
                        mpSend.add(mps141);
                        iotSendData.setTcode(tcode);
                    }
//                    工作状态为2 ：     主轴负载，主轴速度，切削速度 必须为 0
                    if (2 == workStateDef) {
                        Map<String, Object> mps190 = new HashMap<>();
                        mps190.put("SpinLoad1", 0);
                        mps190.put("ts", l);
                        mpSend.add(mps190);
                        iotSendData.setSpinload1("0");
                        Map<String, Object> mps18 = new HashMap<>();
                        mps18.put("ActSpin", 0);
                        mps18.put("ts", l);
                        mpSend.add(mps18);
                        iotSendData.setActspin("0");
                        Map<String, Object> mps16 = new HashMap<>();
                        mps16.put("ActFeed", 0);
                        mps16.put("ts", l);
                        mpSend.add(mps16);
                        iotSendData.setActfeed("0");
                    } else {
                        if (!StringUtils.isEmpty(spinLoad1)) {
                            String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(spinLoad1, "[]");
                            if (!StringUtils.isEmpty(deviceItemValue)) {
                                BigDecimal bigDecimal = new BigDecimal(deviceItemValue);
                                Map<String, Object> mps190 = new HashMap<>();
                                mps190.put("SpinLoad1", bigDecimal.floatValue());
                                mps190.put("ts", l);
                                mpSend.add(mps190);
                                iotSendData.setSpinload1(bigDecimal.floatValue() + "");
                            }
                        }
                        if (!StringUtils.isEmpty(actSpin)) {
                            String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(actSpin, "[]");
                            if (!StringUtils.isEmpty(deviceItemValue)) {
                                float v = new BigDecimal(deviceItemValue).floatValue();
                                Map<String, Object> mps18 = new HashMap<>();
                                mps18.put("ActSpin", v);
                                mps18.put("ts", l);
                                mpSend.add(mps18);
                                iotSendData.setActspin(v + "");
                            }
                        }
                        if (!StringUtils.isEmpty(actFeed)) {
                            String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(actFeed, "[]");
                            if (!StringUtils.isEmpty(deviceItemValue)) {
                                float v = new BigDecimal(deviceItemValue).floatValue();
                                Map<String, Object> mps16 = new HashMap<>();
                                mps16.put("ActFeed", v);
                                mps16.put("ts", l);
                                mpSend.add(mps16);
                                iotSendData.setActfeed(v + "");
                            }
                        }
                    }
//                  工作状态为1 ：     刀具号，主轴负载，主轴速度，切削速度，主程序号，当前程序号，程序行号，不能为0或者空
                    if (!StringUtils.isEmpty(alram)) {
                        Map<String, Object> mps3 = new HashMap<>();
                        mps3.put("Alarm", Integer.valueOf(alram));
                        mps3.put("ts", l);
                        mpSend.add(mps3);
                    }
//                  基础信息
                    String maxSpeed = (String) mpl.get(SkCmd.MaxSpeed);
                    if (!StringUtils.isEmpty(maxSpeed)) {
                        String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(maxSpeed, "[]");
                        if (!StringUtils.isEmpty(deviceItemValue)) {
                            Map<String, Object> mps112 = new HashMap<>();
                            mps112.put("MaxSpeed", new BigDecimal(deviceItemValue).floatValue());
                            mps112.put("ts", l);
                            mpSend.add(mps112);
                        }

                    }

                    String SpinNum = (String) mpl.get(SkCmd.SpinNum);
                    if (!StringUtils.isEmpty(SpinNum)) {
                        Map<String, Object> mps113 = new HashMap<>();
                        mps113.put("SpinNum", Integer.valueOf(SpinNum));
                        mps113.put("ts", l);
                        mpSend.add(mps113);
                    }

                    String Axes = (String) mpl.get(SkCmd.Axes);
                    if (!StringUtils.isEmpty(Axes)) {
                        Map<String, Object> mps114 = new HashMap<>();
                        mps114.put("Axes", Integer.valueOf(Axes));
                        mps114.put("ts", l);
                        mpSend.add(mps114);
                    }

                    String emg = (String) mpl.get(SkCmd.Emg);
                    if (!StringUtils.isEmpty(emg)) {
                        int emgInt = Integer.parseInt(emg);
                        if (emgInt != 0 && emgInt != 1) {
                            emgInt = 0;
                        }
                        Map<String, Object> mps2 = new HashMap<>();
                        mps2.put("Emg", emgInt);
                        mps2.put("ts", l);
                        mpSend.add(mps2);
                    } else {
                        log.warn("mpl:{}", mpl);
                        log.warn("dzDataCollections:{}", dzDataCollections);
                        log.warn("数控设备急停状态不存在：{}", emg);
                    }

                    String mode = (String) mpl.get(SkCmd.Mode);
                    if (!StringUtils.isEmpty(mode)) {
                        Map<String, Object> mps4 = new HashMap<>();
                        mps4.put("Mode", Integer.valueOf(mode));
                        mps4.put("ts", l);
                        mpSend.add(mps4);
                    } else {
                        log.warn("mpl:{}", mpl);
                        log.warn("dzDataCollections:{}", dzDataCollections);
                        log.warn("数控设备操作模式不存在：{}", mode);
                    }

                    String axisName = (String) mpl.get(SkCmd.AxisName);
                    if (!StringUtils.isEmpty(axisName)) {
                        String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(axisName, "[]");
                        if (!StringUtils.isEmpty(deviceItemValue)) {
                            String axisNameSpl = deviceItemValue.replaceAll(",", "|");
                            Map<String, Object> mps5 = new HashMap<>();
                            mps5.put("AxisName", axisNameSpl);
                            mps5.put("ts", l);
                            mpSend.add(mps5);
                        }
                    } else {
                        log.warn("mpl:{}", mpl);
                        log.warn("dzDataCollections:{}", dzDataCollections);
                        log.warn("数控设备轴名称不存在：{}", axisName);
                    }

                    String machPos = (String) mpl.get(SkCmd.MachPos);
                    if (!StringUtils.isEmpty(machPos)) {
                        String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(machPos, "[]");
                        if (!StringUtils.isEmpty(deviceItemValue)) {
                            String machPosSpl = deviceItemValue.replaceAll(",", "|");
                            Map<String, Object> mps6 = new HashMap<>();
                            mps6.put("MachPos", machPosSpl);
                            mps6.put("ts", l);
                            mpSend.add(mps6);
                        }
                    } else {
                        log.warn("mpl:{}", mpl);
                        log.warn("dzDataCollections:{}", dzDataCollections);
                        log.warn("数控机械坐标名称不存在：{}", machPos);
                    }

                    String absPos = (String) mpl.get(SkCmd.AbsPos);
                    if (!StringUtils.isEmpty(absPos)) {
                        String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(absPos, "[]");
                        if (!StringUtils.isEmpty(deviceItemValue)) {
                            String relPosRep = deviceItemValue.replaceAll(",", "|");
                            Map<String, Object> mps7 = new HashMap<>();
                            mps7.put("AbsPos", relPosRep);
                            mps7.put("ts", l);
                            mpSend.add(mps7);
                        }
                    } else {
                        log.warn("mpl:{}", mpl);
                        log.warn("dzDataCollections:{}", dzDataCollections);
                        log.warn("数控绝对坐标名称不存在：{}", absPos);

                    }

                    String relPos = (String) mpl.get(SkCmd.RelPos);
                    if (!StringUtils.isEmpty(relPos)) {
                        String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(relPos, "[]");
                        if (!StringUtils.isEmpty(deviceItemValue)) {
                            String relPosRep = deviceItemValue.replaceAll(",", "|");
                            Map<String, Object> mps111 = new HashMap<>();
                            mps111.put("RelPos", relPosRep);
                            mps111.put("ts", l);
                            mpSend.add(mps111);
                        }
                    }

                    String remPos = (String) mpl.get(SkCmd.RemPos);
                    if (!StringUtils.isEmpty(relPos)) {
                        String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(remPos, "[]");
                        if (!StringUtils.isEmpty(deviceItemValue)) {
                            String relPosRep = deviceItemValue.replaceAll(",", "|");
                            Map<String, Object> mps112 = new HashMap<>();
                            mps112.put("RemPos", relPosRep);
                            mps112.put("ts", l);
                            mpSend.add(mps112);
                        }
                    }

                    String cutTime = (String) mpl.get(SkCmd.CutTime);
                    if (!StringUtils.isEmpty(cutTime)) {
                        int sec = getSec(cutTime);
                        Map<String, Object> mps110 = new HashMap<>();
                        mps110.put("CutTime", sec);
                        mps110.put("ts", l);
                        mpSend.add(mps110);
                    }
                    String cycSec = (String) mpl.get(SkCmd.CycSec);
                    if (!StringUtils.isEmpty(cycSec)) {
                        int sec = getSec(cycSec);
                        Map<String, Object> mps11 = new HashMap<>();
                        mps11.put("CycSec", sec);
                        mps11.put("ts", l);
                        mpSend.add(mps11);
                    } else {
                        log.warn("mpl:{}", mpl);
                        log.warn("dzDataCollections:{}", dzDataCollections);
                        log.warn("数控程序当前加工程序号不存在：{}", cycSec);

                    }

                    String state = "unknown";
                    String ncStatus = (String) mpl.get(SkCmd.NcStatus);
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
                    Map<String, Object> mps8 = new HashMap<>();
                    mps8.put("NcStatus", state);
                    mps8.put("ts", l);
                    mpSend.add(mps8);

                    String mainPgm = (String) mpl.get(SkCmd.MainPgm);
                    if (!StringUtils.isEmpty(mainPgm)) {
                        Map<String, Object> mps9 = new HashMap<>();
                        mps9.put("MainPgm", mainPgm);
                        mps9.put("ts", l);
                        mpSend.add(mps9);
                        iotSendData.setMainpgm(mainPgm);
                    } else {
                        log.warn("mpl:{}", mpl);
                        log.warn("dzDataCollections:{}", dzDataCollections);
                        log.warn("数控程序主程序号不存在：{}", mainPgm);
                    }

                    String curPgm = (String) mpl.get(SkCmd.CurPgm);
                    if (!StringUtils.isEmpty(curPgm)) {
                        Map<String, Object> mps10 = new HashMap<>();
                        mps10.put("CurPgm", curPgm);
                        mps10.put("ts", l);
                        mpSend.add(mps10);
                        iotSendData.setCurpgm(curPgm);
                    } else {
                        log.warn("mpl:{}", mpl);
                        log.warn("dzDataCollections:{}", dzDataCollections);
                        log.warn("数控程序当前加工程序号不存在：{}", curPgm);
                    }

                    String CurSeq = (String) mpl.get(SkCmd.CurSeq);
                    if (!StringUtils.isEmpty(CurSeq)) {
                        Map<String, Object> mps12 = new HashMap<>();
                        mps12.put("CurSeq", CurSeq);
                        mps12.put("ts", l);
                        mpSend.add(mps12);
                        iotSendData.setCurseq(CurSeq);
                    }

                    Map<String, Object> mps13 = new HashMap<>();
                    mps13.put("PartCnt", mpl.get(SkCmd.PartCnt));
                    mps13.put("ts", l);
                    mpSend.add(mps13);

                    String curNcBlk = (String) mpl.get(SkCmd.CurNcBlk);
                    if (!StringUtils.isEmpty(curNcBlk)) {
                        String s = curNcBlk.replaceAll("_A_", "#").replaceAll("_B_", "|").replaceAll("_C_", "[").replaceAll("_D_", "]");
                        Map<String, Object> mps140 = new HashMap<>();
                        mps140.put("CurNcBlk", s);
                        mps140.put("ts", l);
                        mpSend.add(mps140);
                    }

                    String svTemp = (String) mpl.get(SkCmd.SvTemp);
                    if (!StringUtils.isEmpty(svTemp)) {
                        Map<String, Object> mps14 = new HashMap<>();
                        mps14.put("SvTemp", svTemp.replaceAll(",", "|"));
                        mps14.put("ts", l);
                        mpSend.add(mps14);
                    }

                    String svLoad = (String) mpl.get(SkCmd.SvLoad);
                    if (!StringUtils.isEmpty(svLoad)) {
                        Map<String, Object> mps15 = new HashMap<>();
                        mps15.put("SvLoad", svLoad.replaceAll(",", "|"));
                        mps15.put("ts", l);
                        mpSend.add(mps15);
                    }
                    String spinTemp1 = (String) mpl.get(SkCmd.SpinTemp1);
                    if (!StringUtils.isEmpty(spinTemp1)) {
                        Map<String, Object> mps16 = new HashMap<>();
                        mps16.put("SpinTemp1", spinTemp1.split(",")[0]);
                        mps16.put("ts", l);
                        mpSend.add(mps16);
                    }

                    String ncVer = (String) mpl.get(SkCmd.NcVer);
                    if (!StringUtils.isEmpty(ncVer)) {
                        HashMap<String, Object> map9 = new HashMap<>();
                        map9.put("NcVer", ncVer);
                        map9.put("ts", l);
                        mpSend.add(map9);
                    }

                    String ovFeed = (String) mpl.get(SkCmd.OvFeed);
                    if (!StringUtils.isEmpty(ovFeed)) {
                        String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(ovFeed, "[]");
                        if (!StringUtils.isEmpty(deviceItemValue)) {
                            Map<String, Object> mps14 = new HashMap<>();
                            mps14.put("OvFeed", new BigDecimal(deviceItemValue).floatValue());
                            mps14.put("ts", l);
                            mpSend.add(mps14);
                        }
                    }

                    String ovSpin = (String) mpl.get(SkCmd.OvSpin);
                    if (!StringUtils.isEmpty(ovSpin)) {
                        String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(ovSpin, "[]");
                        if (!StringUtils.isEmpty(deviceItemValue)) {
                            Map<String, Object> mps15 = new HashMap<>();
                            mps15.put("OvSpin", new BigDecimal(deviceItemValue));
                            mps15.put("ts", l);
                            mpSend.add(mps15);
                        }
                    }


                    String fCode = (String) mpl.get(SkCmd.FCode);
                    if (!StringUtils.isEmpty(fCode)) {
                        String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(fCode, "[]");
                        if (!StringUtils.isEmpty(deviceItemValue)) {
                            Map<String, Object> mps17 = new HashMap<>();
                            mps17.put("FCode", new BigDecimal(deviceItemValue).floatValue());
                            mps17.put("ts", l);
                            mpSend.add(mps17);
                        }

                    }

                    String scode = (String) mpl.get(SkCmd.SCode);
                    if (!StringUtils.isEmpty(scode)) {
                        String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(scode, "[]");
                        if (!StringUtils.isEmpty(deviceItemValue)) {
                            Map<String, Object> mps19 = new HashMap<>();
                            mps19.put("SCode", new BigDecimal(deviceItemValue).floatValue());
                            mps19.put("ts", l);
                            mpSend.add(mps19);
                        }

                    }

                    Map<String, Object> mps20 = new HashMap<>();
                    String alarmMsg = (String) mpl.get(SkCmd.AlarmMsg);
                    if (!StringUtils.isEmpty(alarmMsg)) {
                        mps20.put("AlarmMsg", alarmMsg);
                        mps20.put("ts", l);
                        mpSend.add(mps20);
                    } else {
                        if (!StringUtils.isEmpty(alram)) {
                            if ("0".equals(alram)) {
                                alarmMsg = "正常";
                            } else {
                                alarmMsg = "报警";
                            }
                            mps20.put("AlarmMsg", alarmMsg);
                            mps20.put("ts", l);
                            mpSend.add(mps20);
                        }
                    }

//                    存储本次发送的部分数据
                    mqttService.sendRealTimeData(mpSend, dzDataDevice.getDeviceType(), dzDataDevice.getDeviceId(), dzDataDevice.getAssetsEncoding());
//                    log.info("发送数控设备实时数据：{}", mpSend);
                    iotSendDataService.save(iotSendData);
                }
            } else {
                log.warn("数控设备数据不存在:deviceIds: {}", deviceIds);
            }
        } else {
            log.warn("数控设备不存在:IotDeviceType: {}", IotDeviceType.SK);
        }

    }

    private int getSec(String cutTime) {
        String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(cutTime, "[]");
        String[] split = deviceItemValue.split(",");
        int sec;
        if (split.length == 2) {
            String min = split[0];
            int minInt = Integer.parseInt(min) * 60;
            String sencd = split[1];
            int sencdInt = Integer.parseInt(sencd);
            sec = (minInt) + sencdInt;
        } else {
            sec = Integer.parseInt(split[0]);
        }
        return sec;
    }


}
