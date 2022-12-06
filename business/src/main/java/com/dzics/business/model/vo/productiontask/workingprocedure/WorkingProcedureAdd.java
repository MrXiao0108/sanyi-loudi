package com.dzics.business.model.vo.productiontask.workingprocedure;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 工序管理
 *
 * @author ZhangChengJun
 * Date 2021/5/18.
 * @since
 */
@Data
public class WorkingProcedureAdd {

    @ApiModelProperty(value = "工序id",required = true)
    private String workingProcedureId;

    @ApiModelProperty(value = "订单id",required = true)
    @NotNull(message = "订单必选")
    private String orderId;

    @ApiModelProperty(value = "产线id",required = true)
    @NotNull(message = "产线必选")
    private String lineId;

    @ApiModelProperty(value = "工序编码",required = true)
    @NotNull(message = "工序编码必填")
    private String workCode;

    @ApiModelProperty(value = "工序名称",required = true)
    @NotNull(message = "工序名称必填")
    private String workName;

    @ApiModelProperty(value = "站点id",required = true)
    @NotNull(message = "站点id必传")
    private String departId;

    @ApiModelProperty(value = "工序排序吗",required = true)
    @NotNull(message = "排序码必传")
    private Integer sortCode;
}
