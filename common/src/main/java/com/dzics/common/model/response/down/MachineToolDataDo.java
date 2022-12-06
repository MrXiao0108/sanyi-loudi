package com.dzics.common.model.response.down;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MachineToolDataDo {

    @ExcelProperty("订单编号")
    private String orderNo;

    @ExcelProperty("归属站点")
    private String departName;

    @ExcelProperty("产线名称")
    private String lineName;

    @ExcelProperty("机床序号")
    private String equipmentNo;

    @ExcelProperty("机床编号")
    private String equipmentCode;

    @ExcelProperty("机床名称")
    private String equipmentName;

    @ExcelProperty("日期")
    private String workData;

    @ExcelProperty("班次")
    private String workName;

    @ExcelProperty("班次开始时间")
    private String workStartTime;

    @ExcelProperty("班次结束时间")
    private String workStartEnd;

    @ExcelProperty("生产数量")
    private String nowNum;

    @ExcelProperty("毛坯数量")
    private String roughNum;

    @ExcelProperty("合格数量")
    private String qualifiedNum;

}
