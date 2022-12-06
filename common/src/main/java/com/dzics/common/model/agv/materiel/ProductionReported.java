package com.dzics.common.model.agv.materiel;

import lombok.Data;

import java.util.List;

@Data
public class ProductionReported {
        private String SequenceNo;//顺序号
        private String reqSys;//系统编码
        private List<ProductionMaterialList> materialList;//物料清单
        private String palletType;//料框类型
        private String sourceNo;//上料点编码
        private String palletNo;//料框编码
        private String reqId;//请求ID
        private String wipOrderNo;//生产订单号
        private String requireTime;//需求时间
        private String sendTime;//发送时间
        private String OprSequenceNo;//工序号
        private String reqType;//操作编码
        private String Facility;//工厂编号
        private String paramRsrv1;
        private String paramRsrv3;
        private String paramRsrv2;

}
