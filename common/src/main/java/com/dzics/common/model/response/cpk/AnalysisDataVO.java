package com.dzics.common.model.response.cpk;

import lombok.Data;

@Data
public class AnalysisDataVO {
    private double[] analysisData;
    private Double standValue;
    private Double lowerLimitValue;
    private Double upperLimitValue;
    private CPK cpk;

}
