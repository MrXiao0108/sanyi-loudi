package com.dzics.common.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class SysOperationLoggingVo {

    @ApiModelProperty("操作模块")
    private String title;
    @ApiModelProperty("操作内容")
    private String operDesc;
    @ApiModelProperty("操作状态")
    private Integer status;
    @ApiModelProperty("搜索起始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    @ApiModelProperty("搜索结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

}
