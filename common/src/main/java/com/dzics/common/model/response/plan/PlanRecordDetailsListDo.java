package com.dzics.common.model.response.plan;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PlanRecordDetailsListDo {
    @ApiModelProperty("站点名称")
    private String departName;
    @ApiModelProperty("产线名称")
    private String lineName;
    @ApiModelProperty("产品名称")
    private String productName;
    @ApiModelProperty("产品id")
    private String productNo;
    @ApiModelProperty("产品类型")
    private String productType;

    @ApiModelProperty("总数")
    private Long totalNum;
    @ApiModelProperty("毛坯")
    private Long roughNum;
    @ApiModelProperty("不合格")
    private Long badnessNum;
    @ApiModelProperty("成品")
    private Long qualifiedNum;
}
