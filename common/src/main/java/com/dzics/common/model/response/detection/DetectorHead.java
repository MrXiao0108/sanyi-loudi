package com.dzics.common.model.response.detection;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "Head数据")
public class DetectorHead {
    @ApiModelProperty(value = "订单号")
    private final String momOrder = "订单号";

    @ApiModelProperty(value = "产品名称")
    private final String productName = "产品名称";

    @ApiModelProperty(value = "二维码")
    private final String productCode = "二维码";

    @ApiModelProperty(value = "物料号")
    private final String materialNo = "物料号";

    @JsonAlias
    @ApiModelProperty(value = "检测项Id")
    private List<String>itemIds;

    @ApiModelProperty(value = "检测项")
    private List<String> itemNames;

    @ApiModelProperty(value = "检测结果")
    private List<String>itemValues;

    @ApiModelProperty(value = "检测时间")
    private final String detectorTime = "检测时间";
}
