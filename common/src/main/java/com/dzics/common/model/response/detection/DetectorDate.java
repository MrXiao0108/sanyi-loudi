package com.dzics.common.model.response.detection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "Data数据")
public class DetectorDate {
    @ApiModelProperty(value = "订单号")
    private String momOrder;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "二维码")
    private String productCode;

    @ApiModelProperty(value = "物料号")
    private String materialNo;

    @ApiModelProperty(value = "检测值")
    private List<String> itemData;

    @ApiModelProperty(value = "检测结果")
    private List<String>itemStates;

    @ApiModelProperty(value = "检测时间")
    private String detectorTime;

}
