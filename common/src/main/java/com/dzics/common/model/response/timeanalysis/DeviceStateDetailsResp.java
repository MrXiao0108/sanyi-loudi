package com.dzics.common.model.response.timeanalysis;

import lombok.Data;

import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/10/13.
 * @since
 */
@Data
public class DeviceStateDetailsResp {
    private List<String> yAxis;
    private List<DeviceStateDetailsData> data;

    private List<DeviceStateDetailsData> dataDoor;
    /**
     * 设备数据计算后结果
     */
    private List<DeviceParseData> deviceBaseData;

    private List<DeviceParseDataBase> deviceBaseDataDoor;
}
