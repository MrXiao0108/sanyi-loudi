package com.dzics.common.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
@Data
public class GetMonthlyCapacityListDo {

    @ApiModelProperty("合格")
    private List<Long> qualifiedList;
    @ApiModelProperty("不合格")
    private List<Long> badnessList;
}
