package com.dzics.common.model.response.charts;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class EquipmentDataChartsListDo {
    private Long equipmentId;
    private String equipmentName;
    private Integer equipmentType;
    @ApiModelProperty("true展示，false不展示")
    private boolean isShow;
    @ApiModelProperty("设备生产数据")
    private List<Long> equipmentData;
    @ApiModelProperty("设备生产日期")
    private List<String> dateData;
}
