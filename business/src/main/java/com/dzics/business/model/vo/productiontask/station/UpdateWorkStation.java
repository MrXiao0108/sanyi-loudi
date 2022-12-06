package com.dzics.business.model.vo.productiontask.station;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 更新工位参数
 *
 * @author ZhangChengJun
 * Date 2021/5/18.
 * @since
 */
@Data
public class UpdateWorkStation {

    @ApiModelProperty("工位ID")
    @NotNull(message = "工位ID必填")
    private String stationId;

    @ApiModelProperty(value = "工序id")
    @NotNull(message = "工序ID必填")
    private String workingProcedureId;

    @ApiModelProperty(value = "工位名称")
    @NotNull(message = "工位名称必填")
    private String stationName;

    @ApiModelProperty(value = "工位编号")
    @NotNull(message = "工位编号必填")
    private String stationCode;

    @ApiModelProperty(value = "排序码")
    @NotNull(message = "排序码必填")
    private Integer sortCode;

    @ApiModelProperty("是否NG工位")
    private String ngCode;

    @ApiModelProperty("是否出料")
    private String outFlag;

    @ApiModelProperty("合并工位标识")
    private String mergeCode;

    @ApiModelProperty("报工工位编号")
    private String dzStationCode;

}
