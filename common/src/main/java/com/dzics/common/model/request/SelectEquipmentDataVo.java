package com.dzics.common.model.request;


import com.dzics.common.util.PageLimit;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Data
public class SelectEquipmentDataVo extends PageLimit {

    @ApiModelProperty("设备类型(不用管这个字段)")
    private Integer equipmentType;
    @ApiModelProperty("数据编码(不用管这个字段)")
    private String orgCode;
    @ApiModelProperty("产线id")
    private String lineId;
    @ApiModelProperty("站点名称")
    private String departName;
    @ApiModelProperty("订单编号")
    private String orderNo;
    @ApiModelProperty("产线名称")
    private String lineName;
    @ApiModelProperty("机器人编码")
    private String equipmentCode;
    @ApiModelProperty("班次名称")
    private String workName;
    @ApiModelProperty("设备序号")
    private String equipmentNo;

    @ApiModelProperty(value = "班次开始时间",dataType = "java.lang.String")
//    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String startTime;
    @ApiModelProperty(value = "班次结束时间",dataType = "java.lang.String")
//    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String endTime;

    @ApiModelProperty("查询不填")
    private String tableKey;


}
