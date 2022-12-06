package com.dzics.sanymom.model.request.searchframe;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2021/11/11.
 * @since 查询料框信息封装
 */
@Data
public class MaterialFrame {

    /**
     *系统编码
     */
    @JsonProperty("reqSys")
    private String reqSys;
    /**
     *工厂编号
     */
    @JsonProperty("Facility")
    private String Facility;
    /**
     *料框编码
     */
    @JsonProperty("palletNo")
    private String palletNo;
    /**
     *料点编码
     */
    @JsonProperty("pointNo")
    private String pointNo;
    /**
     *预留参数1 物料编号
     */
    @JsonProperty("paramRsrv1")
    private String paramRsrv1;
    /**
     *预留参数2
     */
    @JsonProperty("paramRsrv2")
    private String paramRsrv2;
    /**
     *预留参数3
     */
    @JsonProperty("paramRsrv3")
    private String paramRsrv3;
}
