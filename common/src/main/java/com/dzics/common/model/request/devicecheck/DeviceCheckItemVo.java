package com.dzics.common.model.request.devicecheck;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class DeviceCheckItemVo {
    @ApiModelProperty("巡检项id 添加不填  编辑必填")
    private String checkHistoryItemId;
    @ApiModelProperty("巡检项名称")
    @NotBlank(message = "巡检项名称不能为空")
    private String checkName;
    @ApiModelProperty("是否选中")
    @NotNull(message = "是否选中不能为空")
    private Boolean checked;
    @ApiModelProperty("巡检问题描述")
    @NotBlank(message = "巡检问题描述不能为空")
    private String contentText;
}

