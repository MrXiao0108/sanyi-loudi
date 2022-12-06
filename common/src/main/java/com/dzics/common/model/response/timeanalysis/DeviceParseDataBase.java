package com.dzics.common.model.response.timeanalysis;

import lombok.Data;

/**
 * 设备用时分析后数据
 *
 * @author ZhangChengJun
 * Date 2021/10/27.
 * @since
 */
@Data
public class DeviceParseDataBase {

    private String equipmentNo;
    private String doorCode;
    /**
     * 设备类型
     */
    private String deviceType;
    /**
     * 设备名称
     */
    private String deviceName;


}
