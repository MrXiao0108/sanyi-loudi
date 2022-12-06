package com.dzics.common.model.response.feishi;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DayDataResultDo {
    private String date;
    private BigDecimal badnessNum;
    private BigDecimal qualifiedNum;
}
