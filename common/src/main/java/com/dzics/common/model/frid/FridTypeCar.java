package com.dzics.common.model.frid;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 小车类型
 */
@Data
public class FridTypeCar<T> {
    /**
     * 扫描信息
     */
    private T fridAnalysis;
    @ApiModelProperty(value = "车辆编号 A B C", required = true)
    private String basketType;
}
