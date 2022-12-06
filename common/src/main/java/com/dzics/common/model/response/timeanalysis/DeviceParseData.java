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
public class DeviceParseData extends DeviceParseDataBase{

    /**
     * 作业率
     */
    private String operationRate = "0.00";

    /**
     * 作业时长
     */
    private Double operationDuration = 0D;
    /**
     * 待机时长
     */
    private Double standbyDuration = 0D;
    /**
     * 故障时长
     */
    private Double faultDuration = 0D;
    /**
     * 停机时长
     */
    private Double shutdownDuration = 0D;
    /**
     * 设备标准做作业率
     * */
    private Float standardOperationRate;


}
