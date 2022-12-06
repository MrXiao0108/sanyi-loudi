package com.dzics.sanymom.model.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 工序组
 */
@Data
public class ProcessComponents {
    private String SequenceNo;//	顺序号
    private String WorkCenterName;//	工作中心描述
    private BigDecimal Quantity;//	工序数量
    private String OprSequenceType;//	工序类型
    private String paramRsrv3;
    private String paramRsrv4;
    private String paramRsrv5;
    private List<ComponentModel> ComponentList;//	组件列表


//    private String OprSequenceNo;//	工序号
//    private String OprSequenceName;//	工序名称c
//    private String ScheduledStartDate;//	计划开始时间
//    private String ScheduledCompleteDate;//	计划结束时间
//    private String WorkCenter;//	工作中心
//
//    private String WorkStation;//	工位
//    private String WorkStationName;//	工位描述
//    private String ProgressStatus;//	工序状态
}
