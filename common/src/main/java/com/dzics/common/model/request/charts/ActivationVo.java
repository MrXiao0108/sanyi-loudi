package com.dzics.common.model.request.charts;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class ActivationVo {
    @ApiModelProperty(value = "订单id",required = true)
    @NotNull(message = "请选择订单")
    private Long orderId;
    @ApiModelProperty(value = "产线id",required = true)
    @NotNull(message = "请选择产线")
    private List<Long> lineList;
    @ApiModelProperty("搜索起始时间")
    @NotNull(message = "请选择时间范围")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    @ApiModelProperty("搜索结束时间")
    @NotNull(message = "请选择时间范围")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
}
