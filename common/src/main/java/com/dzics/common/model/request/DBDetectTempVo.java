package com.dzics.common.model.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 产品检测设置默认模板
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
@Data
public class DBDetectTempVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "唯一属性")
    private String detectionId;

    @ApiModelProperty(value = "检测编号")
    private String tableColVal;

    @ApiModelProperty(value = "检测内容")
    private String tableColCon;

    @ApiModelProperty("上限值")
    private BigDecimal upperValue;
    @ApiModelProperty("下限值")
    private BigDecimal lowerValue;
    @ApiModelProperty("标准值")
    private BigDecimal standardValue;
    @ApiModelProperty("偏移值")
    private BigDecimal deviationValue;
    @JsonIgnore
    private String orderId;
    @JsonIgnore
    private String lineType;
}
