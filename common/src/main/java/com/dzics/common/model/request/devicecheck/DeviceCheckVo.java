package com.dzics.common.model.request.devicecheck;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DeviceCheckVo {
    @ApiModelProperty("添加不填，修改必填")
    private String checkHistoryId;

    @ApiModelProperty("产线ID")
    private Long lineId;
    @ApiModelProperty("设备id")
    private Long deviceId;
    @ApiModelProperty("巡检类型")
    private String checkType;
    @ApiModelProperty("操作账号")
    private String username;
    @ApiModelProperty("巡检项数组")
    private List<DeviceCheckItemVo> historyItemList;

}
