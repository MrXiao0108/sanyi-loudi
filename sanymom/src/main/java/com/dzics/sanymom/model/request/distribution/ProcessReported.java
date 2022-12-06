package com.dzics.sanymom.model.request.distribution;

import lombok.Data;

import java.util.List;

@Data
public class ProcessReported {
    //请求ID
    private String reqId;
    //系统编码
    private String reqSys;
    //工厂编号
    private String Facility;
    //操作编码
    private String reqType;
    //料框类型
    private String palletType;
    //投料点编码
    private String sourceNo;

    //料框编码
    private String palletNo;

    //需求时间
    private String requireTime;
    //发送时间
    private String sendTime;
    //
    private List<MaterialList> materialList;


}
