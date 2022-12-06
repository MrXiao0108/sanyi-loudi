package com.dzics.common.model.response;

import lombok.Data;

@Data
public class EquipmentAlarmVo {

    private Long lineId;
    private String equipmentNo;
    private String alarmText;
    private String startTime;
    private String endTime;

}
