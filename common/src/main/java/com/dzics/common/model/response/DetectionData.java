package com.dzics.common.model.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 检测设备数据
 *
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since
 */
@Data
public class DetectionData implements Serializable {
    private BigDecimal dataVal;
    private String tableColVal;
    private String tableColCon;
    private BigDecimal standardValue;
    private BigDecimal upperValue;
    private BigDecimal lowerValue;
    private BigDecimal compensationValue;
    private Integer isQualified;
    private Integer allState;
}
