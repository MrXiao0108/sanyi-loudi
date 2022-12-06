package com.dzics.common.model.response.mom;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GetWorkDateDo {

    @ApiModelProperty(value = "当班计划生产数量")
    private String planNum;

    @ApiModelProperty(value = "当班完工数量")
    private String finishNum;

    @ApiModelProperty(value = "当班报工成功数量")
    private String okNum;

    @ApiModelProperty(value = "当班报工失败数量")
    private String errNum;

}
