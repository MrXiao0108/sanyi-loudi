package com.dzics.common.model.request.toolinfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GetToolInfoDataListVo {
    @ApiModelProperty("产线id")
    private String lineId;
    @ApiModelProperty("机床编号")
    private String equipmentNo;
    @ApiModelProperty("刀具组编号")
    private Integer groupNo;

    @ApiModelProperty("参数不填")
    private String orgCode;

    @ApiModelProperty("排序字段")
    private String field;
    @ApiModelProperty("ASC OR DESC OR 空字符串")
    private String type;
}
