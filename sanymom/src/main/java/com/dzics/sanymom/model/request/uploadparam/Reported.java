package com.dzics.sanymom.model.request.uploadparam;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Reported {
    /**
     * 系统编码
     */
    private String reqSys;
    /**
     * 必填 工厂编号
     */
    @JsonProperty(value = "Facility")
    private String Facility;

    /**
     * 订单号
     */
    @JsonProperty(value = "WipOrderNo")
    private String WipOrderNo;
    /**
     * 工序号
     */
    @JsonProperty(value = "OprSequenceNo")
    private String OprSequenceNo;

    /**
     * 顺序号
     */
    @JsonProperty(value = "SequenceNo")
    private String SequenceNo;

    /**
     * 序列号
     */
    @JsonProperty(value = "SerialNo")
    private String SerialNo;

    /**
     * 设备编码
     */
    @JsonProperty(value = "DeviceID")
    private String DeviceID;

    /**
     * 工位
     */
    @JsonProperty(value = "WorkStation")
    private String WorkStation;

    /**
     * 结论
     */
    @JsonProperty(value = "Result")
    private String Result;

    /**
     * 预留参数1	string	32	0	程序号
     */
    private String paramRsrv1;

    /**
     * 预留参数2	string	32	0	工具号
     */
    private String paramRsrv2;

    /**
     * 预留参数3	string	32	0	关重件/非关重件物料号
     */
    private String paramRsrv3;

    /**
     * 当前中控登录人员工编号
     * 预留参数4
     */
    private String paramRsrv4;
    /**
     * 预留参数5
     *
     */
    private String paramRsrv5;

    /**
     * 实测参数数值列表
     */
    @JsonProperty(value = "CharList")
    private List<CharList> CharList;

}
