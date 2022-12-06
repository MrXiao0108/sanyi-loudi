package com.dzics.mqtt.config;

import com.dzics.mqtt.service.MqttService;
import com.dzics.mqtt.service.DzDataCollectionService;
import com.dzics.mqtt.service.DzDataDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Component
public class MqCusomer {
    @Value("${dzics.cmd.iot.query}")
    private String cmdIot;

    @Autowired
    private MqttService mqttService;
    @Autowired
    private DzDataDeviceService dzDataDeviceService;
    @Autowired
    private DzDataCollectionService dzDataCollectionService;

//    @RabbitListener(queues = "${dzics.cmd.iot.query")
//    public void cuttingToolDetection(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
//        log.info("消费：{}, 队列的消息:{}", cmdIot, msg);
//        try {
//            //      获取所有数控设备
//            List<DzDataDevice> dzDataDevices = dzDataDeviceService.getByType(IotDeviceType.JC);
//            for (DzDataDevice dzDataDevice : dzDataDevices) {
//                Long equipmentId = dzDataDevice.getEquipmentId();
//                String deviceId = dzDataDevice.getDeviceId().toString();
//                String companyCode = dzDataDevice.getCompanyCode();
//                String factoryCode = dzDataDevice.getFactoryCode();
//                String assetsEncoding = dzDataDevice.getAssetsEncoding();
//                String deviceName = dzDataDevice.getDeviceName();
//                String deviceTypeCode = dzDataDevice.getDeviceTypeCode();
////      根据设备ID 加载 最新值数据
//                DzDataCollection dzDataCollections = dzDataCollectionService.getDeviceIdDzDataColl(equipmentId);
//                if (dzDataCollections != null) {
//                    Map<String, Object> mps = new HashMap<>();
////            基础数据
//                    mps.put("DeviceID", deviceId);
//                    mps.put("CompanyNo", companyCode);
//                    mps.put("factoryNo", factoryCode);
//                    mps.put("AssetNo", assetsEncoding);
//                    mps.put("DeviceName", deviceName);
//                    mps.put("DeviceType", deviceTypeCode);
////                状态信息
//                    Map<String, Object> mpl = JSONObject.parseObject(JSON.toJSONString(dzDataCollections));
//                    mps.put("Status", mpl.get(JcCmd.Status));
////                TODO   自定义信息 检测数据
//                    mps.put("detection", mpl.get(JcCmd.JCData));
//                    mqttService.sendRealTimeData(Arrays.asList(mps));
//                } else {
//                    log.warn("上报检测设备采集数据错误,根据设备equipmentId:{} 查询值:{}", equipmentId, dzDataCollections);
//                }
//            }
//            boolean bool = mqttService.snedDataCmdJc(msg);
//            channel.basicAck(deliveryTag, true);
//        } catch (Throwable e) {
//            log.error("消费数据处理失败:" + "dzics-dev-gather-v1-queue" + " 队列的消息：" + msg, e);
//            channel.basicReject(deliveryTag, false);
//        }
//
//    }


}
