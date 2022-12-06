package com.dzics.business.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PageUtil<T> {
    @ApiModelProperty(value = "当前页")
    private Integer page=1;
    @ApiModelProperty("每页查询条数")
    private Integer limit=10;
    @ApiModelProperty("查询条件")
    private T data;
}
