package com.dzics.business.model.vo.detectordata.edit;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 对比值修改
 *
 * @author ZhangChengJun
 * Date 2021/2/8.
 * @since
 */
@Data
public class DbProDuctileEditer {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "唯一属性")
    private Long detectionId;

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
    @ApiModelProperty("是否展示")
    private Integer isShow;
    @ApiModelProperty("补偿值")
    private BigDecimal compensationValue;
    @ApiModelProperty("智能检测 0展示 1不展示")
    private Integer whetherShow;

}
