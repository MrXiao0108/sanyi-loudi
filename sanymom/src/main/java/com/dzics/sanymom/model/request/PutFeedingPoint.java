package com.dzics.sanymom.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 更新投料点
 */
@Data
public class PutFeedingPoint {
    /**
     * 系统编码
     */
    private String reqSys;
    /**
     * 工厂编号
     */
    @JsonProperty(value = "Facility")
    private String Facility;
    /**
     *料框编码
     */
    private String palletNo;
    /**
     *投料点编码
     */
    private String sourceNo;
    /**
     *料点类型
     */
    private String pointType;
    /**
     *料点状态
     * 0：空（允许送料）；1：有框（不允许送料）
     */
    private String pointState;
    /**
     *料框状态
     * 0：空；1：非空；不填为不更新
     */
    private String palletState;
    /**
     *上下料点标志
     */
    private String pointFlag;
    /**
     *预留参数1
     */
    private String paramRsrv1;
    /**
     *预留参数2
     */
    private String paramRsrv2;
    /**
     *预留参数3
     */
    private String paramRsrv3;

}
