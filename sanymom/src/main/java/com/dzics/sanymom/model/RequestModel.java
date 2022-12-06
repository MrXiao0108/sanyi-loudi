package com.dzics.sanymom.model;

import com.dzics.sanymom.model.request.ProcessComponents;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 消息内容
 */
@Data
public class RequestModel {

    //订单号   必填
    private String WipOrderNo;
    //
    private String[]SerialNo;
    //
    private String WipOrderType;

    private String ProductName;

    private String GroupCount;

    private String WipOrderGroup;

    private BigDecimal Quantity;

    private String ProductNo;

    private String ScheduledStartDate;

    private String ProgressStatus;

    private String ProductionLine;
    //	工序组
    private List<ProcessComponents> OprSequenceList;

    private String paramRsrv2;
    private String paramRsrv3;
    private String paramRsrv4;
    private String paramRsrv5;


}
