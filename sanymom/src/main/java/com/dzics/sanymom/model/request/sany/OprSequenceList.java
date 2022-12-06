package com.dzics.sanymom.model.request.sany;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

@Data
public class OprSequenceList {
        /**
         * 工序号
         */
        @JsonProperty(value = "OprSequenceNo")
        private String OprSequenceNo;

        /**
         * 顺序号 用来定义总装线与分装线的关系，如果没有分装线，给的是000000
         */
        @JsonProperty(value = "SequenceNo")
        private String SequenceNo;

        /**
         * 工序名称
         */
        @JsonProperty(value = "OprSequenceName")
        private String OprSequenceName;

        /**
         * 工序类型 自制/外协
         */
       @JsonProperty(value = "OprSequenceType")
       private String OprSequenceType;

        /**
         * 计划开始时间
         */
        @JsonProperty(value = "ScheduledStartDate")
        private Date ScheduledStartDate;

        /**
         * 计划结束时间
         */
        @JsonProperty(value = "ScheduledCompleteDate")
        private Date ScheduledCompleteDate;

        /**
         * 工作中心
         */
        @JsonProperty(value = "WorkCenter")
        private String WorkCenter;


        /**
         * 工作中心描述
         */
        @JsonProperty(value = "WorkCenterName")
        private String WorkCenterName;


        /**
         * 工位
         */
        @JsonProperty(value = "WorkStation")
        @NotEmpty(message = "参数必填")
        private String WorkStation;

        /**
         * 工位描述
         */
        @JsonProperty(value = "WorkStationName")
        private String WorkStationName;


    /**
     * 110	已下达	      SAP生产订单释放后，通过接口下发MOM系统
     * 120	已派工	      调度员将“已下达”生产工单进行派工
     * 121	派工失败	  自动派工出现异常时，如IOT反馈派工信息已接收超时，状态变为派工失败。
     * 125	已接收	      操作员在系统中进行接收后，工序状态由"已派工"转为“已接收”
     * 130	进行中	     工序开工后工序状态转为“进行中”，生产作业人员进行生产
     * 135	暂停	     现场由于异常等情况，暂停现场操作
     * 140	已完工	     生产作业人员对“已完成”工序进行完工操作
     * 150	已报工	     SAP将报工结果反馈给MOM
     * 155	报工失败	 SAP报工失败
     * 160	取消报工	"订单已完工需先进行订单取消完工，才能对工序取消报工，非末道工序取消报工，需将后续工序依次取消报工。"
     * 170	已删除	    "接收SAP的删除工序，工序开工后不可删除。SAP中删除工序后重新下发订单信息，删除工序的状态为已删除，MOM接收订单信息后更新删除工序的状态。"
     * 工序状态
     */
        @JsonProperty(value = "ProgressStatus")
        private int ProgressStatus;

        /**
         * 工序数量工序产出物的数量
         */
        @JsonProperty(value = "Quantity")
        private Integer Quantity;

        /**
         * 组件列表
         */
        @JsonProperty(value = "ComponentList")
        @NotEmpty
        private List<ComponentList> ComponentList;

        private String paramRsrv1;
        private String paramRsrv2;
        private String paramRsrv3;
        private String paramRsrv4;
        private String paramRsrv5;

}
