package com.dzics.sanymom.model.request.qualitytest;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class QualityCharList {

    //测量值
    private BigDecimal Value;
    //标准值
    private BigDecimal TargetValue;
    //下限值
    private BigDecimal LowerLimit;
    //上限值
    private BigDecimal UpperLimit;
    //参数名称
    private String Characteristic;
    //参数编码
    private String CharacteristicName;

}
