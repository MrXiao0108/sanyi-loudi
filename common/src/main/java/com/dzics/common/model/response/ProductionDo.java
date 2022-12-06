package com.dzics.common.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * @author xnb
 * @date 2021年06月01日 16:45
 */

@Data
public class ProductionDo {

    @ApiModelProperty("时")
    private List<String> xAxis = Arrays.asList("0时", "1时", "2时", "3时", "4时", "5时", "6时", "7时", "8时", "9时", "10时", "11时", "12时", "13时", "14时", "15时", "16时", "17时", "18时", "19时", "20时", "21时", "22时", "23时");

    @ApiModelProperty("每小时产量")
    private List<DevcieNameHourSum> series;

    @ApiModelProperty("名称")
    private List<String> legend;

}

