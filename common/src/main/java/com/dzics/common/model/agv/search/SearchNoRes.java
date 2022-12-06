package com.dzics.common.model.agv.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SearchNoRes {
    /**
     * 订单号
     */
    private String wipOrderNo;
    /**
     * 顺序号
     */
    private String SequenceNo;
    /**
     * 本工序工序号
     */
    @JsonProperty("OprSequenceNo")
    private String OprSequenceNo;
    /**
     * 下工序工序号
     */
    private String nextOprSeqNo;
}
