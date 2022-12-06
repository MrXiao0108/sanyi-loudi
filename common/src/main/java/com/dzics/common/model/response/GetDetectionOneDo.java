package com.dzics.common.model.response;

import lombok.Data;

@Data
public class GetDetectionOneDo {

    private String producBarcode;
    //产品编号
    private String productNo;
    //产品名称
    private String productName;
    //检测值
    private String  detectValue;
    //检测结果值  （检测结果 1 ok 0 NG）
    private Integer detectOutOk;
    //检测时间
    private String detectorTime;
    //检测项名称（所有产品的检测项字段名都一样）
    private String tableColCon;
}
