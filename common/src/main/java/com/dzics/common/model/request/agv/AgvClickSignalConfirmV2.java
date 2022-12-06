package com.dzics.common.model.request.agv;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * agv 到料信号手动点击 参数 触发机器人读取物料信息
 */
@Data
public class AgvClickSignalConfirmV2 {

    /**
     * 物料信息ID
     */
    @ApiModelProperty(value = "物料信息ID", required = true)
    @NotBlank(message = "消息ID必传")
    private String receiveMaterialId;

    /**
     * 校验结果 ture 通过 false 校验失败
     */
    @ApiModelProperty(value = "校验结果 true 通过 false 校验失败",required = true)
    @NotNull(message = "检验结果必填")
    private Boolean okNg;
}
