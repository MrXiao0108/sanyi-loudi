package com.dzics.sanymom.model.request;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 组件列表
 */
@Data
public class ComponentModel {
    private String ReserveNo;
    private String ReserveLineNo;
    private String MaterialNo;//	组件物料	string
    private BigDecimal Quantity;//	组件数量	decimal
    private String MaterialName;//	组件物料描述	string
    private String Unit;
    private String MaterialAlias;//	组件物料简码	string
    private String paramRsrv1;//	预留参数1	string
    private String paramRsrv2;//	预留参数2	string
    private String paramRsrv3;//	预留参数2	string
    private String paramRsrv4;//	预留参数2	string
    private String paramRsrv5;//	预留参数2	string


}
