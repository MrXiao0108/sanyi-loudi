package com.dzics.common.model.statistics;

import lombok.Data;

/**
 * @Classname DeviceMonthDto
 * @Description 设备月产量查询参数
 * @Date 2022/6/9 10:56
 * @Created by NeverEnd
 */
@Data
public class DeviceMonthDto {
    public DeviceMonthDto(String mouth, Long deviceId, String tableName) {
        this.mouth = mouth;
        this.deviceId = deviceId;
        this.tableName = tableName;
    }

    /**
     * 查询日期
     */
    private String mouth;

    /**
     * 设备ID
     */
    private Long deviceId;

    /**
     * 查询表名称
     * 可以是：dz_equipment_pro_total_signal_month
     *         dz_equipment_pro_total_month
     */
    private String tableName;
}
