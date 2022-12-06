package com.dzics.common.model.response.productiontask.station;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 工位列表
 *
 * @author ZhangChengJun
 * Date 2021/5/18.
 * @since
 */
@Data
public class ResWorkStation {
    @ApiModelProperty("订单ID")
    private String orderId;
    @ApiModelProperty("产线ID")
    private String lineId;

    @ApiModelProperty("产线名称")
    private String lineName;
    /**
     * 订单号
     */
    @ApiModelProperty("订单号")
    private String  orderNo;
    /**
     * 产线号
     */
    @ApiModelProperty("产线号")
    private String lineNo;

    @ExcelProperty("工序编号")
    @ApiModelProperty("工序编号")
    private String workcode;


    @ExcelProperty("工序名称")
    @ApiModelProperty("工序名称")
    private String workName;


    @ExcelProperty("工位编号")
    @ApiModelProperty("工位编号")
    private String stationCode;

    @ExcelProperty("工位名称")
    @ApiModelProperty("工位名称")
    private String stationName;

    @ExcelProperty("排序码")
    @ApiModelProperty("排序码")
    private String sortCode;



    @ApiModelProperty("工序ID")
    @ExcelIgnore
    private String workingProcedureId;



    @ApiModelProperty("1展示0不展示")
    @ExcelIgnore
    private Integer onOff;

    @ApiModelProperty("工位ID")
    @ExcelIgnore
    private String stationId;


    @ApiModelProperty(value = "是否NG工位")
    @ExcelIgnore
    private String  ngCode;
    /**
     * 合并工位标志 merge_code
     */
    @ApiModelProperty(value = "合并工位标志")
    @ExcelIgnore
    private String mergeCode;

    @ApiModelProperty(value = "出料位置标记 1 是出料位置，0 不是出料位置")
    @ExcelIgnore
    private String outFlag;



    @ApiModelProperty("dzics工位编号")
    private String dzStationCode;

}
