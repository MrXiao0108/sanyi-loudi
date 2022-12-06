package com.dzics.common.model.request.plan;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class SelectProductionPlanRecordVo {

    @ApiModelProperty("站点名称")
    private String departName;
    @ApiModelProperty("订单编号")
    private String orderNo;
    @ApiModelProperty("产线名称")
    private String lineName;
    @ApiModelProperty("搜索起始时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    @ApiModelProperty("搜索结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    @ApiModelProperty("搜索不填")
    private String orgCode;
    @ApiModelProperty("产线id")
    private String lineId;
    @ApiModelProperty("搜索不填")
    private String planDayTable;

    @ApiModelProperty("排序字段")
    private String field;
    @ApiModelProperty("ASC OR DESC OR 空字符串")
    private String type;

}
