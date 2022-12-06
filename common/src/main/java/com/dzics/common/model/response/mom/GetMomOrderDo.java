package com.dzics.common.model.response.mom;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.dzics.common.model.write.MomOrderExcelWrite;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class GetMomOrderDo {
    @ApiModelProperty("订单id")
    @ExcelIgnore
    private String proTaskOrderId;

    @ApiModelProperty("产线名称（大正）")
    @ExcelProperty("产线名称")
    private String lineName;

    @ApiModelProperty("生产任务订单号（三一）")
    @ExcelProperty(value = "订单号")
    private String wipOrderNo;

    @ApiModelProperty("生产任务订单类型(1：正常订单；2：返工返修订单)")
    @ExcelProperty(value = "生产任务订单类型",converter = MomOrderExcelWrite.class)
    private String wipOrderType;

    @ApiModelProperty("110已下达 120进行中 130已完工 140已删除 150强制关闭")
    @ExcelProperty(value = "订单状态",converter = MomOrderExcelWrite.class)
    private String progressStatus;

    @ApiModelProperty("产品名称（大正）")
    @ExcelProperty("产品名称")
    private String productName;

    @ApiModelProperty("工序名称")
    @ExcelProperty("工序名称")
    private String OprSequenceName;

    /**
     * 对接码
     */
    @ApiModelProperty(value = "对接码")
    @ExcelProperty("对接码")
    private String dockingCode;

    @ApiModelProperty("产品物料号")
    @ExcelProperty("产品物料号")
    private String productNo;

    @ApiModelProperty("计划生产数量(订单工件数量)")
    @ExcelProperty("计划生产数量")
    private Integer quantity;

    @ApiModelProperty("计划开始时间")
    @ExcelProperty("计划开始时间")
    private Date scheduledStartDate;
    @ApiModelProperty("计划结束时间")
    @ExcelProperty("计划结束时间")
    private Date scheduledCompleteDate;

    @TableField("reality_start_date")
    @ApiModelProperty("实际开始时间")
    @ExcelProperty("实际开始时间")
    private Date realityStartDate;

    @TableField("reality_complete_date")
    @ApiModelProperty("实际结束时间")
    @ExcelProperty("实际结束时间")
    private Date realityCompleteDate;
    /**
     * 订单状态请求变更结果 1.操作进行中  2.操作完成
     */
    @ApiModelProperty(value = "订单状态请求变更结果 1.操作进行中  2.操作完成")
    @ExcelIgnore
    private Integer orderOperationResult;

    @ApiModelProperty(value = "订单实际产出数量")
    @ExcelProperty("实际生产数量")
    private Integer orderOutput;

    /**
     * 产线id
     */
    @ApiModelProperty(value = "产线id")
    @ExcelIgnore
    private Long lineId;

    @ApiModelProperty(value = "订单创建时间")
    @ExcelProperty("创建时间")
    private String createTIme;

    @ApiModelProperty(value = "最后修改时间")
    @ExcelProperty("最后修改时间")
    private String lastUpTime;

    @ApiModelProperty(value = "Ok报工数量")
    @ExcelProperty("Ok报工数量")
    private Integer okReportNum;

    @ApiModelProperty(value = "Ng报工数量")
    @ExcelProperty("Ng报工数量")
    private Integer ngReportNum;

    @ApiModelProperty(value = "订单Json下发原数据")
    @ExcelIgnore
    private String jsonOriginalData;
}
