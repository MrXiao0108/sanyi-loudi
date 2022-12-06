package com.dzics.common.model.request.devicecheck;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CheckTypeVo {
    @ApiModelProperty("ID")
    private String checkTypeId;
    @ApiModelProperty("巡检设备类型ID")
    private String checkItemId;
    @ApiModelProperty("字典值表ID")
    @NotNull(message = "字典值表ID，不能为空")
    private Long dictItemId;
    @NotNull(message = "dict_code，不能为空")
    @ApiModelProperty("dict_code")
    private String dictCode;
    @ApiModelProperty("是否选中 true选中，fale未选中")
    @NotNull(message = "是否选中，不能为空")
    private Boolean checked;
    @ApiModelProperty("巡检项类型名称")
    @NotEmpty(message = "巡检项类型名称，不能为空")
    private String itemText;
}
