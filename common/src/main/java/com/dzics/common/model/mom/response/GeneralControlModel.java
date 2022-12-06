package com.dzics.common.model.mom.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 中控-->总控
 * 生产进度反馈
 * @author Administrator
 */
@Data
public class GeneralControlModel {

    /**
     * 请求ID  必填  单据号，重复发送的请求，此值不变，中控系统编码+年（四位）+月（两位）+日（两位）+6位顺序号
     */
    private String reqId;

    /**
     * 系统编码  必填   中控系统代号
     */
    private String reqSys;
    /**
     * 工厂编号  必填  固定值
     */
    @JsonProperty(value = "Facility")
    private String Facility;
    /**
     * 订单号  必填
     */
    @JsonProperty(value = "WipOrderNo")
    private String WipOrderNo;
    /**
     * 顺序号  必填    固定值000000，对于活塞杆/装配线是否可以不传
     */
    @JsonProperty(value = "SequenceNo")
    private String SequenceNo;
    /**
     * 工序号  必填
     */
    @JsonProperty(value = "OprSequenceNo")
    private String OprSequenceNo;
    /**
     * 实际开始时间  yyyy-mm-dd hh24:mi:ss，ProgressType=0、2必填
     */
    @JsonProperty(value = "ActualStartDate")
    private String ActualStartDate;
    /**
     * 实际结束时间  yyyy-mm-dd hh24:mi:ss，ProgressType=1、3必填
     */
    @JsonProperty(value = "ActualCompleteDate")
    private String ActualCompleteDate;
    /**
     * 设备编号  中控目前无法提供，MOM可以传空值
     */
    @JsonProperty(value = "DeviceID")
    private String DeviceID;
    /**
     * 工序报工类型 必填  0：开工；1：完工；
     */
    @JsonProperty(value = "ProgressType")
    private String ProgressType;
    /**
     * 合格数量 必填 中控是逐个报工，建议改成合格状态字段
     */
    @JsonProperty(value = "Quantity")
    private BigDecimal Quantity;
    /**
     * 不合格数量 必填 与前一个字段合并
     */
    @JsonProperty(value = "NGQuantity")
    private BigDecimal NGQuantity;
    /**
     * 报工员工号   可以传空
     */
    @JsonProperty(value = "EmployeeNo")
    private String EmployeeNo;
    /**
     * 产品物料号 必填
     */
    @JsonProperty(value = "ProductNo")
    private String ProductNo;
    /**
     * 产品序列号 必填 开工时不用输序列号，完工时必输
     */
    @JsonProperty(value = "SerialNo")
    private String SerialNo;

    /**
     * 预留字段1
     */
    private String paramRsrv1;
    /**
     * 预留字段2
     */
    private String paramRsrv2;
    /**
     * 预留字段3
     */
    private String paramRsrv3;
    /**
     * 预留字段4
     */
    private String paramRsrv4;
    /**
     * 预留字段5
     */
    private String paramRsrv5;
    /**
     * 工位编号
     */
    @JsonProperty("WorkStation")
    private String WorkStation;
    /**
     * 工位名称
     */
    @JsonProperty(value = "WorkStationName")
    private String WorkStationName;
    /**
     * 关重件信息
     */
    @JsonProperty(value = "keyaccessoryList")
    private List<KeyAccessoryModel> keyaccessoryList;
}
