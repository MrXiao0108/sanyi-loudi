package com.dzics.common.model.agv.distribution;

import lombok.Data;

@Data
public class MaterialList {
    //生产订单号
    private String wipOrderNo;
    //物料编码
    private String materialNo;
    //数量
    private String quantity;
    //产品序列号
    private String serialNo;
    //顺序号
    private String sourceSequenceNo;
    //工序号
    private String sourceOprSequenceNo;
    //
    private String paramRsrv1;
    //
    private String paramRsrv2;

}
