package com.dzics.common.model.response.charts;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ActivationDo {
    @ApiModelProperty("产线id")
    private Long lineId;

    @ApiModelProperty(value = "产线名称")
    private String lineName;

    @ApiModelProperty("产线稼动率")
    private List<BigDecimal> activationData;

    @ApiModelProperty("产线稼动率")
    private List<String> dateData;

    @ApiModelProperty("true展示，false不展示")
    private boolean isShow;
}
