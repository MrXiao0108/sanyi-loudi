package com.dzics.kanban.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @param
 * @author zhangchengjun
 */
@Data
public class PageLimit {
    @ApiModelProperty("当前页")
    private Integer page=1;
    @ApiModelProperty("每页查询条数")
    private Integer limit=10;

}
