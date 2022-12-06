package com.dzics.common.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @param
 * @author zhangchengjun
 */
@Data
public class PageLimitBase {
    @ApiModelProperty("当前页")
    private int page = 1;
    @ApiModelProperty("每页查询条数")
    private int limit = 10;

    @ApiModelProperty("排序字段")
    private String field;
    @ApiModelProperty("ASC OR DESC OR 空字符串")
    private String type;
}
