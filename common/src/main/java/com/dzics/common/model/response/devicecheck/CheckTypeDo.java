package com.dzics.common.model.response.devicecheck;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CheckTypeDo {
    @ApiModelProperty("主键")
    private String checkTypeId;
    @ApiModelProperty("巡检设备类型ID")
    private String checkItemId;

    @ApiModelProperty("巡检类型名称")
    private String itemText;
    @ApiModelProperty("dict_code")
    private String dictCode;
    @ApiModelProperty("是否选中 true选中，fale未选中")
    private Boolean checked;
}
