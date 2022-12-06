package com.dzics.mqtt.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dzics.mqtt.commom.CmdId;
import com.dzics.mqtt.commom.IotDeviceType;
import com.dzics.mqtt.framework.DzicsMqttClient;
import com.dzics.mqtt.model.entity.SysConfigMqtt;
import com.dzics.mqtt.model.mdc.MdcModel;
import com.dzics.mqtt.service.MqttService;
import com.dzics.mqtt.util.LocalIpAddress;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZhangChengJun
 * Date 2021/7/4.
 * @since
 */
@Service
@Slf4j
public class MqttServiceImpl implements MqttService {

    /**
     * 软件版本
     */
    @Value("${dzdc.emqx.soft.version}")
    private String softVersion;

    private SysConfigMqtt configMqtt;

    private DzicsMqttClient dzicsMqttClient;

    public DzicsMqttClient getDzicsMqttClient() {
        return this.dzicsMqttClient;
    }

    public SysConfigMqtt getConfigMqtt() {
        return this.configMqtt;
    }

    @Autowired
    public MqttServiceImpl(SysConfigMqtt sysConfigMqtt) {
        this.configMqtt = sysConfigMqtt;
        this.dzicsMqttClient = DzicsMqttClient.getInstance(this);
    }


    @Override
    public boolean sendOnLine(String topic, MqttClient client) {
        long currentTimeMillis = System.currentTimeMillis();
        List<Map<String, Object>> onlineMessage = new ArrayList<>();
        Map<String, Object> mapOnline = new HashMap<>();
        mapOnline.put("onLine", "1");
        mapOnline.put("ts", currentTimeMillis);
        onlineMessage.add(mapOnline);
        Map<String, Object> ip = new HashMap<>();
        ip.put("ip", LocalIpAddress.getIpAddress());
        ip.put("ts", currentTimeMillis);
        onlineMessage.add(ip);
      /*  Map<String, Object> location = new HashMap<>();
        location.put("location", "116.40,39.90");
        location.put("ts", currentTimeMillis);
        onlineMessage.add(location);*/
        Map<String, Object> softVersion = new HashMap<>();
        softVersion.put("softVersion", this.softVersion);
        softVersion.put("ts", currentTimeMillis);
        onlineMessage.add(softVersion);
        MdcModel mdcModel = new MdcModel();
        mdcModel.setCmdId(CmdId.cmdOnLine);
        mdcModel.setVersion(Integer.valueOf(configMqtt.getVersion()));
        mdcModel.setEdgeTime(System.currentTimeMillis() / 1000);
        mdcModel.setProductKey(configMqtt.getProductKey());
        mdcModel.setClientUuid(configMqtt.getClientId());
        mdcModel.setReported(onlineMessage);
        String jsonString = JSONObject.toJSONString(mdcModel);
        try {
            client.publish(topic, jsonString.getBytes(), 1, true);
            log.info("发送软件上线topic:{}, 信息:{}", topic);
            return true;
        } catch (Throwable e) {
            log.error("发送软件上线信息失败：topic:{} , message: {}", topic, jsonString);
            return false;
        }

    }

    @Override
    public boolean sendSignal() {
        long currentTimeMillis = System.currentTimeMillis();
        List<Map<String, Object>> onlineMessage = new ArrayList<>();
        Map<String, Object> mapOnline = new HashMap<>();
        mapOnline.put("loState", "1");
        mapOnline.put("ts", currentTimeMillis);
        onlineMessage.add(mapOnline);
//      软件版本信息
        Map<String, Object> softVersion = new HashMap<>();
        softVersion.put("softVersion", this.softVersion);
        softVersion.put("ts", currentTimeMillis);
        onlineMessage.add(softVersion);

        MdcModel mdcModel = new MdcModel();
        mdcModel.setCmdId(CmdId.cmdSignal);
        mdcModel.setVersion(Integer.valueOf(configMqtt.getVersion()));
        mdcModel.setEdgeTime(System.currentTimeMillis() / 1000);
        mdcModel.setProductKey("mdc");
        mdcModel.setClientUuid(configMqtt.getClientId());
        mdcModel.setReported(onlineMessage);
        String topic = configMqtt.getTopicSoft();
        String jsonString = JSONObject.toJSONString(mdcModel);
        boolean b = dzicsMqttClient.sendMessage(topic, jsonString);
        log.debug("发送心跳地址 topic：{}, 信息：{}", topic, jsonString);
        return b;
    }

    /**
     * [{
     * "Status": "设备状态 1:作业 2:待机 3:报警 4:关机",
     * "ts": 1577263339567
     * }, {
     * "变量 1": "值",
     * "ts": 1577263339567
     * }, {
     * " 变量 2 ": "值",
     * "ts": 1577263339567
     * }]
     * Status 设置状态必须填写，1:作业，2:待机、3:报警、4:关机；
     * 当设备为关机状态，不需要发布变量信息，只发布状态 Status 和设备的 Ip 地址，且发布频率为 30s。
     *
     * @param mapList
     * @param assetsEncoding
     * @return
     */
    @Override
    public synchronized  boolean sendRealTimeData(List<Map<String, Object>> mapList, String devceType, Long deviceId, String assetsEncoding) {
        try {
            MdcModel mdcModel = new MdcModel();
            mdcModel.setCmdId(CmdId.realTimeData);
            mdcModel.setVersion(Integer.valueOf(configMqtt.getVersion()));
            mdcModel.setEdgeTime(System.currentTimeMillis() / 1000);
           if (StringUtils.isEmpty(assetsEncoding)){
               if (String.valueOf(IotDeviceType.BY).equals(devceType)) {
                   mdcModel.setProductKey(IotDeviceType.BY_PRO_KEY);
               } else if (String.valueOf(IotDeviceType.SK).equals(devceType)) {
                   mdcModel.setProductKey(IotDeviceType.SK_PRO_KEY);
               } else if (String.valueOf(IotDeviceType.QX).equals(devceType)) {
                   mdcModel.setProductKey(IotDeviceType.QX_PRO_KEY);
               } else if (String.valueOf(IotDeviceType.JC).equals(devceType)) {
                   mdcModel.setProductKey(IotDeviceType.JC_PRO_KEY);
               } else {
                   log.error("为识别设备类型：{}", devceType);
                   return false;
               }
           }else {
               mdcModel.setProductKey(assetsEncoding);
           }
            mdcModel.setClientUuid(String.valueOf(deviceId));
//           判断当前状态是不是关机，如果是只上传状态
            for (Map<String, Object> map : mapList) {
                if(map.containsKey("Status")){
                    Object status = map.get("Status");
                    if(Integer.valueOf(status.toString())==4){
                        List<Map<String, Object>>downMap = new ArrayList<>();
                        downMap.add(map);
                        mdcModel.setReported(downMap);
                    }else{
                        mdcModel.setReported(mapList);
                    }
                }
            }
            String topic = configMqtt.getTopicGateway();
            String jsonString = JSONObject.toJSONString(mdcModel);
            boolean b = dzicsMqttClient.sendMessage(topic, jsonString);
//            log.info("发送实时数据 topic：{}, 信息：{}", topic, jsonString);
            return b;
        } catch (Throwable throwable) {
            log.error("发送实时数据错误: {}", throwable.getMessage(), throwable);
            return false;
        }
    }

    @Override
    public boolean snedDataCmdJc(String msg) {
        return false;
    }


    /**
     * map
     */
    private static void map() {
        Map<String, String> map = new HashMap<>();
        map.put("DeviceID", "");
        map.put("CompanyNo", "");
        map.put("factoryNo", "");
        map.put("AssetNo", "");
        map.put("DeviceName", "");
        map.put("DeviceType", "");
        map.put("CncType", "");
        map.put("SerNum", "");
        map.put("NcVer", "");
        map.put("Axes", "");
        map.put("SpinNum", "");
        map.put("MaxSpeed", "");
        map.put("Status", "");
        map.put("Emg", "");
        map.put("Alarm", "");
        map.put("Mode", "");
        map.put("AxisName", "");
        map.put("MachPos", "");
        map.put("AbsPos", "");
        map.put("RelPos", "");
        map.put("RemPos", "");
        map.put("NcStatus", "");
        map.put("MainPgm", "");
        map.put("MainPgmMsg", "");
        map.put("CurPgm", "");
        map.put("CurPgmMsg", "");
        map.put("CutTime", "");
        map.put("CycSec", "");
        map.put("CurSeq", "");
        map.put("PartCnt", "");
        map.put("CurNcBlk", "");
        map.put("TCode", "");
        map.put("OvFeed", "");
        map.put("OvSpin", "");
        map.put("ActFeed", "");
        map.put("FCode", "");
        map.put("ActSpin ", "");
        map.put("SCode", "");
        map.put("SvTemp", "");
        map.put("SvLoad", "");
        map.put("SpinTemp1", "");
        map.put("SpinLoad1", "");
        map.put("SpinTemp2", "");
        map.put("SpinLoad2", "");
        map.put("AlarmMsg", "");


    }
}
