package com.dzics.common.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 检测配置模板
 *
 * @author ZhangChengJun
 * Date 2021/2/19.
 * @since
 */
@Data
public class ProductTemp implements Serializable {
    private Long detectionId;
    private BigDecimal compensationValue;
    private BigDecimal upperValue;
    private BigDecimal lowerValue;
    private BigDecimal standardValue;
}
