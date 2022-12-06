package com.dzics.common.model.statistics;

import lombok.Data;

import java.time.LocalDate;

/**
 * @Classname DeviceMakeTotalDto
 * @Description 生产生产总数量 参数
 * @Date 2022/6/9 10:37
 * @Created by NeverEnd
 */
@Data
public class DeviceMakeTotalDto {
    public DeviceMakeTotalDto(LocalDate workDate, Long deviceId, String tableName) {
        this.workDate = workDate;
        this.deviceId = deviceId;
        this.tableName = tableName;
    }

    /**
     * 查询日期
     */
    private LocalDate workDate;

    /**
     * 设备ID
     */
    private Long deviceId;

    /**
     * 查询表名称
     * 可以是：dz_equipment_pro_total_signal
     *         dz_equipment_pro_total
     */
    private String tableName;

}
