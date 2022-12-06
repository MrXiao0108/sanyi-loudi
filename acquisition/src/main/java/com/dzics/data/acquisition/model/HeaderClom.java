package com.dzics.data.acquisition.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 表头列检测设置
 *
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since
 */
@Data
public class HeaderClom implements Serializable {
    @ApiModelProperty("列字段值")
    private String colData;
    @ApiModelProperty("列名称")
    private String colName;
}
