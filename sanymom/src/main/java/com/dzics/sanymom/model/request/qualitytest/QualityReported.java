package com.dzics.sanymom.model.request.qualitytest;

import lombok.Data;

import java.util.List;

@Data
public class QualityReported {

        //订单号
        private String WipOrderNo;
        //工序号
        private String OprSequenceNo;
        //顺序号
        private String SequenceNo;
        //产品序列号
        private String SerialNo;
        //系统编码
        private String reqSys;
        //设备编码
        private String DeviceID;
        //工厂编号
        private String Facility;
        //结论
        private String Result;
        //实测参数值列表
        private List<QualityCharList> CharList;

}
