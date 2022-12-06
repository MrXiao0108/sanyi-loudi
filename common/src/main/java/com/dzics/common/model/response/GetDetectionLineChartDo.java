package com.dzics.common.model.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class GetDetectionLineChartDo {

    private String productNo;
    private String productName;
    private List<Map<String,Object>> data;

}
