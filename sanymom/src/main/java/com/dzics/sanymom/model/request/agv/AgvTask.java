package com.dzics.sanymom.model.request.agv;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AgvTask {
        //请求ID
        private String reqId;
        //系统代号
        private String reqSys;
        //工厂
        @JsonProperty(value = "Facility")
        private String Facility;
        //操作编码
        private String reqType;
        //料框类型
        private String palletType;
        //料框编码
        private String palletNo;
        //配送起点编码
        private String sourceNo;
        //配送终点编码
        private String destNo;
        //订单编号
        private String order_id;
        //派车辆
        private String agv_id;
        //订单阶段状态
        private String order_Status;
        //AGV反馈到达时间
        private String arriveTime;
        //产线
        @JsonProperty(value = "ProductionLine")
        private String ProductionLine;
        //工作中心
        @JsonProperty(value = "WorkCenter")
        private String WorkCenter;
        //请求编码
        private String reqCode;
        private String paramRsrv1;
        private String paramRsrv2;
        private String paramRsrv3;
        private String paramRsrv4;
        private String paramRsrv5;


}
