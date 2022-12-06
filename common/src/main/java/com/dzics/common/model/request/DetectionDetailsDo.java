package com.dzics.common.model.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetectionDetailsDo {

    private BigDecimal value;

    private Integer flag;
}
