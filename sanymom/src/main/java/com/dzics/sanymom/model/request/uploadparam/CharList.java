package com.dzics.sanymom.model.request.uploadparam;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CharList {
    /**
     * 必填
     * 参数编码 (RealTorque/RealAngle/StartTime/EndTime)
     */
    @JsonProperty(value = "Characteristic")
    private String Characteristic;
    /**
     * 参数名称 (力矩/角度/开始时间/结束时间)
     */
    @JsonProperty(value = "CharacteristicName")
    private String CharacteristicName;

    /**
     * 定量信息
     * 数值
     * 必填
     */
    @JsonProperty(value = "Value")
    private BigDecimal Value;
    /**
     * 标准值
     */
    @JsonProperty(value = "TargetValue")
    private BigDecimal TargetValue;
    /**
     * 上限值
     */
    @JsonProperty(value = "UpperLimit")
    private BigDecimal UpperLimit;

    /**
     * 下限值
     */
    @JsonProperty(value = "LowerLimit")
    private BigDecimal LowerLimit;
    /**
     * 属性 （定性信息，如外观合格、不合格、开始时间、结束时间）
     */
    @JsonProperty(value = "Attribute")
    private String Attribute;
    /**
     * 预留参数1
     */
    private String paramRsrv1;
    /**
     * 预留参数2
     */
    private String paramRsrv2;


}
