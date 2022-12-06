package com.dzics.data.acquisition.service.qrcode.impl;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.enums.EquiTypeEnum;
import com.dzics.common.model.entity.SysRealTimeLogs;
import com.dzics.common.model.constant.DzUdpType;
import com.dzics.common.model.constant.LogClientType;
import com.dzics.data.acquisition.service.DeviceStatusPush;
import com.dzics.data.acquisition.service.qrcode.QrcodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class QrcodeServiceImpl implements QrcodeService {

    @Autowired
    private DeviceStatusPush deviceStatusPush;
    @Value("${accq.read.cmd.queue.equipment.realTime}")
    private String queueRealTimeEquipment;

    /**
     * | Q,7,1400,orderNo,lineNo        | 上发需要填写信息指令 | 上发 |
     * | ------------------------------ | -------------------- | ---- |
     * | Q,7,1401,orderNo,lineNo,二维码 | 下发二维码信息指令   | 下发 |
     * |                                |                      |      |
     *
     * @param split
     */
    @Override
    public void qrcodeControl(String[] split) {
        String cmd = split[2];
        String orderNo = split[3];
        String lineNo = split[4];
        if (DzUdpType.QR_CODE_INOUT.equals(cmd)) {
//            发送到前端提示需要输入二维码
            deviceStatusPush.sendInputQrCode(orderNo, lineNo);
        } else {
//            发送到日志提示底层接收成功
            String qrCode = split[5];
            SysRealTimeLogs timeLogs = new SysRealTimeLogs();
            timeLogs.setMessageId(UUID.randomUUID().toString().replaceAll("-", ""));
            timeLogs.setQueueName(queueRealTimeEquipment);
            timeLogs.setClientId(LogClientType.ROB_QR_CODE);
            timeLogs.setOrderCode(orderNo);
            timeLogs.setLineNo(lineNo);
            timeLogs.setDeviceType(String.valueOf(EquiTypeEnum.AVG.getCode()));
            timeLogs.setDeviceCode(LogClientType.ROB_QR_CODE);
            timeLogs.setMessageType(1);
            if (DzUdpType.QR_CODE_RECEIVE_OK.equals(cmd)) {
                timeLogs.setMessage("机器人回复收到写入二维码: " + qrCode + " 指令");
            } else if (DzUdpType.QR_CODE_INOUT_OK.equals(cmd)) {
                String okNg = split[6];
                if (okNg.equals("1")) {
                    timeLogs.setMessage("机器人回复二维码: " + qrCode + " 写入指令成功");
                } else {
                    timeLogs.setMessage("机器人回复二维码 " + qrCode + " 写入指令失败");
                }
            }else {
                timeLogs.setMessage("为识别回复指令 " + JSONObject.toJSONString(split) + " 是否写入成功");
            }
            timeLogs.setTimestampTime(new Date());
            deviceStatusPush.sendSysRealTimeLogs(timeLogs);
        }

    }
}
