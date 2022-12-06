package com.dzics.common.model.agv.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SearchNo {
    /**
     * 系统编码	string	20	0	中控系统代号
     */
    @JsonProperty("reqSys")
    private String reqSys;
    /**
     * 工厂编号	string	4	1	中兴默认为：1820
     */
    @JsonProperty("Facility")
    private String Facility;
    /**
     * 订单号	string	12	1	生产订单号
     */
    @JsonProperty("JsonProperty")
    private String wipOrderNo;
    /**
     * 顺序号	string	6	0	订单下发时字段（中兴固定为000000）
     */
    @JsonProperty("SequenceNo")
    private String SequenceNo;
    /**
     * 工序号	string	8	1	所在工序工序号
     */
    @JsonProperty("OprSequenceNo")
    private String OprSequenceNo;
    /**
     * 预留参数1	string	32	0
     */
    private String paramRsrv1;
    /**
     * 预留参数2	string	32	0
     */
    private String paramRsrv2;
    /**
     * 预留参数3	string	32	0
     */
    private String paramRsrv3;
}
