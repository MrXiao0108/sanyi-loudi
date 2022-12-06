package com.dzics.common.model.response.mom;

import com.dzics.common.util.PageLimit;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class WorkStationParms {

    @ApiModelProperty("当前页")
    private int page = 1;
    @ApiModelProperty("每页查询条数")
    private int limit = 10;

    @NotBlank(message = "产线编号必填写")
    @ApiModelProperty("产线编号")
    private String lineNo;

    @NotBlank(message = "订单号必填")
    @ApiModelProperty("订单号")
    private String orderNo;

    @ApiModelProperty("搜索起始时间")
    private String startTime;

    @ApiModelProperty("搜索结束时间")
    private String endTime;
}
