package com.dzics.common.model.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dzics.common.model.request.base.DetectTempVoBase;
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
public class DzDetectTempVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "唯一属性")
    private String detectionId;

    @ApiModelProperty(value = "检测编号")
    private String tableColVal;

    @ApiModelProperty(value = "检测内容")
    private String tableColCon;
    @ApiModelProperty(value = "0展示1不展示")
    private Integer isShow;
    @ApiModelProperty("智能检测 0展示 1不展示")
    private Integer whetherShow;
    @ApiModelProperty(value = "补偿值")
    private BigDecimal compensationValue;

    @ApiModelProperty("组标识")
    private String groupId;

    @JsonIgnore
    private String orderId;
    @JsonIgnore
    private String lineType;
}
