package com.dzics.sanymom.model.request;

import lombok.Data;

/**
 * 移动空料框
 */
@Data
public class MoveFrameParms {

    /**
     *  请求ID	     		单据号，重复发送的请求，此值不变，中控系统编码+年（四位）+月（两位）+日（两位）+6位顺序号
     */
    private String reqId;
    /**
     * 系统编码	     		中控系统代号
     */
    private String reqSys;
    /**
     * 工厂编号	     		固定值
     */
    private String Facility;

    /**
     * 操作编码	     		0：空料框需求；1：空料框移出
     */
    private String reqType;
    /**
     * 料框类型	     		reqType为0时必须
     */
    private String palletType;
    /**
     * 料框编码	     		reqType为1时必须
     */
    private String palletNo;
    /**
     * 需求料点编码	 		reqType=0：下料点编码；1：上料点编码
     */
    private String sourceNo;
    /**
     * 需求时间      		需求AGV到达时间
     */
    private String requireTime;
    /**
     * 发送时间	     		中控发送时间
     */
    private String sendTime;
    /**
     * 预留参数1
     */
    private String paramRsrv1;
    /**
     * 预留参数2
     */
    private String paramRsrv2;
    /**
     * 预留参数3
     */
    private String paramRsrv3;
}
