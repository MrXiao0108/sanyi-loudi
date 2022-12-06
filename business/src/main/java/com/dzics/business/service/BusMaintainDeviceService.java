package com.dzics.business.service;

import com.dzics.common.model.request.base.BaseTimeLimit;
import com.dzics.common.model.request.device.maintain.*;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.device.maintain.MaintainRecord;
import com.dzics.common.model.response.device.maintain.MaintainRecordDetails;

/**
 * 保养接口
 *
 * @author ZhangChengJun
 * Date 2021/9/29.
 * @since
 */
public interface BusMaintainDeviceService {

    Result getMaintainList(String sub, BaseTimeLimit pageLimit, MaintainDeviceParms parmsReq);

    Result addMaintainDevice(String sub, AddMaintainDevice parmsReq);

    Result updateMaintainDevice(String sub, AddMaintainDevice parmsReq);

    Result getMaintainRecord(String sub, BaseTimeLimit pageLimit, MaintainRecordParms parmsReq);

    Result getMaintainRecordDetails(String sub, MaintainDetailsParms parmsReq);

    Result addMaintainRecord(String sub, AddMaintainRecord parmsReq);

    Result delMaintainDevice(String sub, String maintainId);
}
