package com.dzics.business.model.vo.plan;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Date;

/**
 * 产线计划分析请求参数
 *
 * @author ZhangChengJun
 * Date 2021/2/21.
 * @since
 */
@Data
public class PlanAnalysisGraphicalTime {
    @ApiModelProperty("产线ID")
    @NotNull(message = "请选择产线")
    private String lineId;

    @ApiModelProperty("订单ID")
    private String orderId;

    @ApiModelProperty("开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "选择查看日期")
    private Date startTime;

    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;


}
