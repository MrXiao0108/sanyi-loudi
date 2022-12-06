package com.dzics.sanymom.model.request.sany;

/**
 * Copyright 2021 bejson.com
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.util.List;


@Data
public class Task {

    /**
     * 订单号
     */
    @JsonProperty(value = "WipOrderNo")
    private String WipOrderNo;
    /**
     * 订单类型  1：正常订单；2：返工返修订单
     */
    @JsonProperty(value = "WipOrderType")
    private String WipOrderType;

    /**
     * 产线 例如1.5m/2.5m/活塞杆/缸筒/装配线
     */
    @JsonProperty(value = "ProductionLine")
    private String ProductionLine;

    /**
     * 工作中心  可为空
     */
    @JsonProperty(value = "WorkCenter")
    private String WorkCenter;

    /**
     * 组 用来确定唯一工艺路线
     */
    @JsonProperty(value = "WipOrderGroup")
    private String WipOrderGroup;


    /**
     * 组计数器 用来确定唯一工艺路线
     */
    @JsonProperty(value = "GroupCount")
    private String GroupCount;

    /**
     * 订单物料 物料编码
     */
    @JsonProperty(value = "ProductNo")
    private String ProductNo;


    /**
     * 订单物料描述
     */
    @JsonProperty(value = "ProductName")
    private String ProductName;

    /**
     * 订单物料简码  中兴专用
     */
    @JsonProperty(value = "ProductAlias")
    private String ProductAlias;


    /**
     * 序列号  工件ID码/SN号
     */
    @JsonProperty(value = "SerialNo")
    private List<String> SerialNo;

    /**
     * 工厂 1820-中兴
     */
    @JsonProperty(value = "Facility")
    private String Facility;


    /**
     * 数量  订单工件数量
     */
    @JsonProperty(value = "Quantity")
    private Integer Quantity;

    /**
     * 计划开始时间 yyyy-mm-dd hh24:mi:ss
     */
    @JsonProperty(value = "ScheduledStartDate")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private String ScheduledStartDate;


    /**
     * 计划结束时间  yyyy-mm-dd hh24:mi:ss
     */
    @JsonProperty(value = "ScheduledCompleteDate")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private String ScheduledCompleteDate;


    /**
     * 订单状态
     * 110	已下达	SAP生产订单释放后，通过接口下发MOM系统
     * 120	进行中	首道工序开工后，订单状态转为 进行中
     * 130	已完工	最后一道工序全数报完工后，订单状态转为已完工
     * 140	已删除	接收SAP的删除订单，开工之前可以进行删除
     * 150	强制关闭 	接收SAP的强制关闭信息，中断现场操作
     */
    @JsonProperty(value= "ProgressStatus")
    private String ProgressStatus;

    /**
     *
     */
    private String paramRsrv1;
    /**
     *
     */
    private String paramRsrv2;
    /**
     *
     */
    private String paramRsrv3;

    /**
     *
     */
    private String paramRsrv4;

    /**
     *
     */
    private String paramRsrv5;


    /**
     * 工序组
     */
    @JsonProperty(value = "OprSequenceList")
    @NotEmpty
    private List<OprSequenceList> OprSequenceList;


}
