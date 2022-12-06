package com.dzics.common.model.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class SelectTrendChartVo {

    @ApiModelProperty(value = "产品id(序号)", required = true)
    @NotBlank(message = "请选择产品")
    private String productNo;
    @ApiModelProperty(value = "关联产品检测配置表 id", required = true)
    @NotNull(message = "请选择检测项")
    private Long detectionId;

    @ApiModelProperty(value = "搜索起始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    @ApiModelProperty(value = "搜索结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

//    @ApiModelProperty(value = "站点编码",required=false)
//    private String orgCode;


    /**
     * 检测项
     */
    private String detectionName;

}
