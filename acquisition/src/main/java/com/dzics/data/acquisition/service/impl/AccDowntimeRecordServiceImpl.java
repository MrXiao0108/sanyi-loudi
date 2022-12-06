package com.dzics.data.acquisition.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.dao.DzEquipmentDowntimeRecordMapper;
import com.dzics.common.enums.DeviceSocketSendStatus;
import com.dzics.common.model.response.*;
import com.dzics.common.model.response.feishi.DayDataDo;
import com.dzics.common.service.DzProductService;
import com.dzics.data.acquisition.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author ZhangChengJun
 * Date 2021/3/1.
 */
@Service
@Slf4j
public class AccDowntimeRecordServiceImpl implements AccDowntimeRecordService {
    @Autowired
    private AccqDzEquipmentService accqDzEquipmentService;
    @Autowired
    private DzEquipmentDowntimeRecordMapper dzEquipmentDowntimeRecordMapper;

    @Autowired
    private DzProductService dzProductService;
    @Autowired
    private LineDataService lineDataService;
    @Autowired
    @Lazy
    private CacheService cacheService;
    @Autowired
    private AccEquipmentAlarmRecordService alarmRecordService;


    @Override
    public String allDeviceResultDay() {
        //              日生产
        DayDataDo resultDay = lineDataService.dayData();
        //                      月生产
        JCEquimentBase jcEquimentBase = new JCEquimentBase();
        jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_NISSAN.getInfo());
        jcEquimentBase.setData(resultDay);
        return JSONObject.toJSONString(Result.ok(jcEquimentBase));
    }

    @Override
    public String allDeviceResultMouth() {
        //                      月生产
        DayDataDo resultMouth = lineDataService.monthData();
        JCEquimentBase jcEquimentBase = new JCEquimentBase();
        jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_MONTHLY_OUTPUT.getInfo());
        jcEquimentBase.setData(resultMouth);
        return JSONObject.toJSONString(Result.ok(jcEquimentBase));
    }
}
