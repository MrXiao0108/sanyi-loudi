package com.dzics.common.model.response.homepage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GetEquipmentStateDo {


    @ApiModelProperty("当前产线名称")
    private String lineName;
    @ApiModelProperty("设备名称")
    private String equipmentName;
    @ApiModelProperty("设备连接状态")
    private String connectState;
    @ApiModelProperty("设备运行状态")
    private String runStatus;
    @ApiModelProperty("停机次数")
    private Long downNum;
    @ApiModelProperty("停机次数")
    private String alarmStatus;
    @ApiModelProperty("当前生产产品名称")
    private String productName;
    @ApiModelProperty("当前生产产品编号")
    private String productNo;


}
